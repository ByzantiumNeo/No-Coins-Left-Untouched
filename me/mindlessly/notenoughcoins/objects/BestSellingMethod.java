package me.mindlessly.notenoughcoins.objects;

public enum BestSellingMethod {
  NPC("NPC"),
  BAZAAR("Bazaar"),
  LBIN("Lowest-BIN"),
  NONE("None");
  
  private final String string;
  
  BestSellingMethod(String string) {
    this.string = string;
  }
  
  public String toString() {
    return this.string;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\objects\BestSellingMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */