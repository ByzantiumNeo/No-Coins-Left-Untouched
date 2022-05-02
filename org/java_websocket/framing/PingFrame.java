package org.java_websocket.framing;

import org.java_websocket.enums.Opcode;

public class PingFrame extends ControlFrame {
  public PingFrame() {
    super(Opcode.PING);
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\framing\PingFrame.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */