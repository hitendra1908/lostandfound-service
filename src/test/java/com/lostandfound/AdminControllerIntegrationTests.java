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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminControllerIntegrationTests extends AbstractIntegrationTest{

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LostItemRepository lostItemRepository;

    @Autowired
    private ClaimedItemRepository claimedItemRepository;

    @LocalServerPort
    private Integer port;

    private String BASE_URI;

    // static, all tests share this mySQLContainer container
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(
            "mysql:8.0"
    );

    @BeforeEach
    public void setup() {
        BASE_URI = "http://localhost:" + port + "/api/admin";
    }

    @Test
    public void testUploadLostItems() {

        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", new ClassPathResource("SampleFile_LostAndFound.pdf"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);

        ResponseEntity<String> response = restTemplate
                .withBasicAuth(username, password)
                .exchange(
                BASE_URI + "/upload",
                HttpMethod.POST,
                entity,
                String.class,
                "");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("File uploaded: Number of records parsed and saved in DB = 4", response.getBody());

        //check if data is uploaded in db
        List<LostItem> lostItemsUploaded = lostItemRepository.findAll();
        assertEquals(4, lostItemsUploaded.size());
    }
}
