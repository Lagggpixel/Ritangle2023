package me.reid.utils;

import me.reid.Main;
import me.reid.data.Board;
import me.reid.data.Pair;
import me.reid.data.Tournament;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Reid Cao
 * <p>
 * Checks that the criteria are met
 */
public class CriteriaUtils {

  public static void checkAllCriteria(Tournament tournament) {
    System.out.println("Checking all criteria");
    checkCriteriaOne(tournament);
    checkCriteriaTwo(tournament);
    checkCriteriaThree(tournament);
    checkCriteriaFour(tournament);
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
            }
            else {
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