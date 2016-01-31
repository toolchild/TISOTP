package org.toolchild.suffering.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.Id;
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
    if (!keysActive.get("w").isActive) wKeyReleaseEvent(entity);
    if (!keysActive.get("a").isActive) aKeyReleaseEvent(entity);
    if (!keysActive.get("d").isActive) dKeyReleaseEvent(entity);

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
      if (entity.id == Id.player) {
        switch (key) {
          case KeyEvent.VK_W: {
            keysActive.put("w", new KeyStatus(true));
            break;
          }
          case KeyEvent.VK_A: {
            keysActive.put("a", new KeyStatus(true));
            entity.facing = 0;
            break;
          }
          case KeyEvent.VK_D: {
            keysActive.put("d", new KeyStatus(true));
            entity.facing = 1;
            break;
          }
        }
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    int key = e.getKeyCode();
    for (Entity entity : Game.handler.entities) {
      if (entity.id == Id.player) {
        switch (key) {
          case KeyEvent.VK_W: {
            keysActive.put("w", new KeyStatus(false));
            break;
          }
          case KeyEvent.VK_A: {
            keysActive.put("a", new KeyStatus(false));
            break;
          }
          case KeyEvent.VK_D: {
            keysActive.put("d", new KeyStatus(false));
            break;
          }
        }
      }
    }
  }

  private void wKeyEvent(Entity entity) {
    if (!entity.isJumping && entity.gravity < 1.0) {
      entity.isJumping = true;
      entity.gravity = -10.0;
      log.trace("Jumped!");
    }
  }

  private void aKeyEvent(Entity entity) {
    entity.setVelocityX(-5);
    log.trace("Went Left!");
  }

  private void dKeyEvent(Entity entity) {
    entity.setVelocityX(5);
    log.trace("Went Right!");
  }

  private void aKeyReleaseEvent(Entity entity) {
    log.trace("Left Released");
    entity.setVelocityX(0);
  }

  private void dKeyReleaseEvent(Entity entity) {
    log.trace("Right Released");
    entity.setVelocityX(0);
  }

  private void wKeyReleaseEvent(Entity entity) {
    log.trace("Jump Released");
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // TODO Auto-generated method stub

  }

}
