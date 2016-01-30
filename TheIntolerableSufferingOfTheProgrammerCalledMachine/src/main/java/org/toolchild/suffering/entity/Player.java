package org.toolchild.suffering.entity;

import java.awt.Color;
import java.awt.Graphics;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.tile.Tile;
import org.toolchild.suffering.tile.Wall;

public class Player extends Entity {
  private static final Logger log = Logger.getLogger(Game.class);

  public Player(int x, int y, int width, int height, boolean isSolid, Id id, Handler handler) {
    super(x, y, width, height, isSolid, id, handler);
  }

  @Override
  public void tick() {
    log.trace("update position: " + updatePosition());
    log.trace("handle all tile interaction: " + handleAllTileInteraction());
    log.trace("handle gravity and movement " + handleGravityAndMovement());
  }

  @Override
  public void render(Graphics graphics) {
    graphics.drawImage(Game.player.getImage(), x, y, width, height, null);
    graphics.setColor(Color.RED);
    graphics.fillRect(getBoundsRight().x, getBoundsRight().y, getBoundsRight().width, getBoundsRight().height);
    graphics.fillRect(getBoundsLeft().x, getBoundsLeft().y, getBoundsLeft().width, getBoundsLeft().height);
    graphics.fillRect(getBoundsTop().x, getBoundsTop().y, getBoundsTop().width, getBoundsTop().height);
    graphics.fillRect(getBoundsBottom().x, getBoundsBottom().y, getBoundsBottom().width, getBoundsBottom().height);
  }

  // ________________________________________ tick submethods ________________________________________
  /**
   * Updates the {@link x} and {@link y} coordinates of the player.
   * 
   * @return true if successful, otherwise false
   */
  private boolean updatePosition() {
    x = x + velocityX;
    y = y + velocityY;
    if (x <= 0) x = 0;
    if (x + this.width >= Game.SIZE.getWidth()) x = (int) (Game.SIZE.getWidth() - this.width);
    return true;
  }

  private boolean handleGravityAndMovement() {
    log.trace("handle Jumping " + handleJumping());
    log.trace("handle Falling " + handleFalling());
    log.trace("handle Floating " + handleFloating());
    return true;
  }

  private boolean handleFloating() {
    if (!isFalling && !isJumping) { // if neither jumping nor falling player either stands on a block or hangs in the air. So it is necessary to start falling again. If on block, player will hit block and reset height and stop falling.
      gravity = 0.0; // handle gravity
      isFalling = true; // handle movement
    }
    return true;
  }

  private boolean handleFalling() {
    if (isFalling) {
      gravity = gravity + 0.1; // handle gravity
      log.trace("Falling gravity = " + gravity);
      setVelocityY((int) gravity); // handle movement
    }
    return true;
  }

  private boolean handleJumping() {
    if (isJumping) {
      gravity = gravity + 0.1; // handle gravity
      log.trace("Jumping gravity = " + gravity);
      setVelocityY((int) +gravity); // handle movement
      if (gravity >= 0.0) { // handle movement
        // isJumping = false;
        isFalling = true;
      }
    }
    return true;
  }

  private boolean handleAllTileInteraction() {
    for (Tile tile : handler.tiles) {
      String singleTileInteractionStatusMessage = handleSingleTileInteraction(tile);
      if (singleTileInteractionStatusMessage != null) {
        log.debug("single tile interaction: " + singleTileInteractionStatusMessage);
      }
    }
    return true;
  }

  private String handleSingleTileInteraction(Tile tile) {
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

  /**
   * Handles the players interaction with {@link Wall} object.
   * 
   * @param wall
   *          the
   * @return
   */
  private String handleWallInteraction(Wall wall) {
    String statusMessage = null;
    if (getBoundsTop().intersects(wall.getBounds())) {
      statusMessage = "wall interaction: hitTop";
      y = wall.getY() + wall.getHeight();
      setVelocityY(0);
      if (isJumping) {
        // isJumping = false;
        gravity = 0.0;
        isFalling = true;
      }
    } else if (getBoundsBottom().intersects(wall.getBounds())) {
      statusMessage = "wall interaction: hitBottom";
      setVelocityY(0);
      isJumping = false;
      y = wall.getY() - height; // reset height, looks cleaner
      if (isFalling) {
        isFalling = false;
      }
    } else if (getBoundsLeft().intersects(wall.getBounds())) {
      statusMessage = "wall interaction: hitLeft";
      setVelocityX(0);
      x = wall.getX() + wall.getWidth();
    } else if (getBoundsRight().intersects(wall.getBounds())) {
      statusMessage = "wall interaction: hitRight";
      setVelocityX(0);
      x = wall.getX() - width;
    }

    return statusMessage;
  }
  // ######################################## tick submethods ########################################

}
