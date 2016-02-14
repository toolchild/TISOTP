package org.toolchild.suffering.gameobject.tile;

import java.awt.Graphics2D;

import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.SpriteManager;
import org.toolchild.suffering.gameobject.entity.powerup.BlueCrystal;
import org.toolchild.suffering.gfx.Sprite;

public class PowerUpBlock extends Tile {

  private Sprite  sprites[];
  private boolean used    = false;
  private int     spriteY = getY();

  public PowerUpBlock(int x, int y, int width, int height, boolean isSolid, Id id, Handler handler, Sprite[] sprites) {
    super(x, y, width, height, isSolid, id, handler, sprites);
    this.sprites = sprites;
  }

  @Override
  public void tick() {
    if (activated && !used) {
      spriteY--;
      Sprite blueCrystal[] = new Sprite[8];
      for (int i = 0; i < blueCrystal.length; i++) {
        blueCrystal[i] = sprites[i + 2];
      }
      if (spriteY <= y - height) {
        used = true;
        handler.addEntity(new BlueCrystal(x, spriteY, width, height, Id.blueCrystal, handler, blueCrystal));
      }
    }
  }

  @Override
  public void render(Graphics2D graphics2D) {
    if (!used) graphics2D.drawImage(sprites[2].getImage(), x, spriteY, width, height, null);
    if (!activated) graphics2D.drawImage(sprites[0].getImage(), x, y, width, height, null);
    else graphics2D.drawImage(sprites[1].getImage(), x, y, width, height, null);
  }
}
