package org.toolchild.suffering.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.entity.Entity;

public class KeyInput implements KeyListener {
  private static final Logger log = Logger.getLogger(KeyInput.class);

  @Override
  public void keyPressed(KeyEvent e) {
    int key = e.getKeyCode();
    for (Entity entity : Game.handler.entities) {

      switch (key) {
      case KeyEvent.VK_W: {
        if (!entity.isJumping && entity.gravity < 1.0) {
          log.debug("Jump!");
          entity.isJumping = true;
          entity.gravity = -10.0;
        }
        break;
      }

      case KeyEvent.VK_A: {
        entity.setVelocityX(-5);
        break;
      }
      case KeyEvent.VK_D: {
        entity.setVelocityX(5);
        break;
      }
      }
    }
  }

  
  
  @Override
  public void keyReleased(KeyEvent e) {
    int key = e.getKeyCode();
    for (Entity entity : Game.handler.entities) {

      switch (key) {
      case KeyEvent.VK_W: {
        entity.setVelocityY(0);
        break;
      }
      
      case KeyEvent.VK_A: {
        entity.setVelocityX(0);
        break;
      }
      case KeyEvent.VK_D: {
        entity.setVelocityX(0);
        break;
      }
      }
    }

  }

  @Override
  public void keyTyped(KeyEvent e) {
    // TODO Auto-generated method stub

  }

}
