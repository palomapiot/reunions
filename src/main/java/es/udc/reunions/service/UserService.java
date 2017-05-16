package es.udc.reunions.service;

import es.udc.reunions.domain.Authority;
import es.udc.reunions.domain.Organo;
import es.udc.reunions.domain.Participante;
import es.udc.reunions.domain.User;
import es.udc.reunions.domain.enumeration.Asistencia;
import es.udc.reunions.repository.AuthorityRepository;
import es.udc.reunions.repository.ParticipanteRepository;
import es.udc.reunions.repository.PersistentTokenRepository;
import es.udc.reunions.repository.UserRepository;
import es.udc.reunions.repository.search.UserSearchRepository;
import es.udc.reunions.security.AuthoritiesConstants;
import es.udc.reunions.security.SecurityUtils;
import es.udc.reunions.service.dto.LineaResumen;
import es.udc.reunions.service.util.RandomUtil;
import es.udc.reunions.web.rest.vm.ManagedUserVM;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.awt.Color;
import java.io.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.inject.Inject;
import java.util.*;
import java.util.List;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserSearchRepository userSearchRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private ParticipanteRepository participanteRepository;

    /**
     *  Get resumen excel de usuario.
     *  @param login the login of the usuario
     *  @return the excel
     */
    @Transactional(readOnly = true)
    public XSSFWorkbook generateExcel(String login) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Resumen");
        sheet.setDefaultColumnWidth(20*256);

        int rowCount = 0;

        // Cabecera
        Row row = sheet.createRow(rowCount++);
        String[] cabecera = { "Dni", "Apelidos", "Nome", "Órganos, comisións en que participou",
            "Se na columna anterior marcou Outras, especifique o nome.", "Cursos académicos", "En calidade de:",
            "Nº de reunións que tiveron lugar", "Nº de reunións as que asistiu", "Nº de ausencias xustificadas" };
        int columnCount = 0;
        for (String c : cabecera) {
            Cell cell = row.createCell(columnCount++);
            cell.setCellValue(c);
        }
        XSSFCellStyle stylecabecera = workbook.createCellStyle();
        stylecabecera.setFillBackgroundColor(new XSSFColor(new java.awt.Color(51, 51, 153)));
        stylecabecera.setFillPattern(FillPatternType.BIG_SPOTS);
        stylecabecera.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Arial");
        font.setFontHeightInPoints((short)10);
        stylecabecera.setLocked(true);
        stylecabecera.setVerticalAlignment(VerticalAlignment.CENTER);
        stylecabecera.setWrapText(true);
        stylecabecera.setFont(font);
        row.setRowStyle(stylecabecera);

        HashMap resumen = new HashMap();
        Long id = userRepository.findOneByLogin(login).get().getId();
        String curso;
        Organo organo;
        Calendar fecha;
        for (Participante p : participanteRepository.findByUserId(id)) {
            fecha = GregorianCalendar.from(p.getSesion().getPrimeraConvocatoria());
            if (fecha.get(Calendar.MONTH) < Calendar.SEPTEMBER) {
                curso = Integer.toString(fecha.get(Calendar.YEAR) - 1)
                    + "/"
                    + Integer.toString(fecha.get(Calendar.YEAR));
            } else {
                curso = Integer.toString(fecha.get(Calendar.YEAR))
                    + "/"
                    + Integer.toString(fecha.get(Calendar.YEAR) + 1);
            }
            organo = p.getSesion().getOrgano();
            if (!resumen.containsKey(curso + organo.getNombre() + p.getCargo().getNombre())) {
                Row fila = sheet.createRow(rowCount++);
                fila.createCell(0).setCellValue(p.getUser().getDni());
                fila.createCell(1).setCellValue(p.getUser().getLastName());
                fila.createCell(2).setCellValue(p.getUser().getFirstName());
                fila.createCell(3).setCellValue(p.getSesion().getOrgano().getGrupo().getNombre());
                fila.createCell(4).setCellValue(p.getSesion().getOrgano().getNombre());
                fila.createCell(5).setCellValue(curso);
                fila.createCell(6).setCellValue(p.getCargo().getNombre());
                fila.createCell(7, CellType.NUMERIC).setCellValue(0);
                fila.createCell(8, CellType.NUMERIC).setCellValue(0);
                fila.createCell(9, CellType.NUMERIC).setCellValue(0);
                marcarAsistencia(fila, p.getAsistencia());
                resumen.put(curso + organo.getNombre() + p.getCargo().getNombre(), fila);
            } else {
                Row fila =
                    (Row) resumen.get(curso + organo.getNombre() + p.getCargo().getNombre());
                marcarAsistencia(fila, p.getAsistencia());
            }
        }

        sheet.createFreezePane(0,1);

        return workbook;
    }

    private void marcarAsistencia(Row fila, Asistencia asistencia) {
        if (asistencia != null) {
            fila.getCell(7).setCellValue(fila.getCell(7).getNumericCellValue() + 1);
            switch (asistencia) {
                case asiste:
                    fila.getCell(8).setCellValue(fila.getCell(8).getNumericCellValue() + 1);
                    break;
                case falta:
                    break;
                case disculpa:
                    fila.getCell(9).setCellValue(fila.getCell(9).getNumericCellValue() + 1);
                    break;
            }
        }
    }

    /**
     *  Get cuadro resumen de usuario.
     *  @param login the id of the usuario
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<LineaResumen> getResumenDePersona(String login) {
        HashMap resumen = new HashMap();
        Long id = userRepository.findOneByLogin(login).get().getId();
        String curso, key;
        Organo organo;
        Calendar fecha;
        for (Participante p : participanteRepository.findByUserId(id)) {
            fecha = GregorianCalendar.from(p.getSesion().getPrimeraConvocatoria());
            if (fecha.get(Calendar.MONTH) < Calendar.SEPTEMBER) {
                curso = Integer.toString(fecha.get(Calendar.YEAR) - 1)
                    + "/"
                    + Integer.toString(fecha.get(Calendar.YEAR));
            } else {
                curso = Integer.toString(fecha.get(Calendar.YEAR))
                    + "/"
                    + Integer.toString(fecha.get(Calendar.YEAR) + 1);
            }
            organo = p.getSesion().getOrgano();
            if (!resumen.containsKey(curso + organo.getNombre())) {
                LineaResumen lineaResumen = new LineaResumen(curso, organo);
                if (p.getAsistencia() != null) {
                    lineaResumen.add(p.getAsistencia());
                }
                resumen.put(curso + organo.getNombre(), lineaResumen);
            } else {

                if (p.getAsistencia() != null) {
                    LineaResumen lineaResumen =
                        (LineaResumen) resumen.get(curso + organo.getNombre());
                    lineaResumen.add(p.getAsistencia());
                }
            }
        }
        return new ArrayList<LineaResumen>(resumen.values());
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                userSearchRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
       log.debug("Reset user password for reset key {}", key);

       return userRepository.findOneByResetKey(key)
            .filter(user -> {
                ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
                return user.getResetDate().isAfter(oneDayAgo);
           })
           .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                userRepository.save(user);
                return user;
           });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmail(mail)
            .filter(User::getActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(ZonedDateTime.now());
                userRepository.save(user);
                return user;
            });
    }

    public User createUser(String login, String password, String firstName, String lastName, String email,
        String langKey) {

        User newUser = new User();
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        userSearchRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User createUser(ManagedUserVM managedUserVM) {
        User user = new User();
        user.setLogin(managedUserVM.getLogin());
        user.setFirstName(managedUserVM.getFirstName());
        user.setLastName(managedUserVM.getLastName());
        user.setEmail(managedUserVM.getEmail());
        if (managedUserVM.getLangKey() == null) {
            user.setLangKey("gl"); // default language
        } else {
            user.setLangKey(managedUserVM.getLangKey());
        }
        if (managedUserVM.getAuthorities() != null) {
            Set<Authority> authorities = new HashSet<>();
            managedUserVM.getAuthorities().stream().forEach(
                authority -> authorities.add(authorityRepository.findOne(authority))
            );
            user.setAuthorities(authorities);
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(ZonedDateTime.now());
        user.setActivated(true);
        userRepository.save(user);
        userSearchRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void updateUser(String firstName, String lastName, String email, String langKey) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            u.setLangKey(langKey);
            userRepository.save(u);
            userSearchRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    public void updateUser(Long id, String login, String firstName, String lastName, String email,
        boolean activated, String langKey, Set<String> authorities) {

        Optional.of(userRepository
            .findOne(id))
            .ifPresent(u -> {
                u.setLogin(login);
                u.setFirstName(firstName);
                u.setLastName(lastName);
                u.setEmail(email);
                u.setActivated(activated);
                u.setLangKey(langKey);
                Set<Authority> managedAuthorities = u.getAuthorities();
                managedAuthorities.clear();
                authorities.stream().forEach(
                    authority -> managedAuthorities.add(authorityRepository.findOne(authority))
                );
                log.debug("Changed Information for User: {}", u);
            });
    }

    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(u -> {
            userRepository.delete(u);
            userSearchRepository.delete(u);
            log.debug("Deleted User: {}", u);
        });
    }

    public void changePassword(String password) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            userRepository.save(u);
            log.debug("Changed password for User: {}", u);
        });
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneByLogin(login).map(u -> {
            u.getAuthorities().size();
            return u;
        });
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities(Long id) {
        User user = userRepository.findOne(id);
        user.getAuthorities().size(); // eagerly load the association
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        Optional<User> optionalUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        User user = null;
        if (optionalUser.isPresent()) {
          user = optionalUser.get();
            user.getAuthorities().size(); // eagerly load the association
         }
         return user;
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = LocalDate.now();
        persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).stream().forEach(token -> {
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        });
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        ZonedDateTime now = ZonedDateTime.now();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
            userSearchRepository.delete(user);
        }
    }
}
