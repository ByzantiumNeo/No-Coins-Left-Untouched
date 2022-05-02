package org.java_websocket.handshake;

public class HandshakeImpl1Client extends HandshakedataImpl1 implements ClientHandshakeBuilder {
  private String resourceDescriptor = "*";
  
  public void setResourceDescriptor(String resourceDescriptor) {
    if (resourceDescriptor == null)
      throw new IllegalArgumentException("http resource descriptor must not be null"); 
    this.resourceDescriptor = resourceDescriptor;
  }
  
  public String getResourceDescriptor() {
    return this.resourceDescriptor;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\handshake\HandshakeImpl1Client.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */