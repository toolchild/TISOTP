package org.toolchild.suffering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;

import org.apache.log4j.Logger;
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

/**
 * The Handler for every {@link GameObject}, the {@link SpriteManager} and the {@link Camera}.
 * 
 * @author toolchild
 *
 */
public class Handler {
  private static final Logger    log      = Logger.getLogger(Handler.class);
  private Camera                 camera;
  private LinkedList<GameObject> entities = new LinkedList<>();
  private LinkedList<GameObject> tiles    = new LinkedList<>();
  private LinkedList<GameObject> players  = new LinkedList<>();
  private Menu                   menu;

  private boolean                isPaused;
  private int                    tilesTicked;
  private int                    entitiesTicked;
  private int                    levelWidth;
  private int                    levelHeight;

  public Handler() {
    this.camera = new Camera(); // the new camera is locked to the player from start
    this.menu = new Menu(0, 0, (int)Game.SIZE.getWidth(), (int)Game.SIZE.getHeight(), Id.menu, this, Game.SPRITE_MANAGER.getMenuBackground());
  }

  public void init() {
    createLevel();
  }

  public void tick() {

    if (isPaused) {
      menu.tick();
    } else {

      for (GameObject player : this.players) {
        if (player.getId() == Id.player) {
          this.camera.tick((Player) player);
        }
      }

      for (int e = 0; e < this.players.size(); e++) {
        Player player = (Player) this.players.get(e);
        player.tick();
      }

      this.entitiesTicked = 0;
      for (int e = 0; e < this.entities.size(); e++) {
        Entity entity = (Entity) this.entities.get(e);
        entity.tick();
        this.entitiesTicked++;
      }
      this.tilesTicked = 0;
      for (GameObject tile : this.tiles) {
        if (tile.getId() == Id.powerUpBlock) {
          PowerUpBlock powerUpBlock = (PowerUpBlock) tile;
          if (powerUpBlock.getX() >= this.players.getFirst().getX() - Game.SIZE.getWidth() - 64 && tile.getX() <= this.players.getFirst().getX() + Game.SIZE.getWidth() + 64) { // only ticks visible tiles, relative to top left edge of the shown screen -64
            if (powerUpBlock.getY() >= this.players.getFirst().getY() - Game.SIZE.getHeight() - 64 && tile.getY() <= this.players.getFirst().getY() + Game.SIZE.getHeight() + 64) {
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
  public void render(Graphics2D graphics2d, int lastSecondTicks, int lastSecondFrames) {
    if (isPaused) {
      menu.render(graphics2d);
    } else {

      this.camera.releaseGraphicsFromCamera(graphics2d);
      graphics2d.drawImage(Game.SPRITE_MANAGER.getBackground(), 0, 0, this.levelWidth * 64, this.levelHeight * 64, null);

      int tilesRendered = renderTiles(graphics2d);
      log.trace("tilesRendered: " + tilesRendered);
      int entitiesRendered = renderEntities(graphics2d);
      log.trace("entitiesRendered: " + entitiesRendered);

      for (GameObject player : this.players) { // more than 1 player compatible
        ((Player) player).render(graphics2d, this.camera);
      }
      log.trace("entities: " + this.entities.size());
      this.camera.lockGraphicsToCamera(graphics2d);
      renderMinimap(graphics2d);
      renderDebug(graphics2d, lastSecondTicks, lastSecondFrames, tilesRendered, entitiesRendered);
    }
  }

  private void renderMinimap(Graphics2D graphics2d) {
    for (GameObject entity : this.entities) { // after releasing the camera, draw the tiles, not relative to player
      graphics2d.setColor(Color.YELLOW);
      graphics2d.fillRect(Game.getFrameWidth() - 500 + entity.getX() / 32, (entity.getY() / 32) - 2, 4, 4);
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

  private int renderTiles(Graphics2D graphics2d) {
    int tilesRendered = 0;
    for (GameObject tile : this.tiles) { // after releasing the camera, draw the tiles, not relative to player
      if (tile.getX() >= this.players.getFirst().getX() - Game.SIZE.getWidth() - 64 && tile.getX() <= this.players.getFirst().getX() + Game.SIZE.getWidth() + 64) { // only render visible tiles, relative to top left edge of the shown screen -64
        if (tile.getY() >= this.players.getFirst().getY() - Game.SIZE.getHeight() - 64 && tile.getY() <= this.players.getFirst().getY() + Game.SIZE.getHeight() + 64) {
          tile.render(graphics2d);
          tilesRendered++;
        }
      }
    }
    for (GameObject tile : this.tiles) {
      this.camera.lockGraphicsToCamera(graphics2d); // locked to screen/camera
      graphics2d.setColor(Color.WHITE);
      graphics2d.fillRect(Game.getFrameWidth() - 500 + tile.getX() / 32, tile.getY() / 32, 2, 2);
      graphics2d.setColor(Color.RED);
      graphics2d.fillRect(Game.getFrameWidth() - 500 + this.players.getFirst().getX() / 32, (this.players.getFirst().getY() / 32) - 2, 4 * (this.players.getFirst().getHeight() / 64), 4 * (this.players.getFirst().getHeight() / 64));
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

  private void createLevel() {
    this.levelWidth = Game.SPRITE_MANAGER.getLevelImage().getWidth();
    this.levelHeight = Game.SPRITE_MANAGER.getLevelImage().getHeight();
    for (int y = 0; y < this.levelHeight; y++) {
      for (int x = 0; x < this.levelWidth; x++) {
        int pixel = Game.SPRITE_MANAGER.getLevelImage().getRGB(x, y);
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        int size = 64;
        if (red == 0 && green == 0 && blue == 255) addPlayer(new Player(x * size, y * size, size, size, Id.player, this, Game.SPRITE_MANAGER.getPlayer()));
        else if (red == 0 && green == 0 && blue == 0) addTile(new Grass(x * size, y * size, size, size, Id.wall, this, Game.SPRITE_MANAGER.getGrass(), true));
        else if (red == 255 && green == 255 && blue == 0) addTile(new PowerUpBlock(x * size, y * size, size, size, Id.powerUpBlock, this, Game.SPRITE_MANAGER.getPowerUpBlock(), true));
        else if (red == 100 && green == 100 && blue == 100) addTile(new Finish(x * size, y * size, size, size, Id.finish, this, Game.SPRITE_MANAGER.getFinish(), true));
        else if (red == 66 && green == 66 && blue == 66) addEntity(new Mob1(x * size, y * size, size, size, Id.mob1, this, Game.SPRITE_MANAGER.getMob1()));
        else if (red == 255 && green == 0 && blue == 0) addEntity(new BlueCrystal(x * size, y * size, size, size, Id.blueCrystal, this, Game.SPRITE_MANAGER.getBlueCrystal()));
      }
    }
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
