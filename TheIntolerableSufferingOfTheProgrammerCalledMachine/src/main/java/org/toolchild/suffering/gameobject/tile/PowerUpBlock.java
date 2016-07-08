package org.toolchild.suffering.gameobject.tile;

import java.awt.Graphics2D;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.entity.powerup.BlueCrystal;
import org.toolchild.suffering.gfx.Sprite;

public class PowerUpBlock extends Tile {

  private boolean used    = false;
  private int     spriteY = getY();

  public PowerUpBlock(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites, boolean isSolid) {
    super(x, y, width, height, id, handler, sprites, isSolid);
    this.sprites = sprites; // This should be obsolete.
  }

  public void tick() {
    if (this.activated && !this.used) {
      this.spriteY--;
      Sprite[] blueCrystal = new Sprite[8];
      for (int i = 0; i < blueCrystal.length; i++) {
        blueCrystal[i] = this.sprites[i + 2];
      }
      if (this.spriteY <= this.y - this.height) {
        this.used = true;
        this.handler.addEntity(new BlueCrystal(this.x, this.spriteY, this.width, this.height, Id.blueCrystal, this.handler, blueCrystal));
      }
    }
  }

  @Override
  public void render(Graphics2D graphics2D) {
    if (!this.used) graphics2D.drawImage(this.sprites[2].getImage(), this.x, this.spriteY, this.width, this.height, null);
    if (!this.activated) graphics2D.drawImage(this.sprites[0].getImage(), this.x, this.y, this.width, this.height, null);
    else graphics2D.drawImage(this.sprites[1].getImage(), this.x, this.y, this.width, this.height, null);
  }
}
