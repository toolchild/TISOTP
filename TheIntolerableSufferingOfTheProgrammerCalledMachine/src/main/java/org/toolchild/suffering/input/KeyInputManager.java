package org.toolchild.suffering.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Game;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.GameObject;
import org.toolchild.suffering.gameobject.Menu;
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
  private KeyStatus           spaceKeyStatus;

  public void init() {
    this.jumpKeyStatus = new KeyStatus(false);
    this.leftKeyStatus = new KeyStatus(false);
    this.rightKeyStatus = new KeyStatus(false);
    this.spaceKeyStatus = new KeyStatus(false);

  }

  public boolean updateKeyEvents(Player player, Menu menu) {

    if (!this.leftKeyStatus.isActive) leftKeyReleaseEvent(player);
    if (!this.rightKeyStatus.isActive) rightKeyReleaseEvent(player);
    // keep releaseEvent and Event separated for left and right key. You will break your brain if you don't.
    if (this.leftKeyStatus.isActive && !this.rightKeyStatus.isActive || this.leftKeyStatus.isActive && this.leftKeyStatus.timeStamp > this.rightKeyStatus.timeStamp) leftKeyEvent(player);
    if (this.rightKeyStatus.isActive && !this.leftKeyStatus.isActive || this.rightKeyStatus.isActive && this.rightKeyStatus.timeStamp > this.leftKeyStatus.timeStamp) rightKeyEvent(player);

    if (!this.jumpKeyStatus.isActive) jumpKeyReleaseEvent(player);
    else jumpKeyEvent(player);

    if (!this.spaceKeyStatus.isActive) spaceKeyReleaseEvent(player, menu);
    else if (this.spaceKeyStatus.canActivate) spaceKeyEvent(player, menu);

    return true;
  }

  private void jumpKeyEvent(Player player) {
    if (player != null) {
      this.jumpKeyStatus.timeStamp = System.nanoTime();
      player.handleJumpKeyEvent(true);
    }
  }

  private void jumpKeyReleaseEvent(Player player) {
    if (player != null) player.handleJumpKeyEvent(false);
    log.trace("Jump Released");
  }

  private void leftKeyEvent(Player player) {
    if (player != null) player.handleLeftKeyEvent(true);
  }

  private void leftKeyReleaseEvent(Player player) {
    log.info("LeftKeyReleaseEvent: canActivate: " + this.spaceKeyStatus.canActivate + " isActive:" + this.spaceKeyStatus.isActive);
    if (player != null) player.handleLeftKeyEvent(false);
  }

  private void rightKeyEvent(Player player) {

    if (player != null) player.handleRightKeyEvent(true);
  }

  private void rightKeyReleaseEvent(Player player) {
    if (player != null) player.handleRightKeyEvent(false);
  }

  private void spaceKeyEvent(Player player, Menu menu) {
    this.spaceKeyStatus.canActivate = false;
    log.info("canActivate: " + this.spaceKeyStatus.canActivate + " isActive:" + this.spaceKeyStatus.isActive);
    if (player != null) player.handleSpaceKeyEvent(true);
    else if (menu != null) menu.handleSpaceKeyEvent(true);
  }

  private void spaceKeyReleaseEvent(Player player, Menu menu) {
    this.spaceKeyStatus.canActivate = true;
    if (player != null) player.handleSpaceKeyEvent(false);
    else if (menu != null) menu.handleSpaceKeyEvent(false);
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
            log.info("LeftKeyPressed: left time: " + this.leftKeyStatus.timeStamp + " right Time:" + this.rightKeyStatus.timeStamp);
            this.leftKeyStatus.isActive = true;
            break;
          }
          case KeyEvent.VK_D: {
            log.info("RightKeyPressed: left time: " + this.leftKeyStatus.timeStamp + " right Time:" + this.rightKeyStatus.timeStamp);
            this.rightKeyStatus.isActive = true;
            break;
          }
          case KeyEvent.VK_SPACE: {
            this.spaceKeyStatus.isActive = true;
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
            this.leftKeyStatus.timeStamp = System.nanoTime();
            log.info("LeftKeyReleased: left time: " + this.leftKeyStatus.timeStamp + " right Time:" + this.rightKeyStatus.timeStamp);
            this.leftKeyStatus.isActive = false;
            break;
          }
          case KeyEvent.VK_D: {
            this.rightKeyStatus.timeStamp = System.nanoTime();
            log.info("RightKeyRelease: left time: " + this.leftKeyStatus.timeStamp + " right Time:" + this.rightKeyStatus.timeStamp);
            this.rightKeyStatus.isActive = false;
            break;
          }
          case KeyEvent.VK_SPACE: {
            log.info("SpaceKeyReleased");
            this.spaceKeyStatus.isActive = false;
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
