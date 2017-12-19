package es.udc.reunions.web.rest;

import es.udc.reunions.ReunionsApp;

import es.udc.reunions.domain.Sesion;
import es.udc.reunions.domain.Organo;
import es.udc.reunions.repository.SesionRepository;
import es.udc.reunions.repository.UserRepository;
import es.udc.reunions.service.MiembroService;
import es.udc.reunions.service.SesionService;
import es.udc.reunions.repository.search.SesionSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SesionResource REST controller.
 *
 * @see SesionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReunionsApp.class)
public class SesionResourceIntTest {

    private static final Long DEFAULT_NUMERO = 1L;
    private static final Long UPDATED_NUMERO = 2L;

    private static final ZonedDateTime DEFAULT_PRIMERA_CONVOCATORIA = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_PRIMERA_CONVOCATORIA = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_PRIMERA_CONVOCATORIA_STR = DateTimeFormatter.ISO_INSTANT.format(DEFAULT_PRIMERA_CONVOCATORIA);

    private static final ZonedDateTime DEFAULT_SEGUNDA_CONVOCATORIA = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SEGUNDA_CONVOCATORIA = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_SEGUNDA_CONVOCATORIA_STR = DateTimeFormatter.ISO_INSTANT.format(DEFAULT_SEGUNDA_CONVOCATORIA);

    private static final String DEFAULT_LUGAR = "AAAAA";
    private static final String UPDATED_LUGAR = "BBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBB";

    @Inject
    private SesionRepository sesionRepository;

    @Inject
    private SesionService sesionService;
    
    @Inject
    private MiembroService miembroService;

    @Inject
    private SesionSearchRepository sesionSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restSesionMockMvc;

