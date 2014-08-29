package edu.hawaii.jmotif.logic.apriori;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Implements Itemset. Container for items plus support number.
 *
 * @author Pavel Senin
 *
 */
public class Itemset {

  private final Vector<Token> items;
  private int support;

  /**
   * Constructor. Creates empty itemset with rank 1.
   */
  public Itemset() {
    this.items = new Vector<Token>();
    this.support = 1;
  }

  /**
   * Constructor, creates an itemset out of a list of items.
   *
   * @param l The list of items.
   */
  public Itemset(List<Token> l) {
    this.items = new Vector<Token>();
    this.items.addAll(l);
    Collections.sort(this.items);
    this.support = 1;
  }

  /**
   * Constructor, creates an itemset out of an item.
   *
   * @param item The item.
   */
  public Itemset(Token item) {
    this.items = new Vector<Token>();
    this.items.add(item);
    Collections.sort(this.items);
    this.support = 1;
  }

  /**
   * Add the item to the itemset.
   *
   * @param item The item to add.
   */
  public void append(Token item) {
    if (!this.items.contains(item)) {
      this.items.add(item);
      Collections.sort(this.items);
    }
  }

  /**
   * Adds only entries from the given itemset.
   *
   * @param itemset The itemset to process.
   */
  public void addAll(Itemset itemset) {
    for (Token i : itemset.getItems()) {
      if (!this.items.contains(i)) {
        this.items.add(i);
      }
    }
    Collections.sort(this.items);
  }

  /**
   * Add a new item into this itemset.
   *
   * @param newElement The new Item.
   */
  public void addItem(Token newElement) {
    if (!this.items.contains(newElement)) {
      this.items.add(newElement);
      Collections.sort(this.items);
    }
  }

  /**
   * Get all the items.
   *
   * @return The list of items.
   */
  public Vector<Token> getItems() {
    return this.items;
  }

  /**
   * Set the support value here.
   *
   * @param i The new support value.
   */
  public void setSupport(int i) {
    this.support = i;
  }

  /**
   * Get the support value.
   *
   * @return The support value.
   */
  public int getSupport() {
    return this.support;
  }

  /**
   * Increments the support value.
   *
   * @param increment The value to increment by.
   */
  public void incrementSupport(int increment) {
    this.support += increment;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (!this.items.isEmpty()) {
      result = prime + this.items.hashCode();
    }
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
    Itemset other = (Itemset) obj;
    if (items == null) {
      if (other.items != null) {
        return false;
      }
    }
    else if (!items.equals(other.items)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer(2000);
    sb.append("{");
    for (Token s : this.items) {
      sb.append("'" + s.toString() + "', ");
    }
    if (sb.length() > 2) {
      sb.delete(sb.length() - 2, sb.length());
    }
    sb.append("}");
    sb.append(" x " + this.support + ", hash " + this.hashCode());
    return sb.toString();
  }
}
