package org.toolchild.suffering.entity;

import java.awt.Color;
import java.awt.Graphics;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Camera;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.tile.Tile;
import org.toolchild.suffering.tile.Wall;

import com.sun.javafx.binding.StringFormatter;

import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;

public class Player extends Entity {
  private static final Logger log        = Logger.getLogger(Game.class);

  private int                 frame      = 0;
  private int                 frameDelay = 0;
  private boolean             isRunning  = false;

  public Player(int x, int y, int width, int height, boolean isSolid, Id id, Handler handler) {
    super(x, y, width, height, isSolid, id, handler);
  }

  @Override
  public void tick() {
    Game.keyInput.updateKeyEvents(this);
    log.trace("handle gravity and movement " + handleGravityAndMovement());
    log.trace("update position: " + updatePosition());
    log.trace("handle all tile interaction: " + handleAllTileInteraction());
    for (int e = 0; e < handler.entities.size(); e++) {
      Entity entity = handler.entities.get(e);
      if(entity.id == Id.pinkVial){
        if(getBounds().intersects(entity.getBounds())){
          x = x - width;
          y = y - height;
          width = width *2;
          height = height*2;
          entity.die();
        }
      }
    }
  }

  @Override
  public void render(Graphics graphics, Camera camera) {
    int lineHeight = 20;
    renderPlayer(graphics); // renders the player and everything tied to its position
    graphics.setColor(Color.WHITE);
    renderDebug(graphics, camera, lineHeight); // renders the debug messages relative to player
    
  }

  private void renderPlayer(Graphics graphics) {
    if (facing == 0) {
      log.debug("facing left frame " + frame);
      if (!isRunning) {
        frame = 0;
      }
      graphics.drawImage(Game.player[frame].getImage(), x, y, width, height, null);
    }
    if (facing == 1) {
      log.debug("facing right frame:" + frame);
      if (!isRunning) {
        frame = 0;
      }
      graphics.drawImage(Game.player[frame + 5].getImage(), x, y, width, height, null);
    }
    graphics.setColor(Color.RED);
    graphics.fillRect(getBoundsRight().x, getBoundsRight().y, getBoundsRight().width, getBoundsRight().height);
    graphics.fillRect(getBoundsLeft().x, getBoundsLeft().y, getBoundsLeft().width, getBoundsLeft().height);
    graphics.fillRect(getBoundsTop().x, getBoundsTop().y, getBoundsTop().width, getBoundsTop().height);
    graphics.fillRect(getBoundsBottom().x, getBoundsBottom().y, getBoundsBottom().width, getBoundsBottom().height);
  }
  
  private void renderDebug(Graphics graphics, Camera camera, int lineHeight) {
    graphics.translate(-camera.getX(), -camera.getY()); // since nothing is tied to play, the graphics object is tied to camera
    graphics.drawString("player x : " + x, 20 , 3 * lineHeight);
    graphics.drawString("player y : " + y,  20, 4 * lineHeight);
    graphics.drawString("camera x : " + camera.getX(), 100 , 3 * lineHeight);
    graphics.drawString("camera y : " + camera.getY(),  100, 4 * lineHeight);
    
    graphics.drawString("player velocityX : " + velocityX, 20, + 5 * lineHeight);
    graphics.drawString("player velocityY: " + velocityY, + 20, + 6 * lineHeight);
    int gravityStringLength = Double.toString(gravity).length();
    if (gravityStringLength > 5) {
      gravityStringLength = 5;
    }
    graphics.drawString("player gravity : " + Double.toString(gravity).substring(0, gravityStringLength), 20,  7 * lineHeight);
    graphics.drawString("player facing : " + (facing == 0 ? "Left" : "Right"), 20, 8 * lineHeight);
    graphics.drawString("player isRunning: " + isRunning, 20, 9 * lineHeight);
    graphics.translate(camera.getX(), camera.getY()); // untying graphics from camera
  }

  

  // ________________________________________ tick sub-methods ________________________________________
  /**
   * Updates the {@link x} and {@link y} coordinates of the player.
   * 
   * @return true if successful, otherwise false
   */
  private boolean updatePosition() {
    x = x + velocityX;
    y = y + velocityY;
// if (x <= 0) x = 0;
// if (x + this.width >= Game.SIZE.getWidth()) x = (int) (Game.SIZE.getWidth() - this.width);
    return true;
  }

  private boolean handleGravityAndMovement() {
    log.trace("handle Jumping " + handleJumping());
    log.trace("handle Falling " + handleFalling());
    log.trace("handle Floating " + handleFloating());
    if (velocityX != 0) isRunning = true;
    else isRunning = false;
    if (isRunning) {
      frameDelay++;
      if (frameDelay >= 3) {
        frame++;
        if (frame >= Game.player.length / 2) {
          frame = 0;
        }
        frameDelay = 0;
      }
    }
    return true;
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
      setVelocityY((int) gravity);  // handle movement
    }
    return true;
  }

  private boolean handleJumping() {  // handleGravityAndMovement sub-method
    if (isJumping) {
      gravity = gravity + 0.1;  // handle gravity
      log.trace("Jumping gravity = " + gravity);
      setVelocityY((int) +gravity);  // handle movement
      if (gravity >= 0.0) {  // handle movement
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
  // ######################################## tick sub-methods ########################################

}
