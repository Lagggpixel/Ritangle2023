package me.reid.utils;

import me.reid.Main;
import me.reid.data.Board;
import me.reid.data.Pair;
import me.reid.data.Tournament;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Reid Cao
 * <p>
 * Checks that the criteria are met
 */
public class CriteriaUtils {

  public static void checkAllCriteria(Tournament tournament) {
    boolean allCriteriaMet = true;
    System.out.println("Checking all criteria");
    checkCriteriaOne(tournament);
    checkCriteriaTwo(tournament);
    checkCriteriaThree(tournament);
    checkCriteriaFour(tournament);

    checkCriteriaFive(tournament);
    checkCriteriaSix(tournament);
    checkCriteriaSeven(tournament);
    checkCriteriaEight(tournament);
    if (!checkCriteriaNine(tournament)) {
      allCriteriaMet = false;
    }
    if (allCriteriaMet) {
      System.out.println("All criteria met");
    }
    else {
      System.out.println("Not all criteria met");
    }
  }

  private static boolean checkCriteriaNine(Tournament tournament) {
    boolean criteriaMet = true;
    System.out.println("  Checking criteria nine");
    System.out.println("    Over all the rounds taken together, the number of blacks for each team shall equal the number of whites.");
    int[] whites = new int[Main.numberOfTeams];
    int[] blacks = new int[Main.numberOfTeams];
    tournament.getRounds().forEach((roundIndex, round) -> {
      round.getBoards().forEach((boardIndex, board) -> {
        board.getPairs().forEach((pairIndex, pair) -> {
          AtomicInteger team1Index = new AtomicInteger();
          Main.letterIndex.forEach((k, v) -> {
            if (Objects.equals(pair.team1(), v)) {
              team1Index.set(k);
            }
          });
          AtomicInteger team2Index = new AtomicInteger();
          Main.letterIndex.forEach((k, v) -> {
            if (Objects.equals(pair.team2(), v)) {
              team2Index.set(k);
            }
          });
          whites[team1Index.get() - 1]++;
          blacks[team2Index.get() - 1]++;
        });
      });
    });

    for (int teamIndex = 0; teamIndex < whites.length; teamIndex++) {
        int black = blacks[teamIndex];
        int white = whites[teamIndex];
        if (black != white) {
          System.out.println("Invalid team: " + Main.letterIndex.get(teamIndex + 1) + " black: " + black + " white: " + white);
          criteriaMet = false;
          // System.exit(0);
        }
    }
    if(criteriaMet) {
      System.out.println("  Criteria 9 passed");
    }
    else {
      System.out.println("  Criteria 9 failed");
    }
    return criteriaMet;
  }

  private static void checkCriteriaEight(@NotNull Tournament tournament) {
    System.out.println("  Checking criteria eight");
    System.out.println("    The number of times a player is black shall differ by no more than 1 from the number of times a player is white.");
    int[][] whites = new int[Main.numberOfTeams][Main.numberOfPlayers];
    int[][] blacks = new int[Main.numberOfTeams][Main.numberOfPlayers];
    tournament.getRounds().forEach((roundIndex, round) -> {
      round.getBoards().forEach((boardIndex, board) -> {
        board.getPairs().forEach((pairIndex, pair) -> {
          AtomicInteger team1Index = new AtomicInteger();
          Main.letterIndex.forEach((k, v) -> {
            if (Objects.equals(pair.team1(), v)) {
              team1Index.set(k);
            }
          });
          AtomicInteger team2Index = new AtomicInteger();
          Main.letterIndex.forEach((k, v) -> {
            if (Objects.equals(pair.team2(), v)) {
              team2Index.set(k);
            }
          });
          whites[team1Index.get() - 1][pair.team1Board() - 1]++;
          blacks[team2Index.get() - 1][pair.team2Board() - 1]++;
        });
      });
    });

    for (int teamIndex = 0; teamIndex < whites.length; teamIndex++) {
      for (int playerIndex = 0; playerIndex < whites[teamIndex].length; playerIndex++) {
        int black = blacks[teamIndex][playerIndex];
        int white = whites[teamIndex][playerIndex];
        if (Math.abs(black - white) > 1) {
          System.out.println("Invalid team: " + Main.letterIndex.get(teamIndex + 1) + " player: " + (playerIndex + 1) + " black: " + black + " white: " + white);
          System.exit(0);
        }
      }
    }
    System.out.println("  Criteria 8 passed");
  }

