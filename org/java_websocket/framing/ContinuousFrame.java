package org.java_websocket.framing;

import org.java_websocket.enums.Opcode;

public class ContinuousFrame extends DataFrame {
  public ContinuousFrame() {
    super(Opcode.CONTINUOUS);
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\framing\ContinuousFrame.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */