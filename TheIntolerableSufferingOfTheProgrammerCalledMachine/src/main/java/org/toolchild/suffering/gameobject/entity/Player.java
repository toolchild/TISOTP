package org.toolchild.suffering.gameobject.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toolchild.suffering.Camera;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.tile.Tile;
import org.toolchild.suffering.input.KeyInputManager;

/**
 * The {@link Entity} controlled by the person playing this game.
 * 
 * @author Bob
 *
 */
public class Player extends Entity {
  private static final Logger log                  = LogManager.getLogger(Player.class);
  private static final int    PLAYER_DEFAULT_SIZE  = 64;
  private final int           GROWTH_MODIFIER      = 16;
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

  public Player(int x, int y, int width, int height, Id id, Handler handler, BufferedImage[] bufferedImages) {
    super(x, y, width, height, id, handler, bufferedImages);
    this.jumpStartY = y;
  }
  
  public void init(int speedModifier){
    this.movement.setMoveSpeed((int) ((8.0) * speedModifier));
  }

  public void handleJumpKeyEvent(boolean isActive, int speedModifier) {
    // log.trace("y '" + this.y + "' jumpStartY '" + this.jumpStartY + "'");
    this.jumpHeight = this.jumpStartY - this.y;
    boolean canJump = this.jumpCount < 100 /speedModifier && this.movement.getGravity() < 1.0 && this.jumpHeight < this.maxJumpHeight;
    boolean initJump = this.jumpCount < 1;
    // log.trace("jump height : '" + this.jumpHeight + "' key active : '" + isActive + "'count : '" + this.jumpCount + "' canjump : '" + canJump + "' init jump : '" + initJump + "' is jumping : '" + this.movement.isJumping() + "' is falling : '" + this.movement.isFalling() + "'");

    if (isActive && canJump && initJump) {
      this.movement.setJumping(true);
      this.movement.setGravity(-10 * speedModifier);
      this.jumpCount++;
      // log.trace("init jump!");
      this.jumpStartY = this.y;
    } else if (isActive && canJump) {
      this.movement.setGravity(this.movement.getGravity() - 0.5 * speedModifier);
      this.jumpCount++;
      // log.trace("Jumped!");
    } else if (!isActive && this.movement.isJumping() && this.jumpCount != 100 * speedModifier) {
      this.jumpCount = 100 / speedModifier;
      this.movement.setGravity(this.movement.getGravity() + 0.5 * speedModifier);
      // log.trace("Jump Stop!");
    } else if (!isActive && !this.movement.isJumping()) {
      this.jumpCount = 0;
      this.jumpHeight = 0;
      this.jumpStartY = this.y;
      this.movement.setGravity(this.movement.getGravity() + 0.5 * speedModifier);
    }
  }

  public void handleRightKeyEvent(boolean isActive) {
    if (isActive) {
      this.movement.setVelocityX(this.movement.getMoveSpeed());
      this.facing = 1;
      log.trace("Went Right!");
    } else {
      log.trace("Stop Right!");
      this.movement.setVelocityX(0);
    }
  }

  public void handleLeftKeyEvent(boolean isActive) {
    if (isActive) {
      this.facing = 0;
      this.movement.setVelocityX(-this.movement.getMoveSpeed());
      log.trace("Went Left!");
    } else {
      log.trace("Sto Left!");
      this.movement.setVelocityX(0);
    }
  }

  @Override
  public void render(Graphics2D graphics2D) {
    // a special render method is needed.
  }

  public void render(Graphics2D graphics2D, Camera camera, Game game) {
    renderPlayer(graphics2D); // renders the player and everything tied to its position
    graphics2D.setColor(Color.WHITE);
    renderDebug(graphics2D, camera, game); // renders the debug messages relative to player
  }

