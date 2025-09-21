package com.Satander.CQRS.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.Santander.CQRS.user.User;
import com.Santander.CQRS.user.UserController;
import com.Santander.CQRS.user.UserRepository;
import com.Santander.CQRS.user.UserService;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerStandaloneTest {

    @Mock
    UserRepository repo;
    @Mock
    UserService users;

    @InjectMocks
    UserController controller;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void register_returnsUserId() throws Exception {
        // arrange
        when(users.register("João Silva", "12345678900", "joao", "senha"))
                .thenReturn(1L);

        var entity = new User("João Silva", "12345678900", "joao", "hash");
        // garante que o objeto retornado pelo repo tenha id=1
        try {
            entity.setId(1L);
        } catch (Throwable ignored) {
            ReflectionTestUtils.setField(entity, "id", 1L);
        }
        when(repo.findById(1L)).thenReturn(Optional.of(entity));

        // act + assert
        mvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                              "fullName":"João Silva",
                              "cpf":"12345678900",
                              "login":"joao",
                              "password":"senha"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.fullName").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("12345678900"))
                .andExpect(jsonPath("$.login").value("joao"));
    }
}
