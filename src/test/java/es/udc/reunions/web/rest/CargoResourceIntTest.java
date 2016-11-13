package es.udc.reunions.web.rest;

import es.udc.reunions.ReunionsApp;

import es.udc.reunions.domain.Cargo;
import es.udc.reunions.repository.CargoRepository;
import es.udc.reunions.repository.search.CargoSearchRepository;

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
 * Test class for the CargoResource REST controller.
 *
 * @see CargoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReunionsApp.class)
public class CargoResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAAA";
    private static final String UPDATED_NOMBRE = "BBBBB";

    @Inject
    private CargoRepository cargoRepository;

    @Inject
    private CargoSearchRepository cargoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restCargoMockMvc;

    private Cargo cargo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CargoResource cargoResource = new CargoResource();
        ReflectionTestUtils.setField(cargoResource, "cargoSearchRepository", cargoSearchRepository);
        ReflectionTestUtils.setField(cargoResource, "cargoRepository", cargoRepository);
        this.restCargoMockMvc = MockMvcBuilders.standaloneSetup(cargoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cargo createEntity(EntityManager em) {
        Cargo cargo = new Cargo()
                .nombre(DEFAULT_NOMBRE);
        return cargo;
    }

    @Before
    public void initTest() {
        cargoSearchRepository.deleteAll();
        cargo = createEntity(em);
    }

    @Test
    @Transactional
    public void createCargo() throws Exception {
        int databaseSizeBeforeCreate = cargoRepository.findAll().size();

        // Create the Cargo

        restCargoMockMvc.perform(post("/api/cargos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cargo)))
                .andExpect(status().isCreated());

        // Validate the Cargo in the database
        List<Cargo> cargos = cargoRepository.findAll();
        assertThat(cargos).hasSize(databaseSizeBeforeCreate + 1);
        Cargo testCargo = cargos.get(cargos.size() - 1);
        assertThat(testCargo.getNombre()).isEqualTo(DEFAULT_NOMBRE);

        // Validate the Cargo in ElasticSearch
        Cargo cargoEs = cargoSearchRepository.findOne(testCargo.getId());
        assertThat(cargoEs).isEqualToComparingFieldByField(testCargo);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = cargoRepository.findAll().size();
        // set the field null
        cargo.setNombre(null);

        // Create the Cargo, which fails.

        restCargoMockMvc.perform(post("/api/cargos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cargo)))
                .andExpect(status().isBadRequest());

        List<Cargo> cargos = cargoRepository.findAll();
        assertThat(cargos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCargos() throws Exception {
        // Initialize the database
        cargoRepository.saveAndFlush(cargo);

        // Get all the cargos
        restCargoMockMvc.perform(get("/api/cargos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(cargo.getId().intValue())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())));
    }

    @Test
    @Transactional
    public void getCargo() throws Exception {
        // Initialize the database
        cargoRepository.saveAndFlush(cargo);

        // Get the cargo
        restCargoMockMvc.perform(get("/api/cargos/{id}", cargo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(cargo.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCargo() throws Exception {
        // Get the cargo
        restCargoMockMvc.perform(get("/api/cargos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCargo() throws Exception {
        // Initialize the database
        cargoRepository.saveAndFlush(cargo);
        cargoSearchRepository.save(cargo);
        int databaseSizeBeforeUpdate = cargoRepository.findAll().size();

        // Update the cargo
        Cargo updatedCargo = cargoRepository.findOne(cargo.getId());
        updatedCargo
                .nombre(UPDATED_NOMBRE);

        restCargoMockMvc.perform(put("/api/cargos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCargo)))
                .andExpect(status().isOk());

        // Validate the Cargo in the database
        List<Cargo> cargos = cargoRepository.findAll();
        assertThat(cargos).hasSize(databaseSizeBeforeUpdate);
        Cargo testCargo = cargos.get(cargos.size() - 1);
        assertThat(testCargo.getNombre()).isEqualTo(UPDATED_NOMBRE);

        // Validate the Cargo in ElasticSearch
        Cargo cargoEs = cargoSearchRepository.findOne(testCargo.getId());
        assertThat(cargoEs).isEqualToComparingFieldByField(testCargo);
    }

    @Test
    @Transactional
    public void deleteCargo() throws Exception {
        // Initialize the database
        cargoRepository.saveAndFlush(cargo);
        cargoSearchRepository.save(cargo);
        int databaseSizeBeforeDelete = cargoRepository.findAll().size();

        // Get the cargo
        restCargoMockMvc.perform(delete("/api/cargos/{id}", cargo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean cargoExistsInEs = cargoSearchRepository.exists(cargo.getId());
        assertThat(cargoExistsInEs).isFalse();

        // Validate the database is empty
        List<Cargo> cargos = cargoRepository.findAll();
        assertThat(cargos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCargo() throws Exception {
        // Initialize the database
        cargoRepository.saveAndFlush(cargo);
        cargoSearchRepository.save(cargo);

        // Search the cargo
        restCargoMockMvc.perform(get("/api/_search/cargos?query=id:" + cargo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cargo.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())));
    }
}
