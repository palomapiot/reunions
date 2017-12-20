package es.udc.reunions.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.reunions.ReunionsApp;
import es.udc.reunions.domain.Cargo;
import es.udc.reunions.domain.Grupo;
import es.udc.reunions.domain.Miembro;
import es.udc.reunions.domain.Organo;
import es.udc.reunions.domain.PersistentToken;
import es.udc.reunions.domain.User;
import es.udc.reunions.repository.CargoRepository;
import es.udc.reunions.repository.GrupoRepository;
import es.udc.reunions.repository.MiembroRepository;
import es.udc.reunions.repository.UserRepository;
import es.udc.reunions.repository.search.MiembroSearchRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReunionsApp.class)
@Transactional
public class MiembroServiceIntTest {
	@Inject
	private MiembroService miembroService;

	@Inject
	private OrganoService organoService;

	@Inject
	private PasswordEncoder passwordEncoder;

	@Inject
	private CargoRepository cargoRepository;

	@Inject
	private GrupoRepository grupoRepository;

	@Inject
	private MiembroRepository miembroRepository;

	@Inject
	private MiembroSearchRepository miembroSearchRepository;

	@Inject
	private UserRepository userRepository;

	private Cargo validCargo() {
		Cargo cargo = new Cargo();
		cargo.setNombre("Test");

		return cargo;
	}

	private User validUser() {
		User user = new User();
		user.setLogin("userLogin");
		user.setPassword(passwordEncoder.encode("password"));
		user.setDni("11111111A");
		user.setEmail("user@email.com");
		Set<PersistentToken> set = new HashSet<>();
		user.setPersistentTokens(set);
		user.setActivated(true);

		return user;
	}

	private Grupo validGrupo() {
		Grupo grupo = new Grupo();
		grupo.setNombre("Grupo");

		return grupo;
	}

	private Organo validOrgano(Grupo grupo) {
		Organo organo = new Organo();
		organo.setNombre("nombre");
		organo.setGrupo(grupo);

		return organo;
	}

	private Miembro validMiembro(Grupo grupo) {
		Miembro miembro = new Miembro();
		miembro.setCargo(validCargo());
		LocalDate date = new GregorianCalendar(1996, Calendar.JUNE, 28).getTime().toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate();
		miembro.setFechaAlta(date);
		miembro.setUser(validUser());
		miembro.setOrgano(validOrgano(grupo));

		return miembro;
	}

	@Test
	@Transactional
	public void saveTest() {
		// Arrange
		Grupo grupo = grupoRepository.save(validGrupo());
		Cargo cargo = cargoRepository.save(validCargo());
		Organo organo = organoService.save(validOrgano(grupo));
		User user = userRepository.save(validUser());
		Miembro miembro = validMiembro(grupo);
		miembro.setOrgano(organo);
		miembro.setCargo(cargo);
		miembro.setUser(user);
		miembro = miembroService.save(miembro);
		Miembro expectedMiembro = validMiembro(grupo);
		expectedMiembro.setId(miembro.getId());

		// act
		Miembro result = miembroService.save(miembro);

		// assert
		assertThat(result).isEqualTo(expectedMiembro);
	}

	@Test
	@Transactional
	public void findAllTest() {
		// Arrange

		Grupo grupo = grupoRepository.save(validGrupo());
		Cargo cargo = cargoRepository.save(validCargo());
		Organo organo = organoService.save(validOrgano(grupo));
		User user = userRepository.save(validUser());
		Miembro miembro1 = validMiembro(grupo);
		miembro1.setOrgano(organo);
		miembro1.setCargo(cargo);
		miembro1.setUser(user);
		miembro1 = miembroService.save(miembro1);
		Miembro expectedMiembro = validMiembro(grupo);
		expectedMiembro.setId(miembro1.getId());
		Miembro miembro2 = validMiembro(grupo);
		miembro2.setOrgano(organo);
		miembro2.setCargo(cargo);
		miembro2.setUser(user);
		miembro2 = miembroService.save(miembro2);

		List<Miembro> expectedList = new ArrayList<Miembro>();
		expectedList.add(miembro1);
		expectedList.add(miembro2);

		Page<Miembro> expectedPage = new PageImpl<Miembro>(expectedList);

		// Act
		Page<Miembro> pageResult = miembroService.findAll(new PageRequest(0, 10));

		// Assert
		assertThat(pageResult.getContent()).isEqualTo(expectedList);
	}

	@Test
	@Transactional
	public void findByOrganoIdAndFechaBajaIsNullTest() {
		// Arrange

		Grupo grupo = grupoRepository.save(validGrupo());
		Cargo cargo = cargoRepository.save(validCargo());
		Organo organo = organoService.save(validOrgano(grupo));
		User user = userRepository.save(validUser());
		Miembro miembro1 = validMiembro(grupo);
		miembro1.setOrgano(organo);
		miembro1.setCargo(cargo);
		miembro1.setUser(user);
		miembro1 = miembroService.save(miembro1);
		Miembro expectedMiembro = validMiembro(grupo);
		expectedMiembro.setId(miembro1.getId());
		Miembro miembro2 = validMiembro(grupo);
		miembro2.setOrgano(organo);
		miembro2.setCargo(cargo);
		miembro2.setUser(user);
		miembro2 = miembroService.save(miembro2);
		List<Miembro> expectedList = new ArrayList<Miembro>();
		expectedList.add(miembro1);
		expectedList.add(miembro2);

		// Act
		List<Miembro> listResult = miembroService.findByOrganoIdAndFechaBajaIsNull(organo.getId());

		// Assert
		assertThat(listResult).isEqualTo(expectedList);
	}

