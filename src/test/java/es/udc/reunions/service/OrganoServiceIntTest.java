package es.udc.reunions.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.reunions.ReunionsApp;
import es.udc.reunions.domain.Grupo;
import es.udc.reunions.domain.Miembro;
import es.udc.reunions.domain.Organo;
import es.udc.reunions.domain.Sesion;
import es.udc.reunions.repository.GrupoRepository;
import es.udc.reunions.repository.OrganoRepository;
import es.udc.reunions.repository.search.OrganoSearchRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReunionsApp.class)
@Transactional
public class OrganoServiceIntTest {

	@Inject
	private OrganoService organoService;

	@Inject
	private GrupoRepository grupoRepository;

	@Inject
	private OrganoRepository organoRepository;

	@Inject
	private OrganoSearchRepository organoSearchRepository;

	public Organo createOrgano(String nombre, String descripcion, Grupo grupo, Set<Miembro> miembros,
			Set<Sesion> sesions) {
		Organo organo = new Organo();
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
		Grupo grupo = new Grupo();
		grupo.setNombre("grupo");
		grupo = grupoRepository.save(grupo);
		Organo organo = createOrgano("nombre", "descripción", grupo, new HashSet<>(), new HashSet<>());
		assertEquals(organoService.save(organo), organo);
	}

	@Test
	@Transactional
	public void findAllTest() {
		Grupo grupo = new Grupo();
		grupo.setNombre("grupo");
		grupo = grupoRepository.save(grupo);
		Organo organo = createOrgano("nombre", "descripción", grupo, new HashSet<>(), new HashSet<>());
		Organo organo2 = createOrgano("nombre2", "descripción2", grupo, new HashSet<>(), new HashSet<>());
		organo = organoService.save(organo);
		organo2 = organoService.save(organo2);
		List<Organo> list = new ArrayList<>();
		list.add(organo);
		list.add(organo2);
		assertEquals(organoService.findAll(), list);
	}

	@Test
	@Transactional
	public void findOneTest() {
		Grupo grupo = new Grupo();
		grupo.setNombre("grupo");
		grupo = grupoRepository.save(grupo);
		Organo organo = createOrgano("nombre", "descripción", grupo, new HashSet<>(), new HashSet<>());
		organo = organoService.save(organo);
		assertEquals(organoService.findOne(organo.getId()), organo);
	}

	@Test
	@Transactional
	public void deleteTest() {
		Grupo grupo = new Grupo();
		grupo.setNombre("grupo");
		grupo = grupoRepository.save(grupo);
		Organo organo = createOrgano("nombre", "descripción", grupo, new HashSet<>(), new HashSet<>());
		organo = organoService.save(organo);
		List<Organo> list = new ArrayList<>();
		list.add(organo);
		organoService.delete(organo.getId());
		list.remove(organo);
		assertEquals(list, organoService.findAll());
	}

	/*
	 * @Test
	 * 
	 * @Transactional public void searchTest(){ Grupo grupo = new Grupo();
	 * grupo.setNombre("grupo"); grupo = grupoRepository.save(grupo); Organo organo
	 * = createOrgano("nombre", "descripción", grupo, new HashSet<>(), new
	 * HashSet<>()); organo = organoService.save(organo); List<Organo> list = new
	 * ArrayList<>(); List<Organo> list2 = new ArrayList<>(); list.add(organo);
	 * String string = "nombre"; Page<Organo> page = new PageImpl<Organo>(list);
	 * Page<Organo> expectedPage = new PageImpl<Organo>(list2); expectedPage =
	 * organoService.search(string, new PageRequest(0, 10));
	 * assertEquals(expectedPage, page); }
	 */

}
