package com.alten.ecommerce.service.jwtoken;

import com.alten.ecommerce.domain.JwToken;
import com.alten.ecommerce.repositories.JwTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class JwTokenServiceImpl implements JwTokenService{

    private final JwTokenRepository tokenRepository;

    public JwTokenServiceImpl(JwTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public JwToken saveToken(String token) {
        JwToken jwt;
        if(token == null){
            log.error("Error! Missing token");
            throw new RuntimeException("Missing token jwt!");
        }else{
            jwt = new JwToken(token);
            tokenRepository.save(jwt);
            log.info("Token jwt saved to database");
        }
        return jwt;
    }

    @Override
    public void deleteToken(String token) {
        Optional<JwToken> optionalJwt = tokenRepository.findByTokenEquals(token);
        JwToken jwt = null;
        if(optionalJwt.isPresent()){
            jwt = optionalJwt.get();
            tokenRepository.delete(jwt);
            log.info("Jwt token deleted from database");
        }else{
            log.error("Error! Missing token");
            throw new RuntimeException("Missing token jwt!");
        }
    }

    @Override
    public boolean tokenExists(String token) {
        List<JwToken> tokens = tokenRepository.findAll();
        if(tokens.stream().anyMatch(t -> (t.getToken().equals(token)))){
            return true;
        }else{
            return false;
        }
    }
}
