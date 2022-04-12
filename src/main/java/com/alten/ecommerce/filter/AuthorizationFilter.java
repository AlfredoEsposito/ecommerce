package com.alten.ecommerce.filter;

import com.alten.ecommerce.jwt.JwtConfig;
import com.alten.ecommerce.service.jwtoken.JwTokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final JwTokenService jwTokenService;

    public AuthorizationFilter(JwtConfig jwtConfig, JwTokenService jwTokenService) {
        this.jwtConfig = jwtConfig;
        this.jwTokenService = jwTokenService;
    }

    //Questo metodo (filtro) viene eseguito per ogni richiesta in arrivo dal client e si occupa di verificare la validità del token al fine di autorizzare la richiesta
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals("/login")){
            filterChain.doFilter(request, response);
        }else{
            //una volta verificato che la richiesta in arrivo non è la richiesta per il login procediamo con la verifica della validità del token
            String tokenJwt;
            String authorizationHeader = request.getHeader(AUTHORIZATION); //recuperiamo l'header della request
            if(authorizationHeader!=null && authorizationHeader.startsWith(jwtConfig.getTokenPrefix())){ //verifica che l'header della request non sia null, e inizi con "Bearer "
                tokenJwt = authorizationHeader.substring(jwtConfig.getTokenPrefix().length()); //estrae il token effettivo eliminando "Bearer "
                if(jwTokenService.tokenExists(tokenJwt)){ //verifica l'esistenza del token nel database (cioè se un utente ha già effettuato l'accesso)
                    try {
                        //verifica del token
                        log.info("Verifying token...");
                        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecretKey().getBytes());
                        JWTVerifier verifier = JWT.require(algorithm).build();
                        DecodedJWT decodedJWT = verifier.verify(tokenJwt);
                        log.info("Token verified");

                        //recupero parti del token
                        log.info("Retrieving parts of the verified token...");
                        String username = decodedJWT.getSubject();
                        String [] roles = decodedJWT.getClaim("roles").asArray(String.class);
                        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
                        stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

                        //autenticazione token
                        log.info("Authenticating token...");
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        log.info("Token authenticated");
                        filterChain.doFilter(request, response);

                    }catch (TokenExpiredException tokenExpiredException){
                        //nel caso in cui il token sia scaduto, catturiamo l'eccezione,
                        //lanciamo un messaggio di errore json e restituiamo l'errore come header
                        log.error("Error! {}", tokenExpiredException.getMessage());
                        jwTokenService.deleteToken(tokenJwt);
                        response.setHeader("Error", tokenExpiredException.getMessage());
                        response.setStatus(UNAUTHORIZED.value());
                        Map<String, String> errors = new HashMap<>();
                        errors.put("error_message", tokenExpiredException.getMessage());
                        response.setContentType(APPLICATION_JSON_VALUE);
                        new ObjectMapper().writeValue(response.getOutputStream(), errors);

                    }catch (Exception exception){
                        //nel caso in cui dovessero esserci problemi di qualsiasi tipo, catturiamo l'eccezione
                        //lanciamo un messaggio di errore json e restituiamo l'errore come header
                        log.error("Error! {}", exception.getMessage());
                        response.setHeader("Error", exception.getMessage());
                        response.setStatus(FORBIDDEN.value());
                        Map<String, String> errors = new HashMap<>();
                        errors.put("error_message", exception.getMessage());
                        response.setContentType(APPLICATION_JSON_VALUE);
                        new ObjectMapper().writeValue(response.getOutputStream(), errors);
                    }
                }else{
                    //nel caso in cui il token non sia presente nel db, ovvero se un utente si è sloggato
                    //lo stato della response sarà 401 unauthorized e lanciamo il messaggio di errore json
                    log.error("Error! Token unavailable or not found");
                    response.setHeader("Error", "Unauthorized! Token unavailable or not found");
                    response.setStatus(UNAUTHORIZED.value());
                    Map<String, String> errors = new HashMap<>();
                    errors.put("error_message", "Unauthorized! Token unavailable or not found");
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), errors);
                }
            }else{
                //nel caso in cui invece l'header sia nullo o non valido lo stato della response sarà 403 forbidden
                //e lanciamo il messaggio di errore json
                log.error("Error! Invalid Authorization header");
                response.setHeader("Error", "Invalid Authorization header");
                response.setStatus(FORBIDDEN.value());
                Map<String, String> errors = new HashMap<>();
                errors.put("error_message", "Invalid Authorization header");
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), errors);
            }
        }
    }
}
