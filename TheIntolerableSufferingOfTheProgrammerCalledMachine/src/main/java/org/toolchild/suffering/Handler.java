package org.toolchild.suffering;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.toolchild.suffering.entity.Entity;
import org.toolchild.suffering.entity.Player;
import org.toolchild.suffering.entity.mob.Mob1;
import org.toolchild.suffering.entity.powerup.BlueCrystal;
import org.toolchild.suffering.tile.PowerUpBlock;
import org.toolchild.suffering.tile.Tile;
import org.toolchild.suffering.tile.Wall;

public class Handler {
  private static final Logger log      = Logger.getLogger(Handler.class);
  private Camera              camera;
  public LinkedList<Entity>   entities = new LinkedList<Entity>();
  public LinkedList<Tile>     tiles    = new LinkedList<Tile>();
  public int                  tilesTicked;
  public Player               player;
  
  int levelWidth;
  int levelHeight;

  public Handler(BufferedImage levelImage) {
    camera = new Camera(); // the new camera is locked to the player from start
    createLevel(levelImage);
  }

  public void tick() {
    for (Entity entity : entities) {
      if (entity.id == Id.player) {
        camera.tick(player);
      }
    }

    tilesTicked = 0;
    for (int e = 0; e < entities.size(); e++) {
      Entity entity = entities.get(e);
      entity.tick();
    }
    for (Tile tile : tiles) {
      if (tile.getX() >= player.x - Game.SIZE.getWidth() - 64 && tile.getX() <= player.x + Game.SIZE.getWidth() + 64) { // only ticks visible tiles, relative to top left edge of the shown screen -64
        if (tile.getY() >= player.y - Game.SIZE.getHeight() - 64 && tile.getY() <= player.y + Game.SIZE.getHeight() + 64) {
          tile.tick();
          tilesTicked++;
        }
      }
    }
  }

  public void render(Graphics graphics, int lastSecondTicks, int lastSecondFrames) {
    camera.releaseGraphicsFromCamera(graphics);
    graphics.drawImage(Game.backgroundSheet.getSpriteSheet(),0,0,levelWidth*64,levelHeight*64,null);

    int tilesRendered = renderTiles(graphics);
    log.trace("tilesRendered: " + tilesRendered);

    camera.lockGraphicsToCamera(graphics);
    renderDebug(graphics, lastSecondTicks, lastSecondFrames, tilesRendered);
    camera.releaseGraphicsFromCamera(graphics);

    for (Entity entity : entities) {
      entity.render(graphics, camera);
    }

    log.trace("entities: " + entities.size());

  }

  private int renderTiles(Graphics graphics) {
    int tilesRendered = 0;
    for (Tile tile : tiles) { // after releasing the camera, draw the tiles, not relative to player
      if (tile.getX() >= player.x - Game.SIZE.getWidth() - 64 && tile.getX() <= player.x + Game.SIZE.getWidth() + 64) { // only render visible tiles, relative to top left edge of the shown screen -64
        if (tile.getY() >= player.y - Game.SIZE.getHeight() - 64 && tile.getY() <= player.y + Game.SIZE.getHeight() + 64) {
          tile.render(graphics);
          tilesRendered++;
        }
      }
    }
    return tilesRendered;
  }

  // ________________________________________ render sub-methods ________________________________________// ________________________________________ render sub-methods ________________________________________
  private void renderDebug(Graphics graphics, int lastSecondTicks, int lastSecondFrames, int tilesRendered) {
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
        if (red == 0 && green == 0 && blue == 255) addEntity(new Player(x * size, y * size, size, size, Id.player, this));
        if (red == 0 && green == 0 && blue == 0) addTile(new Wall(x * size, y * size, size, size, true, Id.wall, this));
        if (red == 255 && green == 255 && blue == 0) addTile(new PowerUpBlock(x * size, y * size, size, size, true, Id.powerUpBlock, this, Game.powerupBlock));
        if (red == 66 && green == 66 && blue == 66) addEntity(new Mob1(x * size, y * size, size, size, Id.mob1, this));
        if (red == 255 && green == 0 && blue == 0) addEntity(new BlueCrystal(x * size, y * size, size, size,  Id.blueCrystal, this));
        
      }
    }
  }

  public void addEntity(Entity entity) {
    log.debug("entity added : " + entity.id);
    entities.add(entity);
    if (entity.id == Id.player) player = (Player) entity;
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
}
