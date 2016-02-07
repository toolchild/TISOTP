package org.toolchild.suffering.gameobject.entity;

import java.awt.Color;
import java.awt.Graphics;

import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.entity.movement.Movement;
import org.toolchild.suffering.gameobject.GameObject;
import org.toolchild.suffering.gameobject.tile.PowerUpBlock;
import org.toolchild.suffering.gameobject.tile.Tile;
import org.apache.log4j.Logger;
import org.toolchild.suffering.Camera;

public class Player extends GameObject {
  private static final Logger log        = Logger.getLogger(Player.class);
  Movement                    movement;

  private int                 frame      = 0;
  private int                 frameDelay = 0;
  private int                 facing     = 0;
  private int                 jumpCount  = 0;
  private int                 jumpTimeCount  = 0;
  int jumpStartY;
  int maxJumpHeight = 64*3;
  int jumpHeight = 0;

  public Player(int x, int y, int width, int height, Id id, Handler handler) {
    super(x, y, width, height, id, handler);
    movement = new Movement();
    movement.setMoveSpeed(10);
    jumpStartY = y;
  }

  @Override
  public void tick() {
    Game.keyInput.updateKeyEvents(this);
    log.trace("handle gravity and movement " + handleGravityAndMovement());
    log.trace("update position: " + updatePosition());
    log.trace("handle all tile interaction: " + handleAllTileInteraction());
    log.trace("handle Animation " + handleAnimationCycle());
    
    if(movement.isJumping()){
      jumpTimeCount++;
    } else {
      jumpTimeCount = 0;
    }

    for (int e = 0; e < handler.getEntities().size(); e++) { // need to use this for loop and
      Entity entity = (Entity) handler.getEntities().get(e); // get the entity to avoid an UnconcurrentModificationException
      if (entity.getId() == Id.blueCrystal) {
        if (height <= 10 * 64) {
          if (getBounds().intersects(entity.getBounds())) {
            y = y - (int) (height * 0.2);
            height = (int) (height * 1.2);
            width = (int) (width * 1.2);
            entity.die();
          }
        }
      }
      if (entity.getId() == Id.mob1) {
        if (height <= 10 * 64) {
          if (getBoundsBottom().intersects(entity.getBounds())) {
            movement.setGravity(-movement.getGravity() * 0.8);
            height = (int) (height);
            entity.die();
          } else if (getBounds().intersects(entity.getBounds())) {
            movement.setGravity(-movement.getGravity() * 0.8);
            movement.setVelocityX(-movement.getVelocityX());
            y = y - 100;
            height = (int) (height * (1.0 / 1.2));
            width = (int) (width * (1.0 / 1.2));
            if (height < 40) {
              die();
            }

          }
        }
      }
    }
  }

  private void die() {
    handler.removePlayer(this);
  }

  /**
   * Updates the {@link x} and {@link y} coordinates of the player.
   * 
   * @return true if successful, otherwise false
   */
  private boolean updatePosition() {
    x = x + movement.getVelocityX();
    y = y + movement.getVelocityY();
    // if (x <= 0) x = 0;
    // if (x + this.width >= Game.SIZE.getWidth()) x = (int) (Game.SIZE.getWidth() - this.width);
    return true;
  }

  private boolean handleGravityAndMovement() {
    log.trace("handle Jumping " + movement.handlePlayerJumping());
    log.trace("handle Falling " + movement.handleFalling());
    log.trace("handle Floating " + movement.handleFloating());
    return true;
  }

