package org.toolchild.suffering.gameobject;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Menu{
  private static final Logger log = LogManager.getLogger(Menu.class);
  private int                 x;
  private int                 y;
  private int                 width;
  private int                 height;
  private BufferedImage       menuBackgroundImage;

  public Menu(int x, int y, int width, int height, BufferedImage backgroundImage) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.menuBackgroundImage = backgroundImage;

  }

  
  public void render(Graphics2D graphics2d) {
    log.trace("backgRoundImage: " + this.menuBackgroundImage);
    graphics2d.drawImage(this.menuBackgroundImage, this.x, this.y, this.width, this.height, null);

  }

}

