package org.toolchild.suffering.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.toolchild.suffering.SpriteManager;

public class SpriteSheet {
  private static final Logger log = Logger.getLogger(SpriteSheet.class);

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
    catch (Exception e) {
      log.error("Image not found: '" + e.getMessage() + "' path: " + path);
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
