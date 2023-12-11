package me.reid.utils;

import lombok.SneakyThrows;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataAnalysisUtils {

  public static void compareData(File file) {

    if (!file.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + file.getAbsolutePath());
    }

    File[] files = file.listFiles();
    if (files == null) {
      throw new IllegalArgumentException("No files in directory: " + file.getAbsolutePath());
    }

    float currentSmallest = 100;
    File smallestFile = null;

    for (File f : files) {
      List<String> lines = readLastLine(f, 92);
      if (lines.size() >= 92) {
        String[] values = lines.get(91).split("\\s+");
        float total = 0;
        for (String value : values) {
          total += Float.parseFloat(value);
        }
        if (total < currentSmallest) {
          currentSmallest = total;
          smallestFile = f;
        }
      }
    }

    System.out.println("Smallest value: " + currentSmallest);
    assert smallestFile != null;
    System.out.println("Smallest file: " + smallestFile.getAbsolutePath());
  }

  @SneakyThrows
  public static List<String> readLastLine(File file, int numLastLineToRead) {

    List<String> result = new ArrayList<>();

    ReversedLinesFileReader reader = new ReversedLinesFileReader(file, StandardCharsets.UTF_8);

    String line = "";
    while ((line = reader.readLine()) != null && result.size() < numLastLineToRead) {
      result.add(line);
    }

    reader.close();

    return result;

  }

}
