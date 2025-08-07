package assignment1;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class SearchState {
  public static final int NoParent = -1;

  private Hashtable<Integer, Double> gTable;
  public double g(int v) { return gTable.getOrDefault(v, Double.POSITIVE_INFINITY); }
  public void setG(int v, double g) { gTable.put(v, g); }
  public Iterable<Entry<Integer, Double>> gEntries() { return gTable.entrySet(); }

  private Hashtable<Integer, Integer> parentTable;
  public int parent(int v) { return parentTable.getOrDefault(v, NoParent); }
  public void setParent(int v, int p) { parentTable.put(v, p); }
  public Iterable<Entry<Integer, Integer>> parentEntries() { return parentTable.entrySet(); }

  private HashSet<Integer> visited; // the closed list
  public boolean isVisited(int v) { return visited.contains(v); }
  public void setVisited(int v) { visited.add(v); }
  public Iterable<Integer> visitedVertices() { return visited; }
  public int numVisited() { return visited.size(); }

  private Heap<OpenVertex> fringe; // the open list
  private int lastPopped;
  public int lastPoppedVertex() { return lastPopped; }
  public int fringeSize() { return fringe.size(); }

  private record OpenVertex(int v, double g, double h) implements Comparable<OpenVertex> {
    public double f() { return g + h; }

    public int compareTo(OpenVertex e) {
      double diff = f() - e.f();
      if (Math.abs(diff) < 1e-10) {
        return Double.compare(e.g, g);
      }
      return diff < 0 ? -1 : 1;
    }
  }

  public void addToFringe(int v, double g, double h) {
    fringe.add(new OpenVertex(v, g, h));
  }

  public boolean fringeIsEmpty() {
    return fringe.isEmpty();
  }

  public int popFringe() {
    lastPopped = fringe.pop().v();
    return lastPopped;
  }

  public Stream<Integer> fringeEntries() {
    return fringe.unorderedEntries().map(e -> e.v());
  }

  public boolean finished;

  public SearchState(Grid grid, double startH) {
    gTable = new Hashtable<Integer, Double>();
    parentTable = new Hashtable<Integer, Integer>();

    fringe = new Heap<OpenVertex>();
    visited = new HashSet<Integer>();

    setG(grid.start, 0);
    setParent(grid.start, grid.start);
    addToFringe(grid.start, 0, startH);

    lastPopped = NoParent;
  }
}
