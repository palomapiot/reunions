package es.udc.reunions.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

import es.udc.reunions.domain.Grupo;
import es.udc.reunions.domain.Miembro;
import es.udc.reunions.domain.Organo;
import es.udc.reunions.domain.Sesion;
import es.udc.reunions.repository.OrganoRepository;
import es.udc.reunions.repository.search.OrganoSearchRepository;

public class OrganoServiceTest {
	@Mock
	private OrganoRepository organoRepositoryMock;
	@Mock
	private OrganoSearchRepository organoSearchRepositoryMock;
	@InjectMocks
	private OrganoService organoService;

	@Before
	public void setUp() {
		organoRepositoryMock = mock(OrganoRepository.class);
		organoSearchRepositoryMock = mock(OrganoSearchRepository.class);
		organoService = new OrganoService(organoRepositoryMock, organoSearchRepositoryMock);
	}

	public Organo createOrgano(Long id, String nombre, String descripcion, Grupo grupo, Set<Miembro> miembros,
			Set<Sesion> sesions) {
		Organo organo = new Organo();
		organo.setId(id);
		organo.setNombre(nombre);
		organo.setDescripcion(descripcion);
		organo.setGrupo(grupo);
		organo.setMiembros(miembros);
		organo.setSesions(sesions);
		return organo;
	}

	@Test
	@Transactional
	public void saveTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", new Grupo(), new HashSet<>(), new HashSet<>());
		when(organoRepositoryMock.save(organo)).thenReturn(organo);
		when(organoSearchRepositoryMock.save(organo)).thenReturn(organo);
		assertEquals(organoService.save(organo), organo);
	}

	@Test
	@Transactional
	public void findAllTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", new Grupo(), new HashSet<>(), new HashSet<>());
		List<Organo> list = new ArrayList<>();
		list.add(organo);
		Organo organo2 = createOrgano((long) 2, "nombre2", "descripción2", new Grupo(), new HashSet<>(),
				new HashSet<>());
		list.add(organo2);
		when(organoRepositoryMock.findAll()).thenReturn(list);
		assertEquals(organoService.findAll(), list);
	}

	@Test
	@Transactional
	public void findOneTest() {
		Long id = (long) 1;
		Organo organo = createOrgano(id, "nombre", "descripción", new Grupo(), new HashSet<>(), new HashSet<>());
		when(organoRepositoryMock.findOne(id)).thenReturn(organo);
		assertEquals(organoService.findOne(id), organo);
	}

	@Test
	@Transactional
	public void deleteTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", new Grupo(), new HashSet<>(), new HashSet<>());
		List<Organo> list = new ArrayList<>();
		list.add(organo);
		Mockito.doNothing().when(organoRepositoryMock).delete(organo.getId());
		Mockito.doNothing().when(organoSearchRepositoryMock).delete(organo.getId());
		when(organoRepositoryMock.findAll()).thenReturn(list);
		organoService.delete(organo.getId());
		list.remove(organo);
		assertEquals(list, organoService.findAll());
	}

	/*
	 * @Test
	 * 
	 * @Transactional public void searchTest(){ Organo organo = createOrgano((long)
	 * 1, "nombre", "descripción", new Grupo(), new HashSet<>(), new HashSet<>());
	 * List<Organo> list = new ArrayList<>(); list.add(organo); String string =
	 * "descripción"; Page<Organo> page = new PageImpl<Organo>(list);
	 * when(organoSearchRepositoryMock.search(queryStringQuery(string), new
	 * PageRequest(0, 10))).thenReturn(page); Page<Organo> expectedPage =
	 * organoService.search(string, new PageRequest(0, 10));
	 * assertEquals(expectedPage, page); }
	 */

}
