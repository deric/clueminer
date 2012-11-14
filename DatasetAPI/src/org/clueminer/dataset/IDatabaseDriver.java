package org.clueminer.dataset;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Tomas Barton
 */
public interface IDatabaseDriver {
  
  public void connect(String config) throws IOException;
  
  public void disconnect();
  
  public void query(String sql);
  
  public Set getTables() throws IOException;
 
  public Object[][] select(String name) throws IOException;
  
  public Object[][] select(String name, Object[] columns) throws IOException;
  
  public Iterator<Map<String, Object>> iterator(String table) throws IOException;
  
  public Iterator<Map<String, Object>> iterator(String table, Collection<String> columnNames) throws IOException;
  
  public int getRowCount(String name) throws IOException;
  
}
