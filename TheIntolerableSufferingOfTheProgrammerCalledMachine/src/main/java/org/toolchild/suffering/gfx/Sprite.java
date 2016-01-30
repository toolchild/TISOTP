package org.toolchild.suffering.gfx;

import java.awt.image.BufferedImage;

public class Sprite {

  public SpriteSheet spriteSheet;
  
  public BufferedImage image;
  
  public Sprite(SpriteSheet spriteSheet, int x , int y ){
    image = spriteSheet.getSprite(x, y);
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage(BufferedImage image) {
    this.image = image;
  }
  
}
