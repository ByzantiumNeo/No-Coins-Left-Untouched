package org.java_websocket.framing;

import org.java_websocket.enums.Opcode;

public class PongFrame extends ControlFrame {
  public PongFrame() {
    super(Opcode.PONG);
  }
  
  public PongFrame(PingFrame pingFrame) {
    super(Opcode.PONG);
    setPayload(pingFrame.getPayloadData());
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\framing\PongFrame.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */