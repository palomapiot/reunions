package es.udc.reunions.web.rest;

import es.udc.reunions.ReunionsApp;

import es.udc.reunions.domain.Miembro;
import es.udc.reunions.domain.Organo;
import es.udc.reunions.domain.Cargo;
import es.udc.reunions.domain.User;
import es.udc.reunions.repository.MiembroRepository;
import es.udc.reunions.service.MiembroService;
import es.udc.reunions.repository.search.MiembroSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MiembroResource REST controller.
 *
 * @see MiembroResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReunionsApp.class)
public class MiembroResourceIntTest {

    private static final LocalDate DEFAULT_FECHA_ALTA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_ALTA = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_FECHA_BAJA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_BAJA = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_OBSERVACIONES = "AAAAA";
    private static final String UPDATED_OBSERVACIONES = "BBBBB";

    @Inject
    private MiembroRepository miembroRepository;

    @Inject
    private MiembroService miembroService;

    @Inject
    private MiembroSearchRepository miembroSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restMiembroMockMvc;

    private Miembro miembro;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MiembroResource miembroResource = new MiembroResource();
        ReflectionTestUtils.setField(miembroResource, "miembroService", miembroService);
        this.restMiembroMockMvc = MockMvcBuilders.standaloneSetup(miembroResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Miembro createEntity(EntityManager em) {
        Miembro miembro = new Miembro()
                .fechaAlta(DEFAULT_FECHA_ALTA)
                .fechaBaja(DEFAULT_FECHA_BAJA)
                .observaciones(DEFAULT_OBSERVACIONES);
        // Add required entity
        Organo organo = OrganoResourceIntTest.createEntity(em);
        em.persist(organo);
        em.flush();
        miembro.setOrgano(organo);
        // Add required entity
        Cargo cargo = CargoResourceIntTest.createEntity(em);
        em.persist(cargo);
        em.flush();
        miembro.setCargo(cargo);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        miembro.setUser(user);
        return miembro;
    }

    @Before
    public void initTest() {
        miembroSearchRepository.deleteAll();
        miembro = createEntity(em);
    }

    @Test
    @Transactional
    public void createMiembro() throws Exception {
        int databaseSizeBeforeCreate = miembroRepository.findAll().size();

        // Create the Miembro

        restMiembroMockMvc.perform(post("/api/miembros")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(miembro)))
                .andExpect(status().isCreated());

        // Validate the Miembro in the database
        List<Miembro> miembros = miembroRepository.findAll();
        assertThat(miembros).hasSize(databaseSizeBeforeCreate + 1);
        Miembro testMiembro = miembros.get(miembros.size() - 1);
        assertThat(testMiembro.getFechaAlta()).isEqualTo(DEFAULT_FECHA_ALTA);
        assertThat(testMiembro.getFechaBaja()).isEqualTo(DEFAULT_FECHA_BAJA);
        assertThat(testMiembro.getObservaciones()).isEqualTo(DEFAULT_OBSERVACIONES);

        // Validate the Miembro in ElasticSearch
        Miembro miembroEs = miembroSearchRepository.findOne(testMiembro.getId());
        assertThat(miembroEs).isEqualToComparingFieldByField(testMiembro);
    }

    @Test
    @Transactional
    public void checkFechaAltaIsRequired() throws Exception {
        int databaseSizeBeforeTest = miembroRepository.findAll().size();
        // set the field null
        miembro.setFechaAlta(null);

        // Create the Miembro, which fails.

        restMiembroMockMvc.perform(post("/api/miembros")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(miembro)))
                .andExpect(status().isBadRequest());

        List<Miembro> miembros = miembroRepository.findAll();
        assertThat(miembros).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMiembros() throws Exception {
        // Initialize the database
        miembroRepository.saveAndFlush(miembro);

        // Get all the miembros
        restMiembroMockMvc.perform(get("/api/miembros?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(miembro.getId().intValue())))
                .andExpect(jsonPath("$.[*].fechaAlta").value(hasItem(DEFAULT_FECHA_ALTA.toString())))
                .andExpect(jsonPath("$.[*].fechaBaja").value(hasItem(DEFAULT_FECHA_BAJA.toString())))
                .andExpect(jsonPath("$.[*].observaciones").value(hasItem(DEFAULT_OBSERVACIONES.toString())));
    }

    @Test
    @Transactional
    public void getMiembro() throws Exception {
        // Initialize the database
        miembroRepository.saveAndFlush(miembro);

        // Get the miembro
        restMiembroMockMvc.perform(get("/api/miembros/{id}", miembro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(miembro.getId().intValue()))
            .andExpect(jsonPath("$.fechaAlta").value(DEFAULT_FECHA_ALTA.toString()))
            .andExpect(jsonPath("$.fechaBaja").value(DEFAULT_FECHA_BAJA.toString()))
            .andExpect(jsonPath("$.observaciones").value(DEFAULT_OBSERVACIONES.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMiembro() throws Exception {
        // Get the miembro
        restMiembroMockMvc.perform(get("/api/miembros/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMiembro() throws Exception {
        // Initialize the database
        miembroService.save(miembro);

        int databaseSizeBeforeUpdate = miembroRepository.findAll().size();

        // Update the miembro
        Miembro updatedMiembro = miembroRepository.findOne(miembro.getId());
        updatedMiembro
                .fechaAlta(UPDATED_FECHA_ALTA)
                .fechaBaja(UPDATED_FECHA_BAJA)
                .observaciones(UPDATED_OBSERVACIONES);

        restMiembroMockMvc.perform(put("/api/miembros")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedMiembro)))
                .andExpect(status().isOk());

        // Validate the Miembro in the database
        List<Miembro> miembros = miembroRepository.findAll();
        assertThat(miembros).hasSize(databaseSizeBeforeUpdate);
        Miembro testMiembro = miembros.get(miembros.size() - 1);
        assertThat(testMiembro.getFechaAlta()).isEqualTo(UPDATED_FECHA_ALTA);
        assertThat(testMiembro.getFechaBaja()).isEqualTo(UPDATED_FECHA_BAJA);
        assertThat(testMiembro.getObservaciones()).isEqualTo(UPDATED_OBSERVACIONES);

        // Validate the Miembro in ElasticSearch
        Miembro miembroEs = miembroSearchRepository.findOne(testMiembro.getId());
        assertThat(miembroEs).isEqualToComparingFieldByField(testMiembro);
    }

    @Test
    @Transactional
    public void deleteMiembro() throws Exception {
        // Initialize the database
        miembroService.save(miembro);

        int databaseSizeBeforeDelete = miembroRepository.findAll().size();

        // Get the miembro
        restMiembroMockMvc.perform(delete("/api/miembros/{id}", miembro.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean miembroExistsInEs = miembroSearchRepository.exists(miembro.getId());
        assertThat(miembroExistsInEs).isFalse();

        // Validate the database is empty
        List<Miembro> miembros = miembroRepository.findAll();
        assertThat(miembros).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMiembro() throws Exception {
        // Initialize the database
        miembroService.save(miembro);

        // Search the miembro
        restMiembroMockMvc.perform(get("/api/_search/miembros?query=id:" + miembro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(miembro.getId().intValue())))
            .andExpect(jsonPath("$.[*].fechaAlta").value(hasItem(DEFAULT_FECHA_ALTA.toString())))
            .andExpect(jsonPath("$.[*].fechaBaja").value(hasItem(DEFAULT_FECHA_BAJA.toString())))
            .andExpect(jsonPath("$.[*].observaciones").value(hasItem(DEFAULT_OBSERVACIONES.toString())));
    }
}
