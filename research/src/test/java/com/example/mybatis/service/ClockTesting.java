package com.example.mybatis.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClockTesting {

  @Mock
  private Clock clock;

  @InjectMocks
  private CustomClock customClock;

  private static final ZonedDateTime NOW = ZonedDateTime.of(
      2023, 4, 9,
      11, 11, 11, 0,
      ZoneId.of("UTC+7"));

  @Test
  void testClock() {
    when(clock.getZone()).thenReturn(NOW.getZone());
    when(clock.instant()).thenReturn(NOW.toInstant());

    LocalDateTime expiryDate = LocalDateTime.of(2023, 4, 9, 11, 11, 12);
    boolean isValidExpiryDate = expiryDate.isAfter(customClock.now());
    assertTrue(isValidExpiryDate);
  }

}
