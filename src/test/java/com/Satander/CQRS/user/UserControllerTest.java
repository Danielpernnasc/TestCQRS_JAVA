package com.Satander.CQRS.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    UserRepository repo;

    @MockBean
    UserService users;

    @Test
    void register_returnsUserId() throws Exception {
        when(users.register("João Silva", "12345678900", "joao", "senha"))
                .thenReturn(1L);

        when(repo.findById(1L)).thenReturn(java.util.Optional.of(
                new User("João Silva", "12345678900", "joao", "hash")));

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
                .andExpect(jsonPath("$.userId").value(1));
    }
}