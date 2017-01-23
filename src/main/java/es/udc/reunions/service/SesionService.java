package es.udc.reunions.service;

import com.sun.mail.smtp.SMTPTransport;
import es.udc.reunions.domain.Sesion;
import es.udc.reunions.repository.SesionRepository;
import es.udc.reunions.repository.search.SesionSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.security.Security;
import java.text.DateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Sesion.
 */
@Service
@Transactional
public class SesionService {

    private final Logger log = LoggerFactory.getLogger(SesionService.class);

    @Inject
    private SesionRepository sesionRepository;

    @Inject
    private SesionSearchRepository sesionSearchRepository;

    /**
     * Save a sesion.
     *
     * @param sesion the entity to save
     * @return the persisted entity
     */
    public Sesion save(Sesion sesion) {
        log.debug("Request to save Sesion : {}", sesion);
        Sesion result = sesionRepository.save(sesion);
        sesionSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the sesions.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Sesion> findAll() {
        log.debug("Request to get all Sesions");
        List<Sesion> result = sesionRepository.findAll();
        return result;
    }

    /**
     *  Get one sesion by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Sesion findOne(Long id) {
        log.debug("Request to get Sesion : {}", id);
        Sesion sesion = sesionRepository.findOne(id);
        return sesion;
    }

    /**
     *  Get all the sesiones from an organo
     *
     *  @param organoId the organo id
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Sesion> findByOrganoId(Long organoId) {
        log.debug("Request to get all miembros actuales from organo " + organoId);
        List<Sesion> result = sesionRepository.findByOrganoId(organoId);
        return result;
    }

    /**
     *  Get the last sesion for an organo
     *
     *  @param organoId the organo id
     *  @return the last sesion
     */
    @Transactional(readOnly = true)
    public Sesion lastSesion(Long organoId) {
        log.debug("Request to get last sesion for organo " + organoId);
        Sesion sesion = sesionRepository.findTopByOrganoIdOrderByNumeroDesc(organoId);
        return sesion;
    }

    /**
     *  Delete the  sesion by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Sesion : {}", id);
        sesionRepository.delete(id);
        sesionSearchRepository.delete(id);
    }

    /**
     * Search for the sesion corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Sesion> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sesions for query {}", query);
        Page<Sesion> result = sesionSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }

    public void notificar(Sesion sesion, HttpServletRequest request) {
        final Locale locale = request.getLocale();

        final String username = "d.lamas";
        final String password = "Charmander690";

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.udc.es");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "25");
        props.setProperty("mail.smtp.socketFactory.port", "25");
        props.setProperty("mail.smtps.auth", "true");

        Session session = Session.getInstance(props,
            null);

        try {
            final MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress("reunions@udc.es"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("d.lamas@udc.es", false));
            //
            //        if (ccEmail.length() > 0) {
            //            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
            //        }

            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, locale);
            String[] orden = sesion.getDescripcion() != null ? sesion.getDescripcion().split(System.getProperty("line.separator")) : "".split(System.getProperty("line.separator"));
            msg.setSubject(sesion.getOrgano().getNombre() + " - " + "Sesión " + sesion.getNumero(), "UTF-8");
            String msgText = "<h1>" + sesion.getOrgano().getNombre() + "</h1>"
                + "Nueva sesión convocada:"
                + "<li>Lugar: " + sesion.getLugar() + "</li>"
                + "<li>Orden del día: ";
            for (String linea: orden) {
                msgText += "<br/>&emsp;&emsp;" + linea;
            }
            msg.setContent(msgText, "text/html; charset=utf-8");
            msg.setSentDate(new Date());

            SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

            t.connect("smtp.udc.es", username, password);
            t.sendMessage(msg, msg.getAllRecipients());
            t.close();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Sesion> findByPrimeraConvocatoriaGreaterThan(ZonedDateTime zonedDateTime) {
        log.debug("Request to get sesions from last month and after");
        List<Sesion> result = sesionRepository.findByPrimeraConvocatoriaGreaterThan(zonedDateTime);
        return result;
    }
}
