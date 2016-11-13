package es.udc.reunions.web.rest;

import es.udc.reunions.ReunionsApp;

import es.udc.reunions.domain.Grupo;
import es.udc.reunions.repository.GrupoRepository;
import es.udc.reunions.repository.search.GrupoSearchRepository;

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
 * Test class for the GrupoResource REST controller.
 *
 * @see GrupoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReunionsApp.class)
public class GrupoResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAAA";
    private static final String UPDATED_NOMBRE = "BBBBB";

    @Inject
    private GrupoRepository grupoRepository;

    @Inject
    private GrupoSearchRepository grupoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restGrupoMockMvc;

    private Grupo grupo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        GrupoResource grupoResource = new GrupoResource();
        ReflectionTestUtils.setField(grupoResource, "grupoSearchRepository", grupoSearchRepository);
        ReflectionTestUtils.setField(grupoResource, "grupoRepository", grupoRepository);
        this.restGrupoMockMvc = MockMvcBuilders.standaloneSetup(grupoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Grupo createEntity(EntityManager em) {
        Grupo grupo = new Grupo()
                .nombre(DEFAULT_NOMBRE);
        return grupo;
    }

    @Before
    public void initTest() {
        grupoSearchRepository.deleteAll();
        grupo = createEntity(em);
    }

    @Test
    @Transactional
    public void createGrupo() throws Exception {
        int databaseSizeBeforeCreate = grupoRepository.findAll().size();

        // Create the Grupo

        restGrupoMockMvc.perform(post("/api/grupos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(grupo)))
                .andExpect(status().isCreated());

        // Validate the Grupo in the database
        List<Grupo> grupos = grupoRepository.findAll();
        assertThat(grupos).hasSize(databaseSizeBeforeCreate + 1);
        Grupo testGrupo = grupos.get(grupos.size() - 1);
        assertThat(testGrupo.getNombre()).isEqualTo(DEFAULT_NOMBRE);

        // Validate the Grupo in ElasticSearch
        Grupo grupoEs = grupoSearchRepository.findOne(testGrupo.getId());
        assertThat(grupoEs).isEqualToComparingFieldByField(testGrupo);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = grupoRepository.findAll().size();
        // set the field null
        grupo.setNombre(null);

        // Create the Grupo, which fails.

        restGrupoMockMvc.perform(post("/api/grupos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(grupo)))
                .andExpect(status().isBadRequest());

        List<Grupo> grupos = grupoRepository.findAll();
        assertThat(grupos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllGrupos() throws Exception {
        // Initialize the database
        grupoRepository.saveAndFlush(grupo);

        // Get all the grupos
        restGrupoMockMvc.perform(get("/api/grupos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(grupo.getId().intValue())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())));
    }

    @Test
    @Transactional
    public void getGrupo() throws Exception {
        // Initialize the database
        grupoRepository.saveAndFlush(grupo);

        // Get the grupo
        restGrupoMockMvc.perform(get("/api/grupos/{id}", grupo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(grupo.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGrupo() throws Exception {
        // Get the grupo
        restGrupoMockMvc.perform(get("/api/grupos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGrupo() throws Exception {
        // Initialize the database
        grupoRepository.saveAndFlush(grupo);
        grupoSearchRepository.save(grupo);
        int databaseSizeBeforeUpdate = grupoRepository.findAll().size();

        // Update the grupo
        Grupo updatedGrupo = grupoRepository.findOne(grupo.getId());
        updatedGrupo
                .nombre(UPDATED_NOMBRE);

        restGrupoMockMvc.perform(put("/api/grupos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedGrupo)))
                .andExpect(status().isOk());

        // Validate the Grupo in the database
        List<Grupo> grupos = grupoRepository.findAll();
        assertThat(grupos).hasSize(databaseSizeBeforeUpdate);
        Grupo testGrupo = grupos.get(grupos.size() - 1);
        assertThat(testGrupo.getNombre()).isEqualTo(UPDATED_NOMBRE);

        // Validate the Grupo in ElasticSearch
        Grupo grupoEs = grupoSearchRepository.findOne(testGrupo.getId());
        assertThat(grupoEs).isEqualToComparingFieldByField(testGrupo);
    }

    @Test
    @Transactional
    public void deleteGrupo() throws Exception {
        // Initialize the database
        grupoRepository.saveAndFlush(grupo);
        grupoSearchRepository.save(grupo);
        int databaseSizeBeforeDelete = grupoRepository.findAll().size();

        // Get the grupo
        restGrupoMockMvc.perform(delete("/api/grupos/{id}", grupo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean grupoExistsInEs = grupoSearchRepository.exists(grupo.getId());
        assertThat(grupoExistsInEs).isFalse();

        // Validate the database is empty
        List<Grupo> grupos = grupoRepository.findAll();
        assertThat(grupos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchGrupo() throws Exception {
        // Initialize the database
        grupoRepository.saveAndFlush(grupo);
        grupoSearchRepository.save(grupo);

        // Search the grupo
        restGrupoMockMvc.perform(get("/api/_search/grupos?query=id:" + grupo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(grupo.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())));
    }
}
