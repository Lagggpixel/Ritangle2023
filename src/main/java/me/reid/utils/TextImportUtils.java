package me.reid.utils;

import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.util.Scanner;

public class TextImportUtils {

  @SneakyThrows
  public static File loadFromFileOdd(File input, int rounds, int teams, int players) {

    StringBuilder stringBuilder = new StringBuilder();

    Scanner scanner = new Scanner(input);

    while (scanner.hasNextLine()) {
      stringBuilder.append(scanner.nextLine()).append("\n");
    }

    String[] rows = stringBuilder.toString().split("\n");

    String[] games = new String[rounds * teams * players / 2];

    int rowCounter = 0;
    for (String row : rows) {
      String[] extracted = row.split(" ");
      if (extracted.length != 3) {
        continue;
      }
      System.arraycopy(extracted, 0, games, rowCounter * 3, extracted.length);
      rowCounter++;
    }

    scanner.close();

    File output = new File(input.getParent(), FilenameUtils.removeExtension(input.getName()) + "_converted.yml");
    if (output.exists()) {
      if (!output.delete()) {
        throw new RuntimeException("Could not delete file: " + output.getAbsolutePath());
      }
    }
    if (!output.createNewFile()) {
      throw new RuntimeException("Could not create file: " + output.getAbsolutePath());
    }

    int roundCounter = 1;
    int boardCounter = 1;
    int pairCounter = 1;
    int upDownFloatCounter = 1;
    boolean upDownFloat = false;
    int nextUpDownFloat = 1;
    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(output);
    for (String gameString : games) {
      String team1 = String.valueOf(gameString.charAt(0));
      String team2 = String.valueOf(gameString.charAt(1));
      ConfigurationSection roundSection = configuration.getConfigurationSection("Round" + roundCounter);
      if (roundSection == null) {
        roundSection = configuration.createSection("Round" + roundCounter);
      }
      ConfigurationSection boardSection;
      ConfigurationSection pairSection;
      if (!upDownFloat) {
        boardSection = roundSection.getConfigurationSection("Board" + boardCounter);
        if (boardSection == null) {
          boardSection = roundSection.createSection("Board" + boardCounter);
        }
        pairSection = boardSection.getConfigurationSection("Pair" + pairCounter);
        if (pairSection == null) {
          pairSection = boardSection.createSection("Pair" + pairCounter);
        }
        pairSection.set("Team1", team1);
        pairSection.set("Team1Board", boardCounter);
        pairSection.set("Team2", team2);
        pairSection.set("Team2Board", boardCounter);
      }
      else {
        boardSection = roundSection.getConfigurationSection("UpDownFloats");
        if (boardSection == null) {
          boardSection = roundSection.createSection("UpDownFloats");
        }
        pairSection = boardSection.getConfigurationSection("Pair" + upDownFloatCounter);
        if (pairSection == null) {
          pairSection = boardSection.createSection("Pair" + upDownFloatCounter);
        }
        upDownFloatCounter++;
        pairSection.set("Team1", team1);
        pairSection.set("Team1Board", boardCounter);
        pairSection.set("Team2", team2);
        pairSection.set("Team2Board", boardCounter-1);
      }

      roundCounter++;
      if (roundCounter == 4) {
        roundCounter = 1;
        if (upDownFloat) {
          upDownFloat = false;
        } else {
          pairCounter++;
          if (pairCounter == 6) {
            pairCounter = 1;
            boardCounter++;
            nextUpDownFloat--;
            if (nextUpDownFloat==0) {
              upDownFloat = true;
              nextUpDownFloat = 2;
            }
          }
        }
      }
    }
    configuration.save(output);
    return output;
  }
}