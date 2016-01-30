package org.toolchild.suffering.tile;

import java.awt.Color;
import java.awt.Graphics;

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
    graphics.setColor(Color.RED);
    graphics.fillRect(x, y, width, height);
    graphics.setColor(Color.BLUE);
    graphics.fillRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height);

    
  }

}
