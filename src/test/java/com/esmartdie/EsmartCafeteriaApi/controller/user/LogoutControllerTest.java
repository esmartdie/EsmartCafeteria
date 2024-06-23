package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class LogoutControllerTest {

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

    private String adminToken;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.registerParser("text/plain", Parser.TEXT);

        roleRepository.deleteAll();
        userRepository.deleteAll();

        Role adminRole = new Role(null, "ROLE_ADMIN");
        adminRole = roleRepository.save(adminRole);

        Employee admin = new Employee(null, "Admin", "User", "admin@example.com",
                passwordEncoder.encode("password"), true, adminRole, 1L);
        userRepository.save(admin);

        adminToken = obtainToken("admin@example.com", "password");
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
    public void testLogoutWithAuthenticatedUser() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken("admin@example.com", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/logout")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("User logged out successfully."));

        SecurityContextHolder.clearContext();
    }

    @Test
    public void testLogoutWithoutAuthenticatedUser() throws Exception {
        SecurityContextHolder.clearContext();

        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/logout")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(equalTo("No authenticated user to log out."));
    }
}