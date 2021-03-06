package org.toolchild.suffering.gameobject.entity;

public class Movement {
  // private static final Logger log = Logger.getLogger(Movement.class);

  private double  gravity;
  private boolean isMoving  = false;

  private boolean isJumping = false;
  private boolean isFalling = true;

  private int     velocityX;
  private int     velocityY;
  private int     moveSpeed;

  public double getGravity() {
    return this.gravity;
  }

  public void setGravity(double gravity) {
    this.gravity = gravity;
  }

  public boolean isMoving() {
    return this.isMoving;
  }

  public void setMoving(boolean isMoving) {
    this.isMoving = isMoving;
  }

  public boolean isJumping() {
    return this.isJumping;
  }

  public void setJumping(boolean isJumping) {
    this.isJumping = isJumping;
  }

  public boolean isFalling() {
    return this.isFalling;
  }

  public void setFalling(boolean isFalling) {
    this.isFalling = isFalling;
  }

  public int getVelocityX() {
    return this.velocityX;
  }

  public void setVelocityX(int velocityX) {
    this.velocityX = velocityX;
  }

  public int getVelocityY() {
    return this.velocityY;
  }

  public void setVelocityY(int velocityY) {
    this.velocityY = velocityY;
  }

  public int getMoveSpeed() {
    return this.moveSpeed;
  }

  public void setMoveSpeed(int moveSpeed) {
    this.moveSpeed = moveSpeed;
  }

  public boolean handleFloating() {  // handleGravityAndMovement sub-method
    if (!this.isFalling && !this.isJumping) { // if neither jumping nor falling player either stands on a block or hangs in the air. So it is necessary to start falling again. If on block, player will hit block and reset height and stop falling.
      this.gravity = 0.0;  // handle gravity
      this.isFalling = true;  // handle movement
    }
    return true;
  }

  public boolean handleFalling(int speedmodifier) {  // handleGravityAndMovement sub-method
    if (this.isFalling) {
      this.gravity = this.gravity + 0.5 * speedmodifier;  // handle gravity
      // log.debug("Falling gravity = " + gravity);
      this.velocityY = (int) (this.gravity);    // handle movement
    }
    return true;
  }

  public boolean handlePlayerJumping() {  // handleGravityAndMovement sub-method
    if (this.isJumping) {
      // log.debug("Jumping gravity = " + gravity);
      this.velocityY = (int) (this.gravity);  // handle movement
      if (this.gravity >= 0.0) {  // handle movement
        // isJumping = false;
        this.isFalling = true;
      }
    }
    return true;
  }

}
