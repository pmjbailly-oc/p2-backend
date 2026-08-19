package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.StudentRequestDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.repository.UserRepository;
import com.openclassrooms.etudiant.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'intégration du StudentController, de la requête HTTP jusqu'à la base MySQL
 * (container Docker via Testcontainers).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class StudentControllerTest {

    private static final String URL = "/api/students";
    private static final String FIRST_NAME = "Marie";
    private static final String LAST_NAME = "Durand";
    private static final String EMAIL = "marie.durand@example.com";

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.4");

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Crée un utilisateur, se connecte et renvoie un token JWT valide pour les routes protégées.
     */
    private String getAuthToken() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setLogin("login");
        user.setPassword("password");
        userService.register(user);

        String loginBody = "{\"login\":\"login\",\"password\":\"password\"}";
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .content(loginBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return jsonNode.get("token").asText();
    }

    private String buildStudentBody(String firstName, String lastName, String email) throws Exception {
        StudentRequestDTO dto = new StudentRequestDTO();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        return objectMapper.writeValueAsString(dto);
    }

    private void createStudentInDb(String firstName, String lastName, String email) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        studentRepository.save(student);
    }

    @Test
    public void createStudentWithoutData() throws Exception {
        // GIVEN
        String token = getAuthToken();

        // WHEN - corps vide
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .header("Authorization", "Bearer " + token)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                // THEN - validation Bean : champs requis manquants
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createStudentWithInvalidEmail() throws Exception {
        // GIVEN
        String token = getAuthToken();

        // WHEN - email mal formé
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .header("Authorization", "Bearer " + token)
                        .content(buildStudentBody(FIRST_NAME, LAST_NAME, "not-an-email"))
                        .contentType(MediaType.APPLICATION_JSON))
                // THEN - validation @Email
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createStudentWithAlreadyUsedEmail() throws Exception {
        // GIVEN
        String token = getAuthToken();
        createStudentInDb(FIRST_NAME, LAST_NAME, EMAIL);

        // WHEN - même email déjà en base
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .header("Authorization", "Bearer " + token)
                        .content(buildStudentBody("Jean", "Dupont", EMAIL))
                        .contentType(MediaType.APPLICATION_JSON))
                // THEN - unicité de l'email violée
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createStudentSuccess() throws Exception {
        // GIVEN
        String token = getAuthToken();

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .header("Authorization", "Bearer " + token)
                        .content(buildStudentBody(FIRST_NAME, LAST_NAME, EMAIL))
                        .contentType(MediaType.APPLICATION_JSON))
                // THEN - 201 + réponse complète
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    public void getAllStudentsEmpty() throws Exception {
        // GIVEN
        String token = getAuthToken();

        // WHEN - aucune donnée
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .header("Authorization", "Bearer " + token))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllStudents() throws Exception {
        // GIVEN
        String token = getAuthToken();
        createStudentInDb(FIRST_NAME, LAST_NAME, EMAIL);
        createStudentInDb("Jean", "Dupont", "jean.dupont@example.com");

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .header("Authorization", "Bearer " + token))
                // THEN - deux étudiants
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getStudentByIdSuccess() throws Exception {
        // GIVEN
        String token = getAuthToken();
        createStudentInDb(FIRST_NAME, LAST_NAME, EMAIL);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/1")
                        .header("Authorization", "Bearer " + token))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    public void getStudentByIdNotFound() throws Exception {
        // GIVEN
        String token = getAuthToken();

        // WHEN - id inexistant
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/999")
                        .header("Authorization", "Bearer " + token))
                // THEN
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateStudentSuccess() throws Exception {
        // GIVEN
        String token = getAuthToken();
        createStudentInDb(FIRST_NAME, LAST_NAME, EMAIL);

        // WHEN - modification du prénom, email inchangé
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/1")
                        .header("Authorization", "Bearer " + token)
                        .content(buildStudentBody("Marie-Anne", LAST_NAME, EMAIL))
                        .contentType(MediaType.APPLICATION_JSON))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Marie-Anne"))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    public void updateStudentNotFound() throws Exception {
        // GIVEN
        String token = getAuthToken();

        // WHEN - id inexistant
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/999")
                        .header("Authorization", "Bearer " + token)
                        .content(buildStudentBody(FIRST_NAME, LAST_NAME, EMAIL))
                        .contentType(MediaType.APPLICATION_JSON))
                // THEN
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateStudentEmailConflict() throws Exception {
        // GIVEN
        String token = getAuthToken();
        createStudentInDb(FIRST_NAME, LAST_NAME, EMAIL);
        createStudentInDb("Jean", "Dupont", "jean.dupont@example.com");

        // WHEN - on met l'email du second étudiant sur le premier
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/1")
                        .header("Authorization", "Bearer " + token)
                        .content(buildStudentBody(FIRST_NAME, LAST_NAME, "jean.dupont@example.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                // THEN - unicité violée
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteStudentSuccess() throws Exception {
        // GIVEN
        String token = getAuthToken();
        createStudentInDb(FIRST_NAME, LAST_NAME, EMAIL);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/1")
                        .header("Authorization", "Bearer " + token))
                // THEN - 204 puis plus personne
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteStudentNotFound() throws Exception {
        // GIVEN
        String token = getAuthToken();

        // WHEN - id inexistant
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/999")
                        .header("Authorization", "Bearer " + token))
                // THEN
                .andExpect(status().isBadRequest());
    }

    @Test
    public void accessWithoutToken() throws Exception {
        // WHEN - aucun header Authorization
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                // THEN - 401, la route est protégée par le filtre JWT
                .andExpect(status().isUnauthorized());
    }
}
