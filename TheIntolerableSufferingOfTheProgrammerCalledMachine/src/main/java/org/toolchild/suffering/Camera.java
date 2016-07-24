package org.toolchild.suffering;

import java.awt.Graphics;

import org.toolchild.suffering.gameobject.entity.Entity;
import org.toolchild.suffering.gameobject.entity.Player;

/**
 * When created it is locked to the {@link Entity.Player}.
 */
public class Camera {
  private int x;
  private int y;

  /**
   * The camera tick.
   * 
   * @param player Needs the player to follow.
   */
  public void tick(Player player) {
    if (player.getId() == Id.player) {
      moveX(player);
      moveY(player);
    }
  }

  private void moveX(Player player) {
    int middleX = (-player.getX() - player.getWidth() / 2) + Game.getFrameWidth() / 2; // middle of the player x in the middleX of the camera
    this.x = (int) (this.x + (middleX - this.x) * 0.04);
  }

  private void moveY(Player player) {
    int middleY = (int) ((-player.getY() - player.getHeight()) + Game.getFrameHeight() / 2.5); // lowest end of the player y in the middleY of the camera // locked to feet
    this.y = (int) (this.y + (middleY - this.y) * 0.1);
  }

  public void releaseGraphicsFromCamera(Graphics graphics) {
    graphics.translate(this.x, this.y);
  }

  public void lockGraphicsToCamera(Graphics graphics) {
    graphics.translate(-this.x, -this.y); // lock graphics to camera again
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
