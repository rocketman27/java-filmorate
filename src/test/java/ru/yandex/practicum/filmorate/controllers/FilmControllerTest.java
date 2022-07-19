package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void shouldCreateFilm() throws Exception {
        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                                           .content(getBodyForPostRequest())
                                           .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.name").value("nisi eiusmod"))
                    .andExpect(jsonPath("$.description").value("adipisicing"))
                    .andExpect(jsonPath("$.releaseDate").value("1967-03-25"))
                    .andExpect(jsonPath("$.duration").value("100"));
    }

    private String getBodyForPostRequest() {
        return "{\n" +
                "    \"name\": \"nisi eiusmod\",\n" +
                "    \"description\": \"adipisicing\",\n" +
                "    \"releaseDate\": \"1967-03-25\",\n" +
                "    \"duration\": 100\n" +
                "}";
    }

    @ParameterizedTest()
    @Order(2)
    @MethodSource("provideInvalidRequests")
    public void shouldReturnStatusCode400IfValidationFailed(String request) throws Exception {
        this.mockMvc.perform(post("/films")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
                Arguments.of(Named.of("Invalid name",
                        "{\n" +
                                "  \"name\": \"\",\n" +
                                "  \"description\": \"Description\",\n" +
                                "  \"releaseDate\": \"1900-03-25\",\n" +
                                "  \"duration\": 200\n" +
                                "}")),
                Arguments.of(Named.of("Invalid description",
                        "{\n" +
                                "  \"name\": \"Film name\",\n" +
                                "  \"description\": \"Пятеро друзей ( комик-группа «Шарло»), " +
                                "приезжают в город Бризуль. Здесь они хотят разыскать господина " +
                                "Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. " +
                                "о Куглов, который за время «своего отсутствия», стал кандидатом " +
                                "Коломбани.\",\n" +
                                "    \"releaseDate\": \"1900-03-25\",\n" +
                                "  \"duration\": 200\n" +
                                "}")),
                Arguments.of(Named.of("Invalid release date",
                        "{\n" +
                                "  \"name\": \"Film name\",\n" +
                                "  \"description\": \"Description\",\n" +
                                "  \"releaseDate\": \"1895-12-27\",\n" +
                                "  \"duration\": 200\n" +
                                "}")),
                Arguments.of(Named.of("Invalid duration",
                        "{\n" +
                                "  \"name\": \"Film name\",\n" +
                                "  \"description\": \"Description\",\n" +
                                "  \"releaseDate\": \"1895-12-29\",\n" +
                                "  \"duration\": -200\n" +
                                "}")));
    }

    @Test
    @Order(3)
    public void shouldUpdateFilm() throws Exception {
        this.mockMvc.perform(put("/films")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\n" +
                            "    \"id\": \"1\",\n" +
                            "    \"name\": \"Midnight in Paris\",\n" +
                            "    \"description\": \"Woody Allen's film\",\n" +
                            "    \"releaseDate\": \"2011-05-20\",\n" +
                            "    \"duration\": 100\n" +
                            "}")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.name").value("Midnight in Paris"))
                    .andExpect(jsonPath("$.description").value("Woody Allen's film"))
                    .andExpect(jsonPath("$.releaseDate").value("2011-05-20"))
                    .andExpect(jsonPath("$.duration").value("100"));
    }

    @Test
    @Order(4)
    public void shouldReturnStatusCode404IfFilmDoesNotExist() throws Exception {
        this.mockMvc.perform(put("/films")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\n" +
                            "    \"id\": \"-1\",\n" +
                            "    \"name\": \"Midnight in Paris\",\n" +
                            "    \"description\": \"Woody Allen's film\",\n" +
                            "    \"releaseDate\": \"2011-05-20\",\n" +
                            "    \"duration\": 100\n" +
                            "}")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    public void shouldReturnFilms() throws Exception {
        this.mockMvc.perform(get("/films")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value("1"))
                    .andExpect(jsonPath("$[0].name").value("Midnight in Paris"))
                    .andExpect(jsonPath("$[0].description").value("Woody Allen's film"))
                    .andExpect(jsonPath("$[0].releaseDate").value("2011-05-20"))
                    .andExpect(jsonPath("$[0].duration").value("100"));
    }
}