    private Sesion sesion;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SesionResource sesionResource = new SesionResource();
        ReflectionTestUtils.setField(sesionResource, "sesionService", sesionService);
        ReflectionTestUtils.setField(sesionResource, "miembroService", miembroService);
        this.restSesionMockMvc = MockMvcBuilders.standaloneSetup(sesionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sesion createEntity(EntityManager em) {
        Sesion sesion = new Sesion()
                .numero(DEFAULT_NUMERO)
                .primeraConvocatoria(DEFAULT_PRIMERA_CONVOCATORIA)
                .segundaConvocatoria(DEFAULT_SEGUNDA_CONVOCATORIA)
                .lugar(DEFAULT_LUGAR)
                .descripcion(DEFAULT_DESCRIPCION);
        // Add required entity
        Organo organo = OrganoResourceIntTest.createEntity(em);
        em.persist(organo);
        em.flush();
        sesion.setOrgano(organo);
        return sesion;
    }

    @Before
    public void initTest() {
        sesionSearchRepository.deleteAll();
        sesion = createEntity(em);
    }

    
    /** 
     * 
     * Añadido WithMockUser con rol ADMIN para que Spring Security detecte un usuario administrador (que puede crear
     * sesiones). 
     * Añadido el miembroService en el sesionResource (en el setup de los tests) -> CAUSABA NULL POINTER
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(username="admin",authorities={"ROLE_ADMIN"}, password = "admin")
    public void createSesion() throws Exception {
        int databaseSizeBeforeCreate = sesionRepository.findAll().size();

        // Create the Sesion
        restSesionMockMvc.perform(post("/api/sesions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sesion)))
                .andExpect(status().isCreated());

        // Validate the Sesion in the database
        List<Sesion> sesions = sesionRepository.findAll();
        assertThat(sesions).hasSize(databaseSizeBeforeCreate + 1);
        Sesion testSesion = sesions.get(sesions.size() - 1);
        assertThat(testSesion.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testSesion.getPrimeraConvocatoria()).isEqualTo(DEFAULT_PRIMERA_CONVOCATORIA);
        assertThat(testSesion.getSegundaConvocatoria()).isEqualTo(DEFAULT_SEGUNDA_CONVOCATORIA);
        assertThat(testSesion.getLugar()).isEqualTo(DEFAULT_LUGAR);
        assertThat(testSesion.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);

        // Validate the Sesion in ElasticSearch
        Sesion sesionEs = sesionSearchRepository.findOne(testSesion.getId());
        assertThat(sesionEs).isEqualToComparingFieldByField(testSesion);
    }

    @Test
    @Transactional
    public void checkNumeroIsRequired() throws Exception {
        int databaseSizeBeforeTest = sesionRepository.findAll().size();
        // set the field null
        sesion.setNumero(null);

        // Create the Sesion, which fails.

        restSesionMockMvc.perform(post("/api/sesions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sesion)))
                .andExpect(status().isBadRequest());

        List<Sesion> sesions = sesionRepository.findAll();
        assertThat(sesions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPrimeraConvocatoriaIsRequired() throws Exception {
        int databaseSizeBeforeTest = sesionRepository.findAll().size();
        // set the field null
        sesion.setPrimeraConvocatoria(null);

        // Create the Sesion, which fails.

        restSesionMockMvc.perform(post("/api/sesions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sesion)))
                .andExpect(status().isBadRequest());

        List<Sesion> sesions = sesionRepository.findAll();
        assertThat(sesions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLugarIsRequired() throws Exception {
        int databaseSizeBeforeTest = sesionRepository.findAll().size();
        // set the field null
        sesion.setLugar(null);

        // Create the Sesion, which fails.

        restSesionMockMvc.perform(post("/api/sesions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sesion)))
                .andExpect(status().isBadRequest());

        List<Sesion> sesions = sesionRepository.findAll();
        assertThat(sesions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSesions() throws Exception {
        // Initialize the database
        sesionRepository.saveAndFlush(sesion);

        // Get all the sesions
        restSesionMockMvc.perform(get("/api/sesions?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(sesion.getId().intValue())))
                .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO.intValue())))
                .andExpect(jsonPath("$.[*].primeraConvocatoria").value(hasItem(DEFAULT_PRIMERA_CONVOCATORIA_STR)))
                .andExpect(jsonPath("$.[*].segundaConvocatoria").value(hasItem(DEFAULT_SEGUNDA_CONVOCATORIA_STR)))
                .andExpect(jsonPath("$.[*].lugar").value(hasItem(DEFAULT_LUGAR.toString())))
                .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION.toString())));
    }

    @Test
    @Transactional
    public void getSesion() throws Exception {
        // Initialize the database
        sesionRepository.saveAndFlush(sesion);

        // Get the sesion
        restSesionMockMvc.perform(get("/api/sesions/{id}", sesion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(sesion.getId().intValue()))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO.intValue()))
            .andExpect(jsonPath("$.primeraConvocatoria").value(DEFAULT_PRIMERA_CONVOCATORIA_STR))
            .andExpect(jsonPath("$.segundaConvocatoria").value(DEFAULT_SEGUNDA_CONVOCATORIA_STR))
            .andExpect(jsonPath("$.lugar").value(DEFAULT_LUGAR.toString()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSesion() throws Exception {
        // Get the sesion
        restSesionMockMvc.perform(get("/api/sesions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(username="admin",authorities={"ROLE_ADMIN"}, password = "admin")
    public void updateSesion() throws Exception {
        // Initialize the database
        sesionService.save(sesion);

        int databaseSizeBeforeUpdate = sesionRepository.findAll().size();

        // Update the sesion
        Sesion updatedSesion = sesionRepository.findOne(sesion.getId());
        updatedSesion
                .numero(UPDATED_NUMERO)
                .primeraConvocatoria(UPDATED_PRIMERA_CONVOCATORIA)
                .segundaConvocatoria(UPDATED_SEGUNDA_CONVOCATORIA)
                .lugar(UPDATED_LUGAR)
                .descripcion(UPDATED_DESCRIPCION);

        restSesionMockMvc.perform(put("/api/sesions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSesion)))
                .andExpect(status().isOk());

        // Validate the Sesion in the database
        List<Sesion> sesions = sesionRepository.findAll();
        assertThat(sesions).hasSize(databaseSizeBeforeUpdate);
        Sesion testSesion = sesions.get(sesions.size() - 1);
        assertThat(testSesion.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testSesion.getPrimeraConvocatoria()).isEqualTo(UPDATED_PRIMERA_CONVOCATORIA);
        assertThat(testSesion.getSegundaConvocatoria()).isEqualTo(UPDATED_SEGUNDA_CONVOCATORIA);
        assertThat(testSesion.getLugar()).isEqualTo(UPDATED_LUGAR);
        assertThat(testSesion.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);

        // Validate the Sesion in ElasticSearch
        Sesion sesionEs = sesionSearchRepository.findOne(testSesion.getId());
        assertThat(sesionEs).isEqualToComparingFieldByField(testSesion);
    }
  @Test
  @Transactional
  @WithMockUser(username="admin",authorities={"ROLE_ADMIN"}, password = "admin")
  public void deleteSesion() throws Exception {
      // Initialize the database
      sesionService.save(sesion);

      int databaseSizeBeforeDelete = sesionRepository.findAll().size();

      // Get the sesion
      restSesionMockMvc.perform(delete("/api/sesions/{id}", sesion.getId())
              .accept(TestUtil.APPLICATION_JSON_UTF8))
              .andExpect(status().isOk());

      // Validate ElasticSearch is empty
      boolean sesionExistsInEs = sesionSearchRepository.exists(sesion.getId());
      assertThat(sesionExistsInEs).isFalse();

      // Validate the database is empty
      List<Sesion> sesions = sesionRepository.findAll();
      assertThat(sesions).hasSize(databaseSizeBeforeDelete - 1);
  }

    @Test
    @Transactional
    @WithMockUser(username="admin",authorities={"ROLE_ADMIN"}, password = "admin")
    public void searchSesion() throws Exception {
        // Initialize the database
        sesionService.save(sesion);

        // Search the sesion
        restSesionMockMvc.perform(get("/api/_search/sesions?query=id:" + sesion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sesion.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO.intValue())))
            .andExpect(jsonPath("$.[*].primeraConvocatoria").value(hasItem(DEFAULT_PRIMERA_CONVOCATORIA_STR)))
            .andExpect(jsonPath("$.[*].segundaConvocatoria").value(hasItem(DEFAULT_SEGUNDA_CONVOCATORIA_STR)))
            .andExpect(jsonPath("$.[*].lugar").value(hasItem(DEFAULT_LUGAR.toString())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION.toString())));
    }
}
