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
import org.toolchild.suffering.entity.Entity;
import org.toolchild.suffering.gfx.Sprite;
import org.toolchild.suffering.gfx.SpriteSheet;
import org.toolchild.suffering.input.KeyInput;

public class Game extends Canvas implements Runnable {

  private static final Logger   log                         = Logger.getLogger(Game.class);

  private static final int      TICKS_AND_FRAMES_PER_SECOND = 60;

  private static final long     serialVersionUID            = 5680154129348532365L;
  public static final int       WIDTH                       = 370;
  public static final int       HEIGHT                      = WIDTH / 16 * 9;
  public static final int       SCALE                       = 4;

  public static final Dimension SIZE                        = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);

  public static final String    TITLE                       = "The Intolerable Suffering of the Programmer called Machine";
  private Thread                thread;
  private boolean               isRunning;

  public static Handler         handler;
  public static SpriteSheet     spriteSheet;
  public static SpriteSheet     characterSpriteSheet;

  private BufferedImage         levelImage;

  public static Camera          camera;

  public static Sprite          player[];
  public static Sprite          grass;
  public static Sprite          pinkVial;

  public static KeyInput        keyInput;

  public Game() {
    setPreferredSize(SIZE);
    setMaximumSize(SIZE);
    setMinimumSize(SIZE);
  }

  public static void main(String[] args) {
    Game game = new Game();
    JFrame frame = new JFrame(TITLE);
    // frame.setUndecorated(true);
    frame.add(game);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationRelativeTo(null); // put the frame in the middle of the screen
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    game.start();
  }

  private boolean init() {

    try {
      levelImage = ImageIO.read(getClass().getResource("/level1.png"));
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    handler = new Handler(levelImage);
    camera = new Camera();
    spriteSheet = new SpriteSheet("/spriteSheet.png");
    characterSpriteSheet = new SpriteSheet("/charSpriteSheet.png");

    player = new Sprite[10];

    for (int i = 0; i < player.length / 2; i++) {
      player[i] = new Sprite(characterSpriteSheet, i, 3);
      player[i + player.length / 2] = new Sprite(characterSpriteSheet, i, 5);
    }

    grass = new Sprite(spriteSheet, 1, 0);
    pinkVial = new Sprite(spriteSheet, 2, 0);
    addKeyListener(new KeyInput());
    keyInput = (KeyInput) getKeyListeners()[0];
    keyInput.init();
    return true;
  }

  public synchronized boolean start() {
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

  public synchronized boolean stop() {
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
    int currentTicks = 0;
    int lastSecondTicks = 0;

    while (isRunning) {
      long nowTime = System.nanoTime();
      // log.info(lastTime - nowTime);
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
    log.debug("stop: " + stop());
  }

  public void render(int lastSecondTicks, int lastSecondFrames) {
    BufferStrategy bufferStrategy = getBufferStrategy();
    if (bufferStrategy == null) {
      createBufferStrategy(3);
      return;
    }
    Graphics graphics = bufferStrategy.getDrawGraphics();
    graphics.setColor(Color.GRAY);
    graphics.fillRect(0, 0, getWidth(), getHeight());
    graphics.setFont(getFont().deriveFont(Font.BOLD));
    graphics.setColor(Color.WHITE);
    graphics.drawString("Ticks: " + lastSecondTicks, 20, 20);
    graphics.drawString("Frames: " + lastSecondFrames, 20, 40);
    graphics.translate(camera.getX(), camera.getY()); // everything drawn after this will not move with the camera and player + everything tied to player position
    log.debug("cameraX : " + camera.getX() + " cameraY : " + camera.getY());
    handler.render(graphics, camera);
    bufferStrategy.show();
    graphics.dispose();
  }

  public void tick() {
    for (Entity entity : handler.entities) {
      if (entity.id == Id.player) {
        camera.tick(entity);
      }
    }
    handler.tick();

  }

  public static int getFrameWidth() {
    return WIDTH * SCALE;
  }

  public static int getFrameHeight() {
    return HEIGHT * SCALE;
  }

}
