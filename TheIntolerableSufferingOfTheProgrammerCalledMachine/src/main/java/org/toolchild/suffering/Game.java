package org.toolchild.suffering;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.toolchild.suffering.gfx.Sprite;
import org.toolchild.suffering.gfx.SpriteSheet;
import org.toolchild.suffering.input.KeyInputManager;

public class Game extends Canvas implements Runnable {
  private static final long     serialVersionUID            = 5680154129348532365L;
  private static final Logger   log                         = Logger.getLogger(Game.class);
  private static final int      TICKS_AND_FRAMES_PER_SECOND = 60;

  public static final int       WIDTH                       = 64;
  public static final int       SCALE                       = 24;
  public static final int       HEIGHT                      = WIDTH / 16 * 9;
  public static final Dimension SIZE                        = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
  public static final String    TITLE                       = "The Intolerable Suffering of the Programmer called Machine";

  public static int currentTicks = 0;
  
  private Thread                thread;
  private boolean               isRunning;

  public static Handler         handler;

  public static SpriteSheet     spriteSheet;
  public static SpriteSheet     characterSpriteSheet;
  public static SpriteSheet     blueCrystalSpriteSheet;
  private static SpriteSheet    mob1SpriteSheetLeft;
  private static SpriteSheet    mob1SpriteSheetRight;
  public static SpriteSheet     backgroundSheet;

  private BufferedImage         levelImage;

  public static Sprite          grass;
  public static Sprite powerupBlock;
  public static Sprite usedPowerupBlock;

  public static Sprite          player[];
  public static Sprite          mob1[];
  public static Sprite          blueCrystal[];

  public static KeyInputManager keyInput;

  public Game() {
    setPreferredSize(SIZE);
    setMaximumSize(SIZE);
    setMinimumSize(SIZE);
  }

  public static void main(String[] args) {
    Game game = new Game();
    @SuppressWarnings("unused")
    JFrame frame = game.initAndGetJFrame(game);
    game.start();
  }

  private synchronized boolean start() {
    boolean hasStarted = false;
    if (isRunning) {
      hasStarted = false;
    } else {
      isRunning = true;
      thread = new Thread(this, "Game Thread");
      thread.start();
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
    double ns = 1000000000.0 / TICKS_AND_FRAMES_PER_SECOND;
    int currentFrames = 0;
    int lastSecondFrames = 0;
    currentTicks = 0;
    int lastSecondTicks = 0;

    while (isRunning) {
      long nowTime = System.nanoTime();
      delta = delta + (nowTime - lastTime) / ns;
      lastTime = nowTime;
      // log.info(lastTime - nowTime);
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
    log.debug("stop: " + stop());
  }

  private boolean init() {

    try {
      levelImage = ImageIO.read(getClass().getResource("/level1.png"));
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    handler = new Handler(levelImage);
    spriteSheet = new SpriteSheet("/spriteSheet.png");
    characterSpriteSheet = new SpriteSheet("/charSpriteSheet.png");
    blueCrystalSpriteSheet = new SpriteSheet("/crystal-qubodup-ccby3-32-blue.png");
    mob1SpriteSheetLeft = new SpriteSheet("/wellingtonLeft.png");
    mob1SpriteSheetRight = new SpriteSheet("/wellingtonRight.png");
    backgroundSheet = new SpriteSheet("/trip.jpg");

    player = new Sprite[10];
    for (int i = 0; i < player.length / 2; i++) {
      player[i] = new Sprite(characterSpriteSheet, i, 3, false);
      player[i + player.length / 2] = new Sprite(characterSpriteSheet, i, 5, false);
    }

    blueCrystal = new Sprite[8];
    for (int i = 0; i < blueCrystal.length; i++) {
      blueCrystal[i] = new Sprite(blueCrystalSpriteSheet, i, 0, false);
    }

    mob1 = new Sprite[16];
    for (int i = 0; i < mob1.length / 2; i++) {
      mob1[i] = new Sprite(mob1SpriteSheetLeft, i, 0, true);
      mob1[i + mob1.length / 2] = new Sprite(mob1SpriteSheetRight, i, 0, true);
    }

    grass = new Sprite(spriteSheet, 1, 0, false);
    powerupBlock = new Sprite(spriteSheet,3,0, false);
    usedPowerupBlock = new Sprite(spriteSheet,4,0, false);
    addKeyListener(new KeyInputManager());
    keyInput = (KeyInputManager) getKeyListeners()[0];
    keyInput.init();
    return true;
  }

  private synchronized boolean stop() {
    boolean hasStopped = false;
    if (isRunning) {
      isRunning = false;
      try {
        thread.join();
      }
      catch (InterruptedException e) {
        log.error("The " + thread.getName() + " did non stop properly.");
        e.printStackTrace();
      }
      hasStopped = true;
    }
    return hasStopped;
  }

  public void tick() {
    handler.tick();
  }

  public void render(int lastSecondTicks, int lastSecondFrames) {
    BufferStrategy bufferStrategy = getBufferStrategy();
    if (bufferStrategy == null) {
      createBufferStrategy(3);
      return;
    }
    Graphics graphics = bufferStrategy.getDrawGraphics();
    graphics.setFont(getFont().deriveFont(Font.BOLD));
    graphics.setColor(Color.GRAY);
    graphics.fillRect(0, 0, getWidth(), getHeight());
    handler.render(graphics, lastSecondTicks, lastSecondFrames);
    bufferStrategy.show();
    graphics.dispose();
  }

  private JFrame initAndGetJFrame(Game game) {
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
    return WIDTH * SCALE;
  }

  public static int getFrameHeight() {
    return HEIGHT * SCALE;
  }

}
