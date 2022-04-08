package com.alten.ecommerce.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.http.HttpHeaders;

@PropertySource("classpath:application.properties")
@Configuration
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig  {

    private String secretKey;
    private String tokenPrefix;

    public JwtConfig() {
    }

    public JwtConfig(String secretKey, String tokenPrefix) {
        this.secretKey = secretKey;
        this.tokenPrefix = tokenPrefix;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

}
