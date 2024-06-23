package com.esmartdie.EsmartCafeteriaApi.controller.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.YearMonthDTO;
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

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class ReservationRecordControllerTest {


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
    public void testGetReservationRecordsForMonth() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonth().getValue();

        given()
                .contentType(ContentType.JSON)
                .queryParam("year", year)
                .queryParam("month", month)
                .when()
                .get("/api/calendar/empty_spaces_month")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(0));
    }

    @Test
    public void testCreateCalendar() throws Exception {
        YearMonthDTO yearMonthDTO = new YearMonthDTO();
        yearMonthDTO.setYearMonth(generateYearMonth());

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(yearMonthDTO))
                .when()
                .post("/api/calendar/create_month")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("success", equalTo(true))
                .body("message", containsString("Calendar created successfully"));
    }

    private String generateYearMonth(){
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonth().plus(1).getValue();

        return year+"-"+String.format("%02d", month);
    }

}