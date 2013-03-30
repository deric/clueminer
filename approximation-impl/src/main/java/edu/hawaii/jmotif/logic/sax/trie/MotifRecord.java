package edu.hawaii.jmotif.logic.sax.trie;

import java.util.ArrayList;
import java.util.List;

/**
 * The motif data record. Kind of useful of keeping information concerning a motif occurrence etc...
 * 
 * @author Pavel Senin
 * 
 */
public class MotifRecord implements Comparable<MotifRecord> {

  /** Indicates the position of this motif on the timeline. */
  private final ArrayList<Integer> occurrences;

  /** The payload. */
  private String payload;

  /**
   * Constructor.
   */
  public MotifRecord() {
    this.payload = "";
    this.occurrences = new ArrayList<Integer>();
  }

  /**
   * Constructor.
   * 
   * @param str The motif string
   * @param occurences The list of occurences.
   */
  public MotifRecord(char[] str, List<Integer> occurences) {
    StringBuilder sb = new StringBuilder();
    for (char c : str) {
      sb.append(c);
    }
    this.payload = sb.toString();
    this.occurrences = new ArrayList<Integer>();
    this.occurrences.addAll(occurences);
  }

  /**
   * Set the payload value.
   * 
   * @param payload The payload.
   */
  public void setPayload(String payload) {
    this.payload = payload;
  }

  /**
   * Get the payload.
   * 
   * @return The payload.
   */
  public String getPayload() {
    return payload;
  }

  /**
   * Add the position at the time series list.
   * 
   * @param position the position to set.
   */
  public void addIndex(int position) {
    if (!(this.occurrences.contains(position))) {
      this.occurrences.add(position);
    }
  }

  /**
   * Get the positions.
   * 
   * @return the positions at the time series list.
   */
  public int[] getPositions() {
    int[] res = new int[this.occurrences.size()];
    for (int i = 0; i < this.occurrences.size(); i++) {
      res[i] = this.occurrences.get(i).intValue();
    }
    return res;
  }

  /**
   * Get the frequency.
   * 
   * @return Frequency of observance.
   */
  public int getFrequency() {
    return this.occurrences.size();
  }

  /**
   * The simple comparator based on the distance.
   * 
   * @param other The motif record this is compared to.
   * @return True if equals.
   */
  @Override
  public int compareTo(MotifRecord other) {
    if (null == other) {
      throw new NullPointerException("Unable compare to null!");
    }
    if (this.occurrences.size() > other.getFrequency()) {
      return 1;
    }
    else if (this.occurrences.size() < other.getFrequency()) {
      return -1;
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = this.occurrences.size();
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + occurrences.hashCode();
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    MotifRecord other = (MotifRecord) obj;
    if (!this.payload.equalsIgnoreCase(other.getPayload())) {
      return false;
    }
    if (this.occurrences.size() != other.getFrequency()) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "'" + this.payload + "', frequency: " + this.getFrequency() + " positions: "
        + this.occurrences.get(0) + "...";
  }

}
