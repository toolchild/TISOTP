package org.toolchild.suffering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toolchild.suffering.gameobject.GameObject;
import org.toolchild.suffering.gameobject.Menu;
import org.toolchild.suffering.gameobject.entity.Entity;
import org.toolchild.suffering.gameobject.entity.Player;
import org.toolchild.suffering.gameobject.entity.mob.Mob1;
import org.toolchild.suffering.gameobject.entity.powerup.BlueCrystal;
import org.toolchild.suffering.gameobject.tile.Finish;
import org.toolchild.suffering.gameobject.tile.Grass;
import org.toolchild.suffering.gameobject.tile.PowerUpBlock;
import org.toolchild.suffering.gameobject.tile.Tile;
import org.toolchild.suffering.gfx.ImageExtractor;
import org.toolchild.suffering.input.KeyInputManager;

/**
 * The Handler for every {@link GameObject} and the {@link Camera}.
 * 
 * @author toolchild
 *
 */
public class Handler {
  private static final Logger    log      = LogManager.getLogger(Handler.class);
  private Camera                 camera;
  private LinkedList<GameObject> entities = new LinkedList<>();
  private LinkedList<GameObject> tiles    = new LinkedList<>();
  private LinkedList<GameObject> players  = new LinkedList<>();
  private Menu                   menu;
  private ImageExtractor         imageExtractor;

  private boolean                isPaused;
  private int                    level    = 1;

  private int                    tilesTicked;
  private int                    entitiesTicked;
  private int                    levelWidth;
  private int                    levelHeight;

  public Handler() {
    this.camera = new Camera(); // the new camera is locked to the player from start
  }

  public void handleSpaceKeyEvent(boolean isActive) {
    if (this.isPaused && isActive) {
      log.info("Game Resumed");
      setPaused(false);
    } else if (isActive) {
      setPaused(true);
      log.debug("Game Paused");
    }
  }

  public void init(Game game) throws Exception {
    this.imageExtractor = new ImageExtractor();
    this.imageExtractor.init();
    if (this.level > 1) {
      this.entities = new LinkedList<>();
      this.tiles = new LinkedList<>();
      this.players = new LinkedList<>();
    }
    createLevel(this.level);
    this.menu = new Menu(0, 0, game.getFrameWidth(), game.getFrameHeight(), this.imageExtractor.getMenuBackgroundImage());
    for (GameObject gameObject : this.entities) {
      Entity entity = (Entity) gameObject;
    }
    ((Player)(this.players.getFirst())).init(game.SPEED_MODIFIER);
  
  }

  public void tick(Game game, KeyInputManager keyInputManager) throws Exception {
    keyInputManager.updateKeyEvents(null, this, game.SPEED_MODIFIER);
    if (!this.isPaused) {
      for (GameObject player : this.players) {
        if (player.getId() == Id.player) {
          this.camera.tick((Player) player, game.getWidth(), game.getHeight());
        }
      }

      for (int e = 0; e < this.players.size(); e++) {
        Player player = (Player) this.players.get(e);
        player.tick(keyInputManager, game, game.SPEED_MODIFIER);
      }

      this.entitiesTicked = 0;
      for (int e = 0; e < this.entities.size(); e++) {
        Entity entity = (Entity) this.entities.get(e);
        entity.tick(game.SPEED_MODIFIER);
        this.entitiesTicked++;
      }
      this.tilesTicked = 0;
      for (GameObject tile : this.tiles) {
        if (tile.getId() == Id.powerUpBlock) {
          PowerUpBlock powerUpBlock = (PowerUpBlock) tile;
          if (powerUpBlock.getX() >= this.players.getFirst().getX() - game.getFrameWidth() - 64 && tile.getX() <= this.players.getFirst().getX() + game.getFrameWidth() + 64) { // only ticks visible tiles, relative to top left edge of the shown screen -64
            if (powerUpBlock.getY() >= this.players.getFirst().getY() - game.getFrameHeight() - 64 && tile.getY() <= this.players.getFirst().getY() + game.getFrameHeight() + 64) {
              powerUpBlock.tick();
              this.tilesTicked++;
            }
          }
        }
      }
    }
  }

  /**
   * Handles the rendering of every GameObject;
   * 
   * @param graphics2d The Graphics2d object.
   * @param lastSecondTicks The ticks for debug display.
   * @param lastSecondFrames The frames for debug display.
   */
  public void render(Game game, Graphics2D graphics2d, int lastSecondTicks, int lastSecondFrames) {
    if (this.isPaused) {
      this.menu.render(graphics2d, game.getFrameWidth(), game.getFrameHeight());
    } else {

      this.camera.releaseGraphicsFromCamera(graphics2d);
      graphics2d.drawImage(this.imageExtractor.getMenuBackgroundImage(), 0, 0, this.levelWidth * 64, this.levelHeight * 64, null);

      int tilesRendered = renderTiles(graphics2d, game);
      int entitiesRendered = renderEntities(graphics2d);

      for (GameObject player : this.players) { // more than 1 player compatible
        ((Player) player).render(graphics2d, this.camera, game);
      }
      this.camera.lockGraphicsToCamera(graphics2d);
      renderMinimap(game, graphics2d);
      renderDebug(graphics2d, lastSecondTicks, lastSecondFrames, tilesRendered, entitiesRendered);
    }
  }

  private void renderMinimap(Game game, Graphics2D graphics2d) {
    for (GameObject entity : this.entities) { // after releasing the camera, draw the tiles, not relative to player
      graphics2d.setColor(Color.YELLOW);
      graphics2d.fillRect(game.getFrameWidth() - 500 + entity.getX() / 32, (entity.getY() / 32) - 2, 4, 4);
    }
  }

