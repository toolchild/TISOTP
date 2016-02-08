package org.toolchild.suffering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.toolchild.suffering.gameobject.GameObject;
import org.toolchild.suffering.gameobject.entity.Entity;
import org.toolchild.suffering.gameobject.entity.Player;
import org.toolchild.suffering.gameobject.entity.mob.Mob1;
import org.toolchild.suffering.gameobject.entity.powerup.BlueCrystal;
import org.toolchild.suffering.gameobject.tile.PowerUpBlock;
import org.toolchild.suffering.gameobject.tile.Tile;
import org.toolchild.suffering.gameobject.tile.Wall;

public class Handler {
  private static final Logger    log      = Logger.getLogger(Handler.class);
  private Camera                 camera;
  private LinkedList<GameObject> entities = new LinkedList<GameObject>();
  private LinkedList<GameObject> tiles    = new LinkedList<GameObject>();
  private LinkedList<GameObject> players  = new LinkedList<GameObject>();
  private Player player;
  public int                     tilesTicked;
  public int                     entitiesTicked;

  int                            levelWidth;
  int                            levelHeight;

  public Handler(BufferedImage levelImage) {
    camera = new Camera(); // the new camera is locked to the player from start
    createLevel(levelImage);
  }

  public void tick() {
    for (GameObject player : players) {
      if (player.getId() == Id.player) {
        camera.tick((Player)player);
      }
    }

    for (int e = 0; e < players.size(); e++) {
      GameObject player =  players.get(e);
      player.tick();
    }

    entitiesTicked = 0;
    for (int e = 0; e < entities.size(); e++) {
      entities.get(e).tick();
      entitiesTicked++;
    }
    tilesTicked = 0;
    for (GameObject tile : tiles) {
      if (tile.getX() >= player.getX() - Game.SIZE.getWidth() - 64 && tile.getX() <= player.getX() + Game.SIZE.getWidth() + 64) { // only ticks visible tiles, relative to top left edge of the shown screen -64
        if (tile.getY() >= player.getY() - Game.SIZE.getHeight() - 64 && tile.getY() <= player.getY() + Game.SIZE.getHeight() + 64) {
          tile.tick();
          tilesTicked++;
        }
      }
    }
  }

  public void render(Graphics2D graphics2d, int lastSecondTicks, int lastSecondFrames) {
    camera.releaseGraphicsFromCamera(graphics2d);
    graphics2d.drawImage(Game.backgroundSheet.getSpriteSheet(), 0, 0, levelWidth * 64, levelHeight * 64, null);

    int tilesRendered = renderTiles(graphics2d);
    log.trace("tilesRendered: " + tilesRendered);

    camera.lockGraphicsToCamera(graphics2d);
    renderDebug(graphics2d, lastSecondTicks, lastSecondFrames, tilesRendered);
    camera.releaseGraphicsFromCamera(graphics2d);

    for (GameObject entity : entities) {
       entity.render(graphics2d);
    }
    for (GameObject entity : entities) { // after releasing the camera, draw the tiles, not relative to player
      camera.lockGraphicsToCamera(graphics2d);
      graphics2d.setColor(Color.YELLOW);
      graphics2d.fillRect(Game.getFrameWidth()- 500 + entity.getX() / 32, (entity.getY() / 32)-2, 4, 4);
      camera.releaseGraphicsFromCamera(graphics2d);
      }

    for (GameObject player : players) {
       ((Player)player).render(graphics2d, camera);
    }
    log.trace("entities: " + entities.size());

  }

  private int renderTiles(Graphics2D graphics2d) {

    int tilesRendered = 0;
    for (GameObject tile : tiles) { // after releasing the camera, draw the tiles, not relative to player

      if (tile.getX() >= player.getX() - Game.SIZE.getWidth() - 64 && tile.getX() <= player.getX() + Game.SIZE.getWidth() + 64) { // only render visible tiles, relative to top left edge of the shown screen -64
        if (tile.getY() >= player.getY() - Game.SIZE.getHeight() - 64 && tile.getY() <= player.getY() + Game.SIZE.getHeight() + 64) {
          tile.render(graphics2d);
          tilesRendered++;
        }
      }
      }
    for (GameObject tile : tiles) { // after releasing the camera, draw the tiles, not relative to player
    camera.lockGraphicsToCamera(graphics2d);
    graphics2d.setColor(Color.WHITE);
    graphics2d.fillRect(Game.getFrameWidth()- 500 + tile.getX() / 32, tile.getY() / 32, 2, 2);
    graphics2d.setColor(Color.RED);
    graphics2d.fillRect(Game.getFrameWidth()- 500 + player.getX() / 32 , (player.getY() / 32)-2, 4*(player.getHeight()/64) , 4*(player.getHeight()/64) );      
    camera.releaseGraphicsFromCamera(graphics2d);
    }
    return tilesRendered;
  }

  private void renderDebug(Graphics2D graphics, int lastSecondTicks, int lastSecondFrames, int tilesRendered) {
    int column = 150;
    int lineHeight = 20;
    graphics.setColor(Color.WHITE);
    graphics.drawString("Ticks: " + lastSecondTicks, 0, lineHeight);
    graphics.drawString("Frames: " + lastSecondFrames, 0, 2 * lineHeight);
    graphics.drawString("tiles rendered: " + tilesRendered, 2 * column, 3 * lineHeight);
    graphics.drawString("tiles ticked: " + tilesTicked, 2 * column, 4 * lineHeight);
  }

  public void createLevel(BufferedImage levelImage) {
    levelWidth = levelImage.getWidth();
    levelHeight = levelImage.getHeight();
    for (int y = 0; y < levelHeight; y++) {
      for (int x = 0; x < levelWidth; x++) {
        int pixel = levelImage.getRGB(x, y);
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        int size = 64;
        if (red == 0 && green == 0 && blue == 255) addPlayer(new Player(x * size, y * size, size, size, Id.player, this));
        if (red == 0 && green == 0 && blue == 0) addTile(new Wall(x * size, y * size, size, size, true, Id.wall, this));
        if (red == 255 && green == 255 && blue == 0) addTile(new PowerUpBlock(x * size, y * size, size, size, true, Id.powerUpBlock, this, Game.powerupBlock));
        if (red == 66 && green == 66 && blue == 66) addEntity(new Mob1(x * size, y * size, size, size, Id.mob1, this));
        if (red == 255 && green == 0 && blue == 0) addEntity(new BlueCrystal(x * size, y * size, size, size, Id.blueCrystal, this));
      }
    }
  }

  public void addEntity(Entity entity) {
    log.debug("entity added : " + entity.getId());
    entities.add(entity);
  }

  public void addPlayer(GameObject player) {
    log.debug("entity added : " + player.getId());
    players.add(player);
    this.player = (Player)player;
  }

  public void removeEntity(Entity entity) {
    entities.remove(entity);
  }

  public void addTile(Tile tile) {
    tiles.add(tile);
  }

  public void removeTile(Tile tile) {
    tiles.remove(tile);
  }

  public Camera getCamera() {
    return this.camera;
  }

  public LinkedList<GameObject> getEntities() {
    return this.entities;
  }

  public void setEntities(LinkedList<GameObject> entities) {
    this.entities = entities;
  }

  public LinkedList<GameObject> getTiles() {
    return this.tiles;
  }

  public void setTiles(LinkedList<GameObject> tiles) {
    this.tiles = tiles;
  }

  public void removePlayer(GameObject player) {
    players.remove(player);
  }

  public LinkedList<GameObject> getPlayers() {
    return this.players;
  }

  public void setPlayers(LinkedList<GameObject> players) {
    this.players = players;
  }
}
