package com.Satander.CQRS.user;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    @Column(unique = true) private String cpf;
    @Column(unique = true) private String email;
    private String login;
    private String password;

    public User(String fullName, String cpf, String login, String password) {

        this.fullName = fullName;

        this.cpf = cpf;

        this.login = login;

        this.password = password;

    }

    public Long getId() { // Added getId method

        return id;

    }

    public void setId(Long id) { // Optional: Added setId method

        this.id = id;

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
