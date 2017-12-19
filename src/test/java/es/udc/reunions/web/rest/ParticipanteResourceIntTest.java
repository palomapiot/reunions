package es.udc.reunions.web.rest;

import es.udc.reunions.ReunionsApp;

import es.udc.reunions.domain.Participante;
import es.udc.reunions.domain.Sesion;
import es.udc.reunions.domain.Cargo;
import es.udc.reunions.domain.User;
import es.udc.reunions.repository.ParticipanteRepository;
import es.udc.reunions.service.MiembroService;
import es.udc.reunions.service.ParticipanteService;
import es.udc.reunions.repository.search.ParticipanteSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.udc.reunions.domain.enumeration.Asistencia;
/**
 * Test class for the ParticipanteResource REST controller.
 *
 * @see ParticipanteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReunionsApp.class)
public class ParticipanteResourceIntTest {

    private static final Asistencia DEFAULT_ASISTENCIA = Asistencia.asiste;
    private static final Asistencia UPDATED_ASISTENCIA = Asistencia.falta;

    private static final String DEFAULT_OBSERVACIONES = "AAAAA";
    private static final String UPDATED_OBSERVACIONES = "BBBBB";

    @Inject
    private ParticipanteRepository participanteRepository;

    @Inject
    private ParticipanteService participanteService;

    @Inject
    private ParticipanteSearchRepository participanteSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;
    
    @Inject
    private MiembroService miembroService;

    private MockMvc restParticipanteMockMvc;

    private Participante participante;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ParticipanteResource participanteResource = new ParticipanteResource();
        ReflectionTestUtils.setField(participanteResource, "participanteService", participanteService);
        ReflectionTestUtils.setField(participanteResource, "miembroService", miembroService);
        this.restParticipanteMockMvc = MockMvcBuilders.standaloneSetup(participanteResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Participante createEntity(EntityManager em) {
        Participante participante = new Participante()
                .asistencia(DEFAULT_ASISTENCIA)
                .observaciones(DEFAULT_OBSERVACIONES);
        // Add required entity
        Sesion sesion = SesionResourceIntTest.createEntity(em);
        em.persist(sesion);
        em.flush();
        participante.setSesion(sesion);
        // Add required entity
        Cargo cargo = CargoResourceIntTest.createEntity(em);
        em.persist(cargo);
        em.flush();
        participante.setCargo(cargo);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        participante.setUser(user);
        return participante;
    }

    @Before
    public void initTest() {
        participanteSearchRepository.deleteAll();
        participante = createEntity(em);
    }

    @Test
    @Transactional
    @WithMockUser(username="admin",authorities={"ROLE_ADMIN"}, password = "admin")
    public void createParticipante() throws Exception {
        int databaseSizeBeforeCreate = participanteRepository.findAll().size();

        // Create the Participante

        restParticipanteMockMvc.perform(post("/api/participantes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(participante)))
                .andExpect(status().isCreated());

        // Validate the Participante in the database
        List<Participante> participantes = participanteRepository.findAll();
        assertThat(participantes).hasSize(databaseSizeBeforeCreate + 1);
        Participante testParticipante = participantes.get(participantes.size() - 1);
        assertThat(testParticipante.getAsistencia()).isEqualTo(DEFAULT_ASISTENCIA);
        assertThat(testParticipante.getObservaciones()).isEqualTo(DEFAULT_OBSERVACIONES);

        // Validate the Participante in ElasticSearch
        Participante participanteEs = participanteSearchRepository.findOne(testParticipante.getId());
        assertThat(participanteEs).isEqualToComparingFieldByField(testParticipante);
    }

    @Test
    @Transactional
    public void getAllParticipantes() throws Exception {
        // Initialize the database
        participanteRepository.saveAndFlush(participante);

        // Get all the participantes
        restParticipanteMockMvc.perform(get("/api/participantes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(participante.getId().intValue())))
                .andExpect(jsonPath("$.[*].asistencia").value(hasItem(DEFAULT_ASISTENCIA.toString())))
                .andExpect(jsonPath("$.[*].observaciones").value(hasItem(DEFAULT_OBSERVACIONES.toString())));
    }

    @Test
    @Transactional
    public void getParticipante() throws Exception {
        // Initialize the database
        participanteRepository.saveAndFlush(participante);

        // Get the participante
        restParticipanteMockMvc.perform(get("/api/participantes/{id}", participante.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(participante.getId().intValue()))
            .andExpect(jsonPath("$.asistencia").value(DEFAULT_ASISTENCIA.toString()))
            .andExpect(jsonPath("$.observaciones").value(DEFAULT_OBSERVACIONES.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingParticipante() throws Exception {
        // Get the participante
        restParticipanteMockMvc.perform(get("/api/participantes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(username="admin",authorities={"ROLE_ADMIN"}, password = "admin")
    public void updateParticipante() throws Exception {
        // Initialize the database
        participanteService.save(participante);

        int databaseSizeBeforeUpdate = participanteRepository.findAll().size();

        // Update the participante
        Participante updatedParticipante = participanteRepository.findOne(participante.getId());
        updatedParticipante
                .asistencia(UPDATED_ASISTENCIA)
                .observaciones(UPDATED_OBSERVACIONES);

        restParticipanteMockMvc.perform(put("/api/participantes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedParticipante)))
                .andExpect(status().isOk());

        // Validate the Participante in the database
        List<Participante> participantes = participanteRepository.findAll();
        assertThat(participantes).hasSize(databaseSizeBeforeUpdate);
        Participante testParticipante = participantes.get(participantes.size() - 1);
        assertThat(testParticipante.getAsistencia()).isEqualTo(UPDATED_ASISTENCIA);
        assertThat(testParticipante.getObservaciones()).isEqualTo(UPDATED_OBSERVACIONES);

        // Validate the Participante in ElasticSearch
        Participante participanteEs = participanteSearchRepository.findOne(testParticipante.getId());
        assertThat(participanteEs).isEqualToComparingFieldByField(testParticipante);
    }

    @Test
    @Transactional
    @WithMockUser(username="admin",authorities={"ROLE_ADMIN"}, password = "admin")
    public void deleteParticipante() throws Exception {
        // Initialize the database
        participanteService.save(participante);

        int databaseSizeBeforeDelete = participanteRepository.findAll().size();

        // Get the participante
        restParticipanteMockMvc.perform(delete("/api/participantes/{id}", participante.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean participanteExistsInEs = participanteSearchRepository.exists(participante.getId());
        assertThat(participanteExistsInEs).isFalse();

        // Validate the database is empty
        List<Participante> participantes = participanteRepository.findAll();
        assertThat(participantes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchParticipante() throws Exception {
        // Initialize the database
        participanteService.save(participante);

        // Search the participante
        restParticipanteMockMvc.perform(get("/api/_search/participantes?query=id:" + participante.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participante.getId().intValue())))
            .andExpect(jsonPath("$.[*].asistencia").value(hasItem(DEFAULT_ASISTENCIA.toString())))
            .andExpect(jsonPath("$.[*].observaciones").value(hasItem(DEFAULT_OBSERVACIONES.toString())));
    }
}
