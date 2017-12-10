package coop.magnesium.sulfur.api;


import coop.magnesium.sulfur.db.dao.ColaboradorDao;
import coop.magnesium.sulfur.db.entities.Colaborador;
import coop.magnesium.sulfur.utils.KeyGenerator;
import coop.magnesium.sulfur.utils.Logged;
import coop.magnesium.sulfur.utils.PasswordUtils;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdMultipleResultsException;
import coop.magnesium.sulfur.utils.ex.MagnesiumBdNotFoundException;
import coop.magnesium.sulfur.utils.ex.MagnesiumSecurityException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
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
public class UserService {

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
            // Authenticate the sulfurUser using the credentials provided
            Colaborador sulfurUser = authenticate(email, password);
            if (sulfurUser == null) throw new MagnesiumBdNotFoundException("Usuario no existe");
            //Info que quiero guardar en token
            Map<String, Object> map = new HashMap<>();
            map.put("role", sulfurUser.getRole());
            // Issue a token for the sulfurUser
            String token = issueToken(email, map);
            sulfurUser.setToken(token);
            return Response.ok(sulfurUser).build();
        } catch (MagnesiumSecurityException | MagnesiumBdMultipleResultsException | MagnesiumBdNotFoundException e) {
            logger.warning(e.getMessage());
            return Response.status(UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.serverError().build();
        }
    }


    private Colaborador authenticate(String email, String password) throws MagnesiumBdNotFoundException, MagnesiumBdMultipleResultsException, MagnesiumSecurityException {
        Colaborador sulfurUser = colaboradorDao.findByEmail(email);
        if (!PasswordUtils.digestPassword(password).equals(sulfurUser.getPassword()))
            throw new MagnesiumSecurityException("Invalid sulfurUser/password");
        return sulfurUser;
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
