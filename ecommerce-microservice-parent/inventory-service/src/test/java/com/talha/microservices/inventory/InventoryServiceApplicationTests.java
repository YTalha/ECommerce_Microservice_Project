package com.talha.microservices.inventory;

import com.talha.microservices.inventory.dto.InventoryRequest;
import com.talha.microservices.inventory.dto.InventoryResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");
	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mySQLContainer.start();
	}

	@Test
	void shouldAddInventory() {
		InventoryRequest inventoryRequest = new InventoryRequest("sku1", 100);
		var response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body(inventoryRequest)
				.when()
				.post("/api/inventory")
				.then()
				.log().all()
				.statusCode(201)
				.extract().response().as(InventoryResponse.class);
		assertThat(response.skuCode(), is("sku1"));
		assertThat(response.quantity(), is(100));
	}

	@Test
	void shouldGetAllInventory() {
		List<InventoryResponse> inventoryList = RestAssured.given()
				.when()
				.get("/api/inventory")
				.then()
				.log().all()
				.statusCode(200)
				.extract().response().jsonPath().getList(".", InventoryResponse.class);
		assertThat(inventoryList.size(), greaterThanOrEqualTo(0));

	}

	@Test
	void shouldDeleteInventory() {
		Long id = 1L;
		var response = RestAssured.given()
				.when()
				.delete("/api/inventory/delete/{id}", id)
				.then()
				.log().all()
				.statusCode(200)
				.extract().response().asString();
		assertThat(response, is("Inventory deleted successfully"));
	}
}