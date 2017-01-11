package es.udc.reunions.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.udc.reunions.domain.Miembro;
import es.udc.reunions.domain.Participante;
import es.udc.reunions.domain.Sesion;
import es.udc.reunions.security.AuthoritiesConstants;
import es.udc.reunions.security.SecurityUtils;
import es.udc.reunions.service.*;
import es.udc.reunions.web.rest.util.HeaderUtil;
import es.udc.reunions.web.rest.util.PaginationUtil;
import org.apache.poi.util.TempFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.web.header.Header;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Sesion.
 */
@RestController
@RequestMapping("/api")
public class SesionResource {

    private final Logger log = LoggerFactory.getLogger(SesionResource.class);

    @Inject
    private SesionService sesionService;

    @Inject
    private ParticipanteService participanteService;

    @Inject
    private MiembroService miembroService;

    @Inject
    private MailService mailService;

    @Inject
    private UserService userService;

    /**
     * POST  /sesions : Create a new sesion.
     *
     * @param sesion the sesion to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sesion, or with status 400 (Bad Request) if the sesion has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/sesions")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<Sesion> createSesion(@Valid @RequestBody Sesion sesion) throws URISyntaxException {
        log.debug("REST request to save Sesion : {}", sesion);
        if (sesion.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sesion", "idexists", "A new sesion cannot already have an ID")).body(null);
        }
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            Miembro miembro = miembroService.findByOrganoIdAndUserIdAndFechaBajaIsNull(sesion.getOrgano().getId(), userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).get().getId());
            if (miembro == null || miembro.getCargo().getId() > 2)
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sesion", "forbidden", "You are not allowed to create sessions")).body(null);
        }
        Sesion result = sesionService.save(sesion);
        for (Miembro m : miembroService.findByOrganoIdAndFechaBajaIsNull(sesion.getOrgano().getId())) {
            Participante p = new Participante();
            p.setCargo(m.getCargo());
            p.setSesion(result);
            p.setUser(m.getUser());
            participanteService.save(p);
        }
        return ResponseEntity.created(new URI("/api/sesions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("sesion", result.getId().toString()))
            .body(result);
    }

    /**
     * POST   /sesions/notificar : Send an e-mail to participantes of sesion
     *
     * @param sesion the sesion whose participantes to notify
     * @param request the HTTP request
     * @return the ResponseEntity with status 200 (OK) if the e-mail was sent, or status 400 (Bad Request) if the e-mail address is not registered
     */
    @PostMapping(path = "/sesions/notificar",
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<?> notificar(@Valid @RequestBody Sesion sesion, HttpServletRequest request) {
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            Miembro miembro = miembroService.findByOrganoIdAndUserIdAndFechaBajaIsNull(sesion.getOrgano().getId(), userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).get().getId());
            if (miembro == null || miembro.getCargo().getId() > 2)
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sesion", "forbidden", "You are not allowed to notify this session")).body(null);
        }
        sesionService.notificar(sesion, request);
//        return userService.requestPasswordReset(mail)
//            .map(user -> {
//                String baseUrl = request.getScheme() +
//                    "://" +
//                    request.getServerName() +
//                    ":" +
//                    request.getServerPort() +
//                    request.getContextPath();
//                mailService.sendPasswordResetMail(user, baseUrl);
//                return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
//            }).orElse(new ResponseEntity<>("e-mail address not registered", HttpStatus.BAD_REQUEST));
        return new ResponseEntity<>("notification sent", HttpStatus.OK);
    }

    /**
     * PUT   /sesions/marcarAsistencia : Updates asistencia to sesion
     *
     * @param participantes whose participantes to notify
     * @return the ResponseEntity with status 200 (OK) if the e-mail was sent, or status 400 (Bad Request) if the e-mail address is not registered
     */
    @PutMapping(path = "/sesions/marcarAsistencia",
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<?> marcarAsistencia(@Valid @RequestBody List<Participante> participantes) {
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN) && participantes.size() > 0) {
            Miembro miembro = miembroService.findByOrganoIdAndUserIdAndFechaBajaIsNull(participantes.get(0).getSesion().getOrgano().getId(), userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).get().getId());
            if (miembro == null || miembro.getCargo().getId() > 2)
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sesion", "forbidden", "You are not allowed to notify this session")).body(null);
        }
        participanteService.save(participantes);
