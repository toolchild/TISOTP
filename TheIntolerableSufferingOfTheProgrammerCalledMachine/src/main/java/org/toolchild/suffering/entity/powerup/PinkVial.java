package org.toolchild.suffering.entity.powerup;

import java.awt.Graphics;
import java.util.Random;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.entity.Entity;
import org.toolchild.suffering.tile.Tile;
import org.toolchild.suffering.tile.Wall;

import org.toolchild.suffering.Camera;

public class PinkVial extends Entity {
  private static final Logger log        = Logger.getLogger(PinkVial.class);
  private int                 frame      = 0;
  private int                 frameDelay = 0;

  private Random              random     = new Random();

  public PinkVial(int x, int y, int width, int height, boolean isSolid, Id id, Handler handler) {
    super(x, y, width, height, isSolid, id, handler);
    int direction = random.nextInt(2); // direction: 0 = left ; 1 = right
    movement.setVelocityX(direction == 0 ? -3 : +3);
    movement.setMoveSpeed(3);

  }

  @Override
  public void tick() {
    log.trace("update position: " + updatePosition() + " x: " + x + " y: " + y);
    frameDelay++;
    if (frameDelay >= 3) {
      frame++;
      if (frame >= Game.player.length / 2) {
        frame = 0;
      }
      frameDelay = 0;
    }
    log.trace("handle all tile interaction: " + handleAllTileInteraction());
    log.trace("handle gravity and movement: " + handleGravityAndMovement());
  }

  private boolean updatePosition() {
    x = x + movement.getVelocityX();
    y = y + movement.getVelocityY();
    // TODO: use for level bounds
    // if (x <= 0) x = 0;
    // if (x + this.width >= Game.SIZE.getWidth()) x = (int) (Game.SIZE.getWidth() - this.width);
    return true;
  }

  private boolean handleGravityAndMovement() {
    log.trace("handle Falling " + movement.handlePinkVialFalling());
    log.trace("handle Floating " + movement.handleFloating());
    return true;
  }

  private boolean handleAllTileInteraction() {
    for (Tile tile : handler.tiles) {
      if (tile.getX() >= x - 64 && tile.getX() <= x + 64) { // only tick tiles immediately around the entity
        if (tile.getY() >= y - 64 && tile.getY() <= y + 64) {
          String singleTileInteractionStatusMessage = handleSingleTileInteraction(tile);
          if (singleTileInteractionStatusMessage != null) {
            log.trace("single tile interaction: " + singleTileInteractionStatusMessage);
          }
        }
      }
    }
    return true;
  }

  private String handleSingleTileInteraction(Tile tile) {  // handleAllTileInteraction sub-method
    String statusMessage;
    if (!tile.isSolid()) {
      statusMessage = "false, tile not solid";
    } else if (tile.id == Id.wall && tile instanceof Wall) {
      Wall wall = (Wall) tile;
      statusMessage = handleWallInteraction(wall);
    } else {
      statusMessage = "false, tile id not recognized";
    }

    return statusMessage;
  }



  

  private String handleWallInteraction(Wall wall) {
    String statusMessage = null;
    if (getBoundsTop().intersects(wall.getBounds())) {
      statusMessage = "wall interaction: hitTop";
      y = wall.getY() + wall.getHeight();
      movement.setVelocityY(0);
      if (movement.isJumping()) {
        // isJumping = false;
        movement.setGravity(0.0);
        movement.setFalling(true);
      }
    } else if (getBoundsBottom().intersects(wall.getBounds())) {
      statusMessage = "wall interaction: hitBottom";
      movement.setVelocityY(0);
      movement.setJumping(false);
      y = wall.getY() - height; // reset height, looks cleaner
      if (movement.isFalling()) {
        movement.setFalling(false);
      }
    } else if (getBoundsLeft().intersects(wall.getBounds())) {
      movement.setVelocityX(movement.getMoveSpeed());
      x = wall.getX() + wall.getWidth();
    } else if (getBoundsRight().intersects(wall.getBounds())) {
      statusMessage = "wall interaction: hitRight";
      movement.setVelocityX(-movement.getMoveSpeed());
      x = wall.getX() - width; // reset width, looks cleaner
    }
    return statusMessage;
  }

  @Override
  public void render(Graphics graphics, Camera camera) {
    graphics.drawImage(Game.powerup[frame].getImage(), x, y, width, height, null);
  }
}
