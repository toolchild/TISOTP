package org.toolchild.suffering.gameobject.entity.powerup;

import java.awt.Graphics2D;
import java.util.Random;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.SpriteManager;
import org.toolchild.suffering.gameobject.entity.Entity;
import org.toolchild.suffering.gfx.Sprite;

public class BlueCrystal extends Entity {
  private static final Logger log        = Logger.getLogger(BlueCrystal.class);
  private int                 frame      = 0;
  private int                 frameDelay = 0;

  private Random              random     = new Random();

  public BlueCrystal(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites) {
    super(x, y, width, height, id, handler, sprites);
    int direction = random.nextInt(2); // direction: 0 = left ; 1 = right
    movement.setVelocityX(direction == 0 ? -3 : +3);
    movement.setMoveSpeed(3);
  }

  @Override
  public void tick() {
    log.trace("update position: " + updatePosition() + " x: " + x + " y: " + y);
    frameDelay++;
    if (frameDelay >= 3) {
      frame++;
      if (frame >= sprites.length / 2) {
        frame = 0;
      }
      frameDelay = 0;
    }
    log.trace("handle all tile interaction: " + handleAllInteraction());
    log.trace("handle gravity and movement: " + handleGravityAndMovement());
  }

  private boolean updatePosition() {
    x = x + movement.getVelocityX();
    y = y + movement.getVelocityY();
    // TODO: use for level bounds
    // if (x <= 0) x = 0;
    // if (x + this.width >= Game.SIZE.getWidth()) x = (int) (Game.SIZE.getWidth() - this.width);
    return true;
  }

  private boolean handleGravityAndMovement() {
    log.trace("handle Falling " + movement.handleFalling());
    log.trace("handle Floating " + movement.handleFloating());
    return true;
  }


  @Override
  protected void handleAnimationRendering(Graphics2D graphics2D) {
    graphics2D.drawImage(sprites[frame].getImage(), x, y, width, height, null);
  }
}
