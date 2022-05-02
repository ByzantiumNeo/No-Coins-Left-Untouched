package org.java_websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.net.ssl.SSLSession;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.enums.CloseHandshakeType;
import org.java_websocket.enums.HandshakeState;
import org.java_websocket.enums.Opcode;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.enums.Role;
import org.java_websocket.exceptions.IncompleteHandshakeException;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.exceptions.LimitExceededException;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PingFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.interfaces.ISSLChannel;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.util.Charsetfunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketImpl implements WebSocket {
  public static final int DEFAULT_PORT = 80;
  
  public static final int DEFAULT_WSS_PORT = 443;
  
  public static final int RCVBUF = 16384;
  
  private final Logger log = LoggerFactory.getLogger(WebSocketImpl.class);
  
  public final BlockingQueue<ByteBuffer> outQueue;
  
  public final BlockingQueue<ByteBuffer> inQueue;
  
  private final WebSocketListener wsl;
  
  private SelectionKey key;
  
  private ByteChannel channel;
  
  private WebSocketServer.WebSocketWorker workerThread;
  
  private boolean flushandclosestate = false;
  
  private volatile ReadyState readyState = ReadyState.NOT_YET_CONNECTED;
  
  private List<Draft> knownDrafts;
  
  private Draft draft = null;
  
  private Role role;
  
  private ByteBuffer tmpHandshakeBytes = ByteBuffer.allocate(0);
  
  private ClientHandshake handshakerequest = null;
  
  private String closemessage = null;
  
  private Integer closecode = null;
  
  private Boolean closedremotely = null;
  
  private String resourceDescriptor = null;
  
  private long lastPong = System.nanoTime();
  
  private final Object synchronizeWriteObject = new Object();
  
  private Object attachment;
  
  public WebSocketImpl(WebSocketListener listener, List<Draft> drafts) {
    this(listener, (Draft)null);
    this.role = Role.SERVER;
    if (drafts == null || drafts.isEmpty()) {
      this.knownDrafts = new ArrayList<>();
      this.knownDrafts.add(new Draft_6455());
    } else {
      this.knownDrafts = drafts;
    } 
  }
  
  public WebSocketImpl(WebSocketListener listener, Draft draft) {
    if (listener == null || (draft == null && this.role == Role.SERVER))
      throw new IllegalArgumentException("parameters must not be null"); 
    this.outQueue = new LinkedBlockingQueue<>();
    this.inQueue = new LinkedBlockingQueue<>();
    this.wsl = listener;
    this.role = Role.CLIENT;
    if (draft != null)
      this.draft = draft.copyInstance(); 
  }
  
  public void decode(ByteBuffer socketBuffer) {
    assert socketBuffer.hasRemaining();
    this.log.trace("process({}): ({})", Integer.valueOf(socketBuffer.remaining()), 
        (socketBuffer.remaining() > 1000) ? "too big to display" : new String(socketBuffer
          .array(), socketBuffer.position(), socketBuffer.remaining()));
    if (this.readyState != ReadyState.NOT_YET_CONNECTED) {
      if (this.readyState == ReadyState.OPEN)
        decodeFrames(socketBuffer); 
    } else if (decodeHandshake(socketBuffer) && !isClosing() && !isClosed()) {
      assert this.tmpHandshakeBytes.hasRemaining() != socketBuffer.hasRemaining() || 
        !socketBuffer.hasRemaining();
      if (socketBuffer.hasRemaining()) {
        decodeFrames(socketBuffer);
      } else if (this.tmpHandshakeBytes.hasRemaining()) {
        decodeFrames(this.tmpHandshakeBytes);
      } 
    } 
  }
  
  private boolean decodeHandshake(ByteBuffer socketBufferNew) {
    ByteBuffer socketBuffer;
    if (this.tmpHandshakeBytes.capacity() == 0) {
      socketBuffer = socketBufferNew;
    } else {
      if (this.tmpHandshakeBytes.remaining() < socketBufferNew.remaining()) {
        ByteBuffer buf = ByteBuffer.allocate(this.tmpHandshakeBytes.capacity() + socketBufferNew.remaining());
        this.tmpHandshakeBytes.flip();
        buf.put(this.tmpHandshakeBytes);
        this.tmpHandshakeBytes = buf;
      } 
      this.tmpHandshakeBytes.put(socketBufferNew);
      this.tmpHandshakeBytes.flip();
      socketBuffer = this.tmpHandshakeBytes;
    } 
    socketBuffer.mark();
    try {
      if (this.role == Role.SERVER) {
        if (this.draft == null) {
          for (Draft d : this.knownDrafts) {
            d = d.copyInstance();
            try {
              d.setParseMode(this.role);
              socketBuffer.reset();
              Handshakedata handshakedata = d.translateHandshake(socketBuffer);
              if (!(handshakedata instanceof ClientHandshake)) {
                this.log.trace("Closing due to wrong handshake");
                closeConnectionDueToWrongHandshake(new InvalidDataException(1002, "wrong http function"));
                return false;
              } 
              ClientHandshake clientHandshake = (ClientHandshake)handshakedata;
              HandshakeState handshakeState = d.acceptHandshakeAsServer(clientHandshake);
              if (handshakeState == HandshakeState.MATCHED) {
                ServerHandshakeBuilder response;
                this.resourceDescriptor = clientHandshake.getResourceDescriptor();
                try {
                  response = this.wsl.onWebsocketHandshakeReceivedAsServer(this, d, clientHandshake);
                } catch (InvalidDataException e) {
                  this.log.trace("Closing due to wrong handshake. Possible handshake rejection", (Throwable)e);
                  closeConnectionDueToWrongHandshake(e);
                  return false;
                } catch (RuntimeException e) {
                  this.log.error("Closing due to internal server error", e);
                  this.wsl.onWebsocketError(this, e);
                  closeConnectionDueToInternalServerError(e);
                  return false;
                } 
                write(d.createHandshake((Handshakedata)d
                      .postProcessHandshakeResponseAsServer(clientHandshake, response)));
                this.draft = d;
                open((Handshakedata)clientHandshake);
                return true;
              } 
            } catch (InvalidHandshakeException invalidHandshakeException) {}
          } 
          if (this.draft == null) {
            this.log.trace("Closing due to protocol error: no draft matches");
            closeConnectionDueToWrongHandshake(new InvalidDataException(1002, "no draft matches"));
          } 
          return false;
        } 
        Handshakedata tmphandshake = this.draft.translateHandshake(socketBuffer);
        if (!(tmphandshake instanceof ClientHandshake)) {
          this.log.trace("Closing due to protocol error: wrong http function");
          flushAndClose(1002, "wrong http function", false);
          return false;
        } 
        ClientHandshake handshake = (ClientHandshake)tmphandshake;
        HandshakeState handshakestate = this.draft.acceptHandshakeAsServer(handshake);
        if (handshakestate == HandshakeState.MATCHED) {
          open((Handshakedata)handshake);
          return true;
        } 
        this.log.trace("Closing due to protocol error: the handshake did finally not match");
        close(1002, "the handshake did finally not match");
        return false;
      } 
      if (this.role == Role.CLIENT) {
        this.draft.setParseMode(this.role);
        Handshakedata tmphandshake = this.draft.translateHandshake(socketBuffer);
        if (!(tmphandshake instanceof ServerHandshake)) {
          this.log.trace("Closing due to protocol error: wrong http function");
          flushAndClose(1002, "wrong http function", false);
          return false;
        } 
        ServerHandshake handshake = (ServerHandshake)tmphandshake;
        HandshakeState handshakestate = this.draft.acceptHandshakeAsClient(this.handshakerequest, handshake);
        if (handshakestate == HandshakeState.MATCHED) {
          try {
            this.wsl.onWebsocketHandshakeReceivedAsClient(this, this.handshakerequest, handshake);
          } catch (InvalidDataException e) {
            this.log.trace("Closing due to invalid data exception. Possible handshake rejection", (Throwable)e);
            flushAndClose(e.getCloseCode(), e.getMessage(), false);
            return false;
          } catch (RuntimeException e) {
            this.log.error("Closing since client was never connected", e);
            this.wsl.onWebsocketError(this, e);
            flushAndClose(-1, e.getMessage(), false);
            return false;
          } 
          open((Handshakedata)handshake);
          return true;
        } 
        this.log.trace("Closing due to protocol error: draft {} refuses handshake", this.draft);
        close(1002, "draft " + this.draft + " refuses handshake");
      } 
    } catch (InvalidHandshakeException e) {
      this.log.trace("Closing due to invalid handshake", (Throwable)e);
      close((InvalidDataException)e);
    } catch (IncompleteHandshakeException e) {
      if (this.tmpHandshakeBytes.capacity() == 0) {
        socketBuffer.reset();
        int newsize = e.getPreferredSize();
        if (newsize == 0) {
          newsize = socketBuffer.capacity() + 16;
        } else {
          assert e.getPreferredSize() >= socketBuffer.remaining();
        } 
        this.tmpHandshakeBytes = ByteBuffer.allocate(newsize);
        this.tmpHandshakeBytes.put(socketBufferNew);
      } else {
        this.tmpHandshakeBytes.position(this.tmpHandshakeBytes.limit());
        this.tmpHandshakeBytes.limit(this.tmpHandshakeBytes.capacity());
      } 
    } 
    return false;
  }
  
  private void decodeFrames(ByteBuffer socketBuffer) {
    try {
      List<Framedata> frames = this.draft.translateFrame(socketBuffer);
      for (Framedata f : frames) {
        this.log.trace("matched frame: {}", f);
        this.draft.processFrame(this, f);
      } 
    } catch (LimitExceededException e) {
      if (e.getLimit() == Integer.MAX_VALUE) {
        this.log.error("Closing due to invalid size of frame", (Throwable)e);
        this.wsl.onWebsocketError(this, (Exception)e);
      } 
      close((InvalidDataException)e);
    } catch (InvalidDataException e) {
      this.log.error("Closing due to invalid data in frame", (Throwable)e);
      this.wsl.onWebsocketError(this, (Exception)e);
      close(e);
    } 
  }
  
  private void closeConnectionDueToWrongHandshake(InvalidDataException exception) {
    write(generateHttpResponseDueToError(404));
    flushAndClose(exception.getCloseCode(), exception.getMessage(), false);
  }
  
  private void closeConnectionDueToInternalServerError(RuntimeException exception) {
    write(generateHttpResponseDueToError(500));
    flushAndClose(-1, exception.getMessage(), false);
  }
  
  private ByteBuffer generateHttpResponseDueToError(int errorCode) {
    String errorCodeDescription;
    switch (errorCode) {
      case 404:
        errorCodeDescription = "404 WebSocket Upgrade Failure";
        break;
      default:
        errorCodeDescription = "500 Internal Server Error";
        break;
    } 
    return ByteBuffer.wrap(Charsetfunctions.asciiBytes("HTTP/1.1 " + errorCodeDescription + "\r\nContent-Type: text/html\r\nServer: TooTallNate Java-WebSocket\r\nContent-Length: " + (48 + errorCodeDescription
          
          .length()) + "\r\n\r\n<html><head></head><body><h1>" + errorCodeDescription + "</h1></body></html>"));
  }
  
  public synchronized void close(int code, String message, boolean remote) {
    if (this.readyState != ReadyState.CLOSING && this.readyState != ReadyState.CLOSED) {
      if (this.readyState == ReadyState.OPEN) {
        if (code == 1006) {
          assert !remote;
          this.readyState = ReadyState.CLOSING;
          flushAndClose(code, message, false);
          return;
        } 
        if (this.draft.getCloseHandshakeType() != CloseHandshakeType.NONE)
          try {
            if (!remote)
              try {
                this.wsl.onWebsocketCloseInitiated(this, code, message);
              } catch (RuntimeException e) {
                this.wsl.onWebsocketError(this, e);
              }  
            if (isOpen()) {
              CloseFrame closeFrame = new CloseFrame();
              closeFrame.setReason(message);
              closeFrame.setCode(code);
              closeFrame.isValid();
              sendFrame((Framedata)closeFrame);
            } 
          } catch (InvalidDataException e) {
            this.log.error("generated frame is invalid", (Throwable)e);
            this.wsl.onWebsocketError(this, (Exception)e);
            flushAndClose(1006, "generated frame is invalid", false);
          }  
        flushAndClose(code, message, remote);
      } else if (code == -3) {
        assert remote;
        flushAndClose(-3, message, true);
      } else if (code == 1002) {
        flushAndClose(code, message, remote);
      } else {
        flushAndClose(-1, message, false);
      } 
      this.readyState = ReadyState.CLOSING;
      this.tmpHandshakeBytes = null;
      return;
    } 
  }
  
  public void close(int code, String message) {
    close(code, message, false);
  }
  
  public synchronized void closeConnection(int code, String message, boolean remote) {
    if (this.readyState == ReadyState.CLOSED)
      return; 
    if (this.readyState == ReadyState.OPEN && 
      code == 1006)
      this.readyState = ReadyState.CLOSING; 
    if (this.key != null)
      this.key.cancel(); 
    if (this.channel != null)
      try {
        this.channel.close();
      } catch (IOException e) {
        if (e.getMessage() != null && e.getMessage().equals("Broken pipe")) {
          this.log.trace("Caught IOException: Broken pipe during closeConnection()", e);
        } else {
          this.log.error("Exception during channel.close()", e);
          this.wsl.onWebsocketError(this, e);
        } 
      }  
    try {
      this.wsl.onWebsocketClose(this, code, message, remote);
    } catch (RuntimeException e) {
      this.wsl.onWebsocketError(this, e);
    } 
    if (this.draft != null)
      this.draft.reset(); 
    this.handshakerequest = null;
    this.readyState = ReadyState.CLOSED;
  }
  
  protected void closeConnection(int code, boolean remote) {
    closeConnection(code, "", remote);
  }
  
  public void closeConnection() {
    if (this.closedremotely == null)
      throw new IllegalStateException("this method must be used in conjunction with flushAndClose"); 
    closeConnection(this.closecode.intValue(), this.closemessage, this.closedremotely.booleanValue());
  }
  
  public void closeConnection(int code, String message) {
    closeConnection(code, message, false);
  }
  
  public synchronized void flushAndClose(int code, String message, boolean remote) {
    if (this.flushandclosestate)
      return; 
    this.closecode = Integer.valueOf(code);
    this.closemessage = message;
    this.closedremotely = Boolean.valueOf(remote);
    this.flushandclosestate = true;
    this.wsl.onWriteDemand(this);
    try {
      this.wsl.onWebsocketClosing(this, code, message, remote);
    } catch (RuntimeException e) {
      this.log.error("Exception in onWebsocketClosing", e);
      this.wsl.onWebsocketError(this, e);
    } 
    if (this.draft != null)
      this.draft.reset(); 
    this.handshakerequest = null;
  }
  
  public void eot() {
    if (this.readyState == ReadyState.NOT_YET_CONNECTED) {
      closeConnection(-1, true);
    } else if (this.flushandclosestate) {
      closeConnection(this.closecode.intValue(), this.closemessage, this.closedremotely.booleanValue());
    } else if (this.draft.getCloseHandshakeType() == CloseHandshakeType.NONE) {
      closeConnection(1000, true);
    } else if (this.draft.getCloseHandshakeType() == CloseHandshakeType.ONEWAY) {
      if (this.role == Role.SERVER) {
        closeConnection(1006, true);
      } else {
        closeConnection(1000, true);
      } 
    } else {
      closeConnection(1006, true);
    } 
  }
  
  public void close(int code) {
    close(code, "", false);
  }
  
  public void close(InvalidDataException e) {
    close(e.getCloseCode(), e.getMessage(), false);
  }
  
  public void send(String text) {
    if (text == null)
      throw new IllegalArgumentException("Cannot send 'null' data to a WebSocketImpl."); 
    send(this.draft.createFrames(text, (this.role == Role.CLIENT)));
  }
  
  public void send(ByteBuffer bytes) {
    if (bytes == null)
      throw new IllegalArgumentException("Cannot send 'null' data to a WebSocketImpl."); 
    send(this.draft.createFrames(bytes, (this.role == Role.CLIENT)));
  }
  
  public void send(byte[] bytes) {
    send(ByteBuffer.wrap(bytes));
  }
  
  private void send(Collection<Framedata> frames) {
    if (!isOpen())
      throw new WebsocketNotConnectedException(); 
    if (frames == null)
      throw new IllegalArgumentException(); 
    ArrayList<ByteBuffer> outgoingFrames = new ArrayList<>();
    for (Framedata f : frames) {
      this.log.trace("send frame: {}", f);
      outgoingFrames.add(this.draft.createBinaryFrame(f));
    } 
    write(outgoingFrames);
  }
  
  public void sendFragmentedFrame(Opcode op, ByteBuffer buffer, boolean fin) {
    send(this.draft.continuousFrame(op, buffer, fin));
  }
  
  public void sendFrame(Collection<Framedata> frames) {
    send(frames);
  }
  
  public void sendFrame(Framedata framedata) {
    send(Collections.singletonList(framedata));
  }
  
  public void sendPing() throws NullPointerException {
    PingFrame pingFrame = this.wsl.onPreparePing(this);
    if (pingFrame == null)
      throw new NullPointerException("onPreparePing(WebSocket) returned null. PingFrame to sent can't be null."); 
    sendFrame((Framedata)pingFrame);
  }
  
  public boolean hasBufferedData() {
    return !this.outQueue.isEmpty();
  }
  
  public void startHandshake(ClientHandshakeBuilder handshakedata) throws InvalidHandshakeException {
    this.handshakerequest = (ClientHandshake)this.draft.postProcessHandshakeRequestAsClient(handshakedata);
    this.resourceDescriptor = handshakedata.getResourceDescriptor();
    assert this.resourceDescriptor != null;
    try {
      this.wsl.onWebsocketHandshakeSentAsClient(this, this.handshakerequest);
    } catch (InvalidDataException e) {
      throw new InvalidHandshakeException("Handshake data rejected by client.");
    } catch (RuntimeException e) {
      this.log.error("Exception in startHandshake", e);
      this.wsl.onWebsocketError(this, e);
      throw new InvalidHandshakeException("rejected because of " + e);
    } 
    write(this.draft.createHandshake((Handshakedata)this.handshakerequest));
  }
  
  private void write(ByteBuffer buf) {
    this.log.trace("write({}): {}", Integer.valueOf(buf.remaining()), 
        (buf.remaining() > 1000) ? "too big to display" : new String(buf.array()));
    this.outQueue.add(buf);
    this.wsl.onWriteDemand(this);
  }
  
  private void write(List<ByteBuffer> bufs) {
    synchronized (this.synchronizeWriteObject) {
      for (ByteBuffer b : bufs)
        write(b); 
    } 
  }
  
  private void open(Handshakedata d) {
    this.log.trace("open using draft: {}", this.draft);
    this.readyState = ReadyState.OPEN;
    try {
      this.wsl.onWebsocketOpen(this, d);
    } catch (RuntimeException e) {
      this.wsl.onWebsocketError(this, e);
    } 
  }
  
  public boolean isOpen() {
    return (this.readyState == ReadyState.OPEN);
  }
  
  public boolean isClosing() {
    return (this.readyState == ReadyState.CLOSING);
  }
  
  public boolean isFlushAndClose() {
    return this.flushandclosestate;
  }
  
  public boolean isClosed() {
    return (this.readyState == ReadyState.CLOSED);
  }
  
  public ReadyState getReadyState() {
    return this.readyState;
  }
  
  public void setSelectionKey(SelectionKey key) {
    this.key = key;
  }
  
  public SelectionKey getSelectionKey() {
    return this.key;
  }
  
  public String toString() {
    return super.toString();
  }
  
  public InetSocketAddress getRemoteSocketAddress() {
    return this.wsl.getRemoteSocketAddress(this);
  }
  
  public InetSocketAddress getLocalSocketAddress() {
    return this.wsl.getLocalSocketAddress(this);
  }
  
  public Draft getDraft() {
    return this.draft;
  }
  
  public void close() {
    close(1000);
  }
  
  public String getResourceDescriptor() {
    return this.resourceDescriptor;
  }
  
  long getLastPong() {
    return this.lastPong;
  }
  
  public void updateLastPong() {
    this.lastPong = System.nanoTime();
  }
  
  public WebSocketListener getWebSocketListener() {
    return this.wsl;
  }
  
  public <T> T getAttachment() {
    return (T)this.attachment;
  }
  
  public boolean hasSSLSupport() {
    return this.channel instanceof ISSLChannel;
  }
  
  public SSLSession getSSLSession() {
    if (!hasSSLSupport())
      throw new IllegalArgumentException("This websocket uses ws instead of wss. No SSLSession available."); 
    return ((ISSLChannel)this.channel).getSSLEngine().getSession();
  }
  
  public IProtocol getProtocol() {
    if (this.draft == null)
      return null; 
    if (!(this.draft instanceof Draft_6455))
      throw new IllegalArgumentException("This draft does not support Sec-WebSocket-Protocol"); 
    return ((Draft_6455)this.draft).getProtocol();
  }
  
  public <T> void setAttachment(T attachment) {
    this.attachment = attachment;
  }
  
  public ByteChannel getChannel() {
    return this.channel;
  }
  
  public void setChannel(ByteChannel channel) {
    this.channel = channel;
  }
  
  public WebSocketServer.WebSocketWorker getWorkerThread() {
    return this.workerThread;
  }
  
  public void setWorkerThread(WebSocketServer.WebSocketWorker workerThread) {
    this.workerThread = workerThread;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\WebSocketImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */