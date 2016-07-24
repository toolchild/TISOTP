package org.toolchild.suffering;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toolchild.suffering.input.KeyInputManager;

public class Game extends Canvas implements Runnable {
  private static final long     serialVersionUID = 5680154129348532365L;
  private static final Logger   log              = LogManager.getLogger(Game.class);

  public final int              TICKS_PER_SECOND = 60;                                                                                                                                                                                                                                                                                             // multiple of 60
  public final int              SPEED_MODIFIER   = 60 / this.TICKS_PER_SECOND;                                                                                                                                                                                                                                                                                                // not done yet

  public final int              GAME_WIDTH       = 64;
  public final int              SCALE            = 24;
  public final int              GAME_HEIGHT      = this.GAME_WIDTH / 16 * 9;

  private Thread                thread;
  private boolean               isRunning;

  public static final Handler   HANDLER          = new Handler();
  public static KeyInputManager keyInputManager;

  public Game() {
    Dimension size = new Dimension(this.GAME_WIDTH * this.SCALE, this.GAME_HEIGHT * this.SCALE);
    setPreferredSize(size);
    setMaximumSize(size);
    setMinimumSize(size);
  }

  /**
   * This is the entry point of the program.
   * 
   * @param args There are no arguments implemented.
   */

  protected synchronized boolean start() {
    boolean hasStarted;
    if (this.isRunning) {
      hasStarted = false;
    } else {
      this.isRunning = true;
      this.thread = new Thread(this, "Game Thread");
      this.thread.start();
      hasStarted = true;
    }
    return hasStarted;
  }

  @Override
  public void run() {
    try {
      log.debug("init: " + init());
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    requestFocus();

    long lastTime = System.nanoTime();
    long timer = System.currentTimeMillis();
    double delta = 0.0;
    double ns = 1000000000.0 / this.TICKS_PER_SECOND;
    int currentFrames = 0;
    int lastSecondFrames = 0;
    int currentTicks = 0;
    int lastSecondTicks = 0;

    while (this.isRunning) {
      long nowTime = System.nanoTime();
      delta = delta + (nowTime - lastTime) / ns;
      lastTime = nowTime;
      while (delta >= 1) {
        delta--;
        try {
          tick();
        }
        catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        currentTicks++;
      }
      currentFrames++;
      render(lastSecondTicks, lastSecondFrames);

      if (System.currentTimeMillis() - timer >= 1000) {
        timer = timer + 1000;
        log.info("FPS: " + currentFrames + " Ticks: " + currentTicks);
        lastSecondFrames = currentFrames;
        currentFrames = 0;
        lastSecondTicks = currentTicks;
        currentTicks = 0;
      }
    }
    this.stop();
  }

  private boolean init() throws Exception {
    HANDLER.init(this);
    addKeyListener(new KeyInputManager());
    keyInputManager = (KeyInputManager) getKeyListeners()[0];
    keyInputManager.init();
    return true;
  }

  private synchronized void stop() {
    if (!this.isRunning) {
      try {
        log.info("Game about to stop properly.");
        // this.thread.join();
        this.thread.wait();
      }
      catch (InterruptedException e) {
        log.error("The " + this.thread.getName() + " did not stop properly.");
        e.printStackTrace();
      }
    }
  }

  private void tick() throws Exception {
    HANDLER.tick(this, keyInputManager);
  }

  /**
   * Renders all graphics.
   * 
   * @param lastSecondTicks
   * @param lastSecondFrames
   */
  public void render(int lastSecondTicks, int lastSecondFrames) {
    BufferStrategy bufferStrategy = getBufferStrategy();
    if (bufferStrategy == null) {
      createBufferStrategy(3);
      return;
    }
    Graphics2D graphics2d = (Graphics2D) bufferStrategy.getDrawGraphics();
    graphics2d.setFont(getFont().deriveFont(Font.BOLD));
    graphics2d.setColor(Color.GRAY);
    HANDLER.render(this, graphics2d, lastSecondTicks, lastSecondFrames);
    bufferStrategy.show();
    graphics2d.dispose();
  }

  public void initAndGetJFrame() {
    JFrame frame = new JFrame("The Intolerable Suffering of the Programmer called Machine");
    // frame.setUndecorated(true); //this must be switched by a configuration.
    frame.add(this);

    frame.setResizable(false);
    // frame.setLocationRelativeTo(null); // put the frame in the middle of the screen
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  public int getFrameWidth() {
    return this.GAME_WIDTH * this.SCALE;
  }

  public int getFrameHeight() {
    return this.GAME_HEIGHT * this.SCALE;
  }

  public boolean isRunning() {
    return this.isRunning;
  }

  public void setRunning(boolean isRunning) {
    this.isRunning = isRunning;
  }

}
