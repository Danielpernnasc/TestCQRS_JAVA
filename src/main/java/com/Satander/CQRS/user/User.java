package com.Satander.CQRS.user;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String passwordHash;

    public User() {
    }

    public User(String fullName, String cpf, String login, String passwordHash) {
        this.fullName = fullName;
        this.cpf = cpf;
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String v) {
        this.fullName = v;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String v) {
        this.cpf = v;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String v) {
        this.login = v;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String v) {
        this.passwordHash = v;
    }
}
