package org.toolchild.suffering.entity;

import java.awt.Graphics;
import java.awt.Rectangle;

import org.toolchild.suffering.Camera;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;

public abstract class Entity {
  public int lineHeight = 20;
  public int column = 150;
  
  public int x;
  public int y;
  public int width;
  public int height;
  public boolean isSolid;
  
  public int velocityX;
  public int velocityY;
  public int moveSpeed;
  
  public Id id;
  public Handler handler;
  
  public boolean isJumping = false;
  public boolean isFalling = true;
  public double gravity = 0.0;
  public int facing = 0; // 0 = left; 1 = right
  
  
  public Entity(int x, int y, int width, int height, boolean isSolid, Id id, Handler handler){
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.isSolid = isSolid;
    this.id = id;
    this.handler = handler;
  }
  
  public abstract void tick();

  /**
   * 
   * Renders this Entity.
   * 
   * @param graphics  are expected released from the camera
   * @param camera  the camera, for lock and releasing purposes
   */
  public abstract void render(Graphics graphics, Camera camera);
  
  public void die(){
    handler.removeEntity(this);
  }
  public Rectangle getBounds(){
    return new Rectangle(x, y, width, height);
  }
  
  public Rectangle getBoundsTop(){
    return new Rectangle(x+10, y, width-20, 5);
  }
  
  public Rectangle getBoundsBottom(){
    return new Rectangle(x+10, y+height-5, width-20,5);
  }
  
  public Rectangle getBoundsLeft(){
    return new Rectangle(x, y+10, 5 ,height-20);
  }
  
  public Rectangle getBoundsRight(){
    return new Rectangle(x+width-5, y+10, 5 ,height-20);
  }
   
  public Id getId (){
    return id;
  }
}
