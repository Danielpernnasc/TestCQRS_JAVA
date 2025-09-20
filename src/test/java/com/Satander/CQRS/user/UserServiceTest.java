package com.Satander.CQRS.user;

import com.Satander.CQRS.common.CpfValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository repo;
    private PasswordEncoder enc;
    private UserService service;

    @BeforeEach
    void setup() {
        repo = mock(UserRepository.class);
        enc = mock(PasswordEncoder.class);
        service = new UserService(repo, enc);
    }

    @Test
    void register_success() {
        String cpf = "12345678909";
        String encoded = "encoded";
        when(enc.encode("senha")).thenReturn(encoded);
        when(repo.existsByLogin("login")).thenReturn(false);
        when(repo.existsByCpf(cpf)).thenReturn(false);
        User user = new User("nome", cpf, "login", encoded);
        when(repo.save(any(User.class))).thenReturn(user);

        Long id = service.register("nome", cpf, "login", "senha");
        assertThat(id).isEqualTo(user.getId());
    }

    @Test
    void register_invalidCpf_throwsException() {
        String cpf = "00000000000";
        assertThatThrownBy(() -> service.register("nome", cpf, "login", "senha"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF inválido");
    }

    @Test
    void register_loginExists_throwsException() {
        String cpf = "12345678909";
        when(repo.existsByLogin("login")).thenReturn(true);

        assertThatThrownBy(() -> service.register("nome", cpf, "login", "senha"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Login já cadastrado");
    }

    @Test
    void register_cpfExists_throwsException() {
        String cpf = "12345678909";
        when(repo.existsByLogin("login")).thenReturn(false);
        when(repo.existsByCpf(cpf)).thenReturn(true);

        assertThatThrownBy(() -> service.register("nome", cpf, "login", "senha"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CPF já cadastrado");
    }

    @Test
    void check_returnsTrue_whenPasswordMatches() {
        User user = new User("nome", "12345678909", "login", "hash");
        when(enc.matches("senha", "hash")).thenReturn(true);

        assertThat(service.check(user, "senha")).isTrue();
    }

    @Test
    void check_returnsFalse_whenPasswordDoesNotMatch() {
        User user = new User("nome", "12345678909", "login", "hash");
        when(enc.matches("senha", "hash")).thenReturn(false);

        assertThat(service.check(user, "senha")).isFalse();
    }

    @Test
    void create_success() {
        String cpf = "12345678909";
        String encoded = "encoded";
        when(enc.encode("senha")).thenReturn(encoded);
        when(repo.existsByLogin("login")).thenReturn(false);
        when(repo.existsByCpf(cpf)).thenReturn(false);
        User user = new User("nome", cpf, "login", encoded);
        when(repo.save(any(User.class))).thenReturn(user);
        when(repo.findById(user.getId())).thenReturn(Optional.of(user));

        User created = service.create("nome", cpf, "login", "senha");
        assertThat(created).isEqualTo(user);
    }
}