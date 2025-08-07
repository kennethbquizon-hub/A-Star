package assignment1;

import java.io.IOException;
import java.io.File;

public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {
    File source = null;
    boolean runGui = true;
    boolean astar = true;
    int numCols = 100;
    int numRows = 50;
    double distance = Double.NaN;
    int wait = 0;

    if (args.length > 0 && !args[0].startsWith("-")) {
      source = new File(args[0]);
    }

    for (int i = source == null ? 0 : 1; i < args.length; i++) {
      String arg = args[i];
      if (arg.equals("-a")) {
        astar = true;
        runGui = false;
      } else if (arg.equals("-t")) {
        astar = false;
        runGui = false;
      } else if (arg.equals("-c")) {
        if (i == args.length - 1) {
          throw new IllegalArgumentException("The -c flag was provided without an argument.");
        } else {
          numCols = Integer.parseInt(args[i + 1]);
          i ++;
        }
      } else if (arg.equals("-r")) {
        if (i == args.length - 1) {
          throw new IllegalArgumentException("The -r flag was provided without an argument.");
        } else {
          numRows = Integer.parseInt(args[i + 1]);
          i++;
        }
      } else if (arg.equals("-d")) {
        if (i == args.length - 1) {
          throw new IllegalArgumentException("The -d flag was provided without an argument.");
        } else {
          distance = Double.parseDouble(args[i + 1]);
          i++;
        }
      } else if (arg.equals("-w")) {
        if (i == args.length - 1) {
          throw new IllegalArgumentException("The -w flag was provided without an argument.");
        } else {
          wait = Integer.parseInt(args[i + 1]);
          i++;
        }
      } else {
        throw new IllegalArgumentException(String.format("Unknown flag '%s'", arg));
      }
    }

    Grid grid =
      source == null
      ? Double.isNaN(distance)
        ? new Grid(numCols, numRows, numCols * numRows / 10)
        : new Grid(numCols, numRows, numCols * numRows / 10, distance)
      : new Grid(source);

    if (runGui) {
      new GUI(grid).openWindow();
    } else {
      long start = System.currentTimeMillis();

      SearchAlgo search = astar ? new AStar(grid) : new ThetaStar(grid);
      SearchState state = search.state();
      while (!state.finished) {
        search.runStep();
      }

      long finish = System.currentTimeMillis();
      System.out.println(String.format("%s took %dms:", astar ? "A*" : "Theta*", finish - start));

      if (state.parent(grid.goal) == SearchState.NoParent) {
        System.out.println(String.format("  No path was found."));
      } else {
        int length = 0;
        int vertex = grid.goal;
        while (vertex != grid.start) {
          vertex = state.parent(vertex);
          length++;
        }
        System.out.println(String.format("  A path with %d vertices and a length of %.3f was found.", length, state.g(grid.goal)));
        System.out.println(String.format("  %d vertices are closed and %d vertices remain open.", state.numVisited(), state.fringeSize()));
      }

      Thread.sleep(wait * 1000);
    }
  }
}
