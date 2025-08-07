package assignment1;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Heap<T extends Comparable<T>> {
  private ArrayList<T> ranked;

  public Heap() {
    ranked = new ArrayList<T>();
  }

  public boolean isEmpty() { return ranked.isEmpty(); }

  public int size() { return ranked.size(); }

  public void add(T entry) {
    ranked.add(entry);

    // SiftUp
    int i = ranked.size() - 1;
    int p = (i - 1) / 2;
    while (i > 0) {
      T parent = ranked.get(p);
      if (entry.compareTo(parent) < 0) {
        ranked.set(i, parent);
        i = p;
        p = (i - 1) / 2;
      } else {
        break;
      }
    }
    ranked.set(i, entry);
  }

  public T pop() {
    if (isEmpty()) {
      throw new IllegalStateException("The heap is empty.");
    } else {
      T popped = ranked.get(0);

      // SiftDown
      T entry = ranked.remove(ranked.size() - 1); // should be O(1)

      // 'child' starts as the left child,
      // but may be set to the right child.
      int i = 0;
      int c = 1;
      int r = 2;
      T child, right;
      while (c < ranked.size()) {
        child = ranked.get(c);
        if (r < ranked.size()) {
          right = ranked.get(r);
          if (right.compareTo(child) < 0) {
            c = r;
            child = right;
          }
        }

        if (child.compareTo(entry) < 0) {
          ranked.set(i, child);
          i = c;
          c = 2 * i + 1;
          r = c + 1;
        } else {
          break;
        }
      }

      if (!ranked.isEmpty()) {
        ranked.set(i, entry);
      }

      return popped;
    }
  }

  public Stream<T> unorderedEntries() { return ranked.stream(); }
}
