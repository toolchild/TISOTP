package org.toolchild.suffering.gameobject.entity.powerup;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toolchild.suffering.Handler;
import org.toolchild.suffering.Id;
import org.toolchild.suffering.gameobject.entity.Entity;

/**
 * Can be picked up by touch and makes the player grow.
 * 
 * @author Bob
 *
 */
public class BlueCrystal extends Entity {
  private static final Logger log        = LogManager.getLogger(BlueCrystal.class);
  private int                 frame      = 0;
  private int                 frameDelay = 0;

  private Random              random     = new Random();

  public BlueCrystal(int x, int y, int width, int height, Id id, Handler handler, BufferedImage[] blueCrystal) {
    super(x, y, width, height, id, handler, blueCrystal);
    int direction = this.random.nextInt(2); // direction: 0 = left ; 1 = right
    this.movement.setVelocityX(direction == 0 ? -3 : +3);
    this.movement.setMoveSpeed(3);
    this.boundsTrim = this.movement.getMoveSpeed();

  }

  @Override
  public void tick(int speedModifier) {
    log.trace("update position: " + updatePosition() + " x: " + this.x + " y: " + this.y);
    this.frameDelay++;
    if (this.frameDelay >= 3) {
      this.frame++;
      if (this.frame >= this.bufferedImages.length / 2) {
        this.frame = 0;
      }
      this.frameDelay = 0;
    }
    handleAllInteraction();
    handleGravityAndMovement(speedModifier);
  }

  @Override
  protected void handleAnimationRendering(Graphics2D graphics2D) {
    graphics2D.drawImage(this.bufferedImages[this.frame], this.x, this.y, this.width, this.height, null);
  }

}