  private static void checkCriteriaSeven(@NotNull Tournament tournament) {
    System.out.println("  Checking criteria seven");
    System.out.println("    All up-floats shall have the white pieces.");

    tournament.getRounds().forEach((roundIndex, round) -> {
      Board board = round.getBoards().get(-1);
      if (board != null) {
        board.getPairs().forEach((pairIndex, pair) -> {
          if (pair.team1Board() <= pair.team2Board()) {
            System.out.println("Invalid pair: " + pair.team1() + " vs " + pair.team2() + " in round " + roundIndex + " board up down floats.");
            System.exit(0);
          }
        });
      }
    });
    System.out.println("  Criteria 7 passed");
  }

  private static void checkCriteriaSix(@NotNull Tournament tournament) {
    System.out.println("  Checking criteria six");
    int constant = (int) (2 + Math.floor((float) Main.numberOfRounds * Main.numberOfPlayers / (2 * Main.numberOfTeams)));
    System.out.println("    Over all the rounds taken together, no team shall have more than " + constant + " up-floats nor more than " + constant + " down-floats.");
    int[] upFloats = new int[Main.numberOfTeams];
    int[] downFloats = new int[Main.numberOfTeams];
    tournament.getRounds().forEach((roundIndex, round) -> {
      Board board = round.getBoards().get(-1);
      if (board != null) {
        board.getPairs().forEach((pairIndex, pair) -> {
          if (pair.team1Board() != pair.team2Board()) {
            AtomicInteger team1Index = new AtomicInteger();
            Main.letterIndex.forEach((k, v) -> {
              if (Objects.equals(pair.team1(), v)) {
                team1Index.set(k);
              }
            });
            AtomicInteger team2Index = new AtomicInteger();
            Main.letterIndex.forEach((k, v) -> {
              if (Objects.equals(pair.team2(), v)) {
                team2Index.set(k);
              }
            });
            upFloats[team1Index.get() - 1]++;
            downFloats[team2Index.get() - 1]++;
            if (upFloats[team1Index.get() - 1] > constant) {
              System.out.println(pair.team1() + " has had more than " + constant + " up floats.");
              System.exit(0);
            }
            if (downFloats[team2Index.get() - 1] > constant) {
              System.out.println(pair.team2() + " has had more than " + constant + " down floats.");
              System.exit(0);
            }
          }
        });
      }
    });
    System.out.println("  Criteria 6 passed");
  }

  private static void checkCriteriaFive(@NotNull Tournament tournament) {
    System.out.println("  Check criteria five");
    System.out.println("    No player shall have more than one up-float or more than one down-float.");
    for (int teamIndex = 0; teamIndex < Main.numberOfTeams; teamIndex++) {
      AtomicReference<AtomicIntegerArray> upFloats = new AtomicReference<>(new AtomicIntegerArray(new int[Main.numberOfPlayers]));
      AtomicReference<AtomicIntegerArray> downFloats = new AtomicReference<>(new AtomicIntegerArray(new int[Main.numberOfPlayers]));
      tournament.getRounds().forEach((roundIndex, round) -> {
        Board board = round.getBoards().get(-1);
        if (board != null) {
          board.getPairs().forEach((pairIndex, pair) -> {
            if (pair.team1Board() != pair.team2Board()) {
              upFloats.get().getAndIncrement(pair.team1Board() - 1);
              downFloats.get().getAndIncrement(pair.team2Board() - 1);
              if (upFloats.get().get(pair.team1Board() - 1) > 1) {
                System.out.println(pair.team1() + "." + pair.team1Board() + " has had more than 1 up floats.");
                System.exit(0);
              }
              if (downFloats.get().get(pair.team2Board() - 1) > 1) {
                System.out.println(pair.team2() + "." + pair.team2Board() + " has had more than 1 down floats.");
                System.exit(0);
              }
            }
          });
        }
        upFloats.set(new AtomicIntegerArray(new int[Main.numberOfPlayers]));
        downFloats.set(new AtomicIntegerArray(new int[Main.numberOfPlayers]));
      });
    }
    System.out.println("  Criteria 5 passed");
  }

