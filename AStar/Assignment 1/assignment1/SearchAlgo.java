package assignment1;

public interface SearchAlgo {
  public abstract SearchState state();
  public abstract Grid grid();
  public abstract void runStep();
  public abstract double h(int v);
}
