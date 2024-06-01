package com.talha.microservices.product;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mongoDBContainer.start();
	}

	@Test
	void shouldCreateProduct() {
		String requestBody = """
					{
				     "name" : "samsung",
				     "description" : "Samsung is better than Apple",
				     "price" : 500
					}
				""";

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("samsung"))
				.body("description", Matchers.equalTo("Samsung is better than Apple"))
				.body("price", Matchers.equalTo(500));
	}

	@Test
	void shouldCreateProducts() {
		String requestBody = """
                [
                 {
                  "name" : "Samsung",
                  "description" : "Samsung is better than Apple",
                  "price" : 500
                 },
                 {
                  "name" : "Apple",
                  "description" : "Apple is better than Samsung",
                  "price" : 1000
                 }
                ]
            """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product/bulk")
				.then()
				.statusCode(201)
				.body("$.size()", Matchers.equalTo(2));
	}

	@Test
	void shouldGetAllProducts() {
		RestAssured.given()
				.when()
				.get("/api/product")
				.then()
				.statusCode(200)
				.body("$.size()", Matchers.greaterThan(0));
	}

	@Test
	void shouldGetProductsByPriceRange() {
		BigDecimal minPrice = BigDecimal.valueOf(100);
		BigDecimal maxPrice = BigDecimal.valueOf(1000);

		RestAssured.given()
				.queryParam("minPrice", minPrice.toString())
				.queryParam("maxPrice", maxPrice.toString())
				.when()
				.get("/api/product/price")
				.then()
				.statusCode(204); // HTTP 204 "No Content"
	}

	@Test
	void shouldSearchProducts() {
		String keyword = "Samsung"; // Assuming you have products with this keyword in their name or description

		RestAssured.given()
				.queryParam("keyword", keyword)
				.when()
				.get("/api/product/search")
				.then()
				.statusCode(200)
				.body("$.size()", Matchers.greaterThan(0));
	}
}
