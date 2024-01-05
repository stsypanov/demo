package com.example.demo.controller;

import com.example.demo.dto.JokeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JokeControllerTest {
  @Value(value = "${local.server.port}")
  private int port;
  @Autowired
  private TestRestTemplate restTemplate;

  private final ParameterizedTypeReference<List<JokeDto>> responseType = new ParameterizedTypeReference<>() {
  };


  @Test
  void getJokes_expectExceptionForCount101() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getHttpUrl())
            .queryParam("count", 101);

    try {
      restTemplate.exchange(
              builder.build().encode().toUri(),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              responseType);
      fail("Expected to fail");
    } catch (Exception ignored) {
    }
  }

  @Test
  void getJokes_expectExceptionForNegativeCount() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getHttpUrl())
            .queryParam("count", -1);

    try {
      restTemplate.exchange(
              builder.build().encode().toUri(),
              HttpMethod.GET,
              HttpEntity.EMPTY,
              responseType);
      fail("Expected to fail");
    } catch (Exception ignored) {
    }
  }

  @Test
  void getJokes_expectSpecified() {
    int expectedCount = 22;
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getHttpUrl())
            .queryParam("count", expectedCount);

    ResponseEntity<List<JokeDto>> response = restTemplate.exchange(
            builder.build().encode().toUri(),
            HttpMethod.GET,
            HttpEntity.EMPTY,
            responseType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().size()).isEqualTo(expectedCount);
  }

  @Test
  void getJokes_expectDefaultCount() {
    ResponseEntity<List<JokeDto>> response = restTemplate.exchange("/jokes", HttpMethod.GET, HttpEntity.EMPTY, responseType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().size()).isEqualTo(5);
  }

  private String getHttpUrl() {
    return "http://localhost:" + port + "/jokes";
  }

}
