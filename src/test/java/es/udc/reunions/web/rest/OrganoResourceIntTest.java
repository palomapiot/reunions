package es.udc.reunions.web.rest;

import es.udc.reunions.ReunionsApp;

import es.udc.reunions.domain.Organo;
import es.udc.reunions.domain.Grupo;
import es.udc.reunions.repository.OrganoRepository;
import es.udc.reunions.service.OrganoService;
import es.udc.reunions.repository.search.OrganoSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OrganoResource REST controller.
 *
 * @see OrganoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReunionsApp.class)
public class OrganoResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAAA";
    private static final String UPDATED_NOMBRE = "BBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBB";

    @Inject
    private OrganoRepository organoRepository;

    @Inject
    private OrganoService organoService;

    @Inject
    private OrganoSearchRepository organoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restOrganoMockMvc;

    private Organo organo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrganoResource organoResource = new OrganoResource();
        ReflectionTestUtils.setField(organoResource, "organoService", organoService);
        this.restOrganoMockMvc = MockMvcBuilders.standaloneSetup(organoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organo createEntity(EntityManager em) {
        Organo organo = new Organo()
                .nombre(DEFAULT_NOMBRE)
                .descripcion(DEFAULT_DESCRIPCION);
        // Add required entity
        Grupo grupo = GrupoResourceIntTest.createEntity(em);
        em.persist(grupo);
        em.flush();
        organo.setGrupo(grupo);
        return organo;
    }

    @Before
    public void initTest() {
        organoSearchRepository.deleteAll();
        organo = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrgano() throws Exception {
        int databaseSizeBeforeCreate = organoRepository.findAll().size();

        // Create the Organo

        restOrganoMockMvc.perform(post("/api/organos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organo)))
                .andExpect(status().isCreated());

        // Validate the Organo in the database
        List<Organo> organos = organoRepository.findAll();
        assertThat(organos).hasSize(databaseSizeBeforeCreate + 1);
        Organo testOrgano = organos.get(organos.size() - 1);
        assertThat(testOrgano.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testOrgano.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);

        // Validate the Organo in ElasticSearch
        Organo organoEs = organoSearchRepository.findOne(testOrgano.getId());
        assertThat(organoEs).isEqualToComparingFieldByField(testOrgano);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = organoRepository.findAll().size();
        // set the field null
        organo.setNombre(null);

        // Create the Organo, which fails.

        restOrganoMockMvc.perform(post("/api/organos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organo)))
                .andExpect(status().isBadRequest());

        List<Organo> organos = organoRepository.findAll();
        assertThat(organos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOrganos() throws Exception {
        // Initialize the database
        organoRepository.saveAndFlush(organo);

        // Get all the organos
        restOrganoMockMvc.perform(get("/api/organos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(organo.getId().intValue())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
                .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION.toString())));
    }

    @Test
    @Transactional
    public void getOrgano() throws Exception {
        // Initialize the database
        organoRepository.saveAndFlush(organo);

        // Get the organo
        restOrganoMockMvc.perform(get("/api/organos/{id}", organo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(organo.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOrgano() throws Exception {
        // Get the organo
        restOrganoMockMvc.perform(get("/api/organos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrgano() throws Exception {
        // Initialize the database
        organoService.save(organo);

        int databaseSizeBeforeUpdate = organoRepository.findAll().size();

        // Update the organo
        Organo updatedOrgano = organoRepository.findOne(organo.getId());
        updatedOrgano
                .nombre(UPDATED_NOMBRE)
                .descripcion(UPDATED_DESCRIPCION);

        restOrganoMockMvc.perform(put("/api/organos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedOrgano)))
                .andExpect(status().isOk());

        // Validate the Organo in the database
        List<Organo> organos = organoRepository.findAll();
        assertThat(organos).hasSize(databaseSizeBeforeUpdate);
        Organo testOrgano = organos.get(organos.size() - 1);
        assertThat(testOrgano.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testOrgano.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);

        // Validate the Organo in ElasticSearch
        Organo organoEs = organoSearchRepository.findOne(testOrgano.getId());
        assertThat(organoEs).isEqualToComparingFieldByField(testOrgano);
    }

    @Test
    @Transactional
    public void deleteOrgano() throws Exception {
        // Initialize the database
        organoService.save(organo);

        int databaseSizeBeforeDelete = organoRepository.findAll().size();

        // Get the organo
        restOrganoMockMvc.perform(delete("/api/organos/{id}", organo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean organoExistsInEs = organoSearchRepository.exists(organo.getId());
        assertThat(organoExistsInEs).isFalse();

        // Validate the database is empty
        List<Organo> organos = organoRepository.findAll();
        assertThat(organos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOrgano() throws Exception {
        // Initialize the database
        organoService.save(organo);

        // Search the organo
        restOrganoMockMvc.perform(get("/api/_search/organos?query=id:" + organo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organo.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION.toString())));
    }
}
