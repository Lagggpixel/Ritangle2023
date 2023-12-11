package me.reid.utils;

import me.reid.data.Board;
import me.reid.data.Pair;
import me.reid.data.Round;
import me.reid.data.Tournament;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;

public class TournamentLoader {

  public static Tournament load(File input) {
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(input);

    Tournament tournament = new Tournament();

    for (String roundKey : yamlConfiguration.getKeys(false)) {
      if (!roundKey.startsWith("Round")) {
        continue;
      }
      ConfigurationSection roundSection = yamlConfiguration.getConfigurationSection(roundKey);
      assert roundSection != null;
      int roundIndex = Integer.parseInt(roundKey.substring(5));
      Round round = new Round(roundIndex);
      for (String boardKey : roundSection.getKeys(false)) {
        if (!boardKey.startsWith("Board") && !boardKey.equals("UpDownFloats")) {
          continue;
        }
        ConfigurationSection boardSection = roundSection.getConfigurationSection(boardKey);
        assert boardSection != null;
        int boardIndex;
        if (boardKey.equals("UpDownFloats")) {
          boardIndex = -1;
        } else {
          boardIndex = Integer.parseInt(boardKey.substring(5));
        }
        Board board = new Board(boardIndex);
        for (String pairKey : boardSection.getKeys(false)) {
          if (!pairKey.startsWith("Pair")) {
            continue;
          }
          ConfigurationSection pairSection = boardSection.getConfigurationSection(pairKey);
          int pairIndex = Integer.parseInt(pairKey.substring(4));
          assert pairSection != null;
          String team1 = pairSection.getString("Team1");
          String team2 = pairSection.getString("Team2");
          int team1Board = pairSection.getInt("Team1Board");
          int team2Board = pairSection.getInt("Team2Board");
          Pair pair = new Pair(team1, team1Board, team2, team2Board);
          board.getPairs().put(pairIndex, pair);
        }
        round.getBoards().put(boardIndex, board);
      }
      tournament.getRounds().put(roundIndex, round);
    }

    return tournament;
  }

}
