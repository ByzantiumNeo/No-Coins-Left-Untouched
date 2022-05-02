package org.slf4j.helpers;

import java.util.Map;
import org.slf4j.spi.MDCAdapter;

public class NOPMDCAdapter implements MDCAdapter {
  public void clear() {}
  
  public String get(String key) {
    return null;
  }
  
  public void put(String key, String val) {}
  
  public void remove(String key) {}
  
  public Map<String, String> getCopyOfContextMap() {
    return null;
  }
  
  public void setContextMap(Map<String, String> contextMap) {}
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\slf4j\helpers\NOPMDCAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */