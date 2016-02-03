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
    velocityX = direction == 0 ? -3 : +3;
    moveSpeed = 3;

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
    x = x + velocityX;
    y = y + velocityY;
    // TODO: use for level bounds
    // if (x <= 0) x = 0;
    // if (x + this.width >= Game.SIZE.getWidth()) x = (int) (Game.SIZE.getWidth() - this.width);
    return true;
  }

  private boolean handleGravityAndMovement() {
    log.trace("handle Falling " + handleFalling());
    log.trace("handle Floating " + handleFloating());
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

  private boolean handleFloating() {  // handleGravityAndMovement sub-method
    if (!isFalling && !isJumping) { // if neither jumping nor falling player either stands on a block or hangs in the air. So it is necessary to start falling again. If on block, player will hit block and reset height and stop falling.
      gravity = 0.0;  // handle gravity
      isFalling = true;  // handle movement
    }
    return true;
  }

  private boolean handleFalling() {  // handleGravityAndMovement sub-method
    if (isFalling) {
      gravity = gravity + 0.1;  // handle gravity
      log.trace("Falling gravity = " + gravity);
      velocityY = (int) gravity;  // handle movement
    }
    return true;
  }

  private String handleWallInteraction(Wall wall) {
    String statusMessage = null;
    if (getBoundsTop().intersects(wall.getBounds())) {
      statusMessage = "wall interaction: hitTop";
      y = wall.getY() + wall.getHeight();
      velocityY = 0;
      if (isJumping) {
        // isJumping = false;
        gravity = 0.0;
        isFalling = true;
      }
    } else if (getBoundsBottom().intersects(wall.getBounds())) {
      statusMessage = "wall interaction: hitBottom";
      velocityY = 0;
      isJumping = false;
      y = wall.getY() - height; // reset height, looks cleaner
      if (isFalling) {
        isFalling = false;
      }
    } else if (getBoundsLeft().intersects(wall.getBounds())) {
      velocityX = moveSpeed;
      x = wall.getX() + wall.getWidth();
    } else if (getBoundsRight().intersects(wall.getBounds())) {
      statusMessage = "wall interaction: hitRight";
      velocityX = -moveSpeed;
      x = wall.getX() - width; // reset width, looks cleaner
    }
    return statusMessage;
  }

  @Override
  public void render(Graphics graphics, Camera camera) {
    graphics.drawImage(Game.powerup[frame].getImage(), x, y, width, height, null);
  }
}
