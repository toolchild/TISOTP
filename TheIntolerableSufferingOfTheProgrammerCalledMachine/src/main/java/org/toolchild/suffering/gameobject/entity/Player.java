package org.toolchild.suffering.gameobject.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;import org.toolchild.suffering.Camera;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.GameObject;
import org.toolchild.suffering.gameobject.tile.Tile;
import org.toolchild.suffering.gfx.Sprite;

/**
 * The {@link Entity} controlled by the person playing this game.
 * 
 * @author Bob
 *
 */
public class Player extends Entity {
  private static final Logger log                  = LogManager.getLogger(Player.class);
  private static final int    PLAYER_DEFAULT_SIZE  = 63;

  private int                 frame                = 0;
  private int                 frameDelay           = 0;
  private int                 jumpCount            = 0;
  private int                 jumpTimeCount        = 0;
  private int                 jumpStartY;
  private int                 maxJumpHeight        = PLAYER_DEFAULT_SIZE * 3;
  private int                 jumpHeight           = 0;
  private boolean             isValnurable         = true;
  private int                 isValnurableCount    = 0;
  private static final int    IS_INVULNERABLE_TIME = 120;

  public Player(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites) {
    super(x, y, width, height, id, handler, sprites);
    this.movement.setMoveSpeed(10);
    this.jumpStartY = y;
  }

  public void handleJumpKeyEvent(boolean isActive) {
    log.trace("y '" + this.y + "' jumpStartY '" + this.jumpStartY + "'");
    this.jumpHeight = this.jumpStartY - this.y;
    boolean canJump = this.jumpCount < 100 && this.movement.getGravity() < 1.0 && this.jumpHeight < this.maxJumpHeight;
    boolean initJump = this.jumpCount < 1;
    log.trace("jump height : '" + this.jumpHeight + "' key active : '" + isActive + "'count : '" + this.jumpCount + "' canjump : '" + canJump + "' init jump : '" + initJump + "' is jumping : '" + this.movement.isJumping() + "' is falling : '" + this.movement.isFalling() + "'");

    if (isActive && canJump && initJump) {
      this.movement.setJumping(true);
      this.movement.setGravity(-10);
      this.jumpCount++;
      log.trace("init jump!");
      this.jumpStartY = this.y;
    } else if (isActive && canJump) {
      this.movement.setGravity(this.movement.getGravity() - 0.5);
      this.jumpCount++;
      log.trace("Jumped!");
    } else if (!isActive && this.movement.isJumping() && this.jumpCount != 100) {
      this.jumpCount = 100;
      this.movement.setGravity(this.movement.getGravity() + 0.5);
      log.trace("Jump Stop!");
    } else if (!isActive && !this.movement.isJumping()) {
      this.jumpCount = 0;
      this.jumpHeight = 0;
      this.jumpStartY = this.y;
      this.movement.setGravity(this.movement.getGravity() + 0.5);
    }
  }

  public void handleRightKeyEvent(boolean isActive) {
    if (isActive) {
      this.movement.setVelocityX(this.movement.getMoveSpeed());
      this.facing = 1;
      log.trace("Went Right!");
    } else {
      log.trace("Right Released");
      this.movement.setVelocityX(0);
    }
  }

  public void handleLeftKeyEvent(boolean isActive) {
    if (isActive) {
      this.facing = 0;
      this.movement.setVelocityX(-this.movement.getMoveSpeed());
      log.trace("Went Left!");
    } else {
      log.trace("Left Released");
      this.movement.setVelocityX(0);
    }
  }


  @Override
  public void render(Graphics2D graphics2D) {
    // a special render method is needed.
  }

  public void render(Graphics2D graphics2D, Camera camera) {
    renderPlayer(graphics2D); // renders the player and everything tied to its position
    graphics2D.setColor(Color.WHITE);
    renderDebug(graphics2D, camera); // renders the debug messages relative to player
  }

  @Override
  public void tick() {
    Game.keyInput.updateKeyEvents(this, null, this.handler);
    log.trace("handle gravity and movement " + handleGravityAndMovement());
    log.trace("update position: " + updatePosition());
    log.trace("handle all tile interaction: " + handleAllTileInteraction());
    log.trace("handle Animation " + handleAnimationCycle());

    if (this.movement.isJumping()) {
      this.jumpTimeCount++;
    } else {
      this.jumpTimeCount = 0;
    }

    if (!this.isValnurable) {
      this.isValnurableCount++;
    }
    if (this.isValnurableCount >= IS_INVULNERABLE_TIME) {
      this.isValnurableCount = 0;
      this.isValnurable = true;
    }

    handleAllEntityInteraction();
  }

  /**
   * Updates the x and y coordinates of the player.
   * 
   * @return true if successful, otherwise false
   */
  private boolean updatePosition() {
    this.x = this.x + this.movement.getVelocityX();
    this.y = this.y + this.movement.getVelocityY();
    return true;
  }

