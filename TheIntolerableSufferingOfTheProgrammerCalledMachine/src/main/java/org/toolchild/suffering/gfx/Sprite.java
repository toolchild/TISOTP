package org.toolchild.suffering.gfx;

import java.awt.image.BufferedImage;

public class Sprite {

  public BufferedImage image;

  public Sprite(SpriteSheet spriteSheet, int x, int y, boolean isBigger) {
    if (isBigger) {
      image = spriteSheet.getBiggerSprite(x, y);
    } else {
      image = spriteSheet.getSprite(x, y);
    }
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage(BufferedImage image) {
    this.image = image;
  }

}
