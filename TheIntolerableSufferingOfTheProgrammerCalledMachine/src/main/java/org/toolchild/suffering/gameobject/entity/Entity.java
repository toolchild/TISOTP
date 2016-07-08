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

public abstract class Entity extends GameObject {
  private static final Logger log    = Logger.getLogger(Entity.class);
  protected int               facing = 0;// 0 = left; 1 = right
  protected Movement          movement;

  public abstract void tick();

  public Entity(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites) {
    super(x, y, width, height, id, handler, sprites);
    this.movement = new Movement();
  }

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
      if (tile.getX() >= this.x - 64 && tile.getX() <= this.x + 64) { // only tick tiles immediately around the entity
        if (tile.getY() >= this.y - 64 && tile.getY() <= this.y + 64) {
          Tile tileInstance = (Tile) tile;
          String singleTileInteractionStatusMessage = handleSingleTileInteraction(tileInstance);
          if (singleTileInteractionStatusMessage != null) {
            log.trace("single tile interaction: " + singleTileInteractionStatusMessage);
          }
        }
      }
    }
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
      statusMessage = "wall interaction: hitTop";
      this.y = tile.getY() + tile.getHeight();
      // movement.setVelocityY(0);
      if (this.movement.isJumping()) {
        // isJumping = false;
        this.movement.setGravity(0.0);
        this.movement.setFalling(true);
      }
    } else if (getBoundsBottom().intersects(tile.getBounds())) {
      statusMessage = "wall interaction: hitBottom";
      this.movement.setVelocityY(0);
      this.movement.setJumping(false);
      this.y = tile.getY() - this.height; // reset height, looks cleaner
      if (this.movement.isFalling()) {
        this.movement.setFalling(false);
      }
    } else if (getBoundsLeft().intersects(tile.getBounds())) {
      this.movement.setVelocityX(this.movement.getMoveSpeed());
      this.x = tile.getX() + tile.getWidth();
    } else if (getBoundsRight().intersects(tile.getBounds())) {
      statusMessage = "wall interaction: hitRight";
      this.movement.setVelocityX(-this.movement.getMoveSpeed());
      this.x = tile.getX() - this.width; // reset width, looks cleaner
    }
    return statusMessage;
  }

  private void handleAllEntityInteraction() {
    for (GameObject entity : this.handler.getEntities()) {
      if (entity.getX() >= this.x - 64 && entity.getX() <= this.x + 64) { // only tick tiles immediately around the entity
        if (entity.getY() >= this.y - 64 && entity.getY() <= this.y + 64) {
          Entity entityInstance = (Entity) entity;
          String singleTileInteractionStatusMessage = handleSingleEntityInteraction(entityInstance);
          if (singleTileInteractionStatusMessage != null) {
            log.trace("single tile interaction: " + singleTileInteractionStatusMessage);
          }
        }
      }
    }
  }

  private String handleSingleEntityInteraction(Entity entity) {  // handleAllTileInteraction sub-method
    String statusMessage;
    statusMessage = "false, tile not solid";
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
      statusMessage = "entity interaction: hitTop";
      this.y = entity.getY() + entity.getHeight();
      this.movement.setVelocityY(0);
      if (this.movement.isJumping()) {
        // isJumping = false;
        this.movement.setGravity(0.0);
        this.movement.setFalling(true);
      }
    } else if (getBoundsBottom().intersects(entity.getBounds())) {
      statusMessage = "entity interaction: hitBottom";
      this.movement.setVelocityY(0);
      this.movement.setJumping(false);
      this.y = entity.getY() - this.height; // reset height, looks cleaner
      if (this.movement.isFalling()) {
        this.movement.setFalling(false);
      }
    } else if (getBoundsLeft().intersects(entity.getBounds())) {
      this.movement.setVelocityX(this.movement.getMoveSpeed());
      this.x = entity.getX() + entity.getWidth();
    } else if (getBoundsRight().intersects(entity.getBounds())) {
      statusMessage = "wall interaction: hitRight";
      this.movement.setVelocityX(-this.movement.getMoveSpeed());
      this.x = entity.getX() - this.width; // reset width, looks cleaner
    }
    return statusMessage;
  }

  public void die() {
    this.handler.removeEntity(this);
  }

}
