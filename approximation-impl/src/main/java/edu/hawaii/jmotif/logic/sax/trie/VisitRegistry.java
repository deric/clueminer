package edu.hawaii.jmotif.logic.sax.trie;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * The convenient way to keep track of visited locations.
 *
 * @author Pavel Senin.
 */
public class VisitRegistry {

  private final HashSet<Integer> unvisited;
  private final HashSet<Integer> visited;
  private Random randomizer;

  /**
   * Constructor.
   *
   * @param capacity The initial capacity.
   */
  public VisitRegistry(Integer capacity) {
    this.unvisited = new HashSet<Integer>(capacity);
    this.visited = new HashSet<Integer>(capacity);
    for (int i = 0; i < capacity; i++) {
      this.unvisited.add(i);
    }
  }

  /**
   * Mark as visited certain location.
   *
   * @param i The location to mark.
   */
  public void markVisited(Integer i) {
    this.visited.add(i);
    this.unvisited.remove(i);
    // System.out.println("registry: visited " + i + ", left: " + this.unvisited.size());
  }

  /**
   * Get the next random unvisited position.
   *
   * @return The next unvisited position.
   */
  public int getNextRandomUnvisitedPosition() {
    if (this.unvisited.isEmpty()) {
      return -1;
    }
    int size = this.unvisited.size();
    if (null == this.randomizer) {
      this.randomizer = new Random();
    }
    int item = this.randomizer.nextInt(size);
    int i = 0;
    Iterator<Integer> iter = this.unvisited.iterator();
    int res = iter.next();
    while (i < item) {
      res = iter.next();
      i++;
    }
    return res;
  }

  /**
   * Check if position is not visited.
   *
   * @param i The index.
   * @return true if not visited.
   */
  public boolean isNnotVisited(Integer i) {
    return this.unvisited.contains(i);
  }

  /**
   * Check if position was visited.
   *
   * @param i The position.
   * @return True if visited.
   */
  public boolean isVisited(Integer i) {
    return this.visited.contains(i);
  }

  /**
   * Get the list of unvisited positions.
   *
   * @return list of unvisited positions.
   */
  public HashSet<Integer> getUnvisited() {
    return this.unvisited;
  }

  /**
   * Get the list of visited positions.
   *
   * @return list of visited positions.
   */
  public HashSet<Integer> getVisited() {
    return this.visited;
  }

}
