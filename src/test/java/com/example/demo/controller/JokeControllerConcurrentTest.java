package com.example.demo.controller;

import com.example.demo.dto.JokeDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.StopWatch;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JokeControllerConcurrentTest {
  @Value(value = "${local.server.port}")
  private int port;
  @Autowired
  private TestRestTemplate restTemplate;

  private final ParameterizedTypeReference<List<JokeDto>> responseType = new ParameterizedTypeReference<>() {
  };

  @ParameterizedTest
  @ValueSource(ints = {2, 4, 6, 8})
  void getJokesWithSimultaneousRequests(int simultaneousRequestCount) throws Exception {
    var latch = new CountDownLatch(1);
    var futures = new CompletableFuture<?>[simultaneousRequestCount];
    for (int i = 0; i < simultaneousRequestCount; i++) {
      futures[i] = CompletableFuture.runAsync(() -> sendRequest(latch));
    }
    latch.countDown();
    CompletableFuture.allOf(futures).get();
  }

  private void sendRequest(CountDownLatch latch) {
    waitOnLatch(latch);
    log.info("Sending request to server");
    var uriComponents = UriComponentsBuilder.fromHttpUrl(getHttpUrl())
            .queryParam("count", 15)
            .build();
    var stopWatch = new StopWatch();
    stopWatch.start();
    var response = restTemplate.exchange(
            uriComponents.encode().toUri(),
            HttpMethod.GET,
            HttpEntity.EMPTY,
            responseType);
    stopWatch.stop();
    log.info("Request took {} millis", stopWatch.getTotalTimeMillis());
    assertTrue(response.getStatusCode().is2xxSuccessful());
  }

  @SneakyThrows
  private static void waitOnLatch(CountDownLatch latch) {
    latch.await();
  }

  private String getHttpUrl() {
    return "http://localhost:" + port + "/jokes";
  }
}
