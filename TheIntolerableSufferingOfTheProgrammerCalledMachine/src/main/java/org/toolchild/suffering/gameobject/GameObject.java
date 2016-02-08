package org.toolchild.suffering.gameobject;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;

public abstract class GameObject {
  protected int lineHeight = 20;
  protected int column = 150;

  protected int x;
  protected int y;
  protected int width;
  protected int height;

  protected Id  id;

  public abstract void tick();
  
  public abstract void render(Graphics2D graphics2D);

  protected Handler handler;
  public GameObject(int x, int y, int width, int height, Id id, Handler handler) {
    super();
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.id = id;
    this.handler = handler;
    
  }

  protected int boundsOffset = 10;
  protected int boundsWidth = 5;
  
  public Rectangle getBounds() {
    return new Rectangle(x, y, width, height);
  }

  public Rectangle getBoundsTop() {
    return new Rectangle(x + boundsOffset, y, width - 2* boundsOffset, boundsWidth);
  }

  public Rectangle getBoundsBottom() {
    return new Rectangle(x + boundsOffset, y + height, width - 2* boundsOffset, boundsWidth);
  }

  public Rectangle getBoundsLeft() {
    return new Rectangle(x, y + boundsOffset, boundsWidth, height - 2*boundsOffset);
  }

  public Rectangle getBoundsRight() {
    return new Rectangle(x + width - boundsWidth, y + boundsOffset, boundsWidth, height - 2*boundsOffset);
  }

  
  public int getX() {
    return this.x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return this.y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getWidth() {
    return this.width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return this.height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public Id getId() {
    return this.id;
  }

  public void setId(Id id) {
    this.id = id;
  }

  public Handler getHandler() {
    return this.handler;
  }

  public void setHandler(Handler handler) {
    this.handler = handler;
  }

  
}
