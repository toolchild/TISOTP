package org.toolchild.suffering.gameobject.tile;

import java.awt.Graphics;

import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;

public class Wall extends Tile{

  public Wall(int x, int y, int width, int height, boolean isSolid, Id id, Handler handler) {
    super(x, y, width, height, isSolid, id, handler);
  }

  @Override
  public void tick() {
  }

  @Override
  public void render(Graphics graphics) {
  
    graphics.drawImage(Game.grass.getImage(), x, y,width, height, null);
  }

}
