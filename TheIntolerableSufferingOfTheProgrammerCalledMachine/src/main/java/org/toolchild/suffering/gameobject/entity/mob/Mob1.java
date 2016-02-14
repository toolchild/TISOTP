package org.toolchild.suffering.gameobject.entity.mob;

import java.awt.Graphics2D;
import java.util.Random;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.SpriteManager;
import org.toolchild.suffering.gameobject.entity.Entity;
import org.toolchild.suffering.gfx.Sprite;

public class Mob1 extends Entity {
  private static final Logger log        = Logger.getLogger(Mob1.class);

  private Random              random     = new Random();
  private int                 frame      = 0;
  private int                 frameDelay = 0;

  public Mob1(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites) {
    super(x, y, width, height, id, handler, sprites);
    int direction = random.nextInt(2); // direction: 0 = left ; 1 = right
    movement.setVelocityX(direction == 0 ? -3 : +3);
    // movement.setVelocityX(-1);

    movement.setMoveSpeed(5);
    movement.setMoving(true);
  }

  @Override
  public void tick() {
    if (movement.getVelocityX() < 0) {
      facing = 0;
    }
    if (movement.getVelocityX() > 0) {
      facing = 1;
    }
    log.trace("handle animation cycle: " + handleAnimationCycle());
    log.trace("update Position: " + updatePosition());
    log.trace("handle all tile interaction: " + handleAllInteraction());
    log.trace("handle gravity and movement: " + handleGravityAndMovement());
  }

  private boolean updatePosition() {
    x = x + movement.getVelocityX();
    y = y + movement.getVelocityY();
    // if (x <= 0) x = 0;
    // if (x + this.width >= Game.SIZE.getWidth()) x = (int) (Game.SIZE.getWidth() - this.width);
    return true;
  }

  private boolean handleAnimationCycle() {
    if (movement.getVelocityX() != 0) movement.setMoving(true);
    else movement.setMoving(false);
    if (movement.isMoving()) {
      frameDelay++;
      if (frameDelay >= 3) {
        frame++;
        if (frame >= sprites.length / 2) {
          frame = 0;
        }
        frameDelay = 0;
      }
    }
    return true;
  }

  private boolean handleGravityAndMovement() {
    log.trace("handle Falling " + movement.handleFalling());
    log.trace("handle Floating " + movement.handleFloating());
    return true;
  }

  @Override
  protected void handleAnimationRendering(Graphics2D graphics2D) {
    if (facing == 0) {
      graphics2D.drawImage(sprites[frame].getImage(), x, y, width, height, null);
      log.trace("facing left frame " + frame);
    } else if (facing == 1) {
      graphics2D.drawImage(sprites[frame + 8].getImage(), x, y, width, height, null);
      log.trace("facing right frame:" + (frame + 8));
    }
  }


}
