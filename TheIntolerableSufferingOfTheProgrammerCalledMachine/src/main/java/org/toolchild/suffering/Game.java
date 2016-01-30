package org.toolchild.suffering;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.toolchild.suffering.entity.Player;
import org.toolchild.suffering.input.KeyInput;
import org.toolchild.suffering.tile.Wall;

public class Game extends Canvas implements Runnable {

  private static final Logger log = Logger.getLogger(Game.class);

  private static final long serialVersionUID = 5680154129348532365L;
  public static final int WIDTH = 270;
  public static final int HEIGHT = WIDTH / 16 * 9;
  public static final int SCALE = 4;
  
  public static final Dimension SIZE = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
  
  public static final String TITLE = "The Intolerable Suffering of the Programmer called Machine.";
  private Thread thread;
  private boolean isRunning;
  
  public static Handler handler;

  public Game() {
    setPreferredSize(SIZE);
    setMaximumSize(SIZE);
    setMinimumSize(SIZE);
  }
  
  public static void main(String[] args) {
    Game game = new Game();
    JFrame frame = new JFrame();
//    frame.setUndecorated(true);
    frame.add(game);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationRelativeTo(null); // put the frame in the middle of the screen
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    game.start();
    // System.exit(0);
  }

  private void init() {
    handler = new Handler();
    addKeyListener(new KeyInput());
    handler.addEntity(new Player(400, 200, 64, 64, true, Id.player, handler));
    handler.addTile(new Wall(400,400,64,64, true, Id.wall, handler));
  }

  public synchronized void start() {
    if (isRunning) {
      return;
    } else {
      isRunning = true;
      thread = new Thread(this, "Game Thread");
      thread.start();
    }

  }

  public synchronized void stop() {
    if (!isRunning) {
      return;
    } else {
      isRunning = false;
      try {
        thread.join();
      } catch (InterruptedException e) {
        log.error("The Game Thread did non stop properly");
        e.printStackTrace();
      }
    }

  }

  @Override
  public void run() {
    init();
    requestFocus();
    long lastTime = System.nanoTime();
    long timer = System.currentTimeMillis();
    double delta = 0.0;
    double ns = 1000000000.0 / 60.0;
    int frames = 0;
    int ticks = 0;

    while (isRunning) {
      long nowTime = System.nanoTime();
      // log.info(lastTime - nowTime);
      delta = delta + (nowTime - lastTime) / ns;
      lastTime = nowTime;
      while (delta >= 1) {
        tick();
        ticks++;
        delta--;
      }
      render();
      frames++;
      if (System.currentTimeMillis() - timer >= 1000) {
        timer = timer + 1000;
        log.info("FPS: " + frames + " Ticks: " + ticks);
        frames = 0;
        ticks = 0;
      }
    }
    stop();

  }

  public void render() {
    BufferStrategy bufferStrategy = getBufferStrategy();
    if (bufferStrategy == null) {
      createBufferStrategy(3);
      return;
    }
    Graphics graphics = bufferStrategy.getDrawGraphics();
    graphics.setColor(Color.BLACK);
    graphics.fillRect(0, 0, getWidth(), getHeight());
    handler.render(graphics);
    bufferStrategy.show();
    graphics.dispose();
  }

  public void tick() {
    handler.tick();
  }

}
