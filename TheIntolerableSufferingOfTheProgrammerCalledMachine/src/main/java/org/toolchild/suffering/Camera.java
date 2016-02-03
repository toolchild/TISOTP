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
      x = -player.x + (int) Game.SIZE.getWidth() / 2;
      y = -player.y + (int) Game.SIZE.getHeight() / 3;
    }
  }
  
  public void releaseGraphicsFromCamera(Graphics graphics) {
    graphics.translate(x,y);
  }
  
  public void lockGraphicsToCamera(Graphics graphics) {
    graphics.translate(-x, -y); // lock graphics to camera again
  }
}
