package coop.magnesium.vanadium.api;


import coop.magnesium.vanadium.db.dao.ColaboradorDao;
import coop.magnesium.vanadium.db.entities.Colaborador;
import coop.magnesium.vanadium.utils.KeyGenerator;
import coop.magnesium.vanadium.utils.Logged;
import coop.magnesium.vanadium.utils.PasswordUtils;
import coop.magnesium.vanadium.utils.ex.MagnesiumBdMultipleResultsException;
import coop.magnesium.vanadium.utils.ex.MagnesiumBdNotFoundException;
import coop.magnesium.vanadium.utils.ex.MagnesiumSecurityException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
public class UserServiceMock {

    @Inject
    private KeyGenerator keyGenerator;
    @Context
    private UriInfo uriInfo;
    @Inject
    private Logger logger;
    @EJB
    private ColaboradorDao colaboradorDao;

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
            return Response.ok(vanadiumUser).build();
        } catch (MagnesiumSecurityException | MagnesiumBdMultipleResultsException | MagnesiumBdNotFoundException e) {
            logger.warning(e.getMessage());
            return Response.status(UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().build();
        }
    }


    private Colaborador authenticate(String email, String password) throws MagnesiumBdNotFoundException, MagnesiumBdMultipleResultsException, MagnesiumSecurityException {
        Colaborador vanadiumUser = colaboradorDao.findByEmail(email);
        if (!PasswordUtils.digestPassword(password).equals(vanadiumUser.getPassword()))
            throw new MagnesiumSecurityException("Invalid vanadiumUser/password");
        return vanadiumUser;
    }

    private String issueToken(String login, Map<String, Object> claims) {
        return claims.get("role") + ":" + claims.get("id");
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
