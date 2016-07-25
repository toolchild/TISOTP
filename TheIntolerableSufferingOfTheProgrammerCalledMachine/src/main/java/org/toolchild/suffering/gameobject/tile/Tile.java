package org.toolchild.suffering.gameobject.tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.GameObject;

/**
 * Base for all Tiles used in the game.
 * 
 * @author Bob
 *
 */
public abstract class Tile extends GameObject {
  protected boolean isSolid;
  protected boolean activated = false;

  public Tile(int x, int y, int width, int height, Id id, BufferedImage[] bufferedImages, boolean isSolid) {
    super(x, y, width, height, id, bufferedImages);
    this.isSolid = isSolid;
  }

  public boolean isSolid() {
    return this.isSolid;
  }

  public boolean isActivated() {
    return this.activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  @Override
  public void render(Graphics2D graphics2d) {
    graphics2d.drawImage(this.bufferedImages[0], this.x, this.y, this.width, this.height, null);
  }

}
