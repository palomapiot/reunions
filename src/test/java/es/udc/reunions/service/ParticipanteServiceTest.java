package es.udc.reunions.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import es.udc.reunions.domain.Organo;
import es.udc.reunions.domain.Participante;
import es.udc.reunions.domain.Sesion;
import es.udc.reunions.domain.User;
import es.udc.reunions.repository.ParticipanteRepository;
import es.udc.reunions.repository.UserRepository;
import es.udc.reunions.repository.search.ParticipanteSearchRepository;

@RunWith(MockitoJUnitRunner.class)
public class ParticipanteServiceTest {

	@InjectMocks
	private ParticipanteService participanteService = Mockito.spy(new ParticipanteService());

	@Mock
	private ParticipanteRepository participanteRepositoryMock;

	@Mock
	private ParticipanteSearchRepository participanteSearchRepositoryMock;

	@Mock
	private UserRepository userRepositoryMock;

	private Cargo validCargo(Long id) {
		Cargo cargo = new Cargo();
		cargo.setId(id);
		cargo.setNombre("Test");

		return cargo;
	}

	private User validUser(Long id) {
		User user = new User();
		user.setId(id);
		user.setLogin("userLogin");
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

	private Sesion validSesion(Long id) {
		Sesion sesion = new Sesion();
		sesion.setId(id);
		sesion.setNumero(id);
		sesion.setOrgano(validOrgano(id));
		ZoneId zoneId = ZoneId.of("UTC+1");
		ZonedDateTime zonedDateTime = ZonedDateTime.of(2018, 11, 30, 23, 45, 59, 1234, zoneId);
		sesion.setPrimeraConvocatoria(zonedDateTime);
		sesion.setLugar("Madrid");

		return sesion;
	}

	private Participante validParticipante(Long id) {
		Participante participante = new Participante();
		participante.setId(id);
		participante.setUser(validUser(id));
		participante.setCargo(validCargo(id));
		participante.setSesion(validSesion(id));

		return participante;
	}

	@Test
	@Transactional
	public void saveTest() {
		// Arrange
		Participante participante = validParticipante(1L);
		Participante expectedParticipante = validParticipante(1L);
		when(participanteRepositoryMock.save(participante)).thenReturn(expectedParticipante);
		when(participanteSearchRepositoryMock.save(participante)).thenReturn(expectedParticipante);

		// act
		Participante result = participanteService.save(participante);

		// assert
		assertThat(result).isEqualTo(expectedParticipante);
	}

	@Test
	@Transactional
	public void saveListTest() {
		// Arrange
		Participante participante1 = validParticipante(1L);
		Participante participante2 = validParticipante(2L);
		List<Participante> list = new ArrayList<Participante>();
		list.add(participante1);
		list.add(participante2);
		List<Participante> expectedList = new ArrayList<Participante>();
		expectedList.add(participante1);
		expectedList.add(participante2);
		when(participanteRepositoryMock.save(list)).thenReturn(expectedList);
		when(participanteSearchRepositoryMock.save(list)).thenReturn(expectedList);

		// Act
		List<Participante> result = participanteService.save(list);

		// Assert
		assertThat(result).isEqualTo(expectedList);
	}

	@Test
	@Transactional
	public void findAllTest() {
		// Arrange

		Participante participante1 = validParticipante(1L);
		Participante participante2 = validParticipante(2L);

		List<Participante> expectedList = new ArrayList<Participante>();
		expectedList.add(participante1);
		expectedList.add(participante2);

		Page<Participante> expectedPage = new PageImpl<Participante>(expectedList);

		when(participanteRepositoryMock.findAll(new PageRequest(0, 10))).thenReturn(expectedPage);

		// Act
		Page<Participante> pageResult = participanteService.findAll(new PageRequest(0, 10));

		// Assert
		assertThat(pageResult.getContent()).isEqualTo(expectedList);
	}

	@Test
	@Transactional
	public void findOneTest() {
		// Arrange

		Participante participante1 = validParticipante(1L);
		Participante expectedParticipante = validParticipante(1L);

		when(participanteRepositoryMock.findOne(1L)).thenReturn(expectedParticipante);

		// Act
		Participante result = participanteService.findOne(participante1.getId());

		// Assert
		assertThat(result).isEqualTo(expectedParticipante);
	}

	@Test
	@Transactional
	public void findBySesionIdTest() {
		// Arrange

		Participante participante1 = validParticipante(1L);
		Participante participante2 = validParticipante(2L);
		participante2.setSesion(validSesion(1L));

		List<Participante> expectedList = new ArrayList<Participante>();
		expectedList.add(participante1);
		expectedList.add(participante2);

		when(participanteRepositoryMock.findBySesionId(1L)).thenReturn(expectedList);

		// Act
		List<Participante> listResult = participanteService.findBySesionId(validSesion(1L).getId());

		// Assert
		assertThat(listResult).isEqualTo(expectedList);
	}

	@Test
	@Transactional
	public void deleteTest() {
		// Arrange
		Participante participante1 = validParticipante(1L);
		List<Participante> list = new ArrayList<>();
		list.add(participante1);

		doAnswer((Answer<?>) invocation -> {
			list.remove(participante1);

			return null;
		}).when(participanteRepositoryMock).delete(participante1.getId());

		Mockito.doNothing().when(participanteSearchRepositoryMock).delete(participante1.getId());

		// Act
		participanteService.delete(participante1.getId());
		list.remove(participante1);

		// Assert
		assertThat(list.size()).isEqualTo(0);
	}

	@Test
	@Transactional(readOnly = true)
	public void findByUserLoginTest() {
		// Arrange

		Participante participante1 = validParticipante(1L);
		Participante participante2 = validParticipante(2L);
		User user1 = validUser(1L);
		participante2.setUser(user1);

		List<Participante> expectedList = new ArrayList<Participante>();
		expectedList.add(participante1);
		expectedList.add(participante2);

		Optional<User> optionalUser = Optional.of(user1);

		when(userRepositoryMock.findOneByLogin("userLogin")).thenReturn(optionalUser);
		when(participanteRepositoryMock.findByUserId(1L)).thenReturn(expectedList);

		// Act
		List<Participante> listResult = participanteService.findByUserLogin("userLogin");

		// Assert
		assertThat(listResult).isEqualTo(expectedList);
	}

	@Test
	@Transactional(readOnly = true)
	public void findByUserIsNotCurrentUserTest() {
		// Arrange

		Participante participante1 = validParticipante(1L);
		Participante participante2 = validParticipante(2L);
		User user1 = validUser(1L);
		participante2.setUser(user1);

		List<Participante> expectedList = new ArrayList<Participante>();
		expectedList.add(participante1);
		expectedList.add(participante2);

		when(participanteRepositoryMock.findByUserIsNotCurrentUser()).thenReturn(expectedList);

		// Act

		List<Participante> listResult = participanteService.findByUserIsNotCurrentUser();

		// Assert
		assertThat(listResult).isEqualTo(expectedList);

	}

	@Test
	@Transactional(readOnly = true)
	public void findByUserIsCurrentUserTest() {
		// Arrange

		Participante participante1 = validParticipante(1L);
		Participante participante2 = validParticipante(2L);
		User user1 = validUser(1L);
		participante2.setUser(user1);

		List<Participante> expectedList = new ArrayList<Participante>();
		expectedList.add(participante1);
		expectedList.add(participante2);

		when(participanteRepositoryMock.findByUserIsCurrentUser()).thenReturn(expectedList);

		// Act

		List<Participante> listResult = participanteService.findByUserIsCurrentUser();

		// Assert
		assertThat(listResult).isEqualTo(expectedList);

	}
}
