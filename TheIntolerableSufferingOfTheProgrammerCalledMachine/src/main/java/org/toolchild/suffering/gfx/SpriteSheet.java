package org.toolchild.suffering.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {

  private BufferedImage spriteSheet;

  public BufferedImage getSpriteSheet() {
    return this.spriteSheet;
  }

  public void setSpriteSheet(BufferedImage spriteSheet) {
    this.spriteSheet = spriteSheet;
  }

  public SpriteSheet(String path) {
    try {
      this.spriteSheet = ImageIO.read(getClass().getResource(path));
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public BufferedImage getSprite(int x, int y) {
    int factor = 32;
    return this.spriteSheet.getSubimage(x * factor, y * factor, factor, factor);
  }

  public BufferedImage getBiggerSprite(int x, int y) {
    int factor = 64;
    return this.spriteSheet.getSubimage(x * factor, y * factor, factor, factor);
  }

}
