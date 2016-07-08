package org.toolchild.suffering.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.GameObject;
import org.toolchild.suffering.gameobject.entity.Player;
/**
 * 
 * The KeyInputManager adds functionality to the {@link KeyListener}.
 * 
 * @author Bob
 *
 */
public class KeyInputManager implements KeyListener {

  private static final Logger log = Logger.getLogger(KeyInputManager.class);

  private KeyStatus           jumpKeyStatus;
  private KeyStatus           leftKeyStatus;
  private KeyStatus           rightKeyStatus;

  public void init() {
    this.jumpKeyStatus = new KeyStatus(false);
    this.leftKeyStatus = new KeyStatus(false);
    this.rightKeyStatus = new KeyStatus(false);
  }

  public boolean updateKeyEvents(Player player) {
    if (!this.jumpKeyStatus.isActive) jumpKeyReleaseEvent(player);
    if (!this.leftKeyStatus.isActive) leftKeyReleaseEvent(player);
    if (!this.rightKeyStatus.isActive) rightKeyReleaseEvent(player);
    if (this.jumpKeyStatus.isActive) jumpKeyEvent(player);
    if (this.leftKeyStatus.isActive && !this.rightKeyStatus.isActive || this.leftKeyStatus.isActive && this.leftKeyStatus.timeStamp > this.rightKeyStatus.timeStamp) leftKeyEvent(player);
    if (this.rightKeyStatus.isActive && !this.leftKeyStatus.isActive || this.rightKeyStatus.isActive && this.rightKeyStatus.timeStamp > this.leftKeyStatus.timeStamp) rightKeyEvent(player);
    return true;
  }

  private static void jumpKeyEvent(Player player) {
    player.handleJumpKeyEvent(true);
  }

  private static void jumpKeyReleaseEvent(Player player) {
    player.handleJumpKeyEvent(false);
    log.trace("Jump Released");
  }

  private static void leftKeyEvent(Player player) {
    player.handleLeftKeyEvent(true);
  }

  private static void leftKeyReleaseEvent(Player player) {
    player.handleLeftKeyEvent(false);
  }

  private static void rightKeyEvent(Player player) {
    player.handleRightKeyEvent(true);
  }

  private static void rightKeyReleaseEvent(Player player) {
    player.handleRightKeyEvent(false);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    int key = e.getKeyCode();
    for (GameObject player : Game.HANDLER.getPlayers()) {
      if (player.getId() == Id.player) {
        switch (key) {
          case KeyEvent.VK_W: {
            this.jumpKeyStatus.isActive = true;
            break;
          }
          case KeyEvent.VK_A: {
            this.leftKeyStatus.isActive = true;
            break;
          }
          case KeyEvent.VK_D: {
            this.rightKeyStatus.isActive = true;
            break;
          }
          default: {
            break;
          }
        }
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    int key = e.getKeyCode();
    for (GameObject player : Game.HANDLER.getPlayers()) {
      if (player.getId() == Id.player) {
        switch (key) {
          case KeyEvent.VK_W: {
            this.jumpKeyStatus.isActive = false;
            break;
          }
          case KeyEvent.VK_A: {
            this.leftKeyStatus.isActive = false;
            break;
          }
          case KeyEvent.VK_D: {
            this.rightKeyStatus.isActive = false;
            break;
          }
          default: {
            break;
          }
        }
      }
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // inherited but not used
  }

}
