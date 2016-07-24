package org.toolchild.suffering.gfx;

import java.awt.image.BufferedImage;

public class ImageExtractor extends GFXLoader {
  
  
  @Override
  public void init() {
    super.init();
  }
  
  public BufferedImage getMenuBackgroundImage() {
    return this.menuBackgroundImage;
  }

  public BufferedImage getLevel1Image() {
    return this.level1Image;
  }

  public BufferedImage getLevel2Image() {
    return this.level2Image;
  }
  
  public BufferedImage[] getPowerUpBlock() {
    return getBufferedImageArray(this.powerUpBlock);
  }

  public BufferedImage[] getGrass() {
    return getBufferedImageArray(this.grass);
  }

  public BufferedImage[] getPlayer() {
    return getBufferedImageArray(this.player);
  }

  public BufferedImage[] getMob1() {
    return getBufferedImageArray(this.mob1);
  }

  public BufferedImage[] getBlueCrystal() {
    return getBufferedImageArray(this.blueCrystal);
  }

  public BufferedImage[] getFinish() {
    return this.getBufferedImageArray(finish);
  }
  
  private BufferedImage[] getBufferedImageArray(Sprite [] sprites){
  BufferedImage[] bufferedImages= new BufferedImage[sprites.length];
    for (int i = 0; i < bufferedImages.length; i++) {      
      bufferedImages[i] = sprites[i].getImage();
    }
    return bufferedImages;
  }
 
}