	@Test
	@Transactional
	public void findByOrganoIdAndUserIdAndFechaBajaIsNullTest() {
		// Arrange

		Grupo grupo = grupoRepository.save(validGrupo());
		Cargo cargo = cargoRepository.save(validCargo());
		Organo organo = organoService.save(validOrgano(grupo));
		User user = userRepository.save(validUser());
		Miembro miembro = validMiembro(grupo);
		miembro.setOrgano(organo);
		miembro.setCargo(cargo);
		miembro.setUser(user);
		miembro = miembroService.save(miembro);
		Miembro expectedMiembro = validMiembro(grupo);
		expectedMiembro.setId(miembro.getId());

		// Act
		Miembro result = miembroService.findByOrganoIdAndUserIdAndFechaBajaIsNull(organo.getId(), user.getId());

		// Assert
		assertThat(result).isEqualTo(miembro);
	}

	@Test
	@Transactional
	public void findByOrganoIdAndFechaBajaIsNotNullTest() {
		// Arrange

		Grupo grupo = grupoRepository.save(validGrupo());
		Cargo cargo = cargoRepository.save(validCargo());
		Organo organo = organoService.save(validOrgano(grupo));
		User user = userRepository.save(validUser());
		Miembro miembro1 = validMiembro(grupo);
		miembro1.setOrgano(organo);
		miembro1.setCargo(cargo);
		miembro1.setUser(user);
		miembro1.setFechaBaja(new GregorianCalendar(2016, Calendar.JUNE, 28).getTime().toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate());
		miembro1 = miembroService.save(miembro1);
		Miembro expectedMiembro = validMiembro(grupo);
		expectedMiembro.setId(miembro1.getId());
		Miembro miembro2 = validMiembro(grupo);
		miembro2.setOrgano(organo);
		miembro2.setCargo(cargo);
		miembro2.setUser(user);
		miembro2.setFechaBaja(new GregorianCalendar(2016, Calendar.JUNE, 28).getTime().toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate());
		miembro2 = miembroService.save(miembro2);

		List<Miembro> expectedList = new ArrayList<Miembro>();
		expectedList.add(miembro1);
		expectedList.add(miembro2);

		// Act
		List<Miembro> listResult = miembroService.findByOrganoIdAndFechaBajaIsNotNull(organo.getId());

		// Assert
		assertThat(listResult).isEqualTo(expectedList);
	}

	@Test
	@Transactional(readOnly = true)
	public void findByUserLoginAndFechaBajaIsNullTest() {
		// Arrange
		Grupo grupo = grupoRepository.save(validGrupo());
		Cargo cargo = cargoRepository.save(validCargo());
		Organo organo = organoService.save(validOrgano(grupo));
		User user = userRepository.save(validUser());
		Miembro miembro1 = validMiembro(grupo);
		miembro1.setOrgano(organo);
		miembro1.setCargo(cargo);
		miembro1.setUser(user);
		miembro1 = miembroService.save(miembro1);
		Miembro expectedMiembro = validMiembro(grupo);
		expectedMiembro.setId(miembro1.getId());
		Miembro miembro2 = validMiembro(grupo);
		miembro2.setOrgano(organo);
		miembro2.setCargo(cargo);
		miembro2.setUser(user);
		miembro2 = miembroService.save(miembro2);
		User user1 = validUser();
		miembro2.setUser(user1);

		List<Miembro> expectedList = new ArrayList<Miembro>();
		expectedList.add(miembro1);
		expectedList.add(miembro2);

		Optional<User> optionalUser = Optional.of(user1);

		// Act
		List<Miembro> listResult = miembroService.findByUserLoginAndFechaBajaIsNull(miembro1.getUser().getLogin());

		// Assert
		assertThat(listResult).isEqualTo(expectedList);

	}

	@Test
	@Transactional(readOnly = true)
	public void findOneTest() {

		// Arrange
		Grupo grupo = grupoRepository.save(validGrupo());
		Cargo cargo = cargoRepository.save(validCargo());
		Organo organo = organoService.save(validOrgano(grupo));
		User user = userRepository.save(validUser());
		Miembro miembro = validMiembro(grupo);
		miembro.setOrgano(organo);
		miembro.setCargo(cargo);
		miembro.setUser(user);
		miembro = miembroService.save(miembro);
		Miembro expectedMiembro = validMiembro(grupo);
		expectedMiembro.setId(miembro.getId());

		// Act
		Miembro result = miembroService.findOne(miembro.getId());

		// Assert
		assertThat(result).isEqualTo(miembro);
	}

	@Test
	@Transactional
	public void deleteTest() {
		// Arrange
		Grupo grupo = grupoRepository.save(validGrupo());
		Cargo cargo = cargoRepository.save(validCargo());
		Organo organo = organoService.save(validOrgano(grupo));
		User user = userRepository.save(validUser());
		Miembro miembro = validMiembro(grupo);
		miembro.setOrgano(organo);
		miembro.setCargo(cargo);
		miembro.setUser(user);
		miembro = miembroService.save(miembro);
		Miembro expectedMiembro = validMiembro(grupo);
		expectedMiembro.setId(miembro.getId());
		List<Miembro> list = new ArrayList<>();
		list.add(miembro);

		list.remove(miembro);

		// Act
		miembroService.delete(miembro.getId());
		list.remove(miembro);

		// Assert
		assertThat(list.size()).isEqualTo(0);
	}

}
