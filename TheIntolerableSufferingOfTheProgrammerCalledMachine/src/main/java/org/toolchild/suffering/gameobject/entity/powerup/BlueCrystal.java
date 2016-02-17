package org.toolchild.suffering.gameobject.entity.powerup;

import java.awt.Graphics2D;
import java.util.Random;

import org.apache.log4j.Logger;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.entity.Entity;
import org.toolchild.suffering.gfx.Sprite;

public class BlueCrystal extends Entity {
  private static final Logger log        = Logger.getLogger(BlueCrystal.class);
  private int                 frame      = 0;
  private int                 frameDelay = 0;

  private Random              random     = new Random();

  
  public BlueCrystal(int x, int y, int width, int height, Id id, Handler handler, Sprite[] sprites) {
    super(x, y, width, height, id, handler, sprites);
    int direction = this.random.nextInt(2); // direction: 0 = left ; 1 = right
    this.movement.setVelocityX(direction == 0 ? -3 : +3);
    this.movement.setMoveSpeed(3);
  }

  @Override
  public void tick() {
    log.trace("update position: " + updatePosition() + " x: " + this.x + " y: " + this.y);
    this.frameDelay++;
    if (this.frameDelay >= 3) {
      this.frame++;
      if (this.frame >= this.sprites.length / 2) {
        this.frame = 0;
      }
      this.frameDelay = 0;
    }
    log.trace("handle all tile interaction: " + handleAllInteraction());
    log.trace("handle gravity and movement: " + handleGravityAndMovement());
  }

  private boolean updatePosition() {
    this.x = this.x + this.movement.getVelocityX();
    this.y = this.y + this.movement.getVelocityY();
    // TODO: use for level bounds
    // if (x <= 0) x = 0;
    // if (x + this.width >= Game.SIZE.getWidth()) x = (int) (Game.SIZE.getWidth() - this.width);
    return true;
  }

  private boolean handleGravityAndMovement() {
    log.trace("handle Falling " + this.movement.handleFalling());
    log.trace("handle Floating " + this.movement.handleFloating());
    return true;
  }


  @Override
  protected void handleAnimationRendering(Graphics2D graphics2D) {
    graphics2D.drawImage(this.sprites[this.frame].getImage(), this.x, this.y, this.width, this.height, null);
  }
}
