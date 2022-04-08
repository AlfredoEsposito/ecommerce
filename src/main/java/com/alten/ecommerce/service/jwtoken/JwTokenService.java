package com.alten.ecommerce.service.jwtoken;

import com.alten.ecommerce.domain.JwToken;

public interface JwTokenService {

    JwToken saveToken(String token);
    void deleteToken(String token);
    boolean tokenExists(String token);
}
