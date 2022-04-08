package com.alten.ecommerce.security;

import com.alten.ecommerce.filter.AuthenticationFilter;
import com.alten.ecommerce.filter.AuthorizationFilter;
import com.alten.ecommerce.jwt.JwtConfig;
import com.alten.ecommerce.service.jwtoken.JwTokenService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //per abilitare le annotation @Preauthorize nei controller
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtConfig jwtConfig;
    private JwTokenService jwTokenService;

    public SecurityConfig(BCryptPasswordEncoder passwordEncoder, UserDetailsService userDetailsService, JwtConfig jwtConfig, JwTokenService jwTokenService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtConfig = jwtConfig;
        this.jwTokenService = jwTokenService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManagerBean(), jwtConfig, jwTokenService);
        authenticationFilter.setFilterProcessesUrl("/login");

        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(authenticationFilter)
                .addFilterBefore(new AuthorizationFilter(jwtConfig, jwTokenService), UsernamePasswordAuthenticationFilter.class);
    }
}
