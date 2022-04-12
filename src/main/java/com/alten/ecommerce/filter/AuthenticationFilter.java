package com.alten.ecommerce.filter;

import com.alten.ecommerce.jwt.JwtConfig;
import com.alten.ecommerce.service.jwtoken.JwTokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final JwTokenService jwTokenService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig, JwTokenService jwTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.jwTokenService = jwTokenService;
    }

    //metodo che prende le credenziali dell'utente dalla request e le valida
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Username : {}, password : {}", username, password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    //Il seguente metodo viene eseguito dopo attemptAuthentication() solo in caso di autenticazione riuscita.
    //Si occupa di generare il token e inviarlo al client
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        User user = (User) authResult.getPrincipal(); //User di Spring security
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecretKey()); //Forniamo la chiave segreta con cui criptare il token

        //creazione token jwt
        String tokenJwt = JWT.create()
                .withSubject(user.getUsername())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withIssuedAt(new Date())
                .withExpiresAt(java.sql.Timestamp.valueOf(LocalDateTime.now().plus(15, ChronoUnit.MINUTES)))
                .withIssuer(request.getRequestURI())
                .sign(algorithm);

        response.setHeader(HttpHeaders.AUTHORIZATION, jwtConfig.getTokenPrefix()+tokenJwt);
        //salvataggio token nel database
        jwTokenService.saveToken(tokenJwt);
    }
}