  private static void checkCriteriaFour(@NotNull Tournament tournament) {
    System.out.println("  Checking criteria three");
    System.out.println("    Among the players on any given board, there shall be no more than one up-float or down-float per round.");
    tournament.getRounds().forEach((roundIndex, round) -> {
      int[] upFloats = new int[Main.numberOfPlayers];
      int[] downFloats = new int[Main.numberOfPlayers];

      Board board = round.getBoards().get(-1);
      if (board != null) {
        board.getPairs().forEach((pairIndex, pair) -> {
          if (pair.team1Board() != pair.team2Board()) {
            if (pair.team1Board() > pair.team2Board()) {
              upFloats[pair.team1Board() - 1]++;
              downFloats[pair.team2Board() - 1]++;
              if (downFloats[pair.team2Board() - 1] > 1) {
                System.out.println(pair.team2Board() + " has had more than 1 down floats in round" + roundIndex);
                System.exit(0);
              } else if (upFloats[pair.team1Board() - 1] > 1) {
                System.out.println(pair.team1Board() + " has had more than 1 up floats in round" + roundIndex);
                System.exit(0);
              }
            } else {
              upFloats[pair.team2Board() - 1]++;
              downFloats[pair.team1Board() - 1]++;
              if (downFloats[pair.team1Board() - 1] > 1) {
                System.out.println(pair.team1Board() + " has had more than 1 down floats in round" + roundIndex);
                System.exit(0);
              } else if (upFloats[pair.team2Board() - 1] > 1) {
                System.out.println(pair.team2Board() + " has had more than 1 up floats in round" + roundIndex);
                System.exit(0);
              }
            }
          }
        });
      }
    });
    System.out.println("  Criteria 4 passed");
  }

  private static void checkCriteriaThree(Tournament tournament) {
    System.out.println("  Checking criteria three");
    System.out.println("    The number of times that each team plays each of the others");

    double maxNumberOfPlay = Math.ceil((float) Main.numberOfRounds * Main.numberOfPlayers / (Main.numberOfTeams - 1));
    double minNumberOfPlay = Math.floor((float) Main.numberOfRounds * Main.numberOfPlayers / (Main.numberOfTeams - 1));
    ;

    System.out.println("    Maximum value: " + maxNumberOfPlay);
    System.out.println("    Minimum value: " + minNumberOfPlay);

    for (int i = 0; i < Main.numberOfTeams; i++) {
      int[] numberOfPlay = new int[Main.numberOfTeams];
      String teamName = Main.letterIndex.get(i + 1);

      tournament.getRounds().forEach((roundIndex, round) -> {
        round.getBoards().forEach((boardIndex, board) -> {
          board.getPairs().forEach((pairIndex, pair) -> {
            if (Objects.equals(pair.team1(), teamName)) {
              AtomicInteger team2Index = new AtomicInteger();
              Main.letterIndex.forEach((k, v) -> {
                if (Objects.equals(pair.team2(), v)) {
                  team2Index.set(k);
                }
              });
              numberOfPlay[team2Index.get() - 1]++;
            } else if (Objects.equals(pair.team2(), teamName)) {
              AtomicInteger team1Index = new AtomicInteger();
              Main.letterIndex.forEach((k, v) -> {
                if (Objects.equals(pair.team1(), v)) {
                  team1Index.set(k);
                }
              });
              numberOfPlay[team1Index.get() - 1]++;
            }
          });
        });
      });
      for (int teamIndex = 0; teamIndex < numberOfPlay.length; teamIndex++) {
        if (teamIndex == i) continue;
        if (numberOfPlay[teamIndex] != maxNumberOfPlay && numberOfPlay[teamIndex] != minNumberOfPlay) {
          System.out.println("Invalid number of play by team " + teamName + " with team " + Main.letterIndex.get(teamIndex + 1) + " with value of " + numberOfPlay[teamIndex]);
          System.exit(0);
        }
      }
    }

    System.out.println("  Criteria 3 passed");
  }

  private static void checkCriteriaTwo(@NotNull Tournament tournament) {
    System.out.println("  Checking criteria 2");
    System.out.println("    Nobody may play against a team-mate.");
    tournament.getRounds().forEach((roundIndex, round) -> {
      round.getBoards().forEach((boardIndex, board) -> {
        board.getPairs().forEach((pairIndex, pair) -> {
          if (Objects.equals(pair.team1(), pair.team2())) {
            System.out.println("Invalid pair: " + pair.team1() + " vs " + pair.team2() + " in round " + roundIndex + " board " + boardIndex);
          }
        });
      });
    });
    System.out.println("  Criteria 2 passed");
  }

