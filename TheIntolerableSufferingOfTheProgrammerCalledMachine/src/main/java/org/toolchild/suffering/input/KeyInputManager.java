package org.toolchild.suffering.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.entity.Entity;
import org.toolchild.suffering.entity.Player;

public class KeyInputManager implements KeyListener {

  private static final Logger log = Logger.getLogger(KeyInputManager.class);

  private KeyStatus                   jumpKeyStatus;
  private KeyStatus                   leftKeyStatus;
  private KeyStatus                   rightKeyStatus;

  public void init() {
    jumpKeyStatus = new KeyStatus(false);
    leftKeyStatus = new KeyStatus(false);
    rightKeyStatus = new KeyStatus(false);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    int key = e.getKeyCode();
    for (Entity entity : Game.handler.entities) {
      if (entity.id == Id.player) {
        switch (key) {
          case KeyEvent.VK_W: {
            jumpKeyStatus.isActive = true;
            break;
          }
          case KeyEvent.VK_A: {
            leftKeyStatus.isActive = true;
            break;
          }
          case KeyEvent.VK_D: {
            rightKeyStatus.isActive = true;
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
            jumpKeyStatus.isActive = false;
            break;
          }
          case KeyEvent.VK_A: {
            leftKeyStatus.isActive = false;
            break;
          }
          case KeyEvent.VK_D: {
            rightKeyStatus.isActive = false;
            break;
          }
        }
      }
    }
  }
  
  public boolean updateKeyEvents(Player player) {
    if (!jumpKeyStatus.isActive) jumpKeyReleaseEvent(player);
    if (!leftKeyStatus.isActive) leftKeyReleaseEvent(player);
    if (!rightKeyStatus.isActive) rightKeyReleaseEvent(player);
    if (jumpKeyStatus.isActive) jumpKeyEvent(player);
    if (leftKeyStatus.isActive && !rightKeyStatus.isActive || leftKeyStatus.isActive && leftKeyStatus.timeStamp > rightKeyStatus.timeStamp) leftKeyEvent(player);
    if (rightKeyStatus.isActive && !leftKeyStatus.isActive || rightKeyStatus.isActive && rightKeyStatus.timeStamp > leftKeyStatus.timeStamp) rightKeyEvent(player);
    return true;
  }

  // ________________________________________ updateKeyEvents sub-methods ________________________________________

  private void jumpKeyEvent(Player player) {
    player.handleJumpKeyEvent(true);
  }

  private void leftKeyEvent(Player player) {
    player.handleLeftKeyEvent(true);
  }

  private void rightKeyEvent(Player player) {
    player.handleRightKeyEvent(true);
  }

  private void jumpKeyReleaseEvent(Player player) {
    log.trace("Jump Released");
  }

  private void leftKeyReleaseEvent(Player player) {
    player.handleLeftKeyEvent(false);
  }

  private void rightKeyReleaseEvent(Player player) {
    player.handleRightKeyEvent(false);
  }
  // ######################################## updateKeyEvents sub-methods ########################################

  @Override
  public void keyTyped(KeyEvent e) {}

}
