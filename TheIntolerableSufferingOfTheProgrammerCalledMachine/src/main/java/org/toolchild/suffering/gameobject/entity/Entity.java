package org.toolchild.suffering.gameobject.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.GameObject;
import org.toolchild.suffering.gameobject.tile.Tile;

/**
 * A gameobject with Movement
 * 
 * @author Bob
 *
 */
public abstract class Entity extends GameObject {
  private static final Logger log         = LogManager.getLogger(Entity.class);
  protected int               facing      = 0;                                                                                                                                                                                                                                                                                                                                                     // 0 = left;
                                                                                                                                                                                                                                                                                                                                                                                                   // 1 = right
  protected Movement          movement;

  protected int                 boundsTrim;
  protected int               boundsInsetX = 0;
  protected int               boundsInsetY = 0;
  protected int                 boundsWidth = 1;

  protected Handler           handler;

  /**
   * 
   * @param x x-coordinate
   * @param y y-coordinate
   * @param width
   * @param height
   * @param id
   * @param handler
   * @param bufferedImages
   * @return
   */
  public Entity(int x, int y, int width, int height, Id id, Handler handler, BufferedImage[] bufferedImages) {
    super(x, y, width, height, id, bufferedImages);
    this.handler = handler;
    this.movement = new Movement();
    

  }

  public Rectangle getBoundsTop() {
    return new Rectangle(this.x + this.boundsTrim + this.boundsInsetX, this.y+this.boundsInsetY, this.width - 2 * (this.boundsTrim + this.boundsInsetX), this.boundsWidth);
  }

  public Rectangle getBoundsBottom() {
    return new Rectangle(this.x + this.boundsTrim + this.boundsInsetX, this.y + this.height - this.boundsWidth - this.boundsInsetY, this.width - 2 * (this.boundsTrim + this.boundsInsetX), this.boundsWidth);
  }
  

  public Rectangle getBoundsLeft() {
    return new Rectangle(this.x + this.boundsInsetX, this.y + this.boundsTrim + this.boundsInsetY, this.boundsWidth, this.height - 2 * (this.boundsTrim + this.boundsInsetY));
  }

  public Rectangle getBoundsRight() {
    return new Rectangle(this.x - this.boundsInsetX + this.width - this.boundsWidth, this.y + this.boundsTrim + this.boundsInsetY, this.boundsWidth, this.height - 2 * (this.boundsTrim + this.boundsInsetY));
  }

  protected boolean updatePosition() {
    this.x = this.x + this.movement.getVelocityX();
    this.y = this.y + this.movement.getVelocityY();
    return true;
  }

  /**
   * handles the logic
   * 
   * @throws Exception
   */
  public abstract void tick(int speedModifier) throws Exception;

  /**
   * Renders this Entity.
   * 
   * @param graphics2D are expected released from the camera
   * @param camera the camera, for lock and releasing purposes
   */
  @Override
  public void render(Graphics2D graphics2D) {
    handleAnimationRendering(graphics2D);
    graphics2D.setColor(Color.BLUE);
    renderCollisionBoxes(graphics2D);
  }

  protected abstract void handleAnimationRendering(Graphics2D graphics2D);

  protected boolean handleAllInteraction() {
    handleAllEntityInteraction();
    handleAllTileInteraction();
    return true;
  }
  
  private void renderCollisionBoxes(Graphics2D graphics2D) {
    graphics2D.setColor(Color.RED);
    graphics2D.draw(getBoundsBottom());
    graphics2D.draw(getBoundsTop());
    graphics2D.draw(getBoundsLeft());
    graphics2D.draw(getBoundsRight());
  }

  private void handleAllTileInteraction() {
    for (GameObject tile : this.handler.getTiles()) {
      if (isAroundPlayer(tile)) {
        Tile tileInstance = (Tile) tile;
        handleSingleTileInteraction(tileInstance);
      }
    }
  }

  public boolean isAroundPlayer(GameObject entity) {
    return entity.getX() >= this.x - 64 && entity.getX() <= this.x + 64 && entity.getY() >= this.y - 64 && entity.getY() <= this.y + 64;
  }

