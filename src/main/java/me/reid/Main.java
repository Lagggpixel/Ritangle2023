package me.reid;

import lombok.SneakyThrows;
import me.reid.data.Tournament;
import me.reid.utils.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Main {

  public static final Map<Integer, String> letterIndex = new HashMap<>();

  public static final int numberOfTeams = 11;
  public static final int numberOfPlayers = 10;
  public static final int numberOfRounds = 3;

  @SneakyThrows
  public static void main(String[] args) {

    initLetters();

    new GenerationUtils(8, 0);

    /*
    int band = 4;

    Thread thread1 = new Thread(() -> {
      new GenerationUtils(band, 0);
    });
    thread1.start();


    Thread thread2 = new Thread(() -> {
      new GenerationUtils(band, 1);
    });
    thread2.start();

    Thread thread3 = new Thread(() -> {
      new GenerationUtils(band, 2);
    });
    thread3.start();

    Thread thread4 = new Thread(() -> {
      new GenerationUtils(band, 3);
    });
    thread4.start();

    Thread thread5 = new Thread(() -> {
      new GenerationUtils(band, 4);
    });
    thread5.start();

    Thread thread6 = new Thread(() -> {
      new GenerationUtils(band, 5);
    });
    thread6.start();

    Thread thread7 = new Thread(() -> {
      new GenerationUtils(band, 6);
    });
    thread7.start();

    Thread thread8 = new Thread(() -> {
      new GenerationUtils(band, 7);
    });
    thread8.start();

    Thread thread9 = new Thread(() -> {
      new GenerationUtils(band, 8);
    });
    thread9.start();

    Thread thread10 = new Thread(() -> {
      new GenerationUtils(band, 9);
    });
    thread10.start();

    Thread thread11 = new Thread(() -> {
      new GenerationUtils(band, 10);
    });
    thread11.start();

     */
  }

  public static void var1() {
    String input = "sources/task3/0.txt";

    File output = TextImportUtils.loadFromFileOdd(new File(input), numberOfRounds, numberOfTeams, numberOfPlayers);
    CalculationsUtils.calculateFile(output);
    ExportUtils.export(output);
    Tournament tournament = TournamentLoader.load(output);
    CriteriaUtils.checkAllCriteria(tournament);
  }

  public static void var2() {
    DataAnalysisUtils.compareData(new File("MathsPlugin"));
  }

  public static void var3() {

    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter input file path: ");
    String input = scanner.nextLine();
    input = "sources/" + input;
    if (input.chars().filter(c -> c == '.').count() != 1) {
      input += ".xlsx";
    }

    List<File> files = ExcelImportUtils.loadFromExcel(input, numberOfRounds, numberOfTeams, numberOfPlayers);


    for (File file : files) {
      System.out.println("--------------------------------------------------------------------------");
      System.out.println("Sheet name: " + FilenameUtils.removeExtension(file.getName()));
      CalculationsUtils.calculateFile(file);
      ExportUtils.export(file);
      System.out.println("--------------------------------------------------------------------------");
    }

  }


  private static void initLetters() {
    letterIndex.put(1, "A");
    letterIndex.put(2, "B");
    letterIndex.put(3, "C");
    letterIndex.put(4, "D");
    letterIndex.put(5, "E");
    letterIndex.put(6, "F");
    letterIndex.put(7, "G");
    letterIndex.put(8, "H");
    letterIndex.put(9, "I");
    letterIndex.put(10, "J");
    letterIndex.put(11, "K");
    letterIndex.put(12, "L");
    letterIndex.put(13, "M");
    letterIndex.put(14, "N");
    letterIndex.put(15, "O");
    letterIndex.put(16, "P");
    letterIndex.put(17, "Q");
    letterIndex.put(18, "R");
    letterIndex.put(19, "S");
    letterIndex.put(20, "T");
    letterIndex.put(21, "U");
    letterIndex.put(22, "V");
    letterIndex.put(23, "W");
    letterIndex.put(24, "X");
    letterIndex.put(25, "Y");
    letterIndex.put(26, "Z");
  }

}
