package org.toolchild.suffering;


import java.awt.Graphics;

import org.toolchild.suffering.entity.Entity;

/**
 * When created it is locked to the {@link Entity.Player}.
 */
public class Camera {

  public int x;
  public int y;

  public void tick(Entity player) {
    if (player.id == Id.player) {
      x = -player.getX() + (int) Game.SIZE.getWidth() / 2;
      y = -player.getY() + (int) Game.SIZE.getHeight() / 3;
    }
  }
  
  public void releaseGraphicsFromCamera(Graphics graphics) {
    graphics.translate(x,y);
  }
  
  public void lockGraphicsToCamera(Graphics graphics) {
    graphics.translate(-x, -y); // lock graphics to camera again
  }

  public int getX() {
    return this.x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return this.y;
  }

  public void setY(int y) {
    this.y = y;
  }

}
