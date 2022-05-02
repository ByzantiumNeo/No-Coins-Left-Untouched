package org.slf4j.helpers;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Marker;

public class BasicMarker implements Marker {
  private static final long serialVersionUID = -2849567615646933777L;
  
  private final String name;
  
  private List<Marker> referenceList = new CopyOnWriteArrayList<Marker>();
  
  BasicMarker(String name) {
    if (name == null)
      throw new IllegalArgumentException("A marker name cannot be null"); 
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void add(Marker reference) {
    if (reference == null)
      throw new IllegalArgumentException("A null value cannot be added to a Marker as reference."); 
    if (contains(reference))
      return; 
    if (reference.contains(this))
      return; 
    this.referenceList.add(reference);
  }
  
  public boolean hasReferences() {
    return (this.referenceList.size() > 0);
  }
  
  public boolean hasChildren() {
    return hasReferences();
  }
  
  public Iterator<Marker> iterator() {
    return this.referenceList.iterator();
  }
  
  public boolean remove(Marker referenceToRemove) {
    return this.referenceList.remove(referenceToRemove);
  }
  
  public boolean contains(Marker other) {
    if (other == null)
      throw new IllegalArgumentException("Other cannot be null"); 
    if (equals(other))
      return true; 
    if (hasReferences())
      for (Marker ref : this.referenceList) {
        if (ref.contains(other))
          return true; 
      }  
    return false;
  }
  
  public boolean contains(String name) {
    if (name == null)
      throw new IllegalArgumentException("Other cannot be null"); 
    if (this.name.equals(name))
      return true; 
    if (hasReferences())
      for (Marker ref : this.referenceList) {
        if (ref.contains(name))
          return true; 
      }  
    return false;
  }
  
  private static String OPEN = "[ ";
  
  private static String CLOSE = " ]";
  
  private static String SEP = ", ";
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null)
      return false; 
    if (!(obj instanceof Marker))
      return false; 
    Marker other = (Marker)obj;
    return this.name.equals(other.getName());
  }
  
  public int hashCode() {
    return this.name.hashCode();
  }
  
  public String toString() {
    if (!hasReferences())
      return getName(); 
    Iterator<Marker> it = iterator();
    StringBuilder sb = new StringBuilder(getName());
    sb.append(' ').append(OPEN);
    while (it.hasNext()) {
      Marker reference = it.next();
      sb.append(reference.getName());
      if (it.hasNext())
        sb.append(SEP); 
    } 
    sb.append(CLOSE);
    return sb.toString();
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\slf4j\helpers\BasicMarker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */