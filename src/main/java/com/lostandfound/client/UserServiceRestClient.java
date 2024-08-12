package com.lostandfound.client;

import com.lostandfound.exception.claim.ClaimingUserNotFoundException;
import com.lostandfound.exception.claim.DownstreamServiceConnectionException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
public class UserServiceRestClient {
    private static final String USER_SERVICE_URL = "http://localhost:8090/user-service";
    private final RestClient restClient;

    public UserServiceRestClient() {
        restClient = RestClient.builder()
                .baseUrl(USER_SERVICE_URL)
                .build();
    }

    public String getNameById(Long userId) {
        return restClient.get()
                .uri("/users/"+userId)
                .exchange((request, response) -> {
                    if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                        throw new ClaimingUserNotFoundException("User with id " + userId + " is not found");
                    } else if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
                        return Objects.requireNonNull(response.bodyTo(String.class));
                    } else {
                        throw new DownstreamServiceConnectionException("Error from UserService");
                    }
                });
    }
}