  private static void checkCriteriaOne(@NotNull Tournament tournament) {
    System.out.println("  Checking criteria 1");
    System.out.println("    Nobody may play the same opponent twice, nor twice against opponents from the same team.");
    @SuppressWarnings("unchecked")
    ArrayList<Pair>[] existingPairs = (ArrayList<Pair>[]) new ArrayList[Main.numberOfPlayers];
    tournament.getRounds().forEach((roundIndex, round) -> {

      round.getBoards().forEach((boardIndex, board) -> {

        board.getPairs().forEach((pairIndex, pair) -> {
          // Not an up-float or down-float
          if (pair.team1Board() == pair.team2Board()) {
            // Checks in the board index existing pairs
            if (existingPairs[boardIndex - 1] != null) {
              existingPairs[boardIndex - 1].forEach(existingPair -> {
                if ((Objects.equals(existingPair.team1(), pair.team1())
                    && Objects.equals(existingPair.team2(), pair.team2()))
                    || (Objects.equals(existingPair.team1(), pair.team2())
                    && Objects.equals(existingPair.team2(), pair.team1()))
                    || (Objects.equals(pair.team1(), pair.team2()))) {
                  System.out.println("Invalid pair: " + pair.team1() + " vs " + pair.team2() + " in round " + roundIndex + " board " + boardIndex);
                  System.out.println("Existing pair: " + existingPair.team1() + " vs " + existingPair.team2() + " in round " + roundIndex + " board " + boardIndex);
                  System.exit(0);
                }
              });
              existingPairs[boardIndex - 1].add(pair);
            } else {
              existingPairs[boardIndex - 1] = new ArrayList<>();
              existingPairs[boardIndex - 1].add(pair);
            }
          }
          // An up-float or down-float
          else {
            // Checks in the team 1 board existing pairs
            if (existingPairs[pair.team1Board() - 1] != null) {
              existingPairs[pair.team1Board() - 1].forEach(existingPair -> {
                if ((Objects.equals(existingPair.team1(), pair.team1())
                    && existingPair.team1Board() == pair.team1Board()
                    && Objects.equals(existingPair.team2(), pair.team2())
                    && existingPair.team2Board() == pair.team2Board())
                    || (Objects.equals(existingPair.team1(), pair.team2())
                    && existingPair.team1Board() == pair.team2Board()
                    && Objects.equals(existingPair.team2(), pair.team1())
                    && existingPair.team2Board() == pair.team1Board())) {
                  System.out.println("Invalid pair: " + pair.team1() + " vs " + pair.team2() + " in round " + roundIndex + " board " + boardIndex);
                  System.out.println("Existing pair: " + existingPair.team1() + " vs " + existingPair.team2() + " in round " + roundIndex + " board " + boardIndex);
                  System.out.println(1);
                  System.exit(0);
                }
              });
            } else {
              existingPairs[pair.team1Board() - 1] = new ArrayList<>();
            }
            existingPairs[pair.team1Board() - 1].add(pair);
            // Checks in the team 2 board existing pairs
            if (existingPairs[pair.team2Board() - 1] != null) {
              existingPairs[pair.team2Board() - 1].forEach(existingPair -> {
                if ((Objects.equals(existingPair.team1(), pair.team1())
                    && existingPair.team1Board() == pair.team1Board()
                    && Objects.equals(existingPair.team2(), pair.team2())
                    && existingPair.team2Board() == pair.team2Board())
                    || (Objects.equals(existingPair.team1(), pair.team2())
                    && existingPair.team1Board() == pair.team2Board()
                    && Objects.equals(existingPair.team2(), pair.team1())
                    && existingPair.team2Board() == pair.team1Board())) {
                  System.out.println("Invalid pair: " + pair.team1() + " vs " + pair.team2() + " in round " + roundIndex + " board " + boardIndex);
                  System.out.println("Existing pair: " + existingPair.team1() + " vs " + existingPair.team2() + " in round " + roundIndex + " board " + boardIndex);
                  System.out.println(2);
                  System.exit(0);
                }
              });

            } else {
              existingPairs[pair.team2Board() - 1] = new ArrayList<>();
            }
            existingPairs[pair.team2Board() - 1].add(pair);
          }

        });
      });

    });
    System.out.println("  Criteria 1 passed");
  }
}