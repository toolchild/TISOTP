package org.toolchild.suffering.gameobject.entity.mob;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.entity.Entity;

/**
 * Mob1 is a standard mob.
 * 
 * @author Bob
 *
 */
public class Mob1 extends Entity {
  private Random              random     = new Random();
  private int                 frame      = 0;
  private int                 frameDelay = 0;

  public Mob1(int x, int y, int width, int height, Id id, Handler handler, BufferedImage[] bufferedImages) {
    super(x, y, width, height, id, handler, bufferedImages);
    int direction = this.random.nextInt(2); // direction: 0 = left ; 1 = right
    this.movement.setVelocityX(direction == 0 ? -1 : +1);
    // movement.setVelocityX(-1);
    this.movement.setMoveSpeed(3);
    this.movement.setMoving(true);
  }

  @Override
  public void tick(int speedModifier) {
    if (this.movement.getVelocityX() < 0) {
      this.facing = 0;
    }
    if (this.movement.getVelocityX() > 0) {
      this.facing = 1;
    }
    handleAnimationCycle();
    updatePosition();
    handleAllInteraction();
    handleGravityAndMovement(speedModifier);
  }


  private boolean handleAnimationCycle() {
    if (this.movement.getVelocityX() != 0) this.movement.setMoving(true);
    else this.movement.setMoving(false);
    if (this.movement.isMoving()) {
      this.frameDelay++;
      if (this.frameDelay >= 3) {
        this.frame++;
        if (this.frame >= this.bufferedImages.length / 2) {
          this.frame = 0;
        }
        this.frameDelay = 0;
      }
    }
    return true;
  }

 
  @Override
  protected void handleAnimationRendering(Graphics2D graphics2D) {
    if (this.facing == 0) {
      graphics2D.drawImage(this.bufferedImages[this.frame], this.x, this.y, this.width, this.height, null);
    } else if (this.facing == 1) {
      graphics2D.drawImage(this.bufferedImages[this.frame + 8], this.x, this.y, this.width, this.height, null);
    }
  }

}
