package org.toolchild.suffering.input;

public class KeyStatus {
  public boolean isActive;
  public long    timeStamp;
  public boolean canActivate;

  public KeyStatus(boolean isActive) {
    this.isActive = isActive;
    this.timeStamp = System.nanoTime();
    this.canActivate = true;
  }
}
