package edu.hawaii.jmotif.logic.sax.trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The motif records collection.
 *
 * @author Pavel Senin
 *
 */
public class MotifRecords {

  /** Default capacity. */
  private static final Integer defaultCapacity = 10;

  /** Storage container. */
  private final ArrayList<MotifRecord> motifs;

  // max capacity var
  private Integer maxCapacity;

  /**
   * Constructor.
   */
  public MotifRecords() {
    this.maxCapacity = defaultCapacity;
    this.motifs = new ArrayList<MotifRecord>(this.maxCapacity);
  }

  /**
   * Constructor.
   *
   * @param capacity The initial capacity.
   */
  public MotifRecords(int capacity) {
    this.maxCapacity = capacity;
    this.motifs = new ArrayList<MotifRecord>(this.maxCapacity);
  }

  /**
   * Set the max capacity of this collection. The overflow elements will be pushed into the
   * collection pushing out ones with less distance value.
   *
   * @param newSize The size to set.
   */
  public void setMaxCapacity(int newSize) {
    this.maxCapacity = newSize;
  }

  /**
   * Add a new motif to the list. Here is a trick. This method will also check if the current
   * distance is less than best so far (best in the table). If so - there is no need to continue
   * that inner loop - the MAGIC optimization.
   *
   * @param motif The motif instance to add.
   * @return if the motif got added.
   */
  public boolean add(MotifRecord motif) {

    // System.out.println(" + discord record " + discord);

    // check if here is still a room for the new element
    //
    if (this.motifs.size() < this.maxCapacity) {
      if (this.motifs.size() == 0) {
        assert true;
      }
      // if so - just add it in
      this.motifs.add(motif);
    }
    else {
      // more complicated - need to check if it will fit in there
      // DiscordRecord last = discords.get(discords.size() - 1);
      MotifRecord first = motifs.get(0);
      if (first.getFrequency() < motif.getFrequency()) {
        // System.out.println(" - discord record " + discords.get(0));
        motifs.remove(0);
        motifs.add(motif);
      }
    }
    Collections.sort(motifs);
    if (this.motifs.get(0).compareTo(motif) > 0) {
      return true;
    }
    return false;
  }

  /**
   * Returns the number of the top hits.
   *
   * @param num The number of instances to return. If the number larger than the storage size -
   * returns the storage as is.
   * @return the top motif hits.
   */
  public List<MotifRecord> getTopHits(Integer num) {
    Collections.sort(this.motifs);
    if (num >= this.motifs.size()) {
      Collections.reverse(this.motifs);
      return this.motifs;
    }
    List<MotifRecord> res = this.motifs.subList(this.motifs.size() - num, this.motifs.size());
    Collections.reverse(res);
    return res;
  }

  /**
   * Get the minimal distance found among all instances in the collection.
   *
   * @return The minimal distance found among all instances in the collection.
   */
  public double getMinDistance() {
    if (this.motifs.size() > 0) {
      return motifs.get(0).getFrequency();
    }
    return -1;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(1024);
    for (MotifRecord r : motifs) {
      sb.append(r.getFrequency()).append(" at ");
      sb.append(Arrays.toString(r.getPositions())).append("\n");
    }
    return sb.toString();
  }
}