  private boolean handleAnimationCycle() {
    if (movement.getVelocityX() != 0) movement.setMoving(true);
    else movement.setMoving(false);
    if (movement.isMoving()) {
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

  private boolean handleAllTileInteraction() {
    for (GameObject tile : handler.getTiles()) {
      if (tile.getX() >= x - 5 * 64 && tile.getX() <= x + 5 * 64) {
        if (tile.getY() >= y - 5 * 64 && tile.getY() <= y + 5 * 64) {
          Tile tileInstance = (Tile) tile;
          String singleTileInteractionStatusMessage = handleSingleTileInteraction(tileInstance);
          if (singleTileInteractionStatusMessage != null) {
            log.debug("single tile interaction: " + singleTileInteractionStatusMessage);
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
    } else if (tile.getId() == Id.powerUpBlock) {
      statusMessage = handlePowerUpBlockInteraction(tile);
    } else if (tile.getId() == Id.wall) {
      statusMessage = handleLevelTileInteraction(tile);
    } else statusMessage = "false, tile id not recognized";

    return statusMessage;
  }

  private String handlePowerUpBlockInteraction(Tile powerUpBlock) {
    String statusMessage = null;
    if (getBoundsTop().intersects(powerUpBlock.getBounds())) {
      handleLevelTileInteraction(powerUpBlock);
      statusMessage = "powerUpBlock interaction: hitTop";
      powerUpBlock.setActivated(true);
    } else {
      statusMessage = handleLevelTileInteraction(powerUpBlock);
    }
    return statusMessage;
  }

  /**
   * Handles the players interaction with {@link T} object.
   * 
   * @param tile
   *          the
   * @return
   */
  private String handleLevelTileInteraction(Tile tile) {
    String statusMessage = null;
    if (getBoundsTop().intersects(tile.getBounds())) {
      statusMessage = "level tile interaction: hitTop";
      y = tile.getY() + tile.getHeight();
      movement.setVelocityY(0);
      if (movement.isJumping()) {
        // getIsJumping() = false;
         movement.setGravity(1.0);
        movement.setFalling(true);
      }
    } else if (getBoundsBottom().intersects(tile.getBounds()) && movement.getGravity() > 0) {
      statusMessage = "level tile interaction: hitBottom";
      movement.setVelocityY(0);
      movement.setJumping(false);
      jumpCount = 0;
      y = tile.getY() - height; // reset height, looks cleaner
      jumpStartY = y;
      if (movement.isFalling()) {
        movement.setFalling(false);
      }
    } else if (getBoundsLeft().intersects(tile.getBounds())) {
      statusMessage = "level tile interaction: hitLeft";
      movement.setVelocityY(0);
      x = tile.getX() + tile.getWidth();
    } else if (getBoundsRight().intersects(tile.getBounds())) {
      statusMessage = "level tile interaction: hitRight";
      movement.setVelocityY(0);
      x = tile.getX() - width;
    }

    return statusMessage;
  }

  public void render(Graphics graphics, Camera camera) {
    renderPlayer(graphics); // renders the player and everything tied to its position
    graphics.setColor(Color.WHITE);
    renderDebug(graphics, camera); // renders the debug messages relative to player
  }

  private void renderPlayer(Graphics graphics) {
    handleAnimationRendering(graphics);
    // draw collision detection box
    graphics.setColor(Color.RED);
    graphics.fillRect(getBoundsRight().x, getBoundsRight().y, getBoundsRight().width, getBoundsRight().height);
    graphics.fillRect(getBoundsLeft().x, getBoundsLeft().y, getBoundsLeft().width, getBoundsLeft().height);
    graphics.fillRect(getBoundsTop().x, getBoundsTop().y, getBoundsTop().width, getBoundsTop().height);
    graphics.fillRect(getBoundsBottom().x, getBoundsBottom().y, getBoundsBottom().width, getBoundsBottom().height);
  }

  private void handleAnimationRendering(Graphics graphics) {
    if (facing == 0) {
      log.trace("facing left frame " + frame);
      if (!movement.isMoving()) {
        frame = 0;
      }
      graphics.drawImage(Game.player[frame].getImage(), x, y, width, height, null);
    }
    if (facing == 1) {
      log.trace("facing right frame:" + frame);
      if (!movement.isMoving()) {
        frame = 0;
      }
      graphics.drawImage(Game.player[frame + 5].getImage(), x, y, width, height, null);
    }
  }

  private void renderDebug(Graphics graphics, Camera camera) {
    camera.lockGraphicsToCamera(graphics);// since nothing is tied to player, the graphics object is tied to camera
    graphics.drawString("jump height : " + jumpHeight, column, lineHeight);
    graphics.drawString("player x : " + x, 0, 3 * lineHeight);
    graphics.drawString("player y : " + y, column, 3 * lineHeight);
    graphics.drawString("camera x : " + camera.getX(), 0, 4 * lineHeight);
    graphics.drawString("camera y : " + camera.getY(), column, 4 * lineHeight);
    // graphics.drawString("camera y * player y : " + ((double)camera.y * (double)y), 3*column, 4 * lineHeight);
    
    graphics.drawLine(Game.getFrameWidth() - 500, Game.getFrameHeight() ,Game.getFrameWidth() - 500,Game.getFrameHeight()- 300);
    graphics.drawLine(Game.getFrameWidth() - 500, Game.getFrameHeight() ,Game.getFrameWidth() ,Game.getFrameHeight());
    graphics.fillRect(Game.getFrameWidth() - 500 + 5*jumpTimeCount, Game.getFrameHeight()-10 - jumpHeight, 10, 10);
    graphics.drawString("" + jumpHeight, Game.getFrameWidth() - 500 + 5*jumpTimeCount, Game.getFrameHeight()-10 - jumpHeight);
    graphics.finalize();

    graphics.drawString("player velocity x : " + movement.getVelocityX(), 0, 5 * lineHeight);
    graphics.drawString("player velocity y : " + movement.getVelocityY(), 0, 6 * lineHeight);
    graphics.drawString("player height : " + height, column, 5 * lineHeight);
    graphics.drawString("player width : " + width, column, 6 * lineHeight);
    int gravityStringLength = Double.toString(movement.getGravity()).length();
    if (gravityStringLength > 5) {
      gravityStringLength = 5;
    }
    graphics.drawString("player gravity : " + Double.toString(movement.getGravity()).substring(0, gravityStringLength), 0, 7 * lineHeight);
    graphics.drawString("player facing : " + (facing == 0 ? "Left" : "Right"), 0, 8 * lineHeight);
    graphics.drawString("player isRunning: " + movement.isMoving(), 0, 9 * lineHeight);
    graphics.translate(camera.getX(), camera.getY()); // untying graphics from camera
  }

  public void handleJumpKeyEvent(boolean isActive) {
    log.trace("y '" + y  + "' jumpStartY '" +jumpStartY + "'");
    jumpHeight = jumpStartY - y;
    boolean canJump = jumpCount < 100 && movement.getGravity() < 1.0 && jumpHeight < maxJumpHeight;
    boolean initJump = jumpCount < 1 ;
    log.trace("jump height : '" +jumpHeight +"' key active : '" + isActive + "'count : '" + jumpCount + "' canjump : '" + canJump + "' init jump : '" + initJump + "' is jumping : '" + movement.isJumping() + "' is falling : '" + movement.isFalling() + "'");
    
    if (isActive && canJump && initJump) {
      movement.setJumping(true);
      movement.setGravity(-10);
      jumpCount++;
      log.trace("init jump!");
      jumpStartY = y;
    } else if (isActive && canJump) {
      movement.setGravity(movement.getGravity() - 0.5);
      jumpCount++;
      log.trace("Jumped!");
    } else if (!isActive && movement.isJumping() && jumpCount != 100) {
      jumpCount = 100;
      movement.setGravity(movement.getGravity() + 0.5);
      log.trace("Jump Stop!");
    } else if (!isActive && !movement.isJumping()) {
      jumpCount = 0;
      jumpHeight = 0;
      jumpStartY= y;
      movement.setGravity(movement.getGravity() + 0.5);
    }
  }

  public void handleRightKeyEvent(boolean isActive) {
    if (isActive) {
      movement.setVelocityX(movement.getMoveSpeed());
      facing = 1;
      log.trace("Went Right!");
    } else {
      log.trace("Right Released");
      movement.setVelocityX(0);
    }
  }

  public void handleLeftKeyEvent(boolean isActive) {
    if (isActive) {
      facing = 0;
      movement.setVelocityX(-movement.getMoveSpeed());
      log.trace("Went Left!");
    } else {
      log.trace("Left Released");
      movement.setVelocityX(0);
    }
  }

  @Override
  public void render(Graphics graphics) {
    // TODO Auto-generated method stub

  }

}
