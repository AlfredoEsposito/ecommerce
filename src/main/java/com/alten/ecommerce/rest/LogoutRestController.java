package com.alten.ecommerce.rest;

import com.alten.ecommerce.service.jwtoken.JwTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LogoutRestController {

    private JwTokenService jwTokenService;

    @Autowired
    public LogoutRestController(JwTokenService jwTokenService) {
        this.jwTokenService = jwTokenService;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token){
        String jwt = token.substring("Bearer ".length());
        jwTokenService.deleteToken(jwt);
        return ResponseEntity.ok().body("Logout successful!");
    }
}
