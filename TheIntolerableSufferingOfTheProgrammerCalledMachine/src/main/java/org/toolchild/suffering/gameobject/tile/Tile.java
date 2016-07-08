package org.toolchild.suffering.gameobject.tile;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.GameObject;
import org.toolchild.suffering.gfx.Sprite;

/**
 * Base for all Tiles used in the game.
 * 
 * @author Bob
 *
 */
public abstract class Tile extends GameObject {
  protected boolean isSolid;
  protected boolean activated = false;

  public Tile(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites, boolean isSolid) {
    super(x, y, width, height, id, handler, sprites);
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

}
