package com.esmartdie.EsmartCafeteriaApi.controller.user;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @BeforeAll
    public static void setup() {

        RestAssured.baseURI = "http://localhost:8080/api";
    }

    @Test
    public void testCreateClient() {

        String clientJson = """
        {
            "name": "Jane",
            "lastName": "Doe",
            "email": "jane.doe@example.com",
            "password": "password123",
            "active": true
        }
        """;


        Response response = given()
                .contentType(ContentType.JSON)
                .body(clientJson)
                .when()
                .post("/users/client/create")
                .then()
                .statusCode(201)
                .body(containsString("created successfully"))
                .extract()
                .response();


        String email = "jane.doe@example.com";
        given()
                .contentType(ContentType.JSON)
                .queryParam("email", email)
                .when()
                .get("/users/client")
                .then()
                .statusCode(200)
                .body("name", equalTo("Jane"))
                .body("lastName", equalTo("Doe"))
                .body("email", equalTo(email))
                .body("active", equalTo(true));
    }

}