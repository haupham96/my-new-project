package com.example.research.service;

import java.time.Clock;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class CustomClock {

  private final Clock clock;
  private String name;

  public LocalDateTime now(){
    return LocalDateTime.now(clock);
  }

}
