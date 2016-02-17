package org.toolchild.suffering.gameobject.entity.mob;

import java.awt.Graphics2D;
import java.util.Random;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.entity.Entity;
import org.toolchild.suffering.gfx.Sprite;

public class Mob1 extends Entity {
  private static final Logger log        = Logger.getLogger(Mob1.class);

  private Random              random     = new Random();
  private int                 frame      = 0;
  private int                 frameDelay = 0;

  public Mob1(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites) {
    super(x, y, width, height, id, handler, sprites);
    int direction = this.random.nextInt(2); // direction: 0 = left ; 1 = right
    this.movement.setVelocityX(direction == 0 ? -3 : +3);
    // movement.setVelocityX(-1);

    this.movement.setMoveSpeed(5);
    this.movement.setMoving(true);
  }

  @Override
  public void tick() {
    if (this.movement.getVelocityX() < 0) {
      this.facing = 0;
    }
    if (this.movement.getVelocityX() > 0) {
      this.facing = 1;
    }
    log.trace("handle animation cycle: " + handleAnimationCycle());
    log.trace("update Position: " + updatePosition());
    log.trace("handle all tile interaction: " + handleAllInteraction());
    log.trace("handle gravity and movement: " + handleGravityAndMovement());
  }

  private boolean updatePosition() {
    this.x = this.x + this.movement.getVelocityX();
    this.y = this.y + this.movement.getVelocityY();
    // if (x <= 0) x = 0;
    // if (x + this.width >= Game.SIZE.getWidth()) x = (int) (Game.SIZE.getWidth() - this.width);
    return true;
  }

  private boolean handleAnimationCycle() {
    if (this.movement.getVelocityX() != 0) this.movement.setMoving(true);
    else this.movement.setMoving(false);
    if (this.movement.isMoving()) {
      this.frameDelay++;
      if (this.frameDelay >= 3) {
        this.frame++;
        if (this.frame >= this.sprites.length / 2) {
          this.frame = 0;
        }
        this.frameDelay = 0;
      }
    }
    return true;
  }

  private boolean handleGravityAndMovement() {
    log.trace("handle Falling " + this.movement.handleFalling());
    log.trace("handle Floating " + this.movement.handleFloating());
    return true;
  }

  @Override
  protected void handleAnimationRendering(Graphics2D graphics2D) {
    if (this.facing == 0) {
      graphics2D.drawImage(this.sprites[this.frame].getImage(), this.x, this.y, this.width, this.height, null);
      log.trace("facing left frame " + this.frame);
    } else if (this.facing == 1) {
      graphics2D.drawImage(this.sprites[this.frame + 8].getImage(), this.x, this.y, this.width, this.height, null);
      log.trace("facing right frame:" + (this.frame + 8));
    }
  }


}
