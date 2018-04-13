package coop.magnesium.vanadium.api;


import coop.magnesium.vanadium.db.dao.ColaboradorDao;
import coop.magnesium.vanadium.db.entities.Colaborador;
import coop.magnesium.vanadium.db.entities.Notificacion;
import coop.magnesium.vanadium.db.entities.RecuperacionPassword;
import coop.magnesium.vanadium.db.entities.TipoNotificacion;
import coop.magnesium.vanadium.system.MailEvent;
import coop.magnesium.vanadium.system.MailService;
import coop.magnesium.vanadium.system.StartupBean;
import coop.magnesium.vanadium.utils.KeyGenerator;
import coop.magnesium.vanadium.utils.Logged;
import coop.magnesium.vanadium.utils.PasswordUtils;
import coop.magnesium.vanadium.utils.PropertiesFromFile;
import coop.magnesium.vanadium.utils.ex.MagnesiumBdMultipleResultsException;
import coop.magnesium.vanadium.utils.ex.MagnesiumBdNotFoundException;
import coop.magnesium.vanadium.utils.ex.MagnesiumSecurityException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ejb.EJB;
import javax.ejb.ObjectNotFoundException;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Created by rsperoni on 05/05/17.
 */
@Path("/users")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Transactional
@Api(description = "Aplication auth service", tags = "auth")
public class UserService {

    @Inject
    Event<MailEvent> mailEvent;

    @Inject
    Event<Notificacion> notificacionEvent;

    @Inject
    @PropertiesFromFile
    Properties endpointsProperties;
    @Inject
    private KeyGenerator keyGenerator;
    @Context
    private UriInfo uriInfo;
    @Inject
    private Logger logger;
    @EJB
    private ColaboradorDao colaboradorDao;
    @EJB
    private StartupBean startupBean;

    @POST
    @Path("/login")
    @Consumes(APPLICATION_FORM_URLENCODED)
    @ApiOperation(value = "Authenticate user", response = Colaborador.class)
    @Logged
    public Response authenticateUser(@FormParam("email") String email,
                                     @FormParam("password") String password) {
        try {
            // Authenticate the vanadiumUser using the credentials provided
            Colaborador vanadiumUser = authenticate(email, password);
            if (vanadiumUser == null) throw new MagnesiumBdNotFoundException("Usuario no existe");
            //Info que quiero guardar en token
            Map<String, Object> map = new HashMap<>();
            map.put("role", vanadiumUser.getRole());
            map.put("id", String.valueOf(vanadiumUser.getId()));
            // Issue a token for the vanadiumUser
            String token = issueToken(email, map);
            vanadiumUser.setToken(token);
            //Notificacion login
            notificacionEvent.fire(new Notificacion(TipoNotificacion.LOGIN, vanadiumUser, "Login"));
            return Response.ok(vanadiumUser).build();
        } catch (MagnesiumSecurityException | MagnesiumBdMultipleResultsException | MagnesiumBdNotFoundException e) {
            logger.warning(e.getMessage());
            return Response.status(UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().build();
        }
    }

    /**
     * Paso uno de la recuperacion, genero el token y guardo en cache con vencimiento.
     *
     * @param email
     * @return
     */
    @POST
    @Path("/recuperar/{email}")
    @ApiOperation(value = "Recuperar password", response = Response.class)
    @Logged
    public Response recuperarPassword(@PathParam("email") String email) {
        try {
            if (colaboradorDao.findByEmail(email) == null) throw new ObjectNotFoundException("no existe colaborador");
            RecuperacionPassword recuperacionPassword = new RecuperacionPassword(email, UUID.randomUUID().toString(), LocalDateTime.now().plusHours(1));
            startupBean.putRecuperacionPassword(recuperacionPassword);
            mailEvent.fire(new MailEvent(Arrays.asList(email), MailService.generarEmailRecuperacionClave(recuperacionPassword.getToken(), endpointsProperties.getProperty("frontend.host"), endpointsProperties.getProperty("frontend.path")), "MARQ: Recuperación de Contraseña"));
            logger.info(recuperacionPassword.getToken());
            return Response.ok().build();
        } catch (ObjectNotFoundException e) {
            logger.warning(e.getMessage());
            return Response.status(UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().build();
        }
    }

    /**
     * Paso dos, me dan el token y doy el mail.
     *
     * @param token
     * @return
     */
    @GET
    @Path("/recuperar/{token}")
    @ApiOperation(value = "Recuperar email", response = Response.class)
    @Logged
    public Response recuperarEmail(@PathParam("token") String token) {
        try {
            RecuperacionPassword dataRecuperacionPassword = startupBean.getRecuperacionInfo(token);
            if (dataRecuperacionPassword == null) throw new ObjectNotFoundException("no existe recuperación");
            return Response.ok(dataRecuperacionPassword).build();
        } catch (ObjectNotFoundException e) {
            logger.warning(e.getMessage());
            return Response.status(UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/recuperar")
    @Consumes(APPLICATION_FORM_URLENCODED)
    @ApiOperation(value = "Cambiar password", response = Colaborador.class)
    @Logged
    public Response cambiarPassword(@FormParam("token") String token,
                                    @FormParam("password") String password) {
        try {
            RecuperacionPassword dataRecuperacionPassword = startupBean.getRecuperacionInfo(token);
            if (dataRecuperacionPassword == null) throw new MagnesiumBdNotFoundException("no existe recuperación");
            Colaborador colaborador = colaboradorDao.findByEmail(dataRecuperacionPassword.getEmail());
            if (colaborador == null) throw new MagnesiumBdNotFoundException("no existe colaborador");
            colaborador.setPassword(PasswordUtils.digestPassword(password));
            notificacionEvent.fire(new Notificacion(TipoNotificacion.CAMBIO_PASSWORD, colaborador, "Cambio password."));
            return Response.ok().build();
        } catch (MagnesiumBdNotFoundException e) {
            logger.warning(e.getMessage());
            return Response.status(UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().build();
        }
    }


    private Colaborador authenticate(String email, String password) throws MagnesiumBdMultipleResultsException, MagnesiumSecurityException {
        Colaborador vanadiumUser = colaboradorDao.findByEmail(email);
        if (!PasswordUtils.digestPassword(password).equals(vanadiumUser.getPassword()))
            throw new MagnesiumSecurityException("Invalid vanadiumUser/password");
        return vanadiumUser;
    }

    private String issueToken(String login, Map<String, Object> claims) {
        Key key = keyGenerator.generateKey();
        String jwtToken = Jwts.builder()
                .setSubject(login)
                .setClaims(claims)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        return jwtToken;
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