  private void handleSingleTileInteraction(Tile tile) {  // handleAllTileInteraction sub-method
    if (!tile.isSolid()) {
    } else if (tile.getId() == Id.wall || tile.getId() == Id.powerUpBlock) {
      handleLevelInteraction(tile);
    } 
  }

  private void handleLevelInteraction(Tile tile) {
    if (getBoundsTop().intersects(tile.getBounds())) {
      tileInteractionHitTop(tile);
    } else if (getBoundsBottom().intersects(tile.getBounds())) {
      tileInteractionHitBottom(tile);
    } else if (getBoundsLeft().intersects(tile.getBounds())) {
      tileInteractionHitLeft(tile);
    } else if (getBoundsRight().intersects(tile.getBounds())) {
      tileInteractionHitRight(tile);
    }
  }

  private void tileInteractionHitRight(Tile tile) {
    this.movement.setVelocityX(-this.movement.getMoveSpeed());
    this.x = tile.getX() - this.width; // reset width, looks cleaner
  }

  private void tileInteractionHitLeft(Tile tile) {
    this.movement.setVelocityX(this.movement.getMoveSpeed());
    this.x = tile.getX() + tile.getWidth();
  }

  private void tileInteractionHitBottom(Tile tile) {
    this.movement.setVelocityY(0);
    this.movement.setJumping(false);
    this.y = tile.getY() - this.height + this.boundsInsetY; // reset height, looks cleaner
    if (this.movement.isFalling()) {
      this.movement.setFalling(false);
    }
  }

  private void tileInteractionHitTop(Tile tile) {
    this.y = tile.getY() + tile.getHeight() - this.boundsInsetY;
    if (this.movement.isJumping()) {
      this.movement.setGravity(0.0);
      this.movement.setFalling(true);
    }
  }

  private void handleAllEntityInteraction() {
    for (GameObject entity : this.handler.getEntities()) {
      if (isAroundPlayer(entity)) {
        Entity entityInstance = (Entity) entity;
        handleSingleEntityInteraction(entityInstance);
      }
    }
  }

  private void handleSingleEntityInteraction(Entity entity) {  // handleAllTileInteraction sub-method
    if (entity != this && (entity.getId() == Id.mob1 || entity.getId() == Id.blueCrystal)) {
      handleEntityInteraction(entity);
    } 
  }

  private void handleEntityInteraction(Entity entity) {
    if (getBoundsBottom().intersects(entity.getBounds())) {
      entityInteractionHitBottom(entity);
    } else if (getBoundsTop().intersects(entity.getBounds())) {
      entityInteractionHitTop(entity);
    } else if (getBoundsLeft().intersects(entity.getBounds())) {
     entityInteractionHitLeft(entity);
    } else if (getBoundsRight().intersects(entity.getBounds())) {
     entityInteractionHitRight(entity);
    }
  }

  private void entityInteractionHitRight(Entity entity) {
    this.movement.setVelocityX(-this.movement.getMoveSpeed());
    this.x = entity.getX() - this.width; // reset width, looks cleaner
  }

  private void entityInteractionHitLeft(Entity entity) {
    this.movement.setVelocityX(this.movement.getMoveSpeed());
    this.x = entity.getX() + entity.getWidth();
  }

  private void entityInteractionHitBottom(Entity entity) {
    this.movement.setVelocityY(0);
    this.movement.setJumping(false);
    this.y = entity.getY() - this.height; // reset height, looks cleaner
    if (this.movement.isFalling()) {
      this.movement.setFalling(false);
    }
  }

  private void entityInteractionHitTop(Entity entity) {
    this.y = entity.getY() + entity.getHeight();
    this.movement.setVelocityY(0);
    if (this.movement.isJumping()) {
      this.movement.setGravity(0.0);
      this.movement.setFalling(true);
    }
  }

  protected void handleGravityAndMovement(int speedModifier) {
    this.movement.handleFalling(speedModifier);
    this.movement.handleFloating();
  }

  /**
   * Removes the entity from the game.
   */
  public void die() {
    this.handler.removeEntity(this);
  }

}
