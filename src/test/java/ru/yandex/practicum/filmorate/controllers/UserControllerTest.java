package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest()
    @Order(1)
    @MethodSource("provideValidRequests")
    public void shouldCreateUser(String request, int expectedId, String expectedName, String expectedEmail) throws Exception {
        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                                           .content(request)
                                           .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(expectedId))
                    .andExpect(jsonPath("$.login").value("dolore"))
                    .andExpect(jsonPath("$.name").value(expectedName))
                    .andExpect(jsonPath("$.email").value(expectedEmail))
                    .andExpect(jsonPath("$.birthday").value("1946-08-20"));
    }

    private static Stream<Arguments> provideValidRequests() {
        return Stream.of(
                Arguments.of(Named.of("Request with not empty name",
                        "{\n" +
                                "    \"login\": \"dolore\",\n" +
                                "    \"name\": \"Nick Name\",\n" +
                                "    \"email\": \"mail@mail.ru\",\n" +
                                "    \"birthday\": \"1946-08-20\"\n" +
                                "}"), 1, "Nick Name", "mail@mail.ru"),
                Arguments.of(Named.of("Request with empty name",
                        "{\n" +
                                "    \"login\": \"dolore\",\n" +
                                "    \"name\": \"\",\n" +
                                "    \"email\": \"mail@yahoo.com\",\n" +
                                "    \"birthday\": \"1946-08-20\"\n" +
                                "}"), 2, "dolore", "mail@yahoo.com"));
    }

    @ParameterizedTest()
    @Order(2)
    @MethodSource("provideInvalidRequests")
    public void shouldReturnStatusCode400IfValidationFailed(String request) throws Exception {
        this.mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
                Arguments.of(Named.of("Invalid login",
                        "{\n" +
                                "  \"login\": \"dolore ullamco\",\n" +
                                "  \"email\": \"mail.ru\",\n" +
                                "  \"birthday\": \"2446-08-20\"\n" +
                                "}")),
                Arguments.of(Named.of("Invalid email",
                        "{\n" +
                                "  \"login\": \"dolore ullamco\",\n" +
                                "  \"name\": \"\",\n" +
                                "  \"email\": \"mail.ru\",\n" +
                                "  \"birthday\": \"1980-08-20\"\n" +
                                "}")),
                Arguments.of(Named.of("Invalid birthday",
                        "{\n" +
                                "  \"login\": \"dolore\",\n" +
                                "  \"name\": \"\",\n" +
                                "  \"email\": \"test@mail.ru\",\n" +
                                "  \"birthday\": \"2446-08-20\"\n" +
                                "}")));
    }

    @Test
    @Order(3)
    public void shouldUpdateUser() throws Exception {
        this.mockMvc.perform(put("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\n" +
                            "  \"login\": \"doloreUpdate\",\n" +
                            "  \"name\": \"est adipisicing\",\n" +
                            "  \"id\": 1,\n" +
                            "  \"email\": \"mail@yandex.ru\",\n" +
                            "  \"birthday\": \"1976-09-20\"\n" +
                            "}")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.login").value("doloreUpdate"))
                    .andExpect(jsonPath("$.name").value("est adipisicing"))
                    .andExpect(jsonPath("$.email").value("mail@yandex.ru"))
                    .andExpect(jsonPath("$.birthday").value("1976-09-20"));
    }

    @Test
    @Order(4)
    public void shouldReturnStatusCode500IfUserDoesNotExist() throws Exception {
        this.mockMvc.perform(put("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\n" +
                            "  \"login\": \"doloreUpdate\",\n" +
                            "  \"name\": \"est adipisicing\",\n" +
                            "  \"id\": -1,\n" +
                            "  \"email\": \"mail@yandex.ru\",\n" +
                            "  \"birthday\": \"1976-09-20\"\n" +
                            "}")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(5)
    public void shouldReturnUsers() throws Exception {
        this.mockMvc.perform(get("/users")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].login").value("doloreUpdate"))
                    .andExpect(jsonPath("$[0].name").value("est adipisicing"))
                    .andExpect(jsonPath("$[0].id").value("1"))
                    .andExpect(jsonPath("$[0].email").value("mail@yandex.ru"))
                    .andExpect(jsonPath("$[0].birthday").value("1976-09-20"));
    }
}
