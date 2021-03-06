package org.toolchild.suffering.gameobject;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Menu{
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

  
  public void render(Graphics2D graphics2d, int frameWidth, int frameHeight) {
    graphics2d.setFont(new Font("TimesRoman", Font.BOLD, 60)); // the 60 should be derived from Game.Size
    graphics2d.drawImage(this.menuBackgroundImage, this.x, this.y, this.width, this.height, null);
    graphics2d.drawString("Game Paused", frameWidth/3, frameHeight/2); //is this clean?
 


  }

}

