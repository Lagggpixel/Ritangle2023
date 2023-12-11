package me.reid.data;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Round {
  private final int roundIndex;
  /**
   * List of boards
   * <p>
   * The integer denotes the board index
   */
  private final Map<Integer, Board> boards;

  public Round(int roundIndex) {
    this.roundIndex = roundIndex;
    boards = new HashMap<>();
  }
}