  public void tick(KeyInputManager keyInputManager, Game game, int speedModifier) throws Exception {
    keyInputManager.updateKeyEvents(this, this.handler, speedModifier);
    handleGravityAndMovement(speedModifier);
    updatePosition();
    handleAllTileInteraction(game);
    handleAnimationCycle();

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
        this.height = this.height - this.GROWTH_MODIFIER;
        this.width = this.width - this.GROWTH_MODIFIER;
        this.isValnurable = false;
        if (this.height < 30) {
          die();
        }
        log.debug("mob1 interaction");

      }
    }
  }

  private void handleBlueCrystalInteraction(Entity entity) {
    if (entity.getId() == Id.blueCrystal) {
      if (this.height < PLAYER_DEFAULT_SIZE * 2) {
        if (getBounds().intersects(entity.getBounds())) {
          this.height = this.height + this.GROWTH_MODIFIER;
          this.width = this.width + this.GROWTH_MODIFIER;
          this.y = this.y - this.GROWTH_MODIFIER;
          entity.die();
        }
      }
    }
  }

  @Override
  public void die() {
    this.handler.removePlayer(this);
  }

  
  @Override
  protected void handleGravityAndMovement(int speedModifier) {
    this.movement.handlePlayerJumping();
    this.movement.handleFalling(speedModifier);
    this.movement.handleFloating();
  }

  private void handleAnimationCycle() {
    if (this.movement.getVelocityX() != 0) {
      this.movement.setMoving(true);
    } else {
      this.movement.setMoving(false);
    }
    if (this.movement.isMoving()) {
      this.frameDelay++;
      if (this.frameDelay >= 4) {
        this.frame++;
        if (this.frame >= this.bufferedImages.length / 2) {
          this.frame = 0;
        }
        this.frameDelay = 0;
      }
    }
  }

  private void handleAllTileInteraction(Game game) throws Exception {
    ArrayList<Tile> tilesInteracting = new ArrayList<>();
    for (int t = 0; t < this.handler.getTiles().size(); t++) {
      Tile tile = (Tile) this.handler.getTiles().get(t);
      if (tile.getX() >= this.x - 5 * PLAYER_DEFAULT_SIZE && tile.getX() <= this.x + 5 * PLAYER_DEFAULT_SIZE) {
        if (tile.getY() >= this.y - 5 * PLAYER_DEFAULT_SIZE && tile.getY() <= this.y + 5 * PLAYER_DEFAULT_SIZE) {
          boolean topTileInteraction = handleSingleTileInteraction(tile, game);
          if (topTileInteraction) {
            tilesInteracting.add(tile);
          }
        }
      }
    }
    for (Tile tile : tilesInteracting) {
      this.y = tile.getY() + tile.getHeight();
    }
  }

  private boolean handleSingleTileInteraction(Tile tile, Game game) throws Exception {  // handleAllTileInteraction sub-method
    boolean topTileInteraction = false;
    if (tile.isSolid()) {
      if (tile.getId() == Id.powerUpBlock) {
        handlePowerUpBlockInteraction(tile);
      } else if (tile.getId() == Id.wall) {
        handleLevelTileInteraction(tile);
      } else if (tile.getId() == Id.finish) {
        handleFinishInteraction(tile, game);
      }
    }
    return topTileInteraction;
  }

  private String handlePowerUpBlockInteraction(Tile powerUpBlock) {
    String statusMessage = "";
    if (getBoundsTop().intersects(powerUpBlock.getBounds())) {
      statusMessage = "powerUpBlock interaction: hitTop";
      powerUpBlock.setActivated(true);
    }
    handleLevelTileInteraction(powerUpBlock);
    return statusMessage;
  }

  private String handleFinishInteraction(Tile finish, Game game) throws Exception {
    String statusMessage = null;
    if (getBounds().intersects(finish.getBounds()) && this.isValnurable) {
      statusMessage = "finish interaction: Finish touched";
      this.isValnurable = false;
      this.handler.setPaused(true);
      this.handler.nextLevel();
      this.handler.init(game);
      this.handler.setPaused(false);

    }
    return statusMessage;
  }

  /**
   * Handles the players interaction with {@link Tile} object.
   * 
   * @param tile the Tile to interact with.
   * @return The statusMessage
   */
  private boolean handleLevelTileInteraction(Tile tile) {
    boolean hitTop = false;
    if (getBoundsTop().intersects(tile.getBounds())) {
      log.debug("Top Hit! player.y: " + this.y + " tile.y (bottum) " + (tile.getY() + tile.getHeight()));
      log.debug("Top Hit! player.x: " + this.x + " tile.x (right corner) " + (tile.getX() + tile.getWidth()));
      hitTop = true;
      this.y = tile.getY() + tile.getHeight(); // reset height, looks cleaner
      log.debug("Top Reposition! player.y: " + this.y + " tile.y (bottum) " + (tile.getY() + tile.getHeight()));
      log.debug("Top Reposition! player.x: " + this.x + " tile.x (right corner) " + (tile.getX() + tile.getWidth()));
      this.movement.setVelocityY(0);
      if (this.movement.isJumping()) {
        this.movement.setGravity(1.0);
        this.movement.setFalling(true);
      }
    }
    if (getBoundsBottom().intersects(tile.getBounds()) && this.movement.getGravity() > 0) {
      this.movement.setVelocityY(0);
      this.movement.setJumping(false);
      if (this.movement.getGravity() > this.movement.getMoveSpeed()) {
        log.debug("gravity set to 0");
        this.movement.setGravity(1.0);
      }
      this.jumpCount = 0;
      this.y = tile.getY() - this.height; // reset height, looks cleaner
      this.jumpStartY = this.y;
      if (this.movement.isFalling()) {
        this.movement.setFalling(false);
      }
    }
    if (getBoundsLeft().intersects(tile.getBounds())) {
      log.debug("Left Hit! player.x: " + this.x + " tile.x (right corner) " + (tile.getX() + tile.getWidth()));
      this.movement.setVelocityY(0);
      this.x = tile.getX() + tile.getWidth();
    }
    if (getBoundsRight().intersects(tile.getBounds())) {
      log.debug("Right Hit player.x: " + (this.x + this.width) + " tile.x (right corner) " + tile.getX());
      this.movement.setVelocityY(0);
      this.x = tile.getX() - this.width;
    }
    return hitTop;
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
      if (!this.movement.isMoving()) {
        this.frame = 0;
      }
      graphics.drawImage(this.bufferedImages[this.frame], this.x, this.y, this.width, this.height, null);
    }
    if (this.facing == 1) {
      if (!this.movement.isMoving()) {
        this.frame = 0;
      }
      graphics.drawImage(this.bufferedImages[this.frame + 5], this.x, this.y, this.width, this.height, null);
    }

  }

  private void renderDebug(Graphics2D graphics2D, Camera camera, Game game) {
    camera.lockGraphicsToCamera(graphics2D);// since nothing is tied to player, the graphics object is tied to camera
    graphics2D.drawString("jump height : " + this.jumpHeight, this.column, this.lineHeight);
    graphics2D.drawString("player x : " + this.x, 0, 3 * this.lineHeight);
    graphics2D.drawString("player y : " + this.y, this.column, 3 * this.lineHeight);
    graphics2D.drawString("camera x : " + camera.getX(), 0, 4 * this.lineHeight);
    graphics2D.drawString("camera y : " + camera.getY(), this.column, 4 * this.lineHeight);

    graphics2D.drawLine(game.getFrameWidth() - 500, game.getFrameHeight(), game.getFrameWidth() - 500, game.getFrameHeight() - 300);
    graphics2D.drawLine(game.getFrameWidth() - 500, game.getFrameHeight(), game.getFrameWidth(), game.getFrameHeight());
    graphics2D.fillRect(game.getFrameWidth() - 500 + 5 * this.jumpTimeCount, game.getFrameHeight() - 10 - this.jumpHeight, 10, 10);
    graphics2D.drawString("" + this.jumpHeight, game.getFrameWidth() - 500 + 5 * this.jumpTimeCount, game.getFrameHeight() - 10 - this.jumpHeight);

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


  @Override
  public void tick(int speedModifier) throws Exception {
    // TODO Auto-generated method stub
    
  }

}
