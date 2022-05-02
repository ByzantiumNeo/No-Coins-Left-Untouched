package org.java_websocket.exceptions;

public class InvalidDataException extends Exception {
  private static final long serialVersionUID = 3731842424390998726L;
  
  private final int closecode;
  
  public InvalidDataException(int closecode) {
    this.closecode = closecode;
  }
  
  public InvalidDataException(int closecode, String s) {
    super(s);
    this.closecode = closecode;
  }
  
  public InvalidDataException(int closecode, Throwable t) {
    super(t);
    this.closecode = closecode;
  }
  
  public InvalidDataException(int closecode, String s, Throwable t) {
    super(s, t);
    this.closecode = closecode;
  }
  
  public int getCloseCode() {
    return this.closecode;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\exceptions\InvalidDataException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */