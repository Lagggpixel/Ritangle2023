package me.reid.utils;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

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

  int band;
  int index;
  File outputFile;

  @SneakyThrows
  public GenerationUtils(int band, int index) {
    this.band = band;
    this.index = index;
    this.outputFile = new File("task3/" + band + "_" + index + ".txt");
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
    iterate1();
  }

  @SneakyThrows
  public void calculate(int[] a, int[] b, int[][] c, int[] d, int[] e) {
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
    int x = 0;
    int y = 0;
    double z = 0;
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        for (int k = 0; k < 22; k++) {
          cntX[s[i][j].charAt(k) - 'A'][j][k & 1]++;
        }
      }
    }
    for (int i = 0; i < 11; i++) {
      for (int j = 0; j < 3; j++) {
        x += Math.abs(cntX[i][j][0] - cntX[i][j][1]);
      }
    }
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 3; j++) {
        cntY[s[i][j].charAt(10) - 'A'][0]++;
        cntY[s[i][j].charAt(11) - 'A'][1]++;
      }
    }
    for (int i = 0; i < 11; i++) {
      y += Math.abs(cntY[i][0] - cntY[i][1]);
    }
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
      FileWriter fileWriter = writeToFile(s, cntX);
      fileWriter.close();
    }
  }

  @NotNull
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

  public void iterate5(int[] a, int[] b, int[][] c, int[] d) {
    int[] e = new int[5];
    for (e[0] = 0; e[0] < 4; e[0]++) {
      for (e[1] = 0; e[1] < 4; e[1]++) {
        for (e[2] = 0; e[2] < 4; e[2]++) {
          for (e[3] = 0; e[3] < 4; e[3]++) {
            for (e[4] = 0; e[4] < 4; e[4]++) {
              calculate(a, b, c, d, e);
            }
          }
        }
      }
    }
  }

  public void iterate4(int[] a, int[] b, int[][] c) {
    int[] d = new int[5];
    for (d[0] = 0; d[0] < 2; d[0]++) {
      for (d[1] = 0; d[1] < 2; d[1]++) {
        for (d[2] = 0; d[2] < 2; d[2]++) {
          for (d[3] = 0; d[3] < 2; d[3]++) {
            for (d[4] = 0; d[4] < 2; d[4]++) {
              iterate5(a, b, c, d);
            }
          }
        }
      }
    }
  }

  public void iterate3(int[] a, int[] b) {
    int[][] c = {{0, 1, 2}, {0, 1, 2}, {0, 1, 2}, {0, 1, 2}, {0, 1, 2}};
    do {
      do {
        do {
          do {
            iterate4(a, b, c);
          } while (nextPermutation(c[4]));
        } while (nextPermutation(c[3]));
      } while (nextPermutation(c[2]));
    } while (nextPermutation(c[1]));
  }

  public boolean check2(int[] a, int[] b) {
    for (int i = 0; i < 5; i++) {
      if (b[i] == i || str[i].charAt(10) - 'A' == a[b[i]] || str[i].charAt(10) - 'A' == (a[b[i]] + b[i] + 1) % 11 || str[i].charAt(11) - 'A' == a[b[i]] || str[i].charAt(11) - 'A' == (a[b[i]] + b[i] + 1) % 11) {
        return false;
      }
    }
    return true;
  }

  public void iterate2(int[] a) {
    int[] b = {0, 1, 2, 3, 4};
    do {
      if (check2(a, b)) {
        iterate3(a, b);
      }
    } while (nextPermutation(b));
  }

  public int check1(int[] a) {
    int[] aa = {a[0], a[1], a[2], a[3], a[4], (a[0] + 1) % 11, (a[1] + 2) % 11, (a[2] + 3) % 11, (a[3] + 4) % 11, (a[4] + 5) % 11};
    int cnt = 0;
    Arrays.sort(aa);
    for (int i = 0; i < 10; i++) {
      if (i != 0 && aa[i - 1] == aa[i] && aa[i] != 8) {
        return 0;
      }
      if (aa[i] == 8) {
        cnt++;
      }
    }
    return cnt == 2 || cnt == 3 ? 1 : 0;
  }

  public void iterate1() {
    int[] a = new int[5];
    a[0] = band;
    a[1] = index;

    for (a[2] = 0; a[2] < 11; a[2]++) {
      for (a[3] = 0; a[3] < 11; a[3]++) {
        for (a[4] = 0; a[4] < 11; a[4]++) {
          if (check1(a) != 0) {
            iterate2(a);
          }
        }
      }
    }

  }

  public boolean nextPermutation(int[] nums) {
    int i = nums.length - 2;
    while (i >= 0 && nums[i] >= nums[i + 1]) {
      i--;
    }
    if (i < 0) {
      return false;
    }
    int j = nums.length - 1;
    while (nums[j] <= nums[i]) {
      j--;
    }
    swap(nums, i, j);
    reverse(nums, i + 1);
    return true;
  }

  public void swap(int[] nums, int i, int j) {
    int temp = nums[i];
    nums[i] = nums[j];
    nums[j] = temp;
  }

  public void reverse(int[] nums, int start) {
    int i = start;
    int j = nums.length - 1;
    while (i < j) {
      swap(nums, i, j);
      i++;
      j--;
    }
  }
}


