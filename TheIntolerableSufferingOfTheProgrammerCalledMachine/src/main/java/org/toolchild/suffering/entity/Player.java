package org.toolchild.suffering.entity;

import java.awt.Color;
import java.awt.Graphics;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.tile.Tile;

public class Player extends Entity {
  private static final Logger log = Logger.getLogger(Game.class);

  public Player(int x, int y, int width, int height, boolean isSolid, Id id, Handler handler) {
    super(x, y, width, height, isSolid, id, handler);
  }

  @Override
  public void tick() {
    x = x + velocityX;
    y = y + velocityY;
    if (x <= 0) {
      x = 0;
    }
    if (x + this.width >= Game.SIZE.getWidth()) {
      x = (int) (Game.SIZE.getWidth() - this.width);
    }

    for (Tile tile : handler.tiles) {
      if (!tile.isSolid()) {
        break;
      }
      if (tile.id == Id.wall) {
        if (getBoundsTop().intersects(tile.getBounds())) {
          // log.debug("hitTop");
          setVelocityY(0);
          if (isJumping) {
            isJumping = false;
            gravity = 0.0;
            isFalling = true;
          }
        }
        if (getBoundsBottom().intersects(tile.getBounds())) {
           log.debug("hitBottom");
          setVelocityY(0);
          isJumping= false;
          y = tile.getY() - height; // reset height, looks cleaner
          if (isFalling) {
            isFalling = false;
          }
        }
        if (getBoundsLeft().intersects(tile.getBounds())) {
          // log.debug("hitLeft");
          setVelocityX(0);
          x = tile.getX() + tile.getWidth();
        }
        if (getBoundsRight().intersects(tile.getBounds())) {
          // log.debug("hitRight");
          setVelocityX(0);
          x = tile.getX() - width;
        }
      }
    }
    if (isJumping) {
      gravity = gravity + 0.1;
       log.debug("Jumping gravity = "  + gravity);
      setVelocityY((int) + gravity);
      if (gravity >= 0.0) {
//        isJumping = false;
        isFalling = true;
      }
    }
    if (isFalling) {
      gravity = gravity + 0.1;
      log.debug("Falling gravity = " + gravity);
      setVelocityY((int) gravity);
    }

    if (!isFalling && !isJumping) { // if neither jumping nor falling player either stands on a block or hangs in the air. So it is necessary to start falling again. If on block, player will hit block and reset height and stop falling.
      gravity = 0.0;
      isFalling = true;
    }

  }

  @Override
  public void render(Graphics graphics) {
    graphics.setColor(new Color(255, 255, 255));
    graphics.fillRect(x, y, width, height);
    graphics.setColor(Color.BLUE);
    graphics.fillRect(getBoundsRight().x, getBoundsRight().y, getBoundsRight().width, getBoundsRight().height);
    graphics.fillRect(getBoundsLeft().x, getBoundsLeft().y, getBoundsLeft().width, getBoundsLeft().height);
    graphics.fillRect(getBoundsTop().x, getBoundsTop().y, getBoundsTop().width, getBoundsTop().height);
    graphics.fillRect(getBoundsBottom().x, getBoundsBottom().y, getBoundsBottom().width, getBoundsBottom().height);
  }

}
