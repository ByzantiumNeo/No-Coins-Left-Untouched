package org.slf4j;

import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.impl.StaticMarkerBinder;

public class MarkerFactory {
  static IMarkerFactory MARKER_FACTORY;
  
  private static IMarkerFactory bwCompatibleGetMarkerFactoryFromBinder() throws NoClassDefFoundError {
    try {
      return StaticMarkerBinder.getSingleton().getMarkerFactory();
    } catch (NoSuchMethodError nsme) {
      return StaticMarkerBinder.SINGLETON.getMarkerFactory();
    } 
  }
  
  static {
    try {
      MARKER_FACTORY = bwCompatibleGetMarkerFactoryFromBinder();
    } catch (NoClassDefFoundError e) {
      MARKER_FACTORY = (IMarkerFactory)new BasicMarkerFactory();
    } catch (Exception e) {
      Util.report("Unexpected failure while binding MarkerFactory", e);
    } 
  }
  
  public static Marker getMarker(String name) {
    return MARKER_FACTORY.getMarker(name);
  }
  
  public static Marker getDetachedMarker(String name) {
    return MARKER_FACTORY.getDetachedMarker(name);
  }
  
  public static IMarkerFactory getIMarkerFactory() {
    return MARKER_FACTORY;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\slf4j\MarkerFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */