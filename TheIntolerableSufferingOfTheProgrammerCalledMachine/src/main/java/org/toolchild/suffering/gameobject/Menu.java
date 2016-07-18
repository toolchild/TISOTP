package org.toolchild.suffering.gameobject;

import java.awt.Graphics2D;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gfx.Sprite;

public class Menu extends GameObject{
  private static final Logger    log      = Logger.getLogger(Menu.class);

  public Menu(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites) {
    super(x, y, width, height, id, handler, sprites);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void render(Graphics2D graphics2d) {
    // TODO Auto-generated method stub
    
  }
  
  public void tick(){
    log.info("tick");
  }
  
 
  
}
