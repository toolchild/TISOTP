package org.toolchild.suffering.gameobject;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gfx.Sprite;

public class Menu extends GameObject {
  private static final Logger log = Logger.getLogger(Menu.class);

  public Menu(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites) {
    super(x, y, width, height, id, handler, sprites);
  }

  @Override
  public void render(Graphics2D graphics2d) {
    BufferedImage menuBackground = Game.SPRITE_MANAGER.getBackground();
    graphics2d.drawImage(menuBackground, 0, 0, (int) Game.SIZE.getWidth(), (int) Game.SIZE.getHeight(), null);

  }

  public void tick() {
    Game.keyInput.updateKeyEvents(null, this);
//    log.info("tick");
  }

  public void handleSpaceKeyEvent(boolean isActive) {
    if (isActive) {
      log.info("Game Resumed");
      this.handler.setPaused(false);
    }
  }

  public void handleSpaceKeyReleaseEvent(boolean isActive) {
    // TODO Auto-generated method stub
    
  }

}
