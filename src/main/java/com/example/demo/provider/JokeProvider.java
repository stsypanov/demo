package com.example.demo.provider;

import com.example.demo.client.JokeSource;
import com.example.demo.dto.JokeDto;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class JokeProvider {
  private final JokeSource jokeSource;
//  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final ExecutorService executor = Executors.newFixedThreadPool(10);

  public List<JokeDto> getJokes(int count) {
    if (count == 0) {
      return List.of();
    }
    if (count == 1) {
      return List.of(jokeSource.takeAJoke());
    }
    if (count <= 10) {
      return getJokesInBatch(count);
    }
    List<JokeDto> jokes = new ArrayList<>();
    for (int i = count; i > 0; i -= 10) {
      int batchSize = Math.min(i, 10);
      jokes.addAll(getJokesInBatch(batchSize));
    }
    return jokes;
  }

  private List<JokeDto> getJokesInBatch(int count) {
    return IntStream.range(0, count)
            .mapToObj(operand -> fetchAJoke())
            .toList()
            .stream()
            .map(this::fromFuture)
            .toList();
  }

  @SneakyThrows
  private JokeDto fromFuture(Future<JokeDto> future) {
    return future.get();
  }

  private Future<JokeDto> fetchAJoke() {
    return executor.submit(jokeSource::takeAJoke);
  }

  @PreDestroy
  @SneakyThrows
  void shutdownExecutor() {
    executor.shutdown();
    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
      executor.shutdownNow();
    }
  }
}
