package org.java_websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import org.java_websocket.interfaces.ISSLChannel;
import org.java_websocket.util.ByteBufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLSocketChannel implements WrappedByteChannel, ByteChannel, ISSLChannel {
  private final Logger log = LoggerFactory.getLogger(SSLSocketChannel.class);
  
  private final SocketChannel socketChannel;
  
  private final SSLEngine engine;
  
  private ByteBuffer myAppData;
  
  private ByteBuffer myNetData;
  
  private ByteBuffer peerAppData;
  
  private ByteBuffer peerNetData;
  
  private ExecutorService executor;
  
  public SSLSocketChannel(SocketChannel inputSocketChannel, SSLEngine inputEngine, ExecutorService inputExecutor, SelectionKey key) throws IOException {
    if (inputSocketChannel == null || inputEngine == null || this.executor == inputExecutor)
      throw new IllegalArgumentException("parameter must not be null"); 
    this.socketChannel = inputSocketChannel;
    this.engine = inputEngine;
    this.executor = inputExecutor;
    this.myNetData = ByteBuffer.allocate(this.engine.getSession().getPacketBufferSize());
    this.peerNetData = ByteBuffer.allocate(this.engine.getSession().getPacketBufferSize());
    this.engine.beginHandshake();
    if (doHandshake()) {
      if (key != null)
        key.interestOps(key.interestOps() | 0x4); 
    } else {
      try {
        this.socketChannel.close();
      } catch (IOException e) {
        this.log.error("Exception during the closing of the channel", e);
      } 
    } 
  }
  
  public synchronized int read(ByteBuffer dst) throws IOException {
    if (!dst.hasRemaining())
      return 0; 
    if (this.peerAppData.hasRemaining()) {
      this.peerAppData.flip();
      return ByteBufferUtils.transferByteBuffer(this.peerAppData, dst);
    } 
    this.peerNetData.compact();
    int bytesRead = this.socketChannel.read(this.peerNetData);
    if (bytesRead > 0 || this.peerNetData.hasRemaining()) {
      this.peerNetData.flip();
      if (this.peerNetData.hasRemaining()) {
        SSLEngineResult result;
        this.peerAppData.compact();
        try {
          result = this.engine.unwrap(this.peerNetData, this.peerAppData);
        } catch (SSLException e) {
          this.log.error("SSLException during unwrap", e);
          throw e;
        } 
        switch (result.getStatus()) {
          case FINISHED:
            this.peerAppData.flip();
            return ByteBufferUtils.transferByteBuffer(this.peerAppData, dst);
          case NEED_UNWRAP:
            this.peerAppData.flip();
            return ByteBufferUtils.transferByteBuffer(this.peerAppData, dst);
          case NEED_WRAP:
            this.peerAppData = enlargeApplicationBuffer(this.peerAppData);
            return read(dst);
          case NEED_TASK:
            closeConnection();
            dst.clear();
            return -1;
        } 
        throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
      } 
    } else if (bytesRead < 0) {
      handleEndOfStream();
    } 
    ByteBufferUtils.transferByteBuffer(this.peerAppData, dst);
    return bytesRead;
  }
  
  public synchronized int write(ByteBuffer output) throws IOException {
    int num = 0;
    while (output.hasRemaining()) {
      this.myNetData.clear();
      SSLEngineResult result = this.engine.wrap(output, this.myNetData);
      switch (result.getStatus()) {
        case FINISHED:
          this.myNetData.flip();
          while (this.myNetData.hasRemaining())
            num += this.socketChannel.write(this.myNetData); 
          continue;
        case NEED_WRAP:
          this.myNetData = enlargePacketBuffer(this.myNetData);
          continue;
        case NEED_UNWRAP:
          throw new SSLException("Buffer underflow occurred after a wrap. I don't think we should ever get here.");
        case NEED_TASK:
          closeConnection();
          return 0;
      } 
      throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
    } 
    return num;
  }
  
  private boolean doHandshake() throws IOException {
    int appBufferSize = this.engine.getSession().getApplicationBufferSize();
    this.myAppData = ByteBuffer.allocate(appBufferSize);
    this.peerAppData = ByteBuffer.allocate(appBufferSize);
    this.myNetData.clear();
    this.peerNetData.clear();
    SSLEngineResult.HandshakeStatus handshakeStatus = this.engine.getHandshakeStatus();
    boolean handshakeComplete = false;
    while (!handshakeComplete) {
      SSLEngineResult result;
      Runnable task;
      switch (handshakeStatus) {
        case FINISHED:
          handshakeComplete = !this.peerNetData.hasRemaining();
          if (handshakeComplete)
            return true; 
          this.socketChannel.write(this.peerNetData);
          continue;
        case NEED_UNWRAP:
          if (this.socketChannel.read(this.peerNetData) < 0) {
            if (this.engine.isInboundDone() && this.engine.isOutboundDone())
              return false; 
            try {
              this.engine.closeInbound();
            } catch (SSLException sSLException) {}
            this.engine.closeOutbound();
            handshakeStatus = this.engine.getHandshakeStatus();
            continue;
          } 
          this.peerNetData.flip();
          try {
            result = this.engine.unwrap(this.peerNetData, this.peerAppData);
            this.peerNetData.compact();
            handshakeStatus = result.getHandshakeStatus();
          } catch (SSLException sslException) {
            this.engine.closeOutbound();
            handshakeStatus = this.engine.getHandshakeStatus();
            continue;
          } 
          switch (result.getStatus()) {
            case FINISHED:
              continue;
            case NEED_WRAP:
              this.peerAppData = enlargeApplicationBuffer(this.peerAppData);
              continue;
            case NEED_UNWRAP:
              this.peerNetData = handleBufferUnderflow(this.peerNetData);
              continue;
            case NEED_TASK:
              if (this.engine.isOutboundDone())
                return false; 
              this.engine.closeOutbound();
              handshakeStatus = this.engine.getHandshakeStatus();
              continue;
          } 
          throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
        case NEED_WRAP:
          this.myNetData.clear();
          try {
            result = this.engine.wrap(this.myAppData, this.myNetData);
            handshakeStatus = result.getHandshakeStatus();
          } catch (SSLException sslException) {
            this.engine.closeOutbound();
            handshakeStatus = this.engine.getHandshakeStatus();
            continue;
          } 
          switch (result.getStatus()) {
            case FINISHED:
              this.myNetData.flip();
              while (this.myNetData.hasRemaining())
                this.socketChannel.write(this.myNetData); 
              continue;
            case NEED_WRAP:
              this.myNetData = enlargePacketBuffer(this.myNetData);
              continue;
            case NEED_UNWRAP:
              throw new SSLException("Buffer underflow occurred after a wrap. I don't think we should ever get here.");
            case NEED_TASK:
              try {
                this.myNetData.flip();
                while (this.myNetData.hasRemaining())
                  this.socketChannel.write(this.myNetData); 
                this.peerNetData.clear();
              } catch (Exception e) {
                handshakeStatus = this.engine.getHandshakeStatus();
              } 
              continue;
          } 
          throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
        case NEED_TASK:
          while ((task = this.engine.getDelegatedTask()) != null)
            this.executor.execute(task); 
          handshakeStatus = this.engine.getHandshakeStatus();
          continue;
        case NOT_HANDSHAKING:
          continue;
      } 
      throw new IllegalStateException("Invalid SSL status: " + handshakeStatus);
    } 
    return true;
  }
  
  private ByteBuffer enlargePacketBuffer(ByteBuffer buffer) {
    return enlargeBuffer(buffer, this.engine.getSession().getPacketBufferSize());
  }
  
  private ByteBuffer enlargeApplicationBuffer(ByteBuffer buffer) {
    return enlargeBuffer(buffer, this.engine.getSession().getApplicationBufferSize());
  }
  
  private ByteBuffer enlargeBuffer(ByteBuffer buffer, int sessionProposedCapacity) {
    if (sessionProposedCapacity > buffer.capacity()) {
      buffer = ByteBuffer.allocate(sessionProposedCapacity);
    } else {
      buffer = ByteBuffer.allocate(buffer.capacity() * 2);
    } 
    return buffer;
  }
  
  private ByteBuffer handleBufferUnderflow(ByteBuffer buffer) {
    if (this.engine.getSession().getPacketBufferSize() < buffer.limit())
      return buffer; 
    ByteBuffer replaceBuffer = enlargePacketBuffer(buffer);
    buffer.flip();
    replaceBuffer.put(buffer);
    return replaceBuffer;
  }
  
  private void closeConnection() throws IOException {
    this.engine.closeOutbound();
    try {
      doHandshake();
    } catch (IOException iOException) {}
    this.socketChannel.close();
  }
  
  private void handleEndOfStream() throws IOException {
    try {
      this.engine.closeInbound();
    } catch (Exception e) {
      this.log.error("This engine was forced to close inbound, without having received the proper SSL/TLS close notification message from the peer, due to end of stream.");
    } 
    closeConnection();
  }
  
  public boolean isNeedWrite() {
    return false;
  }
  
  public void writeMore() throws IOException {}
  
  public boolean isNeedRead() {
    return (this.peerNetData.hasRemaining() || this.peerAppData.hasRemaining());
  }
  
  public int readMore(ByteBuffer dst) throws IOException {
    return read(dst);
  }
  
  public boolean isBlocking() {
    return this.socketChannel.isBlocking();
  }
  
  public boolean isOpen() {
    return this.socketChannel.isOpen();
  }
  
  public void close() throws IOException {
    closeConnection();
  }
  
  public SSLEngine getSSLEngine() {
    return this.engine;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\SSLSocketChannel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */