package org.java_websocket.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
  private final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
  
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  
  private final String threadPrefix;
  
  public NamedThreadFactory(String threadPrefix) {
    this.threadPrefix = threadPrefix;
  }
  
  public Thread newThread(Runnable runnable) {
    Thread thread = this.defaultThreadFactory.newThread(runnable);
    thread.setName(this.threadPrefix + "-" + this.threadNumber);
    return thread;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocke\\util\NamedThreadFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */