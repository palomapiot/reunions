package es.udc.reunions.web.rest;

import es.udc.reunions.ReunionsApp;

import es.udc.reunions.domain.Documento;
import es.udc.reunions.domain.Sesion;
import es.udc.reunions.repository.DocumentoRepository;
import es.udc.reunions.repository.search.DocumentoSearchRepository;

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
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DocumentoResource REST controller.
 *
 * @see DocumentoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReunionsApp.class)
public class DocumentoResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAAA";
    private static final String UPDATED_NOMBRE = "BBBBB";

    private static final byte[] DEFAULT_ARCHIVO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_ARCHIVO = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_ARCHIVO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_ARCHIVO_CONTENT_TYPE = "image/png";

    @Inject
    private DocumentoRepository documentoRepository;

    @Inject
    private DocumentoSearchRepository documentoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restDocumentoMockMvc;

    private Documento documento;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DocumentoResource documentoResource = new DocumentoResource();
        ReflectionTestUtils.setField(documentoResource, "documentoSearchRepository", documentoSearchRepository);
        ReflectionTestUtils.setField(documentoResource, "documentoRepository", documentoRepository);
        this.restDocumentoMockMvc = MockMvcBuilders.standaloneSetup(documentoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Documento createEntity(EntityManager em) {
        Documento documento = new Documento()
                .nombre(DEFAULT_NOMBRE)
                .archivo(DEFAULT_ARCHIVO)
                .archivoContentType(DEFAULT_ARCHIVO_CONTENT_TYPE);
        // Add required entity
        Sesion sesion = SesionResourceIntTest.createEntity(em);
        em.persist(sesion);
        em.flush();
        documento.setSesion(sesion);
        return documento;
    }

    @Before
    public void initTest() {
        documentoSearchRepository.deleteAll();
        documento = createEntity(em);
    }

    @Test
    @Transactional
    public void createDocumento() throws Exception {
        int databaseSizeBeforeCreate = documentoRepository.findAll().size();

        // Create the Documento

        restDocumentoMockMvc.perform(post("/api/documentos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(documento)))
                .andExpect(status().isCreated());

        // Validate the Documento in the database
        List<Documento> documentos = documentoRepository.findAll();
        assertThat(documentos).hasSize(databaseSizeBeforeCreate + 1);
        Documento testDocumento = documentos.get(documentos.size() - 1);
        assertThat(testDocumento.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testDocumento.getArchivo()).isEqualTo(DEFAULT_ARCHIVO);
        assertThat(testDocumento.getArchivoContentType()).isEqualTo(DEFAULT_ARCHIVO_CONTENT_TYPE);

        // Validate the Documento in ElasticSearch
        Documento documentoEs = documentoSearchRepository.findOne(testDocumento.getId());
        assertThat(documentoEs).isEqualToComparingFieldByField(testDocumento);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = documentoRepository.findAll().size();
        // set the field null
        documento.setNombre(null);

        // Create the Documento, which fails.

        restDocumentoMockMvc.perform(post("/api/documentos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(documento)))
                .andExpect(status().isBadRequest());

        List<Documento> documentos = documentoRepository.findAll();
        assertThat(documentos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkArchivoIsRequired() throws Exception {
        int databaseSizeBeforeTest = documentoRepository.findAll().size();
        // set the field null
        documento.setArchivo(null);

        // Create the Documento, which fails.

        restDocumentoMockMvc.perform(post("/api/documentos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(documento)))
                .andExpect(status().isBadRequest());

        List<Documento> documentos = documentoRepository.findAll();
        assertThat(documentos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDocumentos() throws Exception {
        // Initialize the database
        documentoRepository.saveAndFlush(documento);

        // Get all the documentos
        restDocumentoMockMvc.perform(get("/api/documentos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(documento.getId().intValue())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
                .andExpect(jsonPath("$.[*].archivoContentType").value(hasItem(DEFAULT_ARCHIVO_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].archivo").value(hasItem(Base64Utils.encodeToString(DEFAULT_ARCHIVO))));
    }

    @Test
    @Transactional
    public void getDocumento() throws Exception {
        // Initialize the database
        documentoRepository.saveAndFlush(documento);

        // Get the documento
        restDocumentoMockMvc.perform(get("/api/documentos/{id}", documento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(documento.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()))
            .andExpect(jsonPath("$.archivoContentType").value(DEFAULT_ARCHIVO_CONTENT_TYPE))
            .andExpect(jsonPath("$.archivo").value(Base64Utils.encodeToString(DEFAULT_ARCHIVO)));
    }

    @Test
    @Transactional
    public void getNonExistingDocumento() throws Exception {
        // Get the documento
        restDocumentoMockMvc.perform(get("/api/documentos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDocumento() throws Exception {
        // Initialize the database
        documentoRepository.saveAndFlush(documento);
        documentoSearchRepository.save(documento);
        int databaseSizeBeforeUpdate = documentoRepository.findAll().size();

        // Update the documento
        Documento updatedDocumento = documentoRepository.findOne(documento.getId());
        updatedDocumento
                .nombre(UPDATED_NOMBRE)
                .archivo(UPDATED_ARCHIVO)
                .archivoContentType(UPDATED_ARCHIVO_CONTENT_TYPE);

        restDocumentoMockMvc.perform(put("/api/documentos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDocumento)))
                .andExpect(status().isOk());

        // Validate the Documento in the database
        List<Documento> documentos = documentoRepository.findAll();
        assertThat(documentos).hasSize(databaseSizeBeforeUpdate);
        Documento testDocumento = documentos.get(documentos.size() - 1);
        assertThat(testDocumento.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testDocumento.getArchivo()).isEqualTo(UPDATED_ARCHIVO);
        assertThat(testDocumento.getArchivoContentType()).isEqualTo(UPDATED_ARCHIVO_CONTENT_TYPE);

        // Validate the Documento in ElasticSearch
        Documento documentoEs = documentoSearchRepository.findOne(testDocumento.getId());
        assertThat(documentoEs).isEqualToComparingFieldByField(testDocumento);
    }

    @Test
    @Transactional
    public void deleteDocumento() throws Exception {
        // Initialize the database
        documentoRepository.saveAndFlush(documento);
        documentoSearchRepository.save(documento);
        int databaseSizeBeforeDelete = documentoRepository.findAll().size();

        // Get the documento
        restDocumentoMockMvc.perform(delete("/api/documentos/{id}", documento.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean documentoExistsInEs = documentoSearchRepository.exists(documento.getId());
        assertThat(documentoExistsInEs).isFalse();

        // Validate the database is empty
        List<Documento> documentos = documentoRepository.findAll();
        assertThat(documentos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDocumento() throws Exception {
        // Initialize the database
        documentoRepository.saveAndFlush(documento);
        documentoSearchRepository.save(documento);

        // Search the documento
        restDocumentoMockMvc.perform(get("/api/_search/documentos?query=id:" + documento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(documento.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
            .andExpect(jsonPath("$.[*].archivoContentType").value(hasItem(DEFAULT_ARCHIVO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].archivo").value(hasItem(Base64Utils.encodeToString(DEFAULT_ARCHIVO))));
    }
}
