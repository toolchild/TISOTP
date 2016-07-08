package org.toolchild.suffering.gameobject.entity;

import java.awt.Color;
import java.awt.Graphics2D;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.entity.movement.Movement;
import org.toolchild.suffering.gameobject.GameObject;
import org.toolchild.suffering.gameobject.tile.Tile;
import org.toolchild.suffering.gfx.Sprite;

/**
 * A gameobject with Movement
 * 
 * @author Bob
 *
 */
public abstract class Entity extends GameObject {
  private static final Logger log    = Logger.getLogger(Entity.class);
  protected int               facing = 0;                                                                                       // 0 = left; 1 = right
  protected Movement          movement;

  /**
   * 
   * @param x x-coordinate
   * @param y y-coordinate
   * @param width
   * @param height
   * @param id
   * @param handler
   * @param sprites
   */
  public Entity(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites) {
    super(x, y, width, height, id, handler, sprites);
    this.movement = new Movement();
  }

  /**
   * handles the logic
   */
  public abstract void tick();

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
    graphics2D.draw(getBoundsTop());
    graphics2D.draw(getBoundsBottom());
    graphics2D.draw(getBoundsLeft());
    graphics2D.draw(getBoundsRight());
  }

  protected abstract void handleAnimationRendering(Graphics2D graphics2D);

  protected boolean handleAllInteraction() {
    handleAllEntityInteraction();
    handleAllTileInteraction();
    return true;
  }

  private void handleAllTileInteraction() {
    for (GameObject tile : this.handler.getTiles()) {
      if (isAroundPlayer(tile)) {
        Tile tileInstance = (Tile) tile;
        String singleTileInteractionStatusMessage = handleSingleTileInteraction(tileInstance);
        if (singleTileInteractionStatusMessage != null) {
          log.trace("single tile interaction: " + singleTileInteractionStatusMessage);
        }
      }
    }
  }

  private boolean isAroundPlayer(GameObject entity) {
    return entity.getX() >= this.x - 64 && entity.getX() <= this.x + 64 && entity.getY() >= this.y - 64 && entity.getY() <= this.y + 64;
  }

  private String handleSingleTileInteraction(Tile tile) {  // handleAllTileInteraction sub-method
    String statusMessage;
    if (!tile.isSolid()) {
      statusMessage = "false, tile not solid";
    } else if (tile.getId() == Id.wall || tile.getId() == Id.powerUpBlock) {
      statusMessage = handleLevelInteraction(tile);
    } else {
      statusMessage = "false, tile id not recognized";
    }

    return statusMessage;
  }

  private String handleLevelInteraction(Tile tile) {
    String statusMessage = null;
    if (getBoundsTop().intersects(tile.getBounds())) {
      statusMessage = tileInteractionHitTop(tile);
    } else if (getBoundsBottom().intersects(tile.getBounds())) {
      statusMessage = tileInteractionHitBottom(tile);
    } else if (getBoundsLeft().intersects(tile.getBounds())) {
      tileInteractionHitLeft(tile);
    } else if (getBoundsRight().intersects(tile.getBounds())) {
      statusMessage = tileInteractionHitRight(tile);
    }
    return statusMessage;
  }

  private String tileInteractionHitRight(Tile tile) {
    String statusMessage;
    statusMessage = "wall interaction: hitRight";
    this.movement.setVelocityX(-this.movement.getMoveSpeed());
    this.x = tile.getX() - this.width; // reset width, looks cleaner
    return statusMessage;
  }

  private String tileInteractionHitLeft(Tile tile) {
    String statusMessage;
    statusMessage = "tile interaction : hitLeft";
    this.movement.setVelocityX(this.movement.getMoveSpeed());
    this.x = tile.getX() + tile.getWidth();
    return statusMessage;
  }

  private String tileInteractionHitBottom(Tile tile) {
    String statusMessage;
    statusMessage = "tile interaction: hitBottom";
    this.movement.setVelocityY(0);
    this.movement.setJumping(false);
    this.y = tile.getY() - this.height; // reset height, looks cleaner
    if (this.movement.isFalling()) {
      this.movement.setFalling(false);
    }
    return statusMessage;
  }

  private String tileInteractionHitTop(Tile tile) {
    String statusMessage;
    statusMessage = "tile interaction: hitTop";
    this.y = tile.getY() + tile.getHeight();
    if (this.movement.isJumping()) {
      this.movement.setGravity(0.0);
      this.movement.setFalling(true);
    }
    return statusMessage;
  }

  private void handleAllEntityInteraction() {
    for (GameObject entity : this.handler.getEntities()) {
      if (isAroundPlayer(entity)) {
        Entity entityInstance = (Entity)entity;
        String singleTileInteractionStatusMessage = handleSingleEntityInteraction(entityInstance);
        if (singleTileInteractionStatusMessage != null) {
          log.trace("single tile interaction: " + singleTileInteractionStatusMessage);
        }
      }
    }
  }

  private String handleSingleEntityInteraction(Entity entity) {  // handleAllTileInteraction sub-method
    String statusMessage;
    if (entity != this && (entity.getId() == Id.mob1 || entity.getId() == Id.blueCrystal)) {
      statusMessage = handleEntityInteraction(entity);
    } else {
      statusMessage = "false, tile id not recognized";
    }
    return statusMessage;
  }

  private String handleEntityInteraction(Entity entity) {
    String statusMessage = null;
    if (getBoundsTop().intersects(entity.getBounds())) {
      statusMessage = entityInteractionHitTop(entity);
    } else if (getBoundsBottom().intersects(entity.getBounds())) {
      statusMessage = entityInteractionHitBottom(entity);
    } else if (getBoundsLeft().intersects(entity.getBounds())) {
      statusMessage = entityInteractionHitLeft(entity);
    } else if (getBoundsRight().intersects(entity.getBounds())) {
      statusMessage = entityInteractionHitRight(entity);
    }
    return statusMessage;
  }

  private String entityInteractionHitRight(Entity entity) {
    String statusMessage;
    statusMessage = "wall interaction: hitRight";
    this.movement.setVelocityX(-this.movement.getMoveSpeed());
    this.x = entity.getX() - this.width; // reset width, looks cleaner
    return statusMessage;
  }

  private String entityInteractionHitLeft(Entity entity) {
    String statusMessage;
    statusMessage = "wall interaction: hitLeft";
    this.movement.setVelocityX(this.movement.getMoveSpeed());
    this.x = entity.getX() + entity.getWidth();
    return statusMessage;
  }

  private String entityInteractionHitBottom(Entity entity) {
    String statusMessage;
    statusMessage = "entity interaction: hitBottom";
    this.movement.setVelocityY(0);
    this.movement.setJumping(false);
    this.y = entity.getY() - this.height; // reset height, looks cleaner
    if (this.movement.isFalling()) {
      this.movement.setFalling(false);
    }
    return statusMessage;
  }

  private String entityInteractionHitTop(Entity entity) {
    String statusMessage;
    statusMessage = "entity interaction: hitTop";
    this.y = entity.getY() + entity.getHeight();
    this.movement.setVelocityY(0);
    if (this.movement.isJumping()) {
      this.movement.setGravity(0.0);
      this.movement.setFalling(true);
    }
    return statusMessage;
  }

  /**
   * Removes the entity from the game.
   */
  public void die() {
    this.handler.removeEntity(this);
  }

}
