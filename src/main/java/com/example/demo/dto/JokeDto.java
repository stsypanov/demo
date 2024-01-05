package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class JokeDto implements Serializable {
  private long id;
  private String type;
  private String setup;
  private String punchline;
}
