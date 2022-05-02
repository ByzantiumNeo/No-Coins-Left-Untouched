package me.mindlessly.notenoughcoins;

import gg.essential.vigilance.data.Category;
import gg.essential.vigilance.data.PropertyData;
import gg.essential.vigilance.data.SortingBehavior;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

class CustomSorting extends SortingBehavior {
  @NotNull
  public Comparator<? super Category> getCategoryComparator() {
    return (o1, o2) -> 0;
  }
  
  @NotNull
  public Comparator<? super Map.Entry<String, ? extends List<PropertyData>>> getSubcategoryComparator() {
    return (o1, o2) -> 0;
  }
  
  @NotNull
  public Comparator<? super PropertyData> getPropertyComparator() {
    return (o1, o2) -> 0;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\CustomSorting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */