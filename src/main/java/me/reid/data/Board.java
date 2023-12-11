package me.reid.data;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Board {
  /**
   * If the board index is -1, then it is a up down float pair
   */
  private final int boardIndex;
  /**
   * List of pairs
   * <p>
   * The integer denotes the pair index
   */
  private final Map<Integer, Pair> pairs;

  public Board(int boardIndex) {
    this.boardIndex = boardIndex;
    pairs = new HashMap<>();
  }
}
