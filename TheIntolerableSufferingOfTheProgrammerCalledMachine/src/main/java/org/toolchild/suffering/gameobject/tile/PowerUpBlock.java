package org.toolchild.suffering.gameobject.tile;


import java.awt.Graphics2D;

import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.entity.powerup.BlueCrystal;
import org.toolchild.suffering.gfx.Sprite;

public class PowerUpBlock extends Tile {

//  private Sprite  powerup;
  private boolean used    = false;
  private int     spriteY = getY();

  public PowerUpBlock(int x, int y, int width, int height, boolean isSolid, Id id, Handler handler, Sprite powerup) {
    super(x, y, width, height, isSolid, id, handler);
//    this.powerup = powerup;
  }

  @Override
  public void tick() {
    if(activated && !used){
      spriteY--;
      if(spriteY <= y - height){
        used = true;
        handler.addEntity(new BlueCrystal(x, spriteY, width, height, Id.blueCrystal, handler));
      }
    }
    // TODO Auto-generated method stub

  }

  @Override
  public void render(Graphics2D graphics2D) {
    if (!used) graphics2D.drawImage(Game.blueCrystal[1].getImage(), x, spriteY, width, height, null);
    if (!activated) graphics2D.drawImage(Game.powerupBlock.getImage(), x, y, width, height, null);
    else graphics2D.drawImage(Game.usedPowerupBlock.getImage(), x, y, width, height, null);
  }
}