  private void handleAllEntityInteraction() {
    for (int e = 0; e < this.handler.getEntities().size(); e++) { // need to use this for loop and
      Entity entity = (Entity) this.handler.getEntities().get(e); // get the entity to avoid an UnconcurrentModificationException
      handleSingleEntityInteraction(entity);
    }
  }

  private void handleSingleEntityInteraction(Entity entity) {
    handleBlueCrystalInteraction(entity);
    handleMob1Interaction(entity);
  }

  private void handleMob1Interaction(Entity entity) {
    if (entity.getId() == Id.mob1) {
      if (getBoundsBottom().intersects(entity.getBounds()) && this.isValnurable) {
        this.movement.setGravity(-this.movement.getGravity() * 0.8);
        entity.die();
        log.debug("mob1 interaction bottom");
      } else if (getBounds().intersects(entity.getBounds()) && this.isValnurable) {
        this.height = (int) (this.height * (1.0 / 1.2));
        this.width = (int) (this.width * (1.0 / 1.2));
        this.isValnurable = false;
        if (this.height < 40) {
          die();
        }
        log.debug("mob1 interaction");

      }
    }
  }

  private void handleBlueCrystalInteraction(Entity entity) {
    if (entity.getId() == Id.blueCrystal) {
      if (this.height <= 1.8 * 63) {
        if (getBounds().intersects(entity.getBounds())) {
          this.y = this.y - (int) (this.height * 0.2);
          this.height = (int) (this.height * 1.2);
          this.width = (int) (this.width * 1.2);
          entity.die();
        }
      }
    }
  }

  @Override
  public void die() {
    this.handler.removePlayer(this);
  }

  private boolean handleGravityAndMovement() {
    log.trace("handle Jumping " + this.movement.handlePlayerJumping());
    log.trace("handle Falling " + this.movement.handleFalling());
    log.trace("handle Floating " + this.movement.handleFloating());
    return true;
  }

  private boolean handleAnimationCycle() {
    if (this.movement.getVelocityX() != 0) {
      this.movement.setMoving(true);
    } else {
      this.movement.setMoving(false);
    }
    if (this.movement.isMoving()) {
      this.frameDelay++;
      if (this.frameDelay >= 3) {
        this.frame++;
        if (this.frame >= this.sprites.length / 2) {
          this.frame = 0;
        }
        this.frameDelay = 0;
      }
    }
    return true;
  }

  private boolean handleAllTileInteraction() {
    ArrayList<Tile> tilesInteracting = new ArrayList<>();
    for (GameObject tile : this.handler.getTiles()) {
      if (tile.getX() >= this.x - 5 * PLAYER_DEFAULT_SIZE && tile.getX() <= this.x + 5 * PLAYER_DEFAULT_SIZE) {
        if (tile.getY() >= this.y - 5 * PLAYER_DEFAULT_SIZE && tile.getY() <= this.y + 5 * PLAYER_DEFAULT_SIZE) {
          Tile tileInstance = (Tile) tile;
          String singleTileInteractionStatusMessage = handleSingleTileInteraction(tileInstance);
          if (singleTileInteractionStatusMessage != null) {
            log.trace("single tile interaction: " + singleTileInteractionStatusMessage);
            if (singleTileInteractionStatusMessage.contains("Top")) {
              tilesInteracting.add(tileInstance);
            }
          }
        }
      }
    }
    for (Tile tile : tilesInteracting) {
      this.y = tile.getY() + tile.getHeight();
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
    } else if (tile.getId() == Id.finish) {
      statusMessage = handleFinishInteraction(tile);
    } else statusMessage = "false, tile id not recognized";

    return statusMessage;
  }

  private String handlePowerUpBlockInteraction(Tile powerUpBlock) {
    String statusMessage = null;
    if (getBoundsTop().intersects(powerUpBlock.getBounds())) {
      statusMessage = "powerUpBlock interaction: hitTop";
      powerUpBlock.setActivated(true);
    }
    handleLevelTileInteraction(powerUpBlock);
    return statusMessage;
  }

  private String handleFinishInteraction(Tile finish) {
    String statusMessage = null;
    if (getBounds().intersects(finish.getBounds()) && this.isValnurable) {
      statusMessage = "finish interaction: Finish touched";
      this.isValnurable = false;
      this.handler.setPaused(true);
    }
    return statusMessage;
  }

