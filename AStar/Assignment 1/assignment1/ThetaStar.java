package assignment1;

public class ThetaStar implements SearchAlgo {
  private SearchState state;
  public SearchState state() { return state; }

  private Grid grid;
  public Grid grid() { return grid; };

  public ThetaStar(Grid g) {
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
      int parent = state.parent(vertex);
      int[] neighbors = grid.unblockedNeighbors(vertex);
      for (int i = 0; i < neighbors.length; i++) {
        int next = neighbors[i];
        if (state.isVisited(next)) continue;

        int v = lineOfSight(parent, next) ? parent : vertex;
        double cost = state.g(v) + grid.distance(v, next);
        if (cost < state.g(next)) {
          state.setG(next, cost);
          state.setParent(next, v);
          state.addToFringe(next, cost, h(next));
        }
      }
      return;
    }
    // no path
    state.finished = true;
  }

  public double h(int v) {
    return grid.distance(v, grid.goal);
  }

  public boolean lineOfSight(int s, int e){
    int x0 = grid.xCoord(s), y0 = grid.yCoord(s);
    int x1 = grid.xCoord(e), y1 = grid.yCoord(e);
    int f = 0, dy = y1 - y0, dx = x1 - x0;
    int sx = 1, sy = 1;
    if(dy < 0){
        dy = -dy;
        sy = -1;
    }
    if(dx < 0) {
        dx = -dx;
        sx = -1;
    }
    if(dx > dy) {
        while(x0 != x1){
            f = f + dy;
            int cell = grid.indexFromCoords(x0 + ((sx - 1)/ 2), y0 + ((sy - 1)/ 2));
            if(f >= dx){
                if(grid.isBlocked(cell)){
                    return false;
                }
                y0 = y0 + sy;
                f = f - dx;
            }

            cell = grid.indexFromCoords(x0 + ((sx - 1)/ 2), y0 + ((sy - 1)/ 2));
            if((f != 0) && grid.isBlocked(cell)) {
                return false;
            }

            cell = grid.indexFromCoords(x0 + ((sx - 1)/ 2), y0);
            int cell2 = grid.indexFromCoords(x0 + ((sx - 1)/ 2), y0 - 1);
            if((dy == 0) && (grid.isBlocked(cell)) && (grid.isBlocked(cell2))){
                return false;
            }
            x0 = x0 + sx;
        }
    }
    else {
        while(y0 != y1){
            f = f + dx;
            int cell = grid.indexFromCoords(x0 + ((sx - 1)/ 2), y0 + ((sy - 1)/ 2));
            if (f > dy){
                if (grid.isBlocked(cell)){
                    return false;
                }
                x0 = x0 + sx;
                f = f - dy;
            }

            cell = grid.indexFromCoords(x0 + ((sx - 1)/ 2), y0 + ((sy - 1)/ 2));
            if((f != 0) && (grid.isBlocked(cell))){
                return false;
            }

            cell = grid.indexFromCoords(x0 , y0 + ((sy - 1)/ 2));
            int cell2 = grid.indexFromCoords(x0 - 1 , y0 + ((sy - 1)/ 2));
            if((dx == 0) && (grid.isBlocked(cell)) && (grid.isBlocked(cell2))){
                return false;
            }
            y0 = y0 + sy;
        }
    }
    return true;
  }
}
