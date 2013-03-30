package edu.hawaii.jmotif.logic.sax.datastructures;

import java.util.*;

/**
 * The SAX data structure. Implements optimized storage for the SAX data.
 * 
 * @author Pavel Senin.
 * 
 */
public class SAXFrequencyData implements Iterable<SAXFrequencyEntry> {

  private final HashMap<String, SAXFrequencyEntry> data;

  /**
   * Constructor.
   */
  public SAXFrequencyData() {
    super();
    this.data = new HashMap<String, SAXFrequencyEntry>();
  }

  /**
   * Put the substring with it's index into the storage.
   * 
   * @param substring The substring value.
   * @param idx The substring entry index.
   */
  public void put(String substring, int idx) {
    SAXFrequencyEntry sfe = this.data.get(substring);
    if (null == sfe) {
      this.data.put(substring, new SAXFrequencyEntry(substring, idx));
    }
    else {
      sfe.put(idx);
    }
  }

  /**
   * Get the internal hash size.
   * 
   * @return The number of substrings in the data structure.
   */
  public Integer size() {
    return this.data.size();
  }

  /**
   * Check if the data includes substring.
   * 
   * @param substring The query substring.
   * @return TRUE is contains, FALSE if not.
   */
  public boolean contains(String substring) {
    return this.data.containsKey(substring);
  }

  /**
   * Get the entry information.
   * 
   * @param substring The key get entry for.
   * @return The entry containing the substring occurence frequency information.
   */
  public SAXFrequencyEntry get(String substring) {
    return this.data.get(substring);
  }

  /**
   * Get the set of sorted by the occurence frequencies.
   * 
   * @return The set of sorted by the occurence frequencies.
   */
  public List<SAXFrequencyEntry> getSortedFrequencies() {
    List<SAXFrequencyEntry> l = new ArrayList<SAXFrequencyEntry>();
    l.addAll(this.data.values());
    Collections.sort(l);
    return l;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<SAXFrequencyEntry> iterator() {
    return this.data.values().iterator();
  }

}
