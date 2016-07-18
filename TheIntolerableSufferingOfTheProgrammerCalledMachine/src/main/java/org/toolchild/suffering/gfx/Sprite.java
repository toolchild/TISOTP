package org.toolchild.suffering.gfx;

import java.awt.image.BufferedImage;

public class Sprite{

  public BufferedImage image;

  public Sprite(SpriteSheet spriteSheet, int x, int y, boolean isBigger) {
    if (isBigger) {
      this.image = spriteSheet.getBiggerSprite(x, y);
    } else {
      this.image = spriteSheet.getSprite(x, y);
    }
  }

  public BufferedImage getImage() {
    return this.image;
  }

  public void setImage(BufferedImage image) {
    this.image = image;
  }
  
  public Sprite getFullSpriteSheet(SpriteSheet spriteSheet){
    this.image = spriteSheet.getSpriteSheet();
    return this;
  }
}
