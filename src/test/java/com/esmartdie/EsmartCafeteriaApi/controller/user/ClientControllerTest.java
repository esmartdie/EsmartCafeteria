package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.DTOConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class ClientControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DTOConverter converter;

    private String clientToken, moderatorToken;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);

        RestAssured.port = port;

        roleRepository.deleteAll();
        userRepository.deleteAll();

        Role userRole = new Role(null, "ROLE_USER");
        userRole=roleRepository.save(userRole);


        Role modRole = new Role(null, "ROLE_MODERATOR");;
        modRole=roleRepository.save(modRole);

        Client client0 = new Client(null, "client", "QA", "client0@qa.com",
                passwordEncoder.encode("password"), true, userRole );
        client0=userRepository.save(client0);

        Client client1 = new Client(null, "client", "QA", "client1@qa.com",
                passwordEncoder.encode("password"), true, userRole );
        client1=userRepository.save(client1);

        Client client2 = new Client(null, "client", "QA", "client2@qa.com",
                passwordEncoder.encode("password"), true, userRole );
        client2=userRepository.save(client2);

        Client client3 = new Client(null, "client", "QA", "client3@qa.com",
                passwordEncoder.encode("password"), false, userRole );
        client3=userRepository.save(client3);

        Client client4 = new Client(null, "client", "QA", "client4@qa.com",
                passwordEncoder.encode("password"), false, userRole );
        client4=userRepository.save(client4);


        Employee mode = new Employee(null, "moderator", "QA","mode@qa.com",
                passwordEncoder.encode("password"), true, modRole, 1235L);
        mode=userRepository.save(mode);

        moderatorToken=obtainToken("mode@qa.com", "password");

    }

    private String obtainToken(String email, String password) {
        return given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", email)
                .formParam("password", password)
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }

    @Test
    public void testGetAllClientsActive() throws Exception {

        given()
                .header("Authorization", "Bearer " + moderatorToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/moderator/client/active")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(3))
                .body("[0].email", equalTo("client0@qa.com"))
                .body("[1].email", equalTo("client1@qa.com"))
                .body("[2].email", equalTo("client2@qa.com"));
    }

    @Test
    public void testGetAllClientsInActive() throws Exception {

        given()
                .header("Authorization", "Bearer " + moderatorToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/moderator/client/inactive")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2))
                .body("[0].email", equalTo("client3@qa.com"))
                .body("[1].email", equalTo("client4@qa.com"));

    }

    @Test
    public void testUpdateClientStatus() throws Exception {
        Long clientId = userRepository.findByEmail("client2@qa.com").get().getId();

        given()
                .header("Authorization", "Bearer " + moderatorToken)
                .param("isActive", "false")
                .contentType(ContentType.JSON)
                .when()
                .patch("/api/moderator/client/" + clientId + "/updateStatus")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        User updatedClient = userRepository.findById(clientId).orElseThrow();
        assertThat(updatedClient).isNotNull();
        assertThat(updatedClient.getActive()).isFalse();
    }

    @Test
    public void testUpdateClientsStatus() throws Exception {
        List<Client> clients = List.of(
                (Client) userRepository.findByEmail("client3@qa.com").orElseThrow(),
                (Client) userRepository.findByEmail("client4@qa.com").orElseThrow()
        );

        List<ClientDTO> clientDTOList = converter.createClientDTOList(clients);

        String clientsJson = objectMapper.writeValueAsString(clientDTOList);

        given()
                .header("Authorization", "Bearer " + moderatorToken)
                .contentType(ContentType.JSON)
                .body(clientsJson)
                .param("isActive", "true")
                .when()
                .put("/api/moderator/client/updateMassiveStatus")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        clients.forEach(client -> {
            Client updatedClient = (Client) userRepository.findById(client.getId()).orElseThrow();
            assertThat(updatedClient).isNotNull();
            assertThat(updatedClient.getActive()).isTrue();
        });
    }

    @Test
    public void testUpdateClientRating() throws Exception {
        Long clientId = userRepository.findByEmail("client0@qa.com").get().getId();
        String clientJson = objectMapper.writeValueAsString(Map.of("rating", 4.8));
        given()
                .header("Authorization", "Bearer " + moderatorToken)
                .contentType(ContentType.JSON)
                .body(clientJson)
                .when()
                .patch("/api/moderator/client/" + clientId + "/updateRating")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Client updatedClient = (Client)userRepository.findById(clientId).orElseThrow();
        assertThat(updatedClient).isNotNull();
        assertThat(updatedClient.getRating()).isEqualTo(4.8);
    }
}