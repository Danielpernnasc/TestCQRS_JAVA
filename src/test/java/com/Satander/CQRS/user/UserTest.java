package com.Satander.CQRS.user;

import org.junit.jupiter.api.Test;

import com.Santander.CQRS.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setFullName("João Silva");
        user.setCpf("12345678900");
        user.setLogin("joao");
        user.setPasswordHash("hash123");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getFullName()).isEqualTo("João Silva");
        assertThat(user.getCpf()).isEqualTo("12345678900");
        assertThat(user.getLogin()).isEqualTo("joao");
        assertThat(user.getPasswordHash()).isEqualTo("hash123");
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User("Maria Souza", "98765432100", "maria", "hash456");

        assertThat(user.getFullName()).isEqualTo("Maria Souza");
        assertThat(user.getCpf()).isEqualTo("98765432100");
        assertThat(user.getLogin()).isEqualTo("maria");
        assertThat(user.getPasswordHash()).isEqualTo("hash456");
    }
}