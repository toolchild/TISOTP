package org.toolchild.suffering;

import java.awt.Graphics;

import org.toolchild.suffering.entity.Entity;

/**
 * When created it is locked to the {@link Entity.Player}.
 */
public class Camera {
  private int x;
  private int y;

  public void tick(Entity player) {
    if (player.id == Id.player) {
      moveX(player);
      moveY(player);
    }
  }

  private void moveX(Entity player) {
    int middleX = (-player.x -player.width/2 )+ (int) (Game.SIZE.getWidth() / 2); // middle of the player x in the middleX of the camera
    x = (int) (x +  (middleX- x)* 0.04);
  }

  private void moveY(Entity player) {
    int middleY = (-player.y - player.height )+ (int) (Game.SIZE.getHeight() / 2.5); // lowest end of the player y in the middleY of the camera
   y = (int) (y +  (middleY- y)* 0.1);
  }

  public void releaseGraphicsFromCamera(Graphics graphics) {
    graphics.translate(x, y);
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
