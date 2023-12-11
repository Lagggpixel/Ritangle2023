package me.reid.utils;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class GenerationUtils {
  private final String[] str = {"EFGHIJKABCCDDEFGHIJKAB", "ACEGIKBDFHHJJACEGIKBDF", "HKCFIADGJBBEEHKCFIADGJ", "DHAEIBFJCGGKKDHAEIBFJC", "KEJDICHBGAAFFKEJDICHBG"};
  private final int[] A = new int[5];
  private final int[] B = new int[5];
  private final int[][] C = new int[5][3];
  private final int[] D = new int[5];
  private final int[] E = new int[5];
  private final int a_1;
  private final int a_2;
  private final File outputFile;
  private int X = 100, Y = 100;
  private double Z = 100;

  @SneakyThrows
  public GenerationUtils(int a_1, int a_2) {
    this.a_1 = a_1;
    this.a_2 = a_2;
    this.outputFile = new File("task3/" + a_1 + "_" + a_2 + ".txt");
    if (outputFile.exists()) {
      if (!outputFile.delete()) {
        System.out.println("Could not delete file: " + outputFile.getAbsolutePath());
        System.exit(1);
      }
    }
    if (!outputFile.getParentFile().exists()) {
      if (!outputFile.getParentFile().mkdirs()) {
        System.out.println("Could not create directory: " + outputFile.getParentFile().getAbsolutePath());
        System.exit(1);
      }
    }
    if (!outputFile.createNewFile()) {
      System.out.println("Could not create file: " + outputFile.getAbsolutePath());
      System.exit(1);
    }
    // long start = System.nanoTime();
    iterate1();
    // long stop = System.nanoTime();
    // long duration = stop - start;
    // System.out.println("Duration: " + duration + " nanoseconds");
  }

  @SneakyThrows
  void calculate(int[] a, int[] b, int[][] c, int[] d, int[] e) {
    String[][] s = new String[5][3];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        if (c[i][j] == 0) {
          s[i][j] = str[i];
          if (d[i] != 0)
            for (int k = 0; k < 22; k += 2)
              if (k != 10)
                s[i][j] = swapChars(s[i][j], k, k + 1);
        } else if (c[i][j] == 1) {
          s[i][j] = str[i].substring(12, 22) + str[i].charAt(11) + str[i].charAt(10) + str[i].substring(0, 10);
          if (d[i] != 0)
            for (int k = 0; k < 22; k += 2)
              if (k != 10)
                s[i][j] = swapChars(s[i][j], k, k + 1);
        } else {
          if (e[i] < 2)
            s[i][j] = str[b[i]];
          else
            s[i][j] = str[b[i]].substring(12, 22) + str[b[i]].charAt(11) + str[b[i]].charAt(10) + str[b[i]].substring(0, 10);
          if ((e[i] & 1) != 0)
            for (int k = 0; k < 22; k += 2)
              if (k != 10)
                s[i][j] = swapChars(s[i][j], k, k + 1);
          int shift = (a[b[i]] - (s[i][j].charAt(10 + e[i] / 2) - 'A') + 11) % 11;
          StringBuilder temp = new StringBuilder();
          for (int k = 0; k < 22; k++)
            temp.append((char) ((s[i][j].charAt(k) - 'A' + shift) % 11 + 'A'));
          s[i][j] = temp.toString();
          for (int k = 0; k < 10; k++) {
            if (s[i][j].charAt(k) == str[i].charAt(10 + d[i])) {
              if ((k & 1) != 0)
                s[i][j] = swapChars(s[i][j], k - 1, k);
              break;
            }
          }
          for (int k = 12; k < 22; k++) {
            if (s[i][j].charAt(k) == str[i].charAt(11 - d[i])) {
              if (k % 2 == 0)
                s[i][j] = swapChars(s[i][j], k, k + 1);
              break;
            }
          }
        }
      }
    }
    int[][][] cntX = new int[11][3][2];
    int[][] cntY = new int[11][2];
    int[] cntZ = new int[11];
    int x = 0, y = 0;
    double z = 0;
    for (int i = 0; i < 5; i++)
      for (int j = 0; j < 3; j++)
        for (int k = 0; k < 22; k++)
          cntX[s[i][j].charAt(k) - 'A'][j][k & 1]++;
    for (int i = 0; i < 11; i++)
      for (int j = 0; j < 3; j++)
        x += Math.abs(cntX[i][j][0] - cntX[i][j][1]);
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        cntY[s[i][j].charAt(10) - 'A'][0]++;
        cntY[s[i][j].charAt(11) - 'A'][1]++;
      }
    }
    for (int i = 0; i < 11; i++)
      y += Math.abs(cntY[i][0] - cntY[i][1]);
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        for (int k = 0; k < 10; k += 2)
          cntZ[s[i][j].charAt(k) - 'A'] += i * 2 + 1;
        for (int k = 10; k < 22; k += 2)
          cntZ[s[i][j].charAt(k) - 'A'] += i * 2 + 2;
      }
    }
    for (int i = 0; i < 11; i++)
      z += Math.abs(1 - 4.0 / 330 * cntZ[i]);
    if (X + Y + Z > x + y + z) {
      FileWriter fileWriter = new FileWriter(outputFile, true);
      X = x;
      Y = y;
      Z = z;
      System.arraycopy(a, 0, A, 0, 5);
      System.arraycopy(b, 0, B, 0, 5);
      for (int i = 0; i < 5; i++)
        System.arraycopy(c[i], 0, C[i], 0, 3);
      System.arraycopy(d, 0, D, 0, 5);
      System.arraycopy(e, 0, E, 0, 5);
      fileWriter.write(X + " " + Y + " " + Z + "\n");
      fileWriter.write(Arrays.toString(A) + "\n");
      fileWriter.write(Arrays.toString(B) + "\n");
      for (int i = 0; i < 5; i++)
        fileWriter.write(Arrays.toString(C[i]) + "\n");
      fileWriter.write(Arrays.toString(D) + "\n");
      fileWriter.write(Arrays.toString(E) + "\n");
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 5; j++)
          fileWriter.write(s[i][0].charAt(j * 2) + "" + s[i][0].charAt(j * 2 + 1) + " "
              + s[i][1].charAt(j * 2) + s[i][1].charAt(j * 2 + 1) + " "
              + s[i][2].charAt(j * 2) + s[i][2].charAt(j * 2 + 1) + "\n");
        fileWriter.write("\n");
        for (int j = 5; j < 6; j++)
          fileWriter.write(s[i][0].charAt(j * 2) + "" + s[i][0].charAt(j * 2 + 1) + " "
              + s[i][1].charAt(j * 2) + s[i][1].charAt(j * 2 + 1) + " "
              + s[i][2].charAt(j * 2) + s[i][2].charAt(j * 2 + 1) + "\n");
        fileWriter.write("\n");
        for (int j = 6; j < 11; j++)
          fileWriter.write(s[i][0].charAt(j * 2) + "" + s[i][0].charAt(j * 2 + 1) + " "
              + s[i][1].charAt(j * 2) + s[i][1].charAt(j * 2 + 1) + " "
              + s[i][2].charAt(j * 2) + s[i][2].charAt(j * 2 + 1) + "\n");
        fileWriter.write("\n");
      }
      for (int i = 0; i < 11; i++) {
        for (int j = 0; j < 3; j++)
          fileWriter.write(cntX[i][j][0] + " " + cntX[i][j][1] + " ");
        fileWriter.write("\n");
      }
      fileWriter.write("\n");
      fileWriter.close();
    }
  }

  private void iterate5(int[] a, int[] b, int[][] c, int[] d) {
    int[] e = new int[5];
    for (e[0] = 0; e[0] < 4; e[0]++)
      for (e[1] = 0; e[1] < 4; e[1]++)
        for (e[2] = 0; e[2] < 4; e[2]++)
          for (e[3] = 0; e[3] < 4; e[3]++)
            for (e[4] = 0; e[4] < 4; e[4]++)
              calculate(a, b, c, d, e);
  }

  private void iterate4(int[] a, int[] b, int[][] c) {
    int[] d = new int[5];
    for (d[0] = 0; d[0] < 2; d[0]++)
      for (d[1] = 0; d[1] < 2; d[1]++)
        for (d[2] = 0; d[2] < 2; d[2]++)
          for (d[3] = 0; d[3] < 2; d[3]++)
            for (d[4] = 0; d[4] < 2; d[4]++)
              iterate5(a, b, c, d);
  }

  private void iterate3(int[] a, int[] b) {
    int[][] c = {{0, 1, 2}, {0, 1, 2}, {0, 1, 2}, {0, 1, 2}, {0, 1, 2}};
    do {
      do {
        do {
          iterate4(a, b, c);
        } while (nextPermutation(c[4]));
      } while (nextPermutation(c[3]));
    } while (nextPermutation(c[2]));
    while (nextPermutation(c[1])) ;
//    while (nextPermutation(c[0], c[0]+3));
  }

  private boolean check2(int[] a, int[] b) {
    for (int i = 0; i < 5; i++)
      if (b[i] == i || str[i].charAt(10) - 'A' == a[b[i]] || str[i].charAt(10) - 'A' == (a[b[i]] + b[i] + 1) % 11 || str[i].charAt(11) - 'A' == a[b[i]] || str[i].charAt(11) - 'A' == (a[b[i]] + b[i] + 1) % 11)
        return false;
    return true;
  }

  private void iterate2(int[] a) {
    int[] b = {0, 1, 2, 3, 4};
    do if (check2(a, b))
      iterate3(a, b);
    while (nextPermutation(b));
  }

  private boolean check1(int[] a) {
    int[] aa = {a[0], a[1], a[2], a[3], a[4], (a[0] + 1) % 11, (a[1] + 2) % 11, (a[2] + 3) % 11, (a[3] + 4) % 11, (a[4] + 5) % 11};
    int cnt = 0;
    Arrays.sort(aa);
    for (int i = 0; i < 10; i++) {
      if (i != 0 && aa[i - 1] == aa[i] && aa[i] != 8)
        return false;
      if (aa[i] == 8)
        cnt++;
    }
    return cnt == 2 || cnt == 3;
  }

  @SneakyThrows
  private void iterate1() {
    int[] a = new int[5];
    a[0] = a_1;
    a[1] = a_2;


    System.out.println("a: " + a[0] + " " + a[1] + " started.");

    FileWriter fw = new FileWriter("output.txt", true);
    fw.write("a: " + a[0] + " " + a[1] + "\n");
    fw.close();
    // System.out.println("a: " + a[0] + " " + a[1]);
    for (a[2] = 0; a[2] < 11; a[2]++)
      for (a[3] = 0; a[3] < 11; a[3]++)
        for (a[4] = 0; a[4] < 11; a[4]++)
          if (check1(a))
            iterate2(a);
    System.out.println("a: " + a[0] + " " + a[1] + " completed.");

  }

  private boolean nextPermutation(int[] array) {
    int i = array.length - 1;
    while (i > 0 && array[i - 1] >= array[i]) {
      i--;
    }

    if (i <= 0) {
      return false; // No more permutations
    }

    int j = array.length - 1;
    while (array[j] <= array[i - 1]) {
      j--;
    }

    // Swap i-1 and j
    int temp = array[i - 1];
    array[i - 1] = array[j];
    array[j] = temp;

    // Reverse the suffix
    j = array.length - 1;
    while (i < j) {
      temp = array[i];
      array[i] = array[j];
      array[j] = temp;
      i++;
      j--;
    }

    return true; // Permutation succeeded
  }

  private String swapChars(String input, int i, int j) {
    char[] charArray = input.toCharArray();
    char temp = charArray[i];
    charArray[i] = charArray[j];
    charArray[j] = temp;
    return new String(charArray);
  }

}