//        return userService.requestPasswordReset(mail)
//            .map(user -> {
//                String baseUrl = request.getScheme() +
//                    "://" +
//                    request.getServerName() +
//                    ":" +
//                    request.getServerPort() +
//                    request.getContextPath();
//                mailService.sendPasswordResetMail(user, baseUrl);
//                return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
//            }).orElse(new ResponseEntity<>("e-mail address not registered", HttpStatus.BAD_REQUEST));
        return new ResponseEntity<>("asistencia marcada", HttpStatus.OK);
    }

    /**
     * PUT  /sesions : Updates an existing sesion.
     *
     * @param sesion the sesion to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sesion,
     * or with status 400 (Bad Request) if the sesion is not valid,
     * or with status 500 (Internal Server Error) if the sesion couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/sesions")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<Sesion> updateSesion(@Valid @RequestBody Sesion sesion) throws URISyntaxException {
        log.debug("REST request to update Sesion : {}", sesion);
        if (sesion.getId() == null) {
            return createSesion(sesion);
        }
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            Miembro miembro = miembroService.findByOrganoIdAndUserIdAndFechaBajaIsNull(sesion.getOrgano().getId(), userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).get().getId());
            if (miembro == null || miembro.getCargo().getId() > 2)
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sesion", "forbidden", "You are not allowed to notify this session")).body(null);
        }
        Sesion result = sesionService.save(sesion);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sesion", sesion.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sesions : get all the sesions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of sesions in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/sesions")
    @Timed
    public ResponseEntity<List<Sesion>> getAllSesions(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Sesions");
        Page<Sesion> page = sesionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sesions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /sesions/:id : get the "id" sesion.
     *
     * @param id the id of the sesion to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sesion, or with status 404 (Not Found)
     */
    @GetMapping("/sesions/{id}")
    @Timed
    public ResponseEntity<Sesion> getSesion(@PathVariable Long id) {
        log.debug("REST request to get Sesion : {}", id);
        Sesion sesion = sesionService.findOne(id);
        return Optional.ofNullable(sesion)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /users/events : get the events in current month for the current user.
     *
     * @return the ResponseEntity with status 200 (OK) and with body the events in body
     */
    @GetMapping("/users/events")
    @Timed
    public ResponseEntity<List<Sesion>> getEvents() {
        log.debug("REST request to get events in current month for the current user");
        List<Sesion> sesiones = new ArrayList<Sesion>();
        if (SecurityUtils.isAuthenticated()) {
            for (Participante a : participanteService.findByUserIsCurrentUser()) {
                if (a.getSesion().getPrimeraConvocatoria().isAfter(ZonedDateTime.now().minusMonths(1L)))
                    sesiones.add(a.getSesion());
            }
        } else {
            sesiones = sesionService.findByPrimeraConvocatoriaGreaterThan(ZonedDateTime.now().minusMonths(1L));
        }

        return new ResponseEntity<>(sesiones, HttpStatus.OK);
    }

    /**
     * DELETE  /sesions/:id : delete the "id" sesion.
     *
     * @param id the id of the sesion to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/sesions/{id}")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<Void> deleteSesion(@PathVariable Long id) {
        log.debug("REST request to delete Sesion : {}", id);
        Sesion sesion = sesionService.findOne(id);
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            Miembro miembro = miembroService.findByOrganoIdAndUserIdAndFechaBajaIsNull(sesion.getOrgano().getId(), userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).get().getId());
            if (miembro == null || miembro.getCargo().getId() > 2)
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sesion", "forbidden", "You are not allowed to notify this session")).body(null);
        }
        sesionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("sesion", id.toString())).build();
    }

    /**
     * SEARCH  /_search/sesions?query=:query : search for the sesion corresponding
     * to the query.
     *
     * @param query the query of the sesion search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/sesions")
    @Timed
    public ResponseEntity<List<Sesion>> searchSesions(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Sesions for query {}", query);
        Page<Sesion> page = sesionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/sesions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /organos/:id/sesiones : get sesiones from the "id" organo.
     *
     * @param id the id of the organo
     * @return the ResponseEntity with status 200 (OK) and the list of sesiones in body
     */
    @GetMapping("/organos/{id}/sesiones")
    @Timed
    public ResponseEntity<List<Sesion>> getSesionesByOrganoId( @PathVariable Long id) {
        log.debug("REST request to get sesiones from organo : {}", id);

        List<Sesion> sesiones = sesionService.findByOrganoId(id);
        return new ResponseEntity<>(sesiones, HttpStatus.OK);
    }

    /**
     * GET  /organos/:id/lastSesion : get last sesion for the "id" organo.
     *
     * @param id the id of the organo
     * @return the ResponseEntity with status 200 (OK) and the last sesion in body
     */
    @GetMapping("/organos/{id}/lastSesion")
    @Timed
    public ResponseEntity<Sesion> getLastSesionByOrganoId( @PathVariable Long id) {
        log.debug("REST request to get last sesion for organo : {}", id);

        Sesion lastSesion = sesionService.lastSesion(id);
        return Optional.ofNullable(lastSesion)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.OK));
    }
}
