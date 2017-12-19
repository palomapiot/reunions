package es.udc.reunions.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import es.udc.reunions.domain.Cargo;
import es.udc.reunions.domain.Grupo;
import es.udc.reunions.domain.Miembro;
import es.udc.reunions.domain.Organo;
import es.udc.reunions.domain.User;
import es.udc.reunions.repository.MiembroRepository;
import es.udc.reunions.repository.UserRepository;
import es.udc.reunions.repository.search.MiembroSearchRepository;

@RunWith(MockitoJUnitRunner.class)
public class MiembroServiceTest {

	@InjectMocks
	private MiembroService miembroService = Mockito.spy(new MiembroService());

	@Mock
	private MiembroRepository miembroRepositoryMock;

	@Mock
	private MiembroSearchRepository miembroSearchRepositoryMock;

	@Mock
	private UserRepository userRepositoryMock;

	public Cargo validCargo(Long id) {
		Cargo cargo = new Cargo();
		cargo.setId(id);
		cargo.setNombre("Test");

		return cargo;
	}

	public User validUser(Long id) {
		User user = new User();
		user.setId(id);
		user.setLogin("userLogin");
		user.setPassword("password");
		user.setDni("11111111A");
		user.setEmail("user@email.com");
		user.setActivated(true);

		return user;
	}

	public Grupo validGrupo(Long id) {
		Grupo grupo = new Grupo();
		grupo.setId(id);
		grupo.setNombre("Grupo" + id);

		return grupo;
	}

	public Organo validOrgano(Long id) {
		Organo organo = new Organo();
		organo.setId(id);
		organo.setGrupo(validGrupo(id));

		return organo;
	}

	public Miembro validMiembro(Long id) {
		Miembro miembro = new Miembro();
		miembro.setCargo(validCargo(id));
		LocalDate date = new GregorianCalendar(1996, Calendar.JUNE, 28).getTime().toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate();
		miembro.setFechaAlta(date);
		miembro.setId(id);
		miembro.setUser(validUser(id));
		miembro.setOrgano(validOrgano(id));

		return miembro;
	}

	@Test
	@Transactional
	public void saveTest() {
		// Arrange
		Miembro miembro = validMiembro(1L);
		Miembro expectedMiembro = validMiembro(1L);
		when(miembroRepositoryMock.save(miembro)).thenReturn(expectedMiembro);
		when(miembroSearchRepositoryMock.save(miembro)).thenReturn(expectedMiembro);

		// act
		Miembro result = miembroService.save(miembro);

		// assert
		assertThat(result).isEqualTo(expectedMiembro);
	}

	@Test
	@Transactional
	public void findAllTest() {
		// Arrange

		Miembro miembro1 = validMiembro(1L);
		Miembro miembro2 = validMiembro(2L);

		List<Miembro> expectedList = new ArrayList<Miembro>();
		expectedList.add(miembro1);
		expectedList.add(miembro2);

		Page<Miembro> expectedPage = new PageImpl<Miembro>(expectedList);

		when(miembroRepositoryMock.findAll(new PageRequest(0, 10))).thenReturn(expectedPage);

		// Act
		Page<Miembro> pageResult = miembroService.findAll(new PageRequest(0, 10));

		// Assert
		assertThat(pageResult.getContent()).isEqualTo(expectedList);
	}

	@Test
	@Transactional
	public void findByOrganoIdAndFechaBajaIsNullTest() {
		// Arrange

		Miembro miembro1 = validMiembro(1L);
		Miembro miembro2 = validMiembro(2L);
		miembro2.setOrgano(validOrgano(1L));
		List<Miembro> expectedList = new ArrayList<Miembro>();
		expectedList.add(miembro1);
		expectedList.add(miembro2);

		Organo organo1 = validOrgano(1L);

		when(miembroRepositoryMock.findByOrganoIdAndFechaBajaIsNull(1L)).thenReturn(expectedList);

		// Act
		List<Miembro> listResult = miembroService.findByOrganoIdAndFechaBajaIsNull(organo1.getId());

		// Assert
		assertThat(listResult).isEqualTo(expectedList);
	}

