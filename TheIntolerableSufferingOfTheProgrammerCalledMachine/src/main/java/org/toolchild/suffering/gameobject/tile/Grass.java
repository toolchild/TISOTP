package org.toolchild.suffering.gameobject.tile;

import java.awt.Graphics2D;

import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.SpriteManager;
import org.toolchild.suffering.gfx.Sprite;

public class Grass extends Tile{

  public Grass(int x, int y, int width, int height, boolean isSolid, Id id, Handler handler, Sprite[] sprites) {
    super(x, y, width, height, isSolid, id, handler, sprites);
  }

  @Override
  public void tick() {
  }

  @Override
  public void render(Graphics2D graphics2D) {
  
    graphics2D.drawImage(sprites[0].getImage(), x, y,width, height, null);
  }

}
