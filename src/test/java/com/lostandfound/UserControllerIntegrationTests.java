package com.lostandfound;

import com.lostandfound.model.LostItem;
import com.lostandfound.repository.ClaimedItemRepository;
import com.lostandfound.repository.LostItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerIntegrationTests extends AbstractIntegrationTest{

    @LocalServerPort
    private Integer port;

    private String BASE_URI;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LostItemRepository lostItemRepository;

    @Autowired
    private ClaimedItemRepository claimedItemRepository;

    // static, all tests share this mySQLContainer container
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(
            "mysql:8.0"
    );

    @BeforeEach
    public void setup() {
        BASE_URI = "http://localhost:" + port + "/api/users";
        LostItem item1 = new LostItem(1L, "Wallet", 2, "Bus");
        LostItem item2 = new LostItem(2L, "Laptop", 3, "Train");
        lostItemRepository.saveAll(List.of(item1, item2));
    }

    @Test
    public void testGetAllLostItems() {
        ResponseEntity<List<LostItem>> response = restTemplate
                .withBasicAuth(username, password)
                .exchange(
                BASE_URI + "/lost-items",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<LostItem>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        assertEquals("Wallet", response.getBody().getFirst().getItemName());
    }

}
