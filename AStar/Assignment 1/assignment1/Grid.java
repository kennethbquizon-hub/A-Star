package assignment1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import java.util.Scanner;

public class Grid {
  private int numRows, numCols;
  public int numRows() { return numRows - 2; }
  public int numCols() { return numCols - 2; }

  private BitSet blocked;
  public boolean isBlocked(int cell) { return blocked.get(cell); }

  public int start;
  public int goal;

  public int indexFromCoords(int x, int y) {
    return y * numCols + x;
  }

  public int indexFromOffset(int i, int dx, int dy) {
    return i + dx + dy * numCols;
  }

  public int xCoord(int i) { return i % numCols; }
  public int yCoord(int i) { return i / numCols; }

  public double distance(int a, int b) {
    int x = xCoord(a) - xCoord(b);
    int y = yCoord(a) - yCoord(b);
    return Math.sqrt(x * x + y * y);
  }

  public Grid(File source) throws FileNotFoundException {
    Scanner file = new Scanner(source);

    int startx = file.nextInt();
    int starty = file.nextInt();

    int goalx = file.nextInt();
    int goaly = file.nextInt();

    numCols = file.nextInt() + 2;
    numRows = file.nextInt() + 2;

    start = indexFromCoords(startx, starty);
    goal = indexFromCoords(goalx, goaly);

    setBorderCellsBlocked();

    while (file.hasNextInt()) {
      int x = file.nextInt();
      int y = file.nextInt();
      boolean isBlocked = file.nextInt() == 1;
      blocked.set(indexFromCoords(x, y), isBlocked);
    }

    file.close();
  }

  public Grid(int numCols, int numRows, int numBlocked) {
    this.numCols = numCols + 2;
    this.numRows = numRows + 2;

    setBorderCellsBlocked();

    Random rand = new Random();

    start = indexFromCoords(rand.nextInt(numCols + 1) + 1, rand.nextInt(numRows + 1) + 1);
    goal = indexFromCoords(rand.nextInt(numCols + 1) + 1, rand.nextInt(numRows + 1) + 1);

    while (numBlocked > 0) {
      int cell = indexFromCoords(rand.nextInt(numCols) + 1, rand.nextInt(numRows) + 1);
      if (!isBlocked(cell)) {
        blocked.set(cell, true);
        numBlocked--;
      }
    }
  }

  // distance controls how far away the start and goal should be in terms of the length of the shorter side of the grid.
  // E.g., for a 100x50 grid with distance = 0.5, the goal should be (0.5 * 50 = 25) away from the start.
  public Grid(int numCols, int numRows, int numBlocked, double distance) {
    distance = Math.min(numRows, numCols) * Math.min(Math.abs(distance), 0.5);

    this.numCols = numCols + 2;
    this.numRows = numRows + 2;

    setBorderCellsBlocked();

    Random rand = new Random();

    int startX = rand.nextInt(numCols + 1) + 1;
    int startY = rand.nextInt(numRows + 1) + 1;
    start = indexFromCoords(startX, startY);
    while (true) {
      double radians = rand.nextDouble() * Math.PI * 2;
      int x = startX + (int)(distance * Math.cos(radians));
      int y = startY + (int)(distance * Math.sin(radians));
      if ( 0 <= x && x <= numCols + 1
        && 0 <= y && y <= numRows + 1
      ) {
        goal = indexFromCoords(x, y);
        break;
      }
    }

    while (numBlocked > 0) {
      int cell = indexFromCoords(rand.nextInt(numCols) + 1, rand.nextInt(numRows) + 1);
      if (!isBlocked(cell)) {
        blocked.set(cell, true);
        numBlocked--;
      }
    }
  }

  private void setBorderCellsBlocked() {
    int len = numCols * numRows;
    blocked = new BitSet(len);
    for (int i = 0; i < numCols; i++) {
      blocked.set(i, true); // top edge
    }
    for (int i = numCols * (numRows - 1); i < len; i++) {
      blocked.set(i, true); // bottom edge
    }
    for (int i = numCols; i < len; i += numCols) {
      blocked.set(i - 1, true); // right edge
      blocked.set(i, true); // left edge
    }
  }

  private int[] buffer = new int[8];

  public int[] unblockedNeighbors(int v) {
    boolean topLeftBlocked = isBlocked(indexFromOffset(v, -1, -1));
    boolean topRightBlocked = isBlocked(indexFromOffset(v, 0, -1));
    boolean bottomLeftBlocked = isBlocked(indexFromOffset(v, -1, 0));
    boolean bottomRightBlocked = isBlocked(v);

    int count = 0;

    if (!topLeftBlocked) {
      buffer[count++] = indexFromOffset(v, -1, -1);
    }

    if (!topLeftBlocked || !topRightBlocked) {
      buffer[count++] = indexFromOffset(v, 0, -1);
    }

    if (!topRightBlocked) {
      buffer[count++] = indexFromOffset(v, 1, -1);
    }

    if (!topRightBlocked || !bottomRightBlocked) {
      buffer[count++] = indexFromOffset(v, 1, 0);
    }

    if (!bottomRightBlocked) {
      buffer[count++] = indexFromOffset(v, 1, 1);
    }

    if (!bottomRightBlocked || !bottomLeftBlocked) {
      buffer[count++] = indexFromOffset(v, 0, 1);
    }

    if (!bottomLeftBlocked) {
      buffer[count++] = indexFromOffset(v, -1, 1);
    }

    if (!bottomLeftBlocked || ! topLeftBlocked) {
      buffer[count++] = indexFromOffset(v, -1, 0);
    }

    return Arrays.copyOfRange(buffer, 0, count);
  }

  public void writeToFile(File destination) throws IOException {
    FileWriter write = new FileWriter(destination);

    write.append(Integer.toString(xCoord(start)));
    write.append(" ");
    write.append(Integer.toString(yCoord(start)));
    write.append("\n");

    write.append(Integer.toString(xCoord(goal)));
    write.append(" ");
    write.append(Integer.toString(yCoord(goal)));
    write.append("\n");

    write.append(Integer.toString(numCols()));
    write.append(" ");
    write.append(Integer.toString(numRows()));
    write.append("\n");

    for (int x = 1; x <= numCols(); x++) {
      for (int y = 1; y <= numRows(); y++) {
        write.append(Integer.toString(x));
        write.append(" ");
        write.append(Integer.toString(y));
        write.append(" ");
        write.append(isBlocked(indexFromCoords(x, y)) ? "1" : "0");
        write.append("\n");
      }
    }

    write.close();
  }
}
