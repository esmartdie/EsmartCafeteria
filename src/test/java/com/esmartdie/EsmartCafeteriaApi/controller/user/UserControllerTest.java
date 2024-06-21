package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.NewClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.UpdateClientDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

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

    private String clientToken, adminToken, moderatorToken;



    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);

        RestAssured.port = port;

        roleRepository.deleteAll();
        userRepository.deleteAll();

        Role userRole = new Role(null, "ROLE_USER");
        userRole=roleRepository.save(userRole);

        Role adminRole = new Role(null, "ROLE_ADMIN");;
        adminRole=roleRepository.save(adminRole);

        Role modRole = new Role(null, "ROLE_MODERATOR");;
        modRole=roleRepository.save(modRole);

        Client client = new Client(null, "client", "QA", "client@qa.com",
                passwordEncoder.encode("password"), true, userRole );
        client=userRepository.save(client);

        Employee admin = new Employee(null, "admin", "QA", "admin@qa.com",
                passwordEncoder.encode("password"), true, adminRole, 1234L);
        admin=userRepository.save(admin);

        Employee mode = new Employee(null, "moderator", "QA","mode@qa.com",
                passwordEncoder.encode("password"), true, modRole, 1235L);
        mode=userRepository.save(mode);

        clientToken = obtainToken("client@qa.com", "password");
        adminToken = obtainToken("admin@qa.com", "password");
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
    public void testCreateClient() throws Exception {
        NewClientDTO clientDTO = new NewClientDTO("client", "QA", "newclient@qa.com",
                "password", true);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(clientDTO)
                .when()
                .post("/api/signup")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("success", equalTo(true))
                .body("message", equalTo("User created successfully"))
                .body("data.id", notNullValue())
                .body("data.name", equalTo("client"))
                .body("data.lastName", equalTo("QA"))
                .body("data.email", equalTo("newclient@qa.com"))
                .body("data.rating", equalTo(5.0f))
                .body("data.active", equalTo(true));
    }

    @Test
    public void testCreateClient_ManageMissingInput() throws Exception {
        NewClientDTO clientDTO = new NewClientDTO();

        MockMvcResponse  response=  given()
                .contentType(ContentType.JSON)
                .body(clientDTO)
                .when()
                .post("/api/signup")
                .andReturn();

        System.out.println("Response Body:");
        System.out.println(response.getBody().asString());

        response.then()
                .statusCode(400)
                .body("lastName", equalTo("Last name is required"))
                .body("password", equalTo("Password is required"))
                .body("name", equalTo("Name is required"))
                .body("active", equalTo("must be true"))
                .body("email", equalTo("Email is required"));
    }

    @Test
    public void testCreateClient_ManageWrongInput() throws Exception {
        NewClientDTO clientDTO = new NewClientDTO("a", "b", "kqas@asdaw@asda","123", false);

        MockMvcResponse  response=  given()
                .contentType(ContentType.JSON)
                .body(clientDTO)
                .when()
                .post("/api/signup")
                .andReturn();

        System.out.println("Response Body:");
        System.out.println(response.getBody().asString());

        response.then()
                .statusCode(400)
                .body("lastName", equalTo("Last name must be between 2 and 50 characters"))
                .body("password", equalTo("Password must be at least 8 characters long"))
                .body("name", equalTo("Name must be between 2 and 50 characters"))
                .body("active", equalTo("must be true"))
                .body("email", equalTo("Email should be valid"));
    }

    @Test
    public void testCreateEmployee() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO("employee2","QA","newemployee@qa.com",
                "password", true, 1236L  );

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(employeeDTO)
                .when()
                .post("/api/admin/employee/create")
                .then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("message", equalTo("Employee created successfully"))
                .body("data.id", notNullValue())
                .body("data.name", equalTo("employee2"))
                .body("data.lastName", equalTo("QA"))
                .body("data.email", equalTo("newemployee@qa.com"))
                .body("data.employee_id", equalTo(1236))
                .body("data.active", equalTo(true));

    }

    @Test
    public void testCreateEmployee_ManageMissingInput() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO() ;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(employeeDTO)
                .when()
                .post("/api/admin/employee/create")
                .then()
                .statusCode(400)
                .body("lastName", equalTo("Last name is required"))
                .body("password", equalTo("Password is required"))
                .body("name", equalTo("Name is required"))
                .body("active", equalTo("must be true"))
                .body("email", equalTo("Email is required"))
                .body("employee_id", equalTo("Employee ID is required"));

    }

    @Test
    public void testCreateEmployee_ManageWrongInput() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO("a", "b", "kqas@asdaw@asda","123", false, null) ;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(employeeDTO)
                .when()
                .post("/api/admin/employee/create")
                .then()
                .statusCode(400)
                .body("lastName", equalTo("Last name must be between 2 and 50 characters"))
                .body("password", equalTo("Password must be between 8 and 20 characters long"))
                .body("name", equalTo("Name must be between 2 and 50 characters"))
                .body("active", equalTo("must be true"))
                .body("email", equalTo("Email should be valid"))
                .body("employee_id", equalTo("Employee ID is required"));
    }

    @Test
    public void testCreateEmployee_NonAdmin_Moderator() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO("employee2","QA","newemployee@qa.com",
                "password", true, 1236L  );

        given()
                .header("Authorization", "Bearer " + moderatorToken)
                .contentType(ContentType.JSON)
                .body(employeeDTO)
                .when()
                .post("/api/admin/employee/create")
                .then()
                .statusCode(403);
    }

    @Test
    public void testCreateEmployee_NonAdmin_User() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO("employee2","QA","newemployee@qa.com",
                "password", true, 1236L  );

        given()
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .body(employeeDTO)
                .when()
                .post("/api/admin/employee/create")
                .then()
                .statusCode(403);
    }

    @Test
    public void testGetClientInfo() throws Exception {
        Long clientId = userRepository.findByEmail("client@qa.com").get().getId();

        given()
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("/api/users/client/{id}", clientId.toString())
                .then()
                .statusCode(200)
                .body("email", equalTo("client@qa.com"))
                .body("name", equalTo("client"))
                .body("lastName", equalTo("QA"))
                .body("active", equalTo(true))
                .body("rating", equalTo(5.0F));
    }

    @Test
    public void testGetClientInfo_WrongID() throws Exception {

        Long clientId = 999L;

        given()
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("/api/users/client/{id}", clientId.toString())
                .then()
                .statusCode(404)
                .body(equalTo("User not found with id: 999"));

    }

    @Test
    public void testInvalidClientId() throws Exception {

        given()
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("/api/users/client/{id}", "-1")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGetEmployeeInfo_Admin() throws Exception {
        Long employeeID = userRepository.findByEmail("admin@qa.com").get().getId();

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/admin/employee/{id}", employeeID.toString())
                .then()
                .statusCode(200)
                .body("email", equalTo("admin@qa.com"))
                .body("name", equalTo("admin"))
                .body("lastName", equalTo("QA"))
                .body("active", equalTo(true))
                .body("employee_id", equalTo(1234));
    }

    @Test
    public void testGetEmployeeInfo_Moderator() throws Exception {
        Long employeeID = userRepository.findByEmail("mode@qa.com").get().getId();

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/admin/employee/{id}", employeeID.toString())
                .then()
                .statusCode(200)
                .body("email", equalTo("mode@qa.com"))
                .body("name", equalTo("moderator"))
                .body("lastName", equalTo("QA"))
                .body("active", equalTo(true))
                .body("employee_id", equalTo(1235));
    }

    @Test
    public void testUpdateClient() throws Exception {
        Long clientId = userRepository.findByEmail("client@qa.com").get().getId();

        UpdateClientDTO updateClientDTO = new UpdateClientDTO();
        updateClientDTO.setName("UpdatedName");
        updateClientDTO.setLastName("UpdatedLastName");
        updateClientDTO.setEmail("updatedclient@qa.com");

        given()
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .body(updateClientDTO)
                .when()
                .patch("/api/users/client/{id}/update", clientId.toString())
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("/api/users/client/{id}", clientId.toString())
                .then()
                .statusCode(200)
                .body("email", equalTo("updatedclient@qa.com"))
                .body("name", equalTo("UpdatedName"))
                .body("lastName", equalTo("UpdatedLastName"));
    }


}