  private int renderEntities(Graphics2D graphics2d) {
    int entitiesRendered = 0;
    for (GameObject entity : this.entities) {
      entity.render(graphics2d);
      entitiesRendered++;
    }
    return entitiesRendered;
  }

  private int renderTiles(Graphics2D graphics2d, Game game) {
    int tilesRendered = 0;
    for (GameObject tile : this.tiles) { // after releasing the camera, draw the tiles, not relative to player
      if (tile.getX() >= this.players.getFirst().getX() - game.getFrameWidth() - 64 && tile.getX() <= this.players.getFirst().getX() + game.getFrameWidth() + 64) { // only render visible tiles, relative to top left edge of the shown screen -64
        if (tile.getY() >= this.players.getFirst().getY() - game.getFrameHeight() - 64 && tile.getY() <= this.players.getFirst().getY() + game.getFrameHeight() + 64) {
          tile.render(graphics2d);
          tilesRendered++;
        }
      }
    }
    for (GameObject tile : this.tiles) {
      this.camera.lockGraphicsToCamera(graphics2d); // locked to screen/camera
      graphics2d.setColor(Color.WHITE);
      graphics2d.fillRect(game.getFrameWidth() - 500 + tile.getX() / 32, tile.getY() / 32, 2, 2);
      graphics2d.setColor(Color.RED);
      graphics2d.fillRect(game.getFrameWidth() - 500 + this.players.getFirst().getX() / 32, (this.players.getFirst().getY() / 32) - 2, 4 * (this.players.getFirst().getHeight() / 64), 4 * (this.players.getFirst().getHeight() / 64));
      this.camera.releaseGraphicsFromCamera(graphics2d);
    }
    return tilesRendered;
  }

  private void renderDebug(Graphics2D graphics, int lastSecondTicks, int lastSecondFrames, int tilesRendered, int entitiesRendered) {
    int column = 150;
    int lineHeight = 20;
    graphics.setColor(Color.WHITE);
    graphics.drawString("Ticks: " + lastSecondTicks, 0, lineHeight);
    graphics.drawString("Frames: " + lastSecondFrames, 0, 2 * lineHeight);
    graphics.drawString("tiles rendered: " + tilesRendered, 2 * column, 3 * lineHeight);
    graphics.drawString("tiles ticked: " + this.tilesTicked, 2 * column, 4 * lineHeight);
    graphics.drawString("entities rendered: " + entitiesRendered, 2 * column, 5 * lineHeight);
    graphics.drawString("entities ticked: " + this.entitiesTicked, 2 * column, 6 * lineHeight);

  }

  private void createLevel(int level) throws Exception {
    BufferedImage levelImage;
    switch (level) {
      case 1: {
        levelImage = this.imageExtractor.getLevel1Image();
        this.levelWidth = this.imageExtractor.getLevel1Image().getWidth();
        this.levelHeight = this.imageExtractor.getLevel1Image().getHeight();
        break;
      }
      case 2: {
        levelImage = this.imageExtractor.getLevel2Image();
        this.levelWidth = levelImage.getWidth();
        this.levelHeight = levelImage.getHeight();
        break;
      }
      default: {
        levelImage = null;
        log.error("LevelImage was not loaded.");
        throw new Exception("LevelImage was not loaded.");
      }

    }
    for (int y = 0; y < this.levelHeight; y++) {
      for (int x = 0; x < this.levelWidth; x++) {
        int pixel = levelImage.getRGB(x, y);
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        int size = 64;
        if (red == 0 && green == 0 && blue == 255) addPlayer(new Player(x * size, y * size, size, size, Id.player, this, this.imageExtractor.getPlayer()));
        else if (red == 0 && green == 0 && blue == 0) addTile(new Grass(x * size, y * size, size, size, Id.wall, this, this.imageExtractor.getGrass(), true));
        else if (red == 255 && green == 255 && blue == 0) addTile(new PowerUpBlock(x * size, y * size, size, size, Id.powerUpBlock, this, this.imageExtractor.getPowerUpBlock(), true));
        else if (red == 100 && green == 100 && blue == 100) addTile(new Finish(x * size, y * size, size, size, Id.finish, this, this.imageExtractor.getFinish(), true));
        else if (red == 66 && green == 66 && blue == 66) addEntity(new Mob1(x * size, y * size, size, size, Id.mob1, this, this.imageExtractor.getMob1()));
        else if (red == 255 && green == 0 && blue == 0) addEntity(new BlueCrystal(x * size, y * size, size, size, Id.blueCrystal, this, this.imageExtractor.getBlueCrystal()));
      }
    }
  }

  public void nextLevel() {
    this.level++;
  }

  public void addEntity(Entity entity) {
    log.debug("entity added : " + entity.getId());
    this.entities.add(entity);
  }

  /**
   * Adds a Player on a purely blue pixel. It is intended to be used once.
   * 
   * @param player The player.
   */
  public void addPlayer(Player player) {
    log.debug("entity added : " + player.getId());
    this.players.add(player);
  }

  public void removeEntity(Entity entity) {
    this.entities.remove(entity);
  }

  public void addTile(Tile tile) {
    this.tiles.add(tile);
  }

  public void removeTile(Tile tile) {
    this.tiles.remove(tile);
  }

  public Camera getCamera() {
    return this.camera;
  }

  public LinkedList<GameObject> getEntities() {
    return this.entities;
  }

  public LinkedList<GameObject> getTiles() {
    return this.tiles;
  }

  public void removePlayer(Player player) {
    this.players.remove(player);
  }

  public LinkedList<GameObject> getPlayers() {
    return this.players;
  }

  public boolean isPaused() {
    return this.isPaused;
  }

  public void setPaused(boolean isPaused) {
    this.isPaused = isPaused;
  }

}
