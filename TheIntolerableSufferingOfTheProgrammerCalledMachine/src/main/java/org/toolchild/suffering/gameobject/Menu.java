package org.toolchild.suffering.gameobject;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toolchild.suffering.Game;

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
    graphics2d.setFont(new Font("TimesRoman", Font.BOLD, 60)); // the 60 should be derived from Game.Size
    graphics2d.drawImage(this.menuBackgroundImage, this.x, this.y, this.width, this.height, null);
    graphics2d.drawString("Game Paused", Game.getFrameWidth()/3, Game.getFrameHeight()/2); //is this clean?
 


  }

}

