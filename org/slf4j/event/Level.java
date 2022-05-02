package org.slf4j.event;

public enum Level {
  ERROR(40, "ERROR"),
  WARN(30, "WARN"),
  INFO(20, "INFO"),
  DEBUG(10, "DEBUG"),
  TRACE(0, "TRACE");
  
  private String levelStr;
  
  private int levelInt;
  
  Level(int i, String s) {
    this.levelInt = i;
    this.levelStr = s;
  }
  
  public int toInt() {
    return this.levelInt;
  }
  
  public String toString() {
    return this.levelStr;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\slf4j\event\Level.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */