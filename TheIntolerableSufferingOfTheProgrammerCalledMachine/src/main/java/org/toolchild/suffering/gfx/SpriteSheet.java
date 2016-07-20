package org.toolchild.suffering.gfx;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpriteSheet {
  private static final Logger log = LogManager.getLogger(SpriteSheet.class);

  private BufferedImage       spriteSheetImage;

  public SpriteSheet(String path) {
    try {
      this.spriteSheetImage = ImageIO.read(getClass().getResource(path));
      if (this.spriteSheetImage == null) {
        throw new Exception("spriteSheet '" + path + "' count not be created.");
      }
    }
    catch (Exception e) {
      log.error("Image not found: '" + e.getMessage() + "' path: " + path);
    }
  }

  public BufferedImage getSpriteImage(int x, int y) {
    int factor = 32;
    return this.spriteSheetImage.getSubimage(x * factor, y * factor, factor, factor);
  }

  public BufferedImage getBiggerSpriteImage(int x, int y) {
    int factor = 64;
    return this.spriteSheetImage.getSubimage(x * factor, y * factor, factor, factor);
  }

  public BufferedImage getSpriteSheetImage() {
    return this.spriteSheetImage;
  }

  public void setSpriteSheetImage(BufferedImage spriteSheet) {
    this.spriteSheetImage = spriteSheet;
  }
}
