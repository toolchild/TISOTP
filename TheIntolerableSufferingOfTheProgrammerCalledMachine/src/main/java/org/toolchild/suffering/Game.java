package org.toolchild.suffering;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.toolchild.suffering.input.KeyInputManager;

public class Game extends Canvas implements Runnable {
  private static final long         serialVersionUID = 5680154129348532365L;
  private static final Logger       log              = Logger.getLogger(Game.class);
  private static final int          TICKS_PER_SECOND = 60;

  public static final int           GAME_WIDTH       = 64;
  public static final int           SCALE            = 24;
  public static final int           GAME_HEIGHT      = GAME_WIDTH / 16 * 9;
  public static final Dimension     SIZE             = new Dimension(GAME_WIDTH * SCALE, GAME_HEIGHT * SCALE);
  public static final String        TITLE            = "The Intolerable Suffering of the Programmer called Machine";
  public static final SpriteManager SPRITE_MANAGER   = new SpriteManager();
  
  public static final Game GAME = new Game();


  private Thread                    thread;
  public boolean                   isRunning;

 

  public static Handler             handler;
  public static KeyInputManager     keyInput;

  public Game() {
    setPreferredSize(SIZE);
    setMaximumSize(SIZE);
    setMinimumSize(SIZE);
  }
/**
 * This is the entry point of the program.
 * @param args There are no arguments implemented.
 */
  public static void main(String[] args) {
    Game.initAndGetJFrame(GAME);
    GAME.start();
  }

  private synchronized boolean start() {
    boolean hasStarted = false;
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
    log.debug("init: " + init());
    requestFocus();

    long lastTime = System.nanoTime();
    long timer = System.currentTimeMillis();
    double delta = 0.0;
    double ns = 1000000000.0 / TICKS_PER_SECOND;
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
        tick();
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

  private boolean init() {
    SPRITE_MANAGER.init();
    handler = new Handler(SPRITE_MANAGER);
    addKeyListener(new KeyInputManager());
    keyInput = (KeyInputManager) getKeyListeners()[0];
    keyInput.init();
    return true;
  }

  private synchronized void stop() {
    if (!this.isRunning) {
      try {
        log.info("Game about to stop properly.");
        this.thread.join();
      }
      catch (InterruptedException e) {
        log.error("The " + this.thread.getName() + " did not stop properly.");
        e.printStackTrace();
      }
    }
  }

  private static void tick() {
    handler.tick();
  }

  public void render(int lastSecondTicks, int lastSecondFrames) {
    BufferStrategy bufferStrategy = getBufferStrategy();
    if (bufferStrategy == null) {
      createBufferStrategy(3);
      return;
    }
    Graphics graphics = bufferStrategy.getDrawGraphics(); // TODO: Is this necessary?
    Graphics2D graphics2d = (Graphics2D) graphics;
    graphics2d.setFont(getFont().deriveFont(Font.BOLD));
    graphics2d.setColor(Color.GRAY);
    // graphics2d.fillRect(0, 0, getWidth(), getHeight()); this isn't overdrawn by other graphics.
    handler.render(graphics2d, lastSecondTicks, lastSecondFrames);
    bufferStrategy.show();
    graphics.dispose();
  }

  private static JFrame initAndGetJFrame(Game game) {
    JFrame frame = new JFrame(TITLE);
    // frame.setUndecorated(true);
    frame.add(game);
    frame.pack();
    frame.setResizable(false);
    // frame.setLocationRelativeTo(null); // put the frame in the middle of the screen
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    return frame;
  }

  public static int getFrameWidth() {
    return GAME_WIDTH * SCALE;
  }

  public static int getFrameHeight() {
    return GAME_HEIGHT * SCALE;
  }
  
  public boolean isRunning() {
    return this.isRunning;
  }

  public void setRunning(boolean isRunning) {
    this.isRunning = isRunning;
  }

}
