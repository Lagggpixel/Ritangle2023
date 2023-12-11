package me.reid.utils;

import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;

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
}
