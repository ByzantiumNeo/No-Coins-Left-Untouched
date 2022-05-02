package org.slf4j.helpers;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class NamedLoggerBase implements Logger, Serializable {
  private static final long serialVersionUID = 7535258609338176893L;
  
  protected String name;
  
  public String getName() {
    return this.name;
  }
  
  protected Object readResolve() throws ObjectStreamException {
    return LoggerFactory.getLogger(getName());
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\slf4j\helpers\NamedLoggerBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */