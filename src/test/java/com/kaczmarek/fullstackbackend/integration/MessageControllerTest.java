package com.kaczmarek.fullstackbackend.integration;

import com.kaczmarek.fullstack.generated.model.MessageDto;
import com.kaczmarek.fullstack.generated.model.NewMessageDto;
import com.kaczmarek.fullstackbackend.FullstackBackendApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = FullstackBackendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MessageControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        baseUrl = "http://localhost:" + port + "/v1/api/messages";

        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM message");
        }
    }

    @Nested
    class GetMessagesTest {

        @Test
        void shouldReturnEmptyListWhenNoMessages() {
            var response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull().isEmpty();
        }
    }

    @Nested
    class AddMessageTest {

        @Test
        void shouldAddMessageAndReturnDto() {
            var newMessageDto = new NewMessageDto();
            newMessageDto.setContent("<b>Integration Test</b>");

            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            var request = new HttpEntity<>(newMessageDto, headers);

            var response = restTemplate.postForEntity(baseUrl, request, MessageDto.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            var body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getContent()).contains("&lt;b&gt;Integration Test&lt;/b&gt;");
            assertThat(body.getId()).isNotNull();
        }
    }
}