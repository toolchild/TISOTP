package org.toolchild.suffering;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toolchild.suffering.gfx.Sprite;
import org.toolchild.suffering.gfx.SpriteSheet;

/**
 * Loads the sprites and provides them in the needed object form.
 * 
 * @author Bob
 *
 */
public class SpriteManager {
  private static final Logger log = LogManager.getLogger(SpriteManager.class);

  private SpriteSheet         spriteSheet;
  private SpriteSheet         characterSpriteSheet;
  private SpriteSheet         blueCrystalSpriteSheet;
  private SpriteSheet         mob1SpriteSheetLeft;
  private SpriteSheet         mob1SpriteSheetRight;
  
  private BufferedImage       menuBackgroundImage;
  private BufferedImage       level1Image;
  private BufferedImage       level2Image;
  

  private Sprite[]            grass;
  private Sprite[]            powerUpBlock;
  private Sprite[]            finish;

  private Sprite[]            player;
  private Sprite[]            mob1;
  private Sprite[]            blueCrystal;

  public void init() {
    initSpriteSheets();
    initLevelImage();
    initPlayer();
    initBlueCrystal();
    initMob1();
    initGrass();
    initPowerUpBlock();
    initFinish();
  }

  private void initLevelImage() {
    try {
      this.level1Image = ImageIO.read(getClass().getResource("/level1.png"));
      this.level2Image = ImageIO.read(getClass().getResource("/level2.png"));
    }
    catch (IOException e) {
      log.error("LevelImage not found: '" + e.getMessage() + "'");
    }
  }

  private void initSpriteSheets() {
    this.spriteSheet = new SpriteSheet("/spriteSheet.png");
    this.characterSpriteSheet = new SpriteSheet("/charSpriteSheet.png");
    this.blueCrystalSpriteSheet = new SpriteSheet("/crystal-qubodup-ccby3-32-blue.png");
    this.mob1SpriteSheetLeft = new SpriteSheet("/wellingtonLeft.png");
    this.mob1SpriteSheetRight = new SpriteSheet("/wellingtonRight.png");
    try {
      this.menuBackgroundImage = ImageIO.read(getClass().getResource("/trip.jpg"));
      log.info("menuBackgroundImage: " + this.menuBackgroundImage);
    }
    catch (IOException e) {
      log.error("Background image not found: '" + e.getMessage() + "'");
    }
  }

  private void initPlayer() {
    this.player = new Sprite[10];
    for (int i = 0; i < this.player.length / 2; i++) {
      this.player[i] = new Sprite(this.characterSpriteSheet, i, 3, false);
      this.player[i + this.player.length / 2] = new Sprite(this.characterSpriteSheet, i, 5, false);
    }
  }

  private void initBlueCrystal() {
    this.blueCrystal = new Sprite[8];
    for (int i = 0; i < this.blueCrystal.length; i++) {
      this.blueCrystal[i] = new Sprite(this.blueCrystalSpriteSheet, i, 0, false);
    }
  }

  private void initMob1() {
    this.mob1 = new Sprite[16];
    for (int i = 0; i < this.mob1.length / 2; i++) {
      this.mob1[i] = new Sprite(this.mob1SpriteSheetLeft, i, 0, true);
      this.mob1[i + this.mob1.length / 2] = new Sprite(this.mob1SpriteSheetRight, i, 0, true);
    }
  }

  private void initGrass() {
    this.grass = new Sprite[1];
    this.grass[0] = new Sprite(this.spriteSheet, 1, 0, false);
  }

  private void initPowerUpBlock() {
    this.powerUpBlock = new Sprite[10];
    this.powerUpBlock[0] = new Sprite(this.spriteSheet, 3, 0, false);
    this.powerUpBlock[1] = new Sprite(this.spriteSheet, 4, 0, false);
    for (int i = 2; i < this.blueCrystal.length + 2; i++) {
      this.powerUpBlock[i] = this.blueCrystal[i - 2];
    }
  }

  private void initFinish() {
    this.finish = new Sprite[1];
    this.finish[0] = new Sprite(this.spriteSheet, 5, 0, false);
  }

  public BufferedImage getMenuBackgroundImage() {
    return this.menuBackgroundImage;
  }

  public BufferedImage getLevel1Image() {
    return this.level1Image;
  }

  public BufferedImage getLevel2Image() {
    return this.level2Image;
  }

  public Sprite[] getPowerUpBlock() {
    return this.powerUpBlock;
  }

  public void setPowerUpBlock(Sprite[] powerUpBlock) {
    this.powerUpBlock = powerUpBlock;
  }

  public Sprite[] getGrass() {
    return this.grass;
  }

  public Sprite[] getPlayer() {
    return this.player;
  }

  public Sprite[] getMob1() {
    return this.mob1;
  }

  public Sprite[] getBlueCrystal() {
    return this.blueCrystal;
  }

  public Sprite[] getFinish() {
    return this.finish;
  }

}
