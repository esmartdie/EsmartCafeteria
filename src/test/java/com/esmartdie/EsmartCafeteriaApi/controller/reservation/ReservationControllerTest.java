package com.esmartdie.EsmartCafeteriaApi.controller.reservation;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.NewReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.ReservationStatusUpdatedDTO;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Reservation;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationRecord;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.ReservationStatus;
import com.esmartdie.EsmartCafeteriaApi.model.reservation.Shift;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRecordRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.reservation.IReservationRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.service.reservation.ReservationRecordService;
import com.esmartdie.EsmartCafeteriaApi.service.reservation.ReservationService;
import com.esmartdie.EsmartCafeteriaApi.utils.DTOConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.parsing.Parser;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class ReservationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IReservationRecordRepository reservationRecordRepository;

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRecordService reservationRecordService;

    @Autowired
    private DTOConverter converter;

    private String userToken, modeToken;

    private Client client;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssured.port = port;
        RestAssured.defaultParser = Parser.JSON;

        roleRepository.deleteAll();
        userRepository.deleteAll();
        reservationRepository.deleteAll();
        reservationRecordRepository.deleteAll();


        Role userRole = new Role(null, "ROLE_USER");
        roleRepository.save(userRole);

        Role modeRole = new Role(null, "ROLE_MODERATOR");
        modeRole = roleRepository.save(modeRole);

        client = new Client(null, "client", "QA", "client@qa.com",
                passwordEncoder.encode("password"), true, userRole);
        userRepository.save(client);

        Employee employee1 = new Employee(null, "John", "Doe", "mode@qa.com",
                passwordEncoder.encode("password"), true, modeRole, 123L);
        userRepository.save(employee1);

        userToken = obtainToken("client@qa.com", "password");
        modeToken = obtainToken("mode@qa.com", "password");

        reservationRecordService.createMonthCalendar(YearMonth.now().plusMonths(1));
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
    public void testCreateReservation() throws Exception {

        ClientDTO clientDTO = converter.createClientDTOFromClient(client);
        LocalDate reservationDate = LocalDate.now().plusMonths(1);
        NewReservationDTO request = new NewReservationDTO(
                clientDTO,
                4,
                reservationDate,
                Shift.NIGHT4
        );

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/users/clients/reservation/create")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("success", equalTo(true))
                .body("message", equalTo("Reservation created successfully"));

    }

    @Test
    public void testGetMyReservations() {

        ClientDTO clientDTO = converter.createClientDTOFromClient(client);
        LocalDate reservationDate = LocalDate.now().plusMonths(1);
        NewReservationDTO request = new NewReservationDTO(
                clientDTO,
                4,
                reservationDate,
                Shift.NIGHT4
        );

        reservationService.createReservation(request);

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/users/clients/reservation/my-reservations")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1));
    }

    @Test
    public void testGetMyActiveReservations() {

        ClientDTO clientDTO = converter.createClientDTOFromClient(client);
        LocalDate reservationDate = LocalDate.now().plusMonths(1);
        NewReservationDTO request = new NewReservationDTO(
                clientDTO,
                4,
                reservationDate,
                Shift.NIGHT4
        );

        reservationService.createReservation(request);

        NewReservationDTO request2 = new NewReservationDTO(
                clientDTO,
                4,
                reservationDate,
                Shift.NIGHT3
        );

        ReservationDTO reservationDTO2 = reservationService.createReservation(request2);
        Reservation reservation2 = reservationRepository.findById(reservationDTO2.getId()).get();
        reservation2.setReservationStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation2);


        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/users/clients/reservation/my-active-reservations")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MODERATOR")
    public void testGetReservationById() {

        ClientDTO clientDTO = converter.createClientDTOFromClient(client);
        LocalDate reservationDate = LocalDate.now().plusMonths(1);
        NewReservationDTO request = new NewReservationDTO(
                clientDTO,
                4,
                reservationDate,
                Shift.NIGHT4
        );

        ReservationDTO reservationDTO = reservationService.createReservation(request);

        Long reservationId = reservationDTO.getId();
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/moderator/reservation/" + reservationId)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @WithMockUser(authorities = "ROLE_MODERATOR")
    public void testGetAllReservationsForDay() {

        ClientDTO clientDTO = converter.createClientDTOFromClient(client);
        LocalDate reservationDate = LocalDate.now().plusMonths(1);
        NewReservationDTO request = new NewReservationDTO(
                clientDTO,
                4,
                reservationDate,
                Shift.NIGHT4
        );
        reservationService.createReservation(request);

        given()
                .contentType(ContentType.JSON)
                .queryParam("date", reservationDate.toString())
                .when()
                .get("/api/moderator/reservation/day")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MODERATOR")
    public void testGetAllReservationsForDayAndShift() {

        ClientDTO clientDTO = converter.createClientDTOFromClient(client);
        LocalDate reservationDate = LocalDate.now().plusMonths(1);
        NewReservationDTO request = new NewReservationDTO(
                clientDTO,
                4,
                reservationDate,
                Shift.NIGHT4
        );
        reservationService.createReservation(request);

        given()
                .contentType(ContentType.JSON)
                .queryParam("date", reservationDate.toString())
                .queryParam("shift", Shift.NIGHT4.toString())
                .when()
                .get("/api/moderator/reservation/day-shift")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void testCancelReservation() {

        ClientDTO clientDTO = converter.createClientDTOFromClient(client);
        LocalDate reservationDate = LocalDate.now().plusMonths(1);
        NewReservationDTO request = new NewReservationDTO(
                clientDTO,
                4,
                reservationDate,
                Shift.NIGHT4
        );

        ReservationDTO reservationDTO = reservationService.createReservation(request);

        Long reservationId = reservationDTO.getId();

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/users/clients/reservation/" + reservationId + "/cancel")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("success", equalTo(true))
                .body("message", equalTo("Reservation cancelled successfully"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MODERATOR")
    public void testUpdateReservationStatus() throws JsonProcessingException {

        ClientDTO clientDTO = converter.createClientDTOFromClient(client);
        LocalDate reservationDate = LocalDate.now().plusMonths(1);
        NewReservationDTO newReservationDTO = new NewReservationDTO(
                clientDTO,
                4,
                reservationDate,
                Shift.NIGHT4
        );

        ReservationDTO reservationDTO = reservationService.createReservation(newReservationDTO);

        Long reservationId = reservationDTO.getId();

        ReservationStatusUpdatedDTO request = new ReservationStatusUpdatedDTO(
                reservationDate,
                LocalTime.now(),
                ReservationStatus.LOST
        );


        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .patch("/api/moderator/reservation/" + reservationId + "/updateStatus")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    @WithMockUser(authorities = "ROLE_MODERATOR")
    public void testUpdateReservationsMassivelyToLoss() {
        LocalDate actionDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        given()
                .contentType(ContentType.JSON)
                .queryParam("actionDate", actionDate.toString())
                .queryParam("currentTime", currentTime.toString())
                .when()
                .put("/api/moderator/reservation/massiveReservationUpdatingToLoss")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("success", equalTo(true))
                .body("message", equalTo("Reservation updated successfully"));
    }
}