	@Test
	@Transactional
	public void findByOrganoIdAndUserIdAndFechaBajaIsNullTest() {
		// Arrange

		User user1 = validUser(1L);
		Miembro miembro1 = validMiembro(1L);
		Organo organo1 = validOrgano(1L);

		when(miembroRepositoryMock.findByOrganoIdAndUserIdAndFechaBajaIsNull(1L, 1L)).thenReturn(miembro1);

		// Act
		Miembro result = miembroService.findByOrganoIdAndUserIdAndFechaBajaIsNull(organo1.getId(), user1.getId());

		// Assert
		assertThat(result).isEqualTo(miembro1);
	}

	@Test
	@Transactional
	public void findByOrganoIdAndFechaBajaIsNotNullTest() {
		// Arrange

		Miembro miembro1 = validMiembro(1L);
		Miembro miembro2 = validMiembro(2L);
		miembro1.setFechaBaja(new GregorianCalendar(2016, Calendar.JUNE, 28).getTime().toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate());
		miembro2.setFechaBaja(new GregorianCalendar(2016, Calendar.JUNE, 28).getTime().toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate());
		miembro2.setOrgano(validOrgano(1L));

		Organo organo1 = validOrgano(1L);

		List<Miembro> expectedList = new ArrayList<Miembro>();
		expectedList.add(miembro1);
		expectedList.add(miembro2);

		when(miembroRepositoryMock.findByOrganoIdAndFechaBajaIsNotNull(1L)).thenReturn(expectedList);

		// Act
		List<Miembro> listResult = miembroService.findByOrganoIdAndFechaBajaIsNotNull(organo1.getId());

		// Assert
		assertThat(listResult).isEqualTo(expectedList);
	}

	@Test
	@Transactional(readOnly = true)
	public void findByUserLoginAndFechaBajaIsNullTest() {
		// Arrange
		Miembro miembro1 = validMiembro(1L);
		Miembro miembro2 = validMiembro(2L);
		User user1 = validUser(1L);
		miembro2.setUser(user1);

		List<Miembro> expectedList = new ArrayList<Miembro>();
		expectedList.add(miembro1);
		expectedList.add(miembro2);

		Optional<User> optionalUser = Optional.of(user1);

		when(userRepositoryMock.findOneByLogin("userLogin")).thenReturn(optionalUser);
		when(miembroRepositoryMock.findByUserIdAndFechaBajaIsNull(1L)).thenReturn(expectedList);

		// Act
		List<Miembro> listResult = miembroService.findByUserLoginAndFechaBajaIsNull("userLogin");

		// Assert
		assertThat(listResult).isEqualTo(expectedList);

	}

	@Test
	@Transactional(readOnly = true)
	public void findOneTest() {

		// Arrange
		Miembro miembro1 = validMiembro(1L);

		when(miembroRepositoryMock.findOne(1L)).thenReturn(miembro1);

		// Act
		Miembro result = miembroService.findOne(1L);

		// Assert
		assertThat(result).isEqualTo(miembro1);
	}

	// @Test
	// @Transactional(readOnly = true)
	// public void searchTest() {
	// // Arrange
	// Miembro miembro1 = validMiembro(1L);
	// Miembro miembro2 = validMiembro(2L);
	//
	// List<Miembro> expectedList = new ArrayList<Miembro>();
	// expectedList.add(miembro1);
	// expectedList.add(miembro2);
	//
	// Page<Miembro> expectedPage = new PageImpl<Miembro>(expectedList);
	//
	// when(miembroSearchRepositoryMock.search(queryStringQuery("query"), new
	// PageRequest(0, 10)))
	// .thenReturn(expectedPage);
	//
	// // Act
	// Page<Miembro> pageResult = miembroService.search("query", new PageRequest(0,
	// 10));
	//
	// // Assert
	// assertThat(pageResult).isEqualTo(expectedPage);
	// }

	@Test
	@Transactional
	public void deleteTest() {
		// Arrange
		Miembro miembro1 = validMiembro(1L);
		List<Miembro> list = new ArrayList<>();
		list.add(miembro1);

		doAnswer((Answer<?>) invocation -> {
			list.remove(miembro1);

			return null;
		}).when(miembroRepositoryMock).delete(miembro1.getId());

		Mockito.doNothing().when(miembroSearchRepositoryMock).delete(miembro1.getId());

		// Act
		miembroService.delete(miembro1.getId());
		list.remove(miembro1);

		// Assert
		assertThat(list.size()).isEqualTo(0);
	}
}
