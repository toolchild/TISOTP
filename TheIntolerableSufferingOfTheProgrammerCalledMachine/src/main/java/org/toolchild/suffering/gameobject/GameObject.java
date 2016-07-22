package org.toolchild.suffering.gameobject;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gfx.Sprite;

public abstract class GameObject {
  protected int      lineHeight = 20;
  protected int      column     = 150;

  protected int      x;
  protected int      y;
  protected int      width;
  protected int      height;

  protected Sprite[] sprites;

  protected Id       id;

//  public abstract void tick();

  public abstract void render(Graphics2D graphics2D);

  protected Handler handler;

  public GameObject(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites) {
    super();
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.id = id;
    this.handler = handler;
    this.sprites = sprites;

  }

 
  public Rectangle getBounds() {
    return new Rectangle(this.x, this.y, this.width, this.height);
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public Id getId() {
    return this.id;
  }


}
