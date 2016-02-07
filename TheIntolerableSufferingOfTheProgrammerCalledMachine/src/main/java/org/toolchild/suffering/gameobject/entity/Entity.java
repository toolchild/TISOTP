package org.toolchild.suffering.gameobject.entity;

import java.awt.Graphics;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.entity.movement.Movement;
import org.toolchild.suffering.gameobject.GameObject;
import org.toolchild.suffering.gameobject.tile.Tile;

public abstract class Entity extends GameObject{
  private static final Logger log        = Logger.getLogger(Entity.class);
  protected int facing = 0; // 0 = left; 1 = right
  protected Movement movement;

  public Entity(int x, int y, int width, int height, Id id, Handler handler){
    super(x, y, width, height, id, handler);
    movement = new Movement();
  }
  

  /**
   * 
   * Renders this Entity.
   * 
   * @param graphics  are expected released from the camera
   * @param camera  the camera, for lock and releasing purposes
   */
  public abstract void render(Graphics graphics);
  
  protected boolean handleAllInteraction() {
    handleAllEntityInteraction();
    handleAllTileInteraction();
    return true;
  }
  
  private void handleAllTileInteraction() {
    for (GameObject tile : handler.getTiles()) {
      if (tile.getX() >= x - 64 && tile.getX() <= x + 64) { // only tick tiles immediately around the entity
        if (tile.getY() >= y - 64 && tile.getY() <= y + 64) {
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
      y = tile.getY() + tile.getHeight();
//      movement.setVelocityY(0);
      if (movement.isJumping()) {
        // isJumping = false;
        movement.setGravity(0.0);
        movement.setFalling(true);
      }
    } else if (getBoundsBottom().intersects(tile.getBounds())) {
      statusMessage = "wall interaction: hitBottom";
      movement.setVelocityY(0);
      movement.setJumping(false);
      y = tile.getY() - height; // reset height, looks cleaner
      if (movement.isFalling()) {
        movement.setFalling(false);
      }
    } else if (getBoundsLeft().intersects(tile.getBounds())) {
      movement.setVelocityX(movement.getMoveSpeed());
      x = tile.getX() + tile.getWidth();
    } else if (getBoundsRight().intersects(tile.getBounds())) {
      statusMessage = "wall interaction: hitRight";
      movement.setVelocityX(-movement.getMoveSpeed());
      x = tile.getX() - width; // reset width, looks cleaner
    }
    return statusMessage;
  }
  
  
  private void handleAllEntityInteraction() {
    for (GameObject entity : handler.getEntities()) {
      if (entity.getX() >= x - 64 && entity.getX() <= x + 64) { // only tick tiles immediately around the entity
        if (entity.getY() >= y - 64 && entity.getY() <= y + 64) {
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
    if (entity != this && (entity.getId() == Id.mob1 || entity.getId() ==Id.blueCrystal)) {
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
      y = entity.getY() + entity.getHeight();
      movement.setVelocityY(0);
      if (movement.isJumping()) {
        // isJumping = false;
        movement.setGravity(0.0);
        movement.setFalling(true);
      }
    } else if (getBoundsBottom().intersects(entity.getBounds())) {
      statusMessage = "entity interaction: hitBottom";
      movement.setVelocityY(0);
      movement.setJumping(false);
      y = entity.getY() - height; // reset height, looks cleaner
      if (movement.isFalling()) {
        movement.setFalling(false);
      }
    } else if (getBoundsLeft().intersects(entity.getBounds())) {
      movement.setVelocityX(movement.getMoveSpeed());
      x = entity.getX() + entity.getWidth();
    } else if (getBoundsRight().intersects(entity.getBounds())) {
      statusMessage = "wall interaction: hitRight";
      movement.setVelocityX(-movement.getMoveSpeed());
      x = entity.getX() - width; // reset width, looks cleaner
    }
    return statusMessage;
  }
  
  
  
  public void die(){
    handler.removeEntity(this);
  }
   
  public Id getId (){
    return id;
  }
}
