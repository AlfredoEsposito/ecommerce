package com.alten.ecommerce.repositories;

import com.alten.ecommerce.domain.JwToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwTokenRepository extends JpaRepository<JwToken, Long> {

    Optional<JwToken> findByTokenEquals(String token);
}
