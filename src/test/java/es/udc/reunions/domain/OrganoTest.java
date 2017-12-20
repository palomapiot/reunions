package es.udc.reunions.domain;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class OrganoTest {

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
	public void equalsTest() {
		Organo organo1 = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Organo organo2 = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		assertEquals(organo1.equals(organo2), true);
	}

	@Test
	public void equalsTest2() {
		Organo organo1 = null;
		Organo organo2 = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		assertEquals(organo2.equals(organo1), false);
	}

	@Test
	public void equalsTest3() {
		Organo organo1 = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Organo organo2 = createOrgano(null, "nombre", "descripción", null, null, null);
		assertEquals(organo2.equals(organo1), false);
	}

	@Test
	public void hashCodeTest() {
		Organo organo1 = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Organo organo2 = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		assertEquals(organo1.hashCode(), organo2.hashCode());
	}

	@Test
	public void toStringTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		String string = "Organo{" + "id=" + organo.getId() + ", nombre='" + organo.getNombre() + "'" + ", descripcion='"
				+ organo.getDescripcion() + "'" + '}';
		assertEquals(organo.toString(), string);
	}

	@Test
	public void getIdTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		assertEquals((long) 1, (long) organo.getId());
	}

	@Test
	public void getNombreTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		assertEquals("nombre", organo.getNombre());
	}

	@Test
	public void getDescTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		assertEquals("descripción", organo.getDescripcion());
	}

	@Test
	public void getGrupoTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		assertEquals(null, organo.getGrupo());
	}

	@Test
	public void getMiembrosTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Set<Miembro> set = null;
		assertEquals(set, organo.getMiembros());
	}

	@Test
	public void getSesionsTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		assertEquals(null, organo.getSesions());
	}

	@Test
	public void setIdTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		organo.setId((long) 2);
		assertEquals((long) 2, (long) organo.getId());
	}

	@Test
	public void setNombreTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		organo.setNombre("name");
		assertEquals("name", organo.getNombre());
	}

	@Test
	public void setDescripcionTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		organo.setDescripcion("desc");
		assertEquals("desc", organo.getDescripcion());
	}

	@Test
	public void setGrupoTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Grupo grupo = new Grupo();
		organo.setGrupo(grupo);
		assertEquals(grupo, organo.getGrupo());
	}

	@Test
	public void setMiembrosTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Set<Miembro> set = new HashSet<>();
		organo.setMiembros(set);
		assertEquals(set, organo.getMiembros());
	}

	@Test
	public void setSesionsTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Set<Sesion> set = new HashSet<>();
		organo.setSesions(set);
		assertEquals(set, organo.getSesions());
	}

	@Test
	public void addMiembroTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Set<Miembro> set = new HashSet<>();
		organo.setMiembros(set);
		Miembro miembro = new Miembro();
		set.add(miembro);
		organo.addMiembro(miembro);
		assertEquals(set, organo.getMiembros());
	}

	@Test
	public void removeMiembroTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Set<Miembro> set = new HashSet<>();
		Miembro miembro = new Miembro();
		set.add(miembro);
		organo.setMiembros(set);
		organo.removeMiembro(miembro);
		set.remove(miembro);
		assertEquals(set, organo.getMiembros());
	}

	@Test
	public void addSesionTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Set<Sesion> set = new HashSet<>();
		organo.setSesions(set);
		Sesion sesion = new Sesion();
		set.add(sesion);
		organo.addSesion(sesion);
		assertEquals(set, organo.getSesions());
	}

	@Test
	public void removeSesionTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Set<Sesion> set = new HashSet<>();
		Sesion sesion = new Sesion();
		set.add(sesion);
		organo.setSesions(set);
		organo.removeSesion(sesion);
		set.remove(sesion);
		assertEquals(set, organo.getSesions());
	}

	@Test
	public void nombreTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		organo = organo.nombre("name");
		assertEquals("name", organo.getNombre());
	}

	@Test
	public void descripcionTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		organo = organo.descripcion("desc");
		assertEquals("desc", organo.getDescripcion());
	}

	@Test
	public void grupoTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Grupo grupo = new Grupo();
		organo = organo.grupo(grupo);
		assertEquals(grupo, organo.getGrupo());
	}

	@Test
	public void miembrosTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Set<Miembro> set = new HashSet<>();
		organo.setMiembros(set);
		set.add(new Miembro());
		organo = organo.miembros(set);
		assertEquals(set, organo.getMiembros());
	}

	@Test
	public void sesionsTest() {
		Organo organo = createOrgano((long) 1, "nombre", "descripción", null, null, null);
		Set<Sesion> set = new HashSet<>();
		organo.setSesions(set);
		set.add(new Sesion());
		organo = organo.sesions(set);
		assertEquals(set, organo.getSesions());
	}

}
