package org.toolchild.suffering.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.entity.Entity;

public class KeyInput implements KeyListener {
  private class KeyStatus {
    boolean isActive;
    Long    timeStamp;

    public KeyStatus(boolean isActive) {
      this.isActive = isActive;
      this.timeStamp = System.nanoTime();
    }
  }

  private static final Logger    log = Logger.getLogger(KeyInput.class);

  private Map<String, KeyStatus> keysActive;

  public void init() {
    keysActive = new HashMap<String, KeyStatus>();
    keysActive.put("w", new KeyStatus(false));
    keysActive.put("a", new KeyStatus(false));
    keysActive.put("d", new KeyStatus(false));
  }

  public boolean updateKeyEvents(Entity entity) {
    if (!keysActive.get("w").isActive) dKeyReleaseEvent(entity);
    if (!keysActive.get("a").isActive) aKeyReleaseEvent(entity);
    if (!keysActive.get("d").isActive) aKeyReleaseEvent(entity);
    
    if (keysActive.get("w").isActive) wKeyEvent(entity);
   
    boolean isAKeyActive = keysActive.get("a").isActive; 
    long aKeyTimeStamp = keysActive.get("a").timeStamp; 
    boolean isDKeyActive = keysActive.get("d").isActive; 
    long dKeyTimeStamp = keysActive.get("d").timeStamp;    
    if (isAKeyActive && !isDKeyActive || isAKeyActive && aKeyTimeStamp > dKeyTimeStamp) aKeyEvent(entity);
    if (isDKeyActive && !isAKeyActive || isDKeyActive && dKeyTimeStamp > aKeyTimeStamp) dKeyEvent(entity);

    return true;
  }

  @Override
  public void keyPressed(KeyEvent e) {

    int key = e.getKeyCode();
    for (Entity entity : Game.handler.entities) {

      switch (key) {
        case KeyEvent.VK_W: {
          keysActive.put("w", new KeyStatus(true));
          wKeyEvent(entity);
          break;
        }

        case KeyEvent.VK_A: {
          keysActive.put("a", new KeyStatus(true));
          aKeyEvent(entity);
          break;
        }
        case KeyEvent.VK_D: {
          keysActive.put("d", new KeyStatus(true));
          dKeyEvent(entity);
          break;
        }
      }
    }
  }

  private void wKeyEvent(Entity entity) {
    if (!entity.isJumping && entity.gravity < 1.0) {
      entity.isJumping = true;
      entity.gravity = -10.0;
      log.debug("Jumped!");
    }
  }

  private void aKeyEvent(Entity entity) {
    entity.setVelocityX(-5);
    log.debug("Went Left!");
  }

  private void dKeyEvent(Entity entity) {
    entity.setVelocityX(5);
    log.debug("Went Right!");
  }

  @Override
  public void keyReleased(KeyEvent e) {
    int key = e.getKeyCode();
    for (Entity entity : Game.handler.entities) {

      switch (key) {
        case KeyEvent.VK_W: {
          keysActive.put("w", new KeyStatus(false));
          wKeyReleaseEvent(entity);
          break;
        }

        case KeyEvent.VK_A: {
          keysActive.put("a", new KeyStatus(false));
          aKeyReleaseEvent(entity);
          break;
        }
        case KeyEvent.VK_D: {
          keysActive.put("d", new KeyStatus(false));
          dKeyReleaseEvent(entity);
          break;
        }
      }
    }

  }

  private void aKeyReleaseEvent(Entity entity) {
    log.debug("Jump Released");
    entity.setVelocityX(0);
  }

  private void dKeyReleaseEvent(Entity entity) {
    log.debug("Right Released");
    entity.setVelocityX(0);
  }

  private void wKeyReleaseEvent(Entity entity) {
    log.debug("Left Released");
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // TODO Auto-generated method stub

  }

}
