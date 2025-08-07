package assignment1;

public class AStar implements SearchAlgo {
  private SearchState state;
  public SearchState state() { return state; }

  private Grid grid;
  public Grid grid() { return grid; };

  public AStar(Grid g) {
    grid = g;
    state = new SearchState(grid, h(grid.start));
  }

  public void runStep() {
    while (!state.fringeIsEmpty()) {
      int vertex = state.popFringe();
      if (vertex == grid.goal) {
        state.finished = true;
        return;
      }
      if (state.isVisited(vertex)) continue;

      state.setVisited(vertex);
      int[] neighbors = grid.unblockedNeighbors(vertex);
      for (int i = 0; i < neighbors.length; i++) {
        int next = neighbors[i];
        if (state.isVisited(next)) continue;

        double cost = state.g(vertex) + grid.distance(vertex, next);
        if (cost < state.g(next)) {
          state.setG(next, cost);
          state.setParent(next, vertex);
          state.addToFringe(next, cost, h(next));
        }
      }
      return;
    }
    // no path
    state.finished = true;
  }

  public double h(int v) {
    int x = Math.abs(grid.xCoord(v) - grid.xCoord(grid.goal));
    int y = Math.abs(grid.yCoord(v) - grid.yCoord(grid.goal));
    return Math.sqrt(2) * Math.min(x, y) + Math.max(x, y) - Math.min(x, y);
  }
}
