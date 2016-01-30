package org.toolchild.suffering.tile;

import java.awt.Graphics;
import java.awt.Rectangle;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;

public abstract class Tile {
  int x;
  int y;
  int width;
  int height;
  boolean isSolid;
  
  public int velocityX;
  public int velocityY;
  
  public Id id;
  public Handler handler;
  
  
  public Tile(int x, int y, int width, int height, boolean isSolid, Id id, Handler handler){
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.isSolid = isSolid;
    this.id = id;
    this.handler = handler;
  }
  
  public abstract void tick();

  public abstract void render(Graphics graphics);

  
  public void die(){
    handler.removeTile(this);
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public boolean isSolid() {
    return isSolid;
  }

  public void setSolid(boolean isSolid) {
    this.isSolid = isSolid;
  }

  public void setVelocityX(int velocityX) {
    this.velocityX = velocityX;
  }

  public void setVelocityY(int velocityY) {
    this.velocityY = velocityY;
  }

  public Id getId() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }
  
  public Rectangle getBounds(){
    return new Rectangle(x, y, width, height);
  }
}