  /**
   * Handles the players interaction with {@link Tile} object.
   * 
   * @param tile the Tile to interact with.
   * @return The statusMessage
   */
  private String handleLevelTileInteraction(Tile tile) {
    String statusMessage = null;
    if (getBoundsTop().intersects(tile.getBounds())) {
      statusMessage = "level tile interaction: hitTop";
      this.movement.setVelocityY(0);
      if (this.movement.isJumping()) {
        this.movement.setGravity(1.0);
        this.movement.setFalling(true);
      }
    } else if (getBoundsBottom().intersects(tile.getBounds()) && this.movement.getGravity() > 0) {
      statusMessage = "level tile interaction: hitBottom";
      this.movement.setVelocityY(0);
      this.movement.setJumping(false);
      this.jumpCount = 0;
      this.y = tile.getY() - this.height; // reset height, looks cleaner
      this.jumpStartY = this.y;
      if (this.movement.isFalling()) {
        this.movement.setFalling(false);
      }
    } else if (getBoundsLeft().intersects(tile.getBounds())) {
      statusMessage = "level tile interaction: hitLeft";
      this.movement.setVelocityY(0);
      this.x = tile.getX() + tile.getWidth();
    } else if (getBoundsRight().intersects(tile.getBounds())) {
      statusMessage = "level tile interaction: hitRight";
      this.movement.setVelocityY(0);
      this.x = tile.getX() - this.width;
    }

    return statusMessage;
  }

  private void renderPlayer(Graphics2D graphics2D) {
    if (!this.isValnurable) {
      graphics2D.setColor(Color.WHITE);
      graphics2D.drawRect(this.x, this.y, this.width, this.height);
    }

    handleAnimationRendering(graphics2D);
    // draw collision detection box
    graphics2D.setColor(Color.RED);
    graphics2D.draw(getBoundsTop());
    graphics2D.draw(getBoundsBottom());
    graphics2D.draw(getBoundsLeft());
    graphics2D.draw(getBoundsRight());
  }

  @Override
  protected void handleAnimationRendering(Graphics2D graphics) {
    if (this.facing == 0) {
      log.trace("facing left frame " + this.frame);
      if (!this.movement.isMoving()) {
        this.frame = 0;
      }
      graphics.drawImage(this.sprites[this.frame].getImage(), this.x, this.y, this.width, this.height, null);
    }
    if (this.facing == 1) {
      log.trace("facing right frame:" + this.frame);
      if (!this.movement.isMoving()) {
        this.frame = 0;
      }
      graphics.drawImage(this.sprites[this.frame + 5].getImage(), this.x, this.y, this.width, this.height, null);
    }

  }

  private void renderDebug(Graphics2D graphics2D, Camera camera) {
    camera.lockGraphicsToCamera(graphics2D);// since nothing is tied to player, the graphics object is tied to camera
    graphics2D.drawString("jump height : " + this.jumpHeight, this.column, this.lineHeight);
    graphics2D.drawString("player x : " + this.x, 0, 3 * this.lineHeight);
    graphics2D.drawString("player y : " + this.y, this.column, 3 * this.lineHeight);
    graphics2D.drawString("camera x : " + camera.getX(), 0, 4 * this.lineHeight);
    graphics2D.drawString("camera y : " + camera.getY(), this.column, 4 * this.lineHeight);

    graphics2D.drawLine(Game.getFrameWidth() - 500, Game.getFrameHeight(), Game.getFrameWidth() - 500, Game.getFrameHeight() - 300);
    graphics2D.drawLine(Game.getFrameWidth() - 500, Game.getFrameHeight(), Game.getFrameWidth(), Game.getFrameHeight());
    graphics2D.fillRect(Game.getFrameWidth() - 500 + 5 * this.jumpTimeCount, Game.getFrameHeight() - 10 - this.jumpHeight, 10, 10);
    graphics2D.drawString("" + this.jumpHeight, Game.getFrameWidth() - 500 + 5 * this.jumpTimeCount, Game.getFrameHeight() - 10 - this.jumpHeight);

    graphics2D.drawString("player velocity x : " + this.movement.getVelocityX(), 0, 5 * this.lineHeight);
    graphics2D.drawString("player velocity y : " + this.movement.getVelocityY(), 0, 6 * this.lineHeight);
    graphics2D.drawString("player height : " + this.height, this.column, 5 * this.lineHeight);
    graphics2D.drawString("player width : " + this.width, this.column, 6 * this.lineHeight);
    int gravityStringLength = Double.toString(this.movement.getGravity()).length();
    if (gravityStringLength > 5) {
      gravityStringLength = 5;
    }
    graphics2D.drawString("player gravity : " + Double.toString(this.movement.getGravity()).substring(0, gravityStringLength), 0, 7 * this.lineHeight);
    graphics2D.drawString("player facing : " + (this.facing == 0 ? "Left" : "Right"), 0, 8 * this.lineHeight);
    graphics2D.drawString("player isRunning: " + this.movement.isMoving(), 0, 9 * this.lineHeight);
    camera.releaseGraphicsFromCamera(graphics2D);
  }

}
