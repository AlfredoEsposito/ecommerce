package com.alten.ecommerce.domain;

import javax.persistence.*;

@Entity
@Table(name = "jwt")
public class JwToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token")
    private String token;

    public JwToken() {
    }

    public JwToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "JwToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                '}';
    }
}
