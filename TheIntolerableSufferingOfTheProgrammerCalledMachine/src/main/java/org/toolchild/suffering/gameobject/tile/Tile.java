package org.toolchild.suffering.gameobject.tile;


import java.awt.Graphics2D;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.GameObject;
import org.toolchild.suffering.gfx.Sprite;

public abstract class Tile extends GameObject {
  protected boolean isSolid;
  protected boolean activated = false;
  
  public Tile(int x, int y, int width, int height, boolean isSolid, Id id, Handler handler, Sprite[] sprites){
    super(x, y, width, height, id, handler, sprites);
    this.isSolid = isSolid;
   
  }
  
  public abstract void tick();

  public abstract void render(Graphics2D graphics2D);
  
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

  public Id getId() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }
  
  
  public boolean isActivated() {
    return this.activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  public Handler getHandler() {
    return this.handler;
  }

  public void setHandler(Handler handler) {
    this.handler = handler;
  }
  
}
