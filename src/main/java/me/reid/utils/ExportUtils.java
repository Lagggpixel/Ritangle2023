package me.reid.utils;

import lombok.SneakyThrows;
import me.reid.Main;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ExportUtils {
  @SneakyThrows
  public static void export(File dataFile) {

    File output = new File(dataFile.getParent(), FilenameUtils.removeExtension(dataFile.getName()) + "_export.txt");

    if (output.exists()) {
      if(!output.delete()) {
        throw new RuntimeException("Could not delete file: " + output.getAbsolutePath());
      }
    }
    if (!output.createNewFile()) {
      throw new RuntimeException("Could not create file: " + output.getAbsolutePath());
    }

    FileWriter fileWriter = new FileWriter(output);

    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);

    double q_x = Math.round(CalculationsUtils.getQ_x(yamlConfiguration) * 100.0) / 100.0;
    double q_y = Math.round(CalculationsUtils.getQ_y(yamlConfiguration) * 100.0) / 100.0;
    double q_z = Math.round(CalculationsUtils.getQ_z(yamlConfiguration) * 100.0) / 100.0;

    fileWriter.write(q_x + "\n");
    fileWriter.write(q_y + "\n");
    fileWriter.write(q_z + "\n");

    for (String round : yamlConfiguration.getKeys(false)) {
      int roundNumber = Integer.parseInt(String.valueOf(round.charAt(5)));
      ConfigurationSection roundConfiguration = yamlConfiguration.getConfigurationSection(round);
      assert roundConfiguration != null;
      for (String board : roundConfiguration.getKeys(false)) {
        ConfigurationSection boardConfiguration = roundConfiguration.getConfigurationSection(board);
        assert boardConfiguration != null;
        for (String pair : boardConfiguration.getKeys(false)) {
          ConfigurationSection pairConfiguration = boardConfiguration.getConfigurationSection(pair);
          assert pairConfiguration != null;
          String team1 = pairConfiguration.getString("Team1");
          int team1BoardNumber = pairConfiguration.getInt("Team1Board");
          String team2 = pairConfiguration.getString("Team2");
          int team2BoardNumber = pairConfiguration.getInt("Team2Board");
          // Format: 1,D.01,C.01
          fileWriter.write(roundNumber + "," + team1 + "." + CalculationsUtils.getTwoDigitNumber(team1BoardNumber) + "," + team2 + "." + CalculationsUtils.getTwoDigitNumber(team2BoardNumber) + "\n");
        }
      }
    }

    System.out.println("Qx: " + q_x);
    System.out.println("Qy: " + q_y);
    System.out.println("Qz: " + q_z);

    fileWriter.close();

  }

  public static File importData(File textFile) {
    try {
      File output = new File(textFile.getParent(), FilenameUtils.removeExtension(textFile.getName()) + "_import.yml");
      if (output.exists()) {
        if(!output.delete()) {
          throw new RuntimeException("Could not delete file: " + output.getAbsolutePath());
        }
      }
      if (!output.createNewFile()) {
        throw new RuntimeException("Could not create file: " + output.getAbsolutePath());
      }

      List<String> lines = FileUtils.readLines(textFile, "UTF-8");

      double q_x = Double.parseDouble(lines.get(0));
      double q_y = Double.parseDouble(lines.get(1));
      double q_z = Double.parseDouble(lines.get(2));

      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(output);
      int[][] pairCounters = new int[Main.numberOfRounds][Main.numberOfTeams];
      int[] upDownFloatPairCounters = new int[Main.numberOfRounds];

      for (int i = 3; i < lines.size(); i++) {
        String[] parts = lines.get(i).split(",");
        int roundNumber = Integer.parseInt(parts[0]);
        String[] teams = parts[1].split("\\.");
        String team1 = teams[0];
        int team1BoardNumber = Integer.parseInt(teams[1]);
        teams = parts[2].split("\\.");
        String team2 = teams[0];
        int team2BoardNumber = Integer.parseInt(teams[1]);

        // Build the YAML structure
        String roundKey = "Round" + roundNumber;
        String boardKey;
        String pairKey;
        if (team2BoardNumber == team1BoardNumber) {
          boardKey = "Board" + team1BoardNumber;
          pairKey = "Pair" + pairCounters[roundNumber - 1][team1BoardNumber - 1];
          System.out.println(pairKey);
          pairCounters[roundNumber - 1][team1BoardNumber - 1]++;
        }
        else {
          boardKey = "UpDownFloats";
          pairKey = "Pair" + upDownFloatPairCounters[roundNumber - 1];
          System.out.println(pairKey);
          upDownFloatPairCounters[roundNumber - 1]++;
        }

        ConfigurationSection roundSection = yamlConfiguration.getConfigurationSection(roundKey);
        if (roundSection == null) {
          roundSection = yamlConfiguration.createSection(roundKey);
        }
        ConfigurationSection boardSection = roundSection.getConfigurationSection(boardKey);
        if (boardSection == null) {
          boardSection = roundSection.createSection(boardKey);
        }
        ConfigurationSection pairSection = boardSection.getConfigurationSection(pairKey);
        if (pairSection == null) {
          pairSection = boardSection.createSection(pairKey);
        }
        pairSection.set("Team1", team1);
        pairSection.set("Team1Board", team1BoardNumber);
        pairSection.set("Team2", team2);
        pairSection.set("Team2Board", team2BoardNumber);
      }

      System.out.println(Arrays.deepToString(pairCounters));
      yamlConfiguration.save(output);

      return output;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
