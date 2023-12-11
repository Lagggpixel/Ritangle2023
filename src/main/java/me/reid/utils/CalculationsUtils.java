package me.reid.utils;

import lombok.SneakyThrows;
import me.reid.Main;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CalculationsUtils {

  @SneakyThrows
  public static void calculateFile(String path) {
    File file = new File(path);
    calculateFile(file);
  }

  /**
   * Calculate the values of q_x, q_y, and q_z based on the provided file.
   *
   * @param file The file containing the configuration data in YAML format.
   */
  @SneakyThrows
  public static void calculateFile(File file) {
    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    int q_x = CalculationsUtils.getQ_x(configuration);
    int q_y = CalculationsUtils.getQ_y(configuration);
    float q_z = CalculationsUtils.getQ_z(configuration);
    System.out.println("Output");
    System.out.println("  Qx: " + q_x);
    System.out.println("  Qy: " + q_y);
    System.out.println("  Qz: " + q_z);
    float q = q_z + q_y + q_x;
    System.out.println("  Q: " + q);
  }

  /**
   * Calculates the files in the given directory.
   *
   * @param directory the directory to calculate the files from
   */
  public static void calculateFiles(@NotNull File directory) {
    HashMap<Float, String> games = new HashMap<>();
    if (directory.isDirectory()) {
      for (File file : Objects.requireNonNull(directory.listFiles())) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        int q_x = CalculationsUtils.getQ_x(configuration);
        int q_y = CalculationsUtils.getQ_y(configuration);
        float q_z = CalculationsUtils.getQ_z(configuration);
        float q = q_z + q_y + q_x;
        games.put(q, file.getName());
      }
    }
    games.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByKey())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
        .forEach((k, v) -> {
          System.out.println("File name: " + v + " Q: " + k);
        });
  }

  /**
   * Calculates the value of q_x based on the provided YAML configuration.
   *
   * @param yamlConfiguration the YAML configuration object
   * @return the calculated value of q_x
   */
  public static int getQ_x(@NotNull YamlConfiguration yamlConfiguration) {
    System.out.println("Qx debug");
    int q_x = 0;
    for (String round : yamlConfiguration.getKeys(false)) {
      int roundNumber = Integer.parseInt(String.valueOf(round.charAt(round.length() - 1)));
      System.out.println("  Qx round: " + roundNumber);
      int[] blacks = new int[Main.numberOfTeams];
      int[] whites = new int[Main.numberOfTeams];
      ConfigurationSection roundConfiguration = yamlConfiguration.getConfigurationSection(round);
      assert roundConfiguration != null;
      for (String board : roundConfiguration.getKeys(false)) {
        ConfigurationSection boardConfiguration = roundConfiguration.getConfigurationSection(board);
        assert boardConfiguration != null;
        for (String pair : boardConfiguration.getKeys(false)) {
          ConfigurationSection pairConfiguration = boardConfiguration.getConfigurationSection(pair);
          assert pairConfiguration != null;
          String team1 = pairConfiguration.getString("Team1");
          String team2 = pairConfiguration.getString("Team2");
          incrementTeamCount(team1, blacks);
          incrementTeamCount(team2, whites);
        }
      }
      System.out.println("    Qx blacks array[team]: " + Arrays.toString(blacks));
      System.out.println("    Qx whites array[team]: " + Arrays.toString(whites));

      for (int i = 0; i < Main.numberOfTeams; i++) {
        q_x += Math.abs(blacks[i] - whites[i]);
      }
      System.out.println("    Qx value(cumulative) for round " + roundNumber + ": " + q_x);
    }
    System.out.println("  Qx value: " + q_x);
    return q_x;
  }

  /**
   * Increments the count for a specific team in the teamArray.
   *
   * @param team      the team to increment the count for
   * @param teamArray the array containing the count for each team
   */
  public static void incrementTeamCount(String team, int[] teamArray) {
    String teamStr = String.valueOf(team);
    int teamInt = -1;
    for (Map.Entry<Integer, String> entry : Main.letterIndex.entrySet()) {
      if (entry.getValue().equals(teamStr)) {
        teamInt = entry.getKey();
        break;
      }
    }
    if (teamInt == -1) {
      return;
    }
    teamArray[teamInt - 1]++;
  }

  public static int getQ_y(@NotNull YamlConfiguration yamlConfiguration) {
    System.out.println("Qy debug");
    int q_y = 0;
    int[] teamUpFloats = new int[Main.numberOfTeams];
    int[] teamDownFloats = new int[Main.numberOfTeams];
    for (String round : yamlConfiguration.getKeys(false)) {
      ConfigurationSection roundConfiguration = yamlConfiguration.getConfigurationSection(round);
      assert roundConfiguration != null;
      ConfigurationSection upDownFloatSection = roundConfiguration.getConfigurationSection("UpDownFloats");
      if (upDownFloatSection == null) {
        continue;
      }
      for (String pair : upDownFloatSection.getKeys(false)) {
        ConfigurationSection pairConfiguration = upDownFloatSection.getConfigurationSection(pair);
        assert pairConfiguration != null;
        String team1 = pairConfiguration.getString("Team1");
        String team2 = pairConfiguration.getString("Team2");
        incrementTeamCount(team1, teamUpFloats);
        incrementTeamCount(team2, teamDownFloats);
      }
    }
    System.out.println("  Qy team up floats array[team]: " + Arrays.toString(teamUpFloats));
    System.out.println("  Qy team down floats array[team]: " + Arrays.toString(teamDownFloats));
    for (int i = 0; i < Main.numberOfTeams; i++) {
      q_y += Math.abs(teamUpFloats[i] - teamDownFloats[i]);
    }
    System.out.println("  Qy value: " + q_y);
    return q_y;
  }

  public static float getQ_z(@NotNull YamlConfiguration yamlConfiguration) {
    System.out.println("Qz debug");
    float const_z = ((float) 4) / (Main.numberOfRounds * Main.numberOfPlayers * (Main.numberOfPlayers + 1));
    System.out.println("  Qz constant: " + const_z);

    float q_z = 0;
    int[][] q_z_array_whites_2d = new int[Main.numberOfTeams][Main.numberOfPlayers];
    int[] q_z_array_whites_1d = new int[Main.numberOfTeams];
    for (String round : yamlConfiguration.getKeys(false)) {
      ConfigurationSection roundConfiguration = yamlConfiguration.getConfigurationSection(round);
      assert roundConfiguration != null;
      for (String board : roundConfiguration.getKeys(false)) {
        ConfigurationSection boardConfiguration = roundConfiguration.getConfigurationSection(board);
        assert boardConfiguration != null;
        for (String pair : boardConfiguration.getKeys(false)) {
          ConfigurationSection pairConfiguration = boardConfiguration.getConfigurationSection(pair);
          assert pairConfiguration != null;
          String team1 = pairConfiguration.getString("Team1");
          int team1Board = pairConfiguration.getInt("Team1Board");
          int teamInt = -1;
          for (Map.Entry<Integer, String> entry : Main.letterIndex.entrySet()) {
            if (entry.getValue().equals(team1)) {
              teamInt = entry.getKey();
              break;
            }
          }
          if (teamInt == -1) {
            throw new RuntimeException("Invalid team: " + team1);
          }
          q_z_array_whites_2d[teamInt - 1][team1Board - 1]++;
        }
      }
    }

    for (int i = 0; i < q_z_array_whites_2d.length; i++) {
      for (int j = 0; j < q_z_array_whites_2d[i].length; j++) {
        q_z_array_whites_1d[i] += (j + 1) * q_z_array_whites_2d[i][j];
      }
    }
    for (int i : q_z_array_whites_1d) {
      q_z += Math.abs(1 - const_z * i);
    }
    System.out.println("  Qz 2d array[team][board]: ");
    for (int i = 0; i < q_z_array_whites_2d.length; i++) {
      if (i == 0) {
        System.out.print("    [" + Arrays.toString(q_z_array_whites_2d[i]));
      } else {
        System.out.print("    " + Arrays.toString(q_z_array_whites_2d[i]));
      }
      if (i < q_z_array_whites_2d.length - 1) {
        System.out.println(",");
      } else {
        System.out.println("]");
      }
    }
    System.out.println("  Qz 1d array[team]: " + Arrays.toString(q_z_array_whites_1d));
    System.out.println("  Qz value: " + q_z);
    return q_z;
  }

  public static String getTwoDigitNumber(int team1BoardNumber) {
    if (team1BoardNumber < 10) {
      return "0" + team1BoardNumber;
    } else {
      return String.valueOf(team1BoardNumber);
    }
  }
}
