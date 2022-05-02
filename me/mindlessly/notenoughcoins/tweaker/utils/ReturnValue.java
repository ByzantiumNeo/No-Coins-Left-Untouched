package me.mindlessly.notenoughcoins.tweaker.utils;

public class ReturnValue<R> {
  private boolean cancelled;
  
  private R returnValue;
  
  public void cancel() {
    cancel(null);
  }
  
  public void cancel(R returnValue) {
    this.cancelled = true;
    this.returnValue = returnValue;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public R getReturnValue() {
    return this.returnValue;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\tweake\\utils\ReturnValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */