package com.example.demo.controller;

import com.example.demo.provider.JokeProvider;
import com.example.demo.dto.JokeDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class JokeController {
  private final JokeProvider jokeProvider;

  @GetMapping("/jokes")
  public ResponseEntity<List<JokeDto>> getJokes(
          @RequestParam(value = "count", defaultValue = "5")
          @Min(value = 0, message = "Укажите число от 0 до 100")
          @Max(value = 100, message = "За один раз можно получить не более 100 шуток.")
          int count
  ) {
    List<JokeDto> jokes = jokeProvider.getJokes(count);
    return ResponseEntity.ok(jokes);
  }
}
