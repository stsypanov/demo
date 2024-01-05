package com.example.demo.client;

import com.example.demo.dto.JokeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "jokeClient", url = "https://official-joke-api.appspot.com")
public interface JokeForeignClient {

  @GetMapping("/random_joke")
  JokeDto takeAJoke();
}
