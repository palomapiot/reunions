package es.udc.reunions.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

	private int x = 25; // Lonitud cadenas aleatorias

	private Cargo validCargo(Long id) {
		Cargo cargo = new Cargo();
		cargo.setId(id);
		cargo.setNombre("Test");

		return cargo;
	}

	private User validUser(Long id) {
		int randomNum = ThreadLocalRandom.current().nextInt(1, x + 1);
		byte[] array = new byte[randomNum]; // length is bounded by randomNum
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		User user = new User();
		user.setId(id);
		user.setLogin(generatedString);
		user.setPassword("password");
		user.setDni("11111111A");
		user.setEmail("user@email.com");
		user.setActivated(true);

		return user;
	}

	private Grupo validGrupo(Long id) {
		Grupo grupo = new Grupo();
		grupo.setId(id);
		grupo.setNombre("Grupo" + id);

		return grupo;
	}

	private Organo validOrgano(Long id) {
		Organo organo = new Organo();
		organo.setId(id);
		organo.setGrupo(validGrupo(id));

		return organo;
	}

	private Miembro validMiembro(Long id) {
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
		Random gen = new Random();
		long randomLong = gen.nextLong();
		long randomLong2 = gen.nextLong() + 1L;

		Miembro miembro1 = validMiembro(randomLong);
		Miembro miembro2 = validMiembro(randomLong2);

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
		Random gen = new Random();
		long randomLong = gen.nextLong();
		long randomLong2 = gen.nextLong() + 1L;

		Miembro miembro1 = validMiembro(randomLong);
		Miembro miembro2 = validMiembro(randomLong2);
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
		Random gen = new Random();
		long randomLong = gen.nextLong();
		long randomLong2 = gen.nextLong() + 1L;

		User user1 = validUser(randomLong);
		Miembro miembro1 = validMiembro(1L);
		Organo organo1 = validOrgano(randomLong2);

		when(miembroRepositoryMock.findByOrganoIdAndUserIdAndFechaBajaIsNull(randomLong2, randomLong))
				.thenReturn(miembro1);

		// Act
		Miembro result = miembroService.findByOrganoIdAndUserIdAndFechaBajaIsNull(organo1.getId(), user1.getId());

		// Assert
		assertThat(result).isEqualTo(miembro1);
	}

	@Test
	@Transactional
	public void findByOrganoIdAndFechaBajaIsNotNullTest() {
		// Arrange
		Random gen = new Random();
		long randomLong = gen.nextLong();
		long randomLong2 = gen.nextLong() + 1L;

		Miembro miembro1 = validMiembro(randomLong);
		Miembro miembro2 = validMiembro(randomLong2);
		miembro1.setFechaBaja(new GregorianCalendar(2016, Calendar.JUNE, 28).getTime().toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate());
		miembro2.setFechaBaja(new GregorianCalendar(2016, Calendar.JUNE, 28).getTime().toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate());

		Random genOrgano = new Random();
		long randomLongOrgano = genOrgano.nextLong();

		miembro2.setOrgano(validOrgano(randomLongOrgano));

		Organo organo1 = validOrgano(randomLongOrgano);

		List<Miembro> expectedList = new ArrayList<Miembro>();
		expectedList.add(miembro1);
		expectedList.add(miembro2);

		when(miembroRepositoryMock.findByOrganoIdAndFechaBajaIsNotNull(randomLongOrgano)).thenReturn(expectedList);

		// Act
		List<Miembro> listResult = miembroService.findByOrganoIdAndFechaBajaIsNotNull(organo1.getId());

		// Assert
		assertThat(listResult).isEqualTo(expectedList);
	}

	@Test
	@Transactional(readOnly = true)
	public void findByUserLoginAndFechaBajaIsNullTest() {
		// Arrange
		Random gen = new Random();
		long randomLong = gen.nextLong();
		long randomLong2 = gen.nextLong() + 1L;

		Miembro miembro1 = validMiembro(randomLong);
		Miembro miembro2 = validMiembro(randomLong2);

		Random genUser = new Random();
		long randomLongUser = genUser.nextLong();

		User user1 = validUser(randomLongUser);
		miembro2.setUser(user1);

		List<Miembro> expectedList = new ArrayList<Miembro>();
		expectedList.add(miembro1);
		expectedList.add(miembro2);

		Optional<User> optionalUser = Optional.of(user1);

		when(userRepositoryMock.findOneByLogin("userLogin")).thenReturn(optionalUser);
		when(miembroRepositoryMock.findByUserIdAndFechaBajaIsNull(randomLongUser)).thenReturn(expectedList);

		// Act
		List<Miembro> listResult = miembroService.findByUserLoginAndFechaBajaIsNull("userLogin");

		// Assert
		assertThat(listResult).isEqualTo(expectedList);

	}

	@Test
	@Transactional(readOnly = true)
	public void findOneTest() {
		// Arrange
		Random gen = new Random();
		long randomLong = gen.nextLong();
		Miembro miembro1 = validMiembro(randomLong);

		when(miembroRepositoryMock.findOne(randomLong)).thenReturn(miembro1);

		// Act
		Miembro result = miembroService.findOne(randomLong);

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
	// PageRequest request = new PageRequest(0, 10);
	// when(miembroSearchRepositoryMock.search(eq((QueryBuilder) any(Object.class)),
	// request))
	// .thenReturn(expectedPage);
	//
	// // Act
	// Page<Miembro> pageResult = miembroService.search("query", request);
	//
	// // Assert
	// assertThat(pageResult).isEqualTo(expectedPage);
	// }

	@Test
	@Transactional
	public void deleteTest() {
		// Arrange
		Random gen = new Random();
		long randomLong = gen.nextLong();
		Miembro miembro1 = validMiembro(randomLong);
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
