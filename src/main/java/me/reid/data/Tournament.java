package me.reid.data;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Tournament {
  /**
   * List of rounds
   * <p>
   * The integer denotes the round index
   */
  private final Map<Integer, Round> rounds;

  public Tournament() {
    rounds = new HashMap<>();
  }
}

