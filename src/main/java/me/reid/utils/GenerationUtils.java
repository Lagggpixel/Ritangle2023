package me.reid.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings("DuplicatedCode")
public class GenerationUtils {

  static String[] str = {"EFGHIJKABCCDDEFGHIJKAB", "ACEGIKBDFHHJJACEGIKBDF", "HKCFIADGJBBEEHKCFIADGJ", "DHAEIBFJCGGKKDHAEIBFJC", "KEJDICHBGAAFFKEJDICHBG"};
  static int[] A = new int[5];
  static int[] B = new int[5];
  static int[][] C = new int[5][3];
  static int[] D = new int[5];
  static int[] E = new int[5];
  static int X = 100;
  static int Y = 100;
  static double Z = 100;
  File outputFile;
  int band;
  int index;
  String prefix;

  public GenerationUtils(int band, int index)  {
    this.band = band;
    this.index = index;
    prefix = "a: " + band + " " + index + " >> ";
    this.outputFile = new File("sources/task3/generated/" + band + "_" + index + ".txt");
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
    try {
      if (!outputFile.createNewFile()) {
        System.out.println("Could not create file: " + outputFile.getAbsolutePath());
        System.exit(1);
      }
      FileWriter fileWriter = new FileWriter(outputFile);
      fileWriter.write("a: " + band + " " + index + "\n");
      fileWriter.close();
      iterate1();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void calculate(int[] a, int[] b, int[][] c, int[] d, int[] e) throws IOException {
    String[][] s = new String[5][3];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        if (c[i][j] == 0) {
          s[i][j] = str[i];
          if (d[i] != 0) {
            for (int k = 0; k < 22; k += 2) {
              if (k != 10) {
                char temp = s[i][j].charAt(k);
                s[i][j] = s[i][j].substring(0, k) + s[i][j].charAt(k + 1) + s[i][j].substring(k + 2);
                s[i][j] = s[i][j].substring(0, k + 1) + temp + s[i][j].substring(k + 1);
              }
            }
          }
        } else if (c[i][j] == 1) {
          s[i][j] = str[i].substring(12, 22) + str[i].charAt(11) + str[i].charAt(10) + str[i].substring(0, 10);
          if (d[i] != 0) {
            for (int k = 0; k < 22; k += 2) {
              if (k != 10) {
                char temp = s[i][j].charAt(k);
                s[i][j] = s[i][j].substring(0, k) + s[i][j].charAt(k + 1) + s[i][j].substring(k + 2);
                s[i][j] = s[i][j].substring(0, k + 1) + temp + s[i][j].substring(k + 1);
              }
            }
          }
        } else {
          if (e[i] < 2) {
            s[i][j] = str[b[i]];
          } else {
            s[i][j] = str[b[i]].substring(12, 22) + str[b[i]].charAt(11) + str[b[i]].charAt(10) + str[b[i]].substring(0, 10);
          }
          if ((e[i] & 1) != 0) {
            for (int k = 0; k < 22; k += 2) {
              if (k != 10) {
                char temp = s[i][j].charAt(k);
                s[i][j] = s[i][j].substring(0, k) + s[i][j].charAt(k + 1) + s[i][j].substring(k + 2);
                s[i][j] = s[i][j].substring(0, k + 1) + temp + s[i][j].substring(k + 1);
              }
            }
          }
          int shift = (a[b[i]] - (s[i][j].charAt(10 + e[i] / 2) - 'A') + 11) % 11;
          char[] sArray = s[i][j].toCharArray();
          for (int k = 0; k < 22; k++) {
            sArray[k] = (char) ((sArray[k] - 'A' + shift) % 11 + 'A');
          }
          s[i][j] = new String(sArray);
          for (int k = 0; k < 10; k++) {
            if (s[i][j].charAt(k) == str[i].charAt(10 + d[i])) {
              if ((k & 1) != 0) {
                char temp = s[i][j].charAt(k - 1);
                s[i][j] = s[i][j].substring(0, k - 1) + s[i][j].charAt(k) + s[i][j].substring(k + 1);
                s[i][j] = s[i][j].substring(0, k) + temp + s[i][j].substring(k);
              }
              break;
            }
          }
          for (int k = 12; k < 22; k++) {
            if (s[i][j].charAt(k) == str[i].charAt(11 - d[i])) {
              if (k % 2 == 0) {
                char temp = s[i][j].charAt(k);
                s[i][j] = s[i][j].substring(0, k) + s[i][j].charAt(k + 1) + s[i][j].substring(k + 2);
                s[i][j] = s[i][j].substring(0, k + 1) + temp + s[i][j].substring(k + 1);
              }
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
    boolean ok = true;
    double z = 0;
    for (int i = 0; i < 5; i++)
      for (int j = 0; j < 3; j++)
        for (int k = 0; k < 22; k++)
          cntX[s[i][j].charAt(k) - 'A'][j][k & 1]++;
    for (int i = 0; i < 11; i++) {
      int sum0 = 0, sum1 = 0;
      for (int j = 0; j < 3; j++) {
        x += Math.abs(cntX[i][j][0] - cntX[i][j][1]);
        sum0 += cntX[i][j][0];
        sum1 += cntX[i][j][1];
      }
      if (sum0 != sum1) {
        ok = false;
        break;
      }
    }
    if (!ok)
      return;
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        for (int k = 0; k < 10; k += 2) {
          cntZ[s[i][j].charAt(k) - 'A'] += i * 2 + 1;
        }
        for (int k = 10; k < 22; k += 2) {
          cntZ[s[i][j].charAt(k) - 'A'] += i * 2 + 2;
        }
      }
    }
    for (int i = 0; i < 11; i++) {
      z += Math.abs(1 - 4.0 / 330 * cntZ[i]);
    }

    if (X + Y + Z > x + y + z) {
      X = x;
      Y = y;
      Z = z;
      for (int i = 0; i < 5; i++) {
        A[i] = a[i];
        B[i] = b[i];
        C[i][0] = c[i][0];
        C[i][1] = c[i][1];
        C[i][2] = c[i][2];
        D[i] = d[i];
        E[i] = e[i];
      }
      FileWriter fw = writeToFile(s, cntX);
      fw.close();
    }
  }

  private FileWriter writeToFile(String[][] s, int[][][] cntX) throws IOException {
    FileWriter fileWriter = new FileWriter(outputFile, true);
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
    return fileWriter;
  }


  private void iterate5(int[] a, int[] b, int[][] c, int[] d) throws IOException {
    int[] e = new int[5];
    for (e[0] = 0; e[0] < 4; e[0]++)
      for (e[1] = 0; e[1] < 4; e[1]++)
        for (e[2] = 0; e[2] < 4; e[2]++)
          for (e[3] = 0; e[3] < 4; e[3]++)
            for (e[4] = 0; e[4] < 4; e[4]++)
              calculate(a, b, c, d, e);
  }

  private void iterate4(int[] a, int[] b, int[][] c) throws IOException {
    int[] d = new int[5];
    for (d[0] = 0; d[0] < 2; d[0]++)
      for (d[1] = 0; d[1] < 2; d[1]++)
        for (d[2] = 0; d[2] < 2; d[2]++)
          for (d[3] = 0; d[3] < 2; d[3]++)
            for (d[4] = 0; d[4] < 2; d[4]++)
              iterate5(a, b, c, d);
  }

  public void iterate3(int[] a, int[] b) throws IOException {
    int[][] c = new int[5][3];
    c[0] = new int[]{0, 1, 2};
    int[][] c_permutations = {{0, 1, 2}, {0, 2, 1}, {1, 2, 0}, {1, 0, 2}, {2, 0, 1}, {2, 1, 0}};
    c[1] = new int[]{0, 1, 2};
    for (int[] c2 : c_permutations) {
      for (int[] c3 : c_permutations) {
        for (int[] c4 : c_permutations) {
          c[2] = c2;
          c[3] = c3;
          c[4] = c4;
          iterate4(a, b, c);
        }
      }
    }
  }

  private boolean check2(int[] a, int[] b) {
    for (int i = 0; i < 5; i++)
      if (b[i] == i || str[i].charAt(10) - 'A' == a[b[i]] || str[i].charAt(10) - 'A' == (a[b[i]] + b[i] + 1) % 11 || str[i].charAt(11) - 'A' == a[b[i]] || str[i].charAt(11) - 'A' == (a[b[i]] + b[i] + 1) % 11)
        return false;
    return true;
  }

  public void iterate2(int[] a) throws IOException {

    //<editor-fold desc="B Permutations">
    int[][] b_permutations = {{0, 1, 2, 3, 4},
        {0, 1, 2, 4, 3},
        {0, 1, 3, 2, 4},
        {0, 1, 3, 4, 2},
        {0, 1, 4, 2, 3},
        {0, 1, 4, 3, 2},
        {0, 2, 1, 3, 4},
        {0, 2, 1, 4, 3},
        {0, 2, 3, 1, 4},
        {0, 2, 3, 4, 1},
        {0, 2, 4, 1, 3},
        {0, 2, 4, 3, 1},
        {0, 3, 1, 2, 4},
        {0, 3, 1, 4, 2},
        {0, 3, 2, 1, 4},
        {0, 3, 2, 4, 1},
        {0, 3, 4, 1, 2},
        {0, 3, 4, 2, 1},
        {0, 4, 1, 2, 3},
        {0, 4, 1, 3, 2},
        {0, 4, 2, 1, 3},
        {0, 4, 2, 3, 1},
        {0, 4, 3, 1, 2},
        {0, 4, 3, 2, 1},
        {1, 0, 2, 3, 4},
        {1, 0, 2, 4, 3},
        {1, 0, 3, 2, 4},
        {1, 0, 3, 4, 2},
        {1, 0, 4, 2, 3},
        {1, 0, 4, 3, 2},
        {1, 2, 0, 3, 4},
        {1, 2, 0, 4, 3},
        {1, 2, 3, 0, 4},
        {1, 2, 3, 4, 0},
        {1, 2, 4, 0, 3},
        {1, 2, 4, 3, 0},
        {1, 3, 0, 2, 4},
        {1, 3, 0, 4, 2},
        {1, 3, 2, 0, 4},
        {1, 3, 2, 4, 0},
        {1, 3, 4, 0, 2},
        {1, 3, 4, 2, 0},
        {1, 4, 0, 2, 3},
        {1, 4, 0, 3, 2},
        {1, 4, 2, 0, 3},
        {1, 4, 2, 3, 0},
        {1, 4, 3, 0, 2},
        {1, 4, 3, 2, 0},
        {2, 0, 1, 3, 4},
        {2, 0, 1, 4, 3},
        {2, 0, 3, 1, 4},
        {2, 0, 3, 4, 1},
        {2, 0, 4, 1, 3},
        {2, 0, 4, 3, 1},
        {2, 1, 0, 3, 4},
        {2, 1, 0, 4, 3},
        {2, 1, 3, 0, 4},
        {2, 1, 3, 4, 0},
        {2, 1, 4, 0, 3},
        {2, 1, 4, 3, 0},
        {2, 3, 0, 1, 4},
        {2, 3, 0, 4, 1},
        {2, 3, 1, 0, 4},
        {2, 3, 1, 4, 0},
        {2, 3, 4, 0, 1},
        {2, 3, 4, 1, 0},
        {2, 4, 0, 1, 3},
        {2, 4, 0, 3, 1},
        {2, 4, 1, 0, 3},
        {2, 4, 1, 3, 0},
        {2, 4, 3, 0, 1},
        {2, 4, 3, 1, 0},
        {3, 0, 1, 2, 4},
        {3, 0, 1, 4, 2},
        {3, 0, 2, 1, 4},
        {3, 0, 2, 4, 1},
        {3, 0, 4, 1, 2},
        {3, 0, 4, 2, 1},
        {3, 1, 0, 2, 4},
        {3, 1, 0, 4, 2},
        {3, 1, 2, 0, 4},
        {3, 1, 2, 4, 0},
        {3, 1, 4, 0, 2},
        {3, 1, 4, 2, 0},
        {3, 2, 0, 1, 4},
        {3, 2, 0, 4, 1},
        {3, 2, 1, 0, 4},
        {3, 2, 1, 4, 0},
        {3, 2, 4, 0, 1},
        {3, 2, 4, 1, 0},
        {3, 4, 0, 1, 2},
        {3, 4, 0, 2, 1},
        {3, 4, 1, 0, 2},
        {3, 4, 1, 2, 0},
        {3, 4, 2, 0, 1},
        {3, 4, 2, 1, 0},
        {4, 0, 1, 2, 3},
        {4, 0, 1, 3, 2},
        {4, 0, 2, 1, 3},
        {4, 0, 2, 3, 1},
        {4, 0, 3, 1, 2},
        {4, 0, 3, 2, 1},
        {4, 1, 0, 2, 3},
        {4, 1, 0, 3, 2},
        {4, 1, 2, 0, 3},
        {4, 1, 2, 3, 0},
        {4, 1, 3, 0, 2},
        {4, 1, 3, 2, 0},
        {4, 2, 0, 1, 3},
        {4, 2, 0, 3, 1},
        {4, 2, 1, 0, 3},
        {4, 2, 1, 3, 0},
        {4, 2, 3, 0, 1},
        {4, 2, 3, 1, 0},
        {4, 3, 0, 1, 2},
        {4, 3, 0, 2, 1},
        {4, 3, 1, 0, 2},
        {4, 3, 1, 2, 0},
        {4, 3, 2, 0, 1},
        {4, 3, 2, 1, 0}
    };
    //</editor-fold>

    for (int[] b : b_permutations) {
      if (check2(a, b)) {
        iterate3(a, b);
      }
    }
  }


  private boolean check1(int[] a) {
    int[] aa = {a[0], a[1], a[2], a[3], a[4], (a[0] + 1) % 11, (a[1] + 2) % 11, (a[2] + 3) % 11, (a[3] + 4) % 11, (a[4] + 5) % 11};
    int cnt = 0;
    Arrays.sort(aa);
    for (int i = 0; i < 10; i++) {
      if (i != 0 && aa[i - 1] == aa[i] && aa[i] != 8) {
        return false;
      }
      if (aa[i] == 8) {
        cnt++;
      }
    }
    return cnt == 2 || cnt == 3;
  }

  private void iterate1() throws IOException {
    int[] a = new int[5];
    for (a[0] = 9; a[0] < 11; a[0]++) {
      for (a[1] = 0; a[1] < 11; a[1]++) {
        System.out.print("a: " + a[0] + ' ' + a[1] + '\n');
        for (a[2] = 0; a[2] < 11; a[2]++)
          for (a[3] = 0; a[3] < 11; a[3]++)
            for (a[4] = 0; a[4] < 11; a[4]++)
              if (check1(a))
                iterate2(a);
      }
    }
  }
}
