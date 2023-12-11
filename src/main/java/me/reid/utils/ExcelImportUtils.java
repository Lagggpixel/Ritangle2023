package me.reid.utils;

import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExcelImportUtils {

  public static List<File> loadFromExcel(String input, int rounds, int teams, int players) {
    return loadFromExcel(new File(input), rounds, teams, players);
  }

  public static List<File> loadFromExcel(File input, int rounds, int teams, int players) {
    if (teams % 2 == 1) {
      return loadFromExcelOdd(input, rounds, teams, players);
    }
    else {
      return loadFromExcelEven(input, rounds, teams, players);
    }
  }

  @SneakyThrows
  public static @NotNull List<File> loadFromExcelOdd(File input, int rounds, int teams, int players) {

    List<File> outputPaths = new ArrayList<>();
    File directoryFile = initOutputDirectoryFile(input);

    StringBuilder game;
    StringBuilder upDownFloats;

    int nextUpDownFloat = 0;
    FileInputStream fileInputStream = new FileInputStream(input);
    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
    int numberOfSheets = workbook.getNumberOfSheets();
    for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
      game = new StringBuilder();
      upDownFloats = new StringBuilder();
      XSSFSheet currentSheet = workbook.getSheetAt(sheetIndex);
      //<editor-fold desc="Extracting Data">
      for (int j = 2; j < rounds + 2; j++) {
        for (int i = 2; i < players + (players / 2) + 2; i++) {
          XSSFRow row = currentSheet.getRow(i);
          XSSFCell cell = row.getCell(j);
          game.append(cell.toString()).append("\n");
          if (nextUpDownFloat == 0) {
            i++;
            row = currentSheet.getRow(i);
            cell = row.getCell(j);
            upDownFloats.append(cell.toString()).append("\n");
            nextUpDownFloat = 1;
          } else {
            nextUpDownFloat--;
          }
        }
      }
      //</editor-fold>

      String[] gamesArray = game.toString().split("\n");
      String[] upDownFloatsArray = upDownFloats.toString().split("\n");
      int roundCounter = 1;
      int boardCounter = 1;
      int pairCounter = 1;

      File currentOutput = new File(directoryFile, workbook.getSheetName(sheetIndex) + ".yml");
      if (currentOutput.exists()) {
        if (!(currentOutput.delete())) {
          throw new RuntimeException("Failed to delete file: " + currentOutput.getName());
        }
      }
      if (!currentOutput.createNewFile()) {
        throw new RuntimeException("Error: Failed to create file: " + currentOutput.getName());
      }

      YamlConfiguration configuration = YamlConfiguration.loadConfiguration(currentOutput);
      for (String gameString : gamesArray) {
        String team1 = String.valueOf(gameString.charAt(0));
        String team2 = String.valueOf(gameString.charAt(1));
        ConfigurationSection roundSection = configuration.getConfigurationSection("Round" + roundCounter);
        if (roundSection == null) {
          roundSection = configuration.createSection("Round" + roundCounter);
        }
        ConfigurationSection boardSection = roundSection.getConfigurationSection("Board" + boardCounter);
        if (boardSection == null) {
          boardSection = roundSection.createSection("Board" + boardCounter);
        }
        ConfigurationSection pairSection = boardSection.getConfigurationSection("Pair" + pairCounter);
        if (pairSection == null) {
          pairSection = boardSection.createSection("Pair" + pairCounter);
        }
        pairSection.set("Team1", team1);
        pairSection.set("Team1Board", boardCounter);
        pairSection.set("Team2", team2);
        pairSection.set("Team2Board", boardCounter);

        pairCounter++;

        if (pairCounter > teams / 2) {
          pairCounter = 1;
          boardCounter++;
          if (boardCounter > players) {
            boardCounter = 1;
            roundCounter++;
          }
        }
      }
      if (!(roundCounter == rounds + 1 && boardCounter == 1 && pairCounter == 1)) {
        System.out.println("Error, rounds overflowed.");
      }

      roundCounter = 1;
      pairCounter = 1;
      boardCounter = 1;

      for (String gameString : upDownFloatsArray) {
        String team1 = String.valueOf(gameString.charAt(0));
        String team2 = String.valueOf(gameString.charAt(1));
        ConfigurationSection roundSection = configuration.getConfigurationSection("Round" + roundCounter);
        if (roundSection == null) {
          roundSection = configuration.createSection("Round" + roundCounter);
        }
        ConfigurationSection upDownFloatsSection = roundSection.getConfigurationSection("UpDownFloats");
        if (upDownFloatsSection == null) {
          upDownFloatsSection = roundSection.createSection("UpDownFloats");
        }
        ConfigurationSection pairSection = upDownFloatsSection.getConfigurationSection("Pair" + pairCounter);
        if (pairSection == null) {
          pairSection = upDownFloatsSection.createSection("Pair" + pairCounter);
        }
        pairSection.set("Team2", team2);
        pairSection.set("Team2Board", boardCounter);
        boardCounter++;
        pairSection.set("Team1", team1);
        pairSection.set("Team1Board", boardCounter);
        boardCounter++;

        pairCounter++;


        if (pairCounter > Math.floor((double) teams / 2)) {
          pairCounter = 1;
          roundCounter++;
          boardCounter = 1;
        }
      }
      configuration.save(currentOutput);
      outputPaths.add(currentOutput);
    }
    fileInputStream.close();

    return outputPaths;
  }

  @NotNull
  private static File initOutputDirectoryFile(@NotNull File input) {
    String directory = FilenameUtils.removeExtension(input.getPath());
    File directoryFile = new File(directory);

    if (directoryFile.exists()) {
      if (directoryFile.isDirectory()) {
        for (File file : Objects.requireNonNull(directoryFile.listFiles())) {
          if (!(file.delete())) {
            throw new RuntimeException("Failed to delete file: " + file.getName() + " in directory: " + directoryFile.getName());
          }
        }
        if (!(directoryFile.delete())) {
          throw new RuntimeException("Failed to delete directory: " + directoryFile.getName());
        }
      }
      else {
        if (!(directoryFile.delete())) {
          throw new RuntimeException("Failed to delete file: " + directoryFile.getName());
        }
      }
    }
    if (!directoryFile.mkdir()) {
      throw new RuntimeException("Failed to create directory.");
    }
    return directoryFile;
  }

  @SneakyThrows
  public static @NotNull List<File> loadFromExcelEven(File input, int rounds, int teams, int players) {

    List<File> outputPaths = new ArrayList<>();
    File directoryFile = initOutputDirectoryFile(input);

    StringBuilder game;

    FileInputStream fileInputStream = new FileInputStream(input);
    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
    int numberOfSheets = workbook.getNumberOfSheets();
    for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
      game = new StringBuilder();
      XSSFSheet currentSheet = workbook.getSheetAt(sheetIndex);
      //<editor-fold desc="Extracting Data">
      for (int j = 2; j < rounds + 2; j++) {
        for (int i = 2; i < players + 2; i++) {
          XSSFRow row = currentSheet.getRow(i);
          XSSFCell cell = row.getCell(j);
          game.append(cell.toString()).append("\n");
        }
      }
      //</editor-fold>

      String[] gamesArray = game.toString().split("\n");
      int roundCounter = 1;
      int boardCounter = 1;
      int pairCounter = 1;

      File currentOutput = new File(directoryFile, workbook.getSheetName(sheetIndex) + ".yml");
      if (currentOutput.exists()) {
        if (!(currentOutput.delete())) {
          throw new RuntimeException("Failed to delete file: " + currentOutput.getName());
        }
      }
      if (!currentOutput.createNewFile()) {
        throw new RuntimeException("Error: Failed to create file: " + currentOutput.getName());
      }

      YamlConfiguration configuration = YamlConfiguration.loadConfiguration(currentOutput);
      for (String gameString : gamesArray) {
        String team1 = String.valueOf(gameString.charAt(0));
        String team2 = String.valueOf(gameString.charAt(1));
        ConfigurationSection roundSection = configuration.getConfigurationSection("Round" + roundCounter);
        if (roundSection == null) {
          roundSection = configuration.createSection("Round" + roundCounter);
        }
        ConfigurationSection boardSection = roundSection.getConfigurationSection("Board" + boardCounter);
        if (boardSection == null) {
          boardSection = roundSection.createSection("Board" + boardCounter);
        }
        ConfigurationSection pairSection = boardSection.getConfigurationSection("Pair" + pairCounter);
        if (pairSection == null) {
          pairSection = boardSection.createSection("Pair" + pairCounter);
        }
        pairSection.set("Team1", team1);
        pairSection.set("Team1Board", boardCounter);
        pairSection.set("Team2", team2);
        pairSection.set("Team2Board", boardCounter);

        pairCounter++;

        if (pairCounter > teams / 2) {
          pairCounter = 1;
          boardCounter++;
          if (boardCounter > players) {
            boardCounter = 1;
            roundCounter++;
          }
        }
      }
      if (!(roundCounter == rounds + 1 && boardCounter == 1 && pairCounter == 1)) {
        System.out.println("Error, rounds overflowed.");
      }

      configuration.save(currentOutput);
      outputPaths.add(currentOutput);
    }
    fileInputStream.close();

    return outputPaths;
  }
}

