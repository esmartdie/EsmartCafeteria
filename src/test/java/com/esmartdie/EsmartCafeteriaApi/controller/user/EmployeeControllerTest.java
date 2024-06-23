package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class EmployeeControllerTest {

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

        roleRepository.deleteAll();
        userRepository.deleteAll();

        Role adminRole = new Role(null, "ROLE_ADMIN");
        adminRole = roleRepository.save(adminRole);

        Role modeRole = new Role(null, "ROLE_MODERATOR");
        modeRole = roleRepository.save(modeRole);

        Employee employee1 = new Employee(null, "John", "Doe", "john.doe@example.com",
                passwordEncoder.encode("password"), true, modeRole, 123L);
        userRepository.save(employee1);

        Employee employee2 = new Employee(null, "Jane", "Doe", "jane.doe@example.com",
                passwordEncoder.encode("password"), true, modeRole, 124L);
        userRepository.save(employee2);

        Employee employee3 = new Employee(null, "Jim", "Beam", "jim.beam@example.com",
                passwordEncoder.encode("password"), false, modeRole, 125L);
        userRepository.save(employee3);

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
    public void testGetAllEmployeesActive() throws Exception {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/admin/employee/active")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(3))
                .body("[0].email", equalTo("john.doe@example.com"))
                .body("[1].email", equalTo("jane.doe@example.com"))
                .body("[2].email", equalTo("admin@example.com"));
    }

    @Test
    public void testGetAllEmployeesInactive() throws Exception {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/admin/employee/inactive")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1))
                .body("[0].email", equalTo("jim.beam@example.com"));
    }

    @Test
    public void testUpdateEmployeeStatus() throws Exception {
        Long employeeId = userRepository.findByEmail("jim.beam@example.com").get().getId();
        given()
                .header("Authorization", "Bearer " + adminToken)
                .param("isActive", "true")
                .contentType(ContentType.JSON)
                .when()
                .patch("/api/admin/employee/" + employeeId + "/updateStatus")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Employee updatedEmployee = (Employee) userRepository.findById(employeeId).orElseThrow();
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getActive()).isTrue();
    }

    @Test
    public void testUpdateEmployeeStatusWithInvalidId() throws Exception {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .param("isActive", "true")
                .contentType(ContentType.JSON)
                .when()
                .patch("/api/admin/employee/-1/updateStatus")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}