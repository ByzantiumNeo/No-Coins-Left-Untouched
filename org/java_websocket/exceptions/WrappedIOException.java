package org.java_websocket.exceptions;

import java.io.IOException;
import org.java_websocket.WebSocket;

public class WrappedIOException extends Exception {
  private final transient WebSocket connection;
  
  private final IOException ioException;
  
  public WrappedIOException(WebSocket connection, IOException ioException) {
    this.connection = connection;
    this.ioException = ioException;
  }
  
  public WebSocket getConnection() {
    return this.connection;
  }
  
  public IOException getIOException() {
    return this.ioException;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\exceptions\WrappedIOException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */