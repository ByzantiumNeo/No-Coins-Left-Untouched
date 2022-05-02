package org.java_websocket.handshake;

public class HandshakeImpl1Server extends HandshakedataImpl1 implements ServerHandshakeBuilder {
  private short httpstatus;
  
  private String httpstatusmessage;
  
  public String getHttpStatusMessage() {
    return this.httpstatusmessage;
  }
  
  public short getHttpStatus() {
    return this.httpstatus;
  }
  
  public void setHttpStatusMessage(String message) {
    this.httpstatusmessage = message;
  }
  
  public void setHttpStatus(short status) {
    this.httpstatus = status;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\handshake\HandshakeImpl1Server.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */