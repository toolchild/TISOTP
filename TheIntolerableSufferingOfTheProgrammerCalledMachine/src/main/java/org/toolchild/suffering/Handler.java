package org.toolchild.suffering;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.toolchild.suffering.entity.Entity;
import org.toolchild.suffering.entity.Player;
import org.toolchild.suffering.entity.powerup.PinkVial;
import org.toolchild.suffering.input.KeyInput;
import org.toolchild.suffering.tile.Tile;
import org.toolchild.suffering.tile.Wall;

public class Handler {
  private static final Logger log      = Logger.getLogger(Handler.class);

  public LinkedList<Entity>   entities = new LinkedList<Entity>();
  public LinkedList<Tile>     tiles    = new LinkedList<Tile>();

  public int                  tilesTicked;
  public int                  tilesRendere;

  public Handler(BufferedImage levelImage) {
    createLevel(levelImage);
  }

  public void render(Graphics graphics, Camera camera) {
    int tilesRendered = 0;
    for (Tile tile : tiles) {
      if (tile.getX() >= -camera.getX()-64&& tile.getX() <= -camera.getX() + Game.SIZE.getWidth()+64) {
        if (tile.getY() >= -camera.getY()-64&& tile.getY() <= -camera.getY() + Game.SIZE.getHeight()+64) {
          tile.render(graphics);
          tilesRendered++;
        }
      }
    }
    log.trace("tilesRendered: " + tilesRendered);
    int column = 150;
    int lineHeight = 20;
    graphics.translate(-camera.getX(), -camera.getY()); // since nothing is tied to play, the graphics object is tied to camera to fix the folloing on the screen
    graphics.drawString("tiles rendered: " + tilesRendered, 2*column, 3*lineHeight);
    graphics.drawString("tiles ticked: " + tilesTicked, 2*column, 4*lineHeight);
    graphics.translate(camera.getX(), camera.getY()); // untying graphics from camera

    for (Entity entity : entities) {
      entity.render(graphics, camera);
    }

    log.trace("entities: " + entities.size());

  }

  public void tick() {
//    log.debug(entities);
    for (int e = 0; e < entities.size(); e++) {
      Entity entity = entities.get(e);
      entity.tick();
    }

    tilesTicked = 0 ;
    for (Tile tile : tiles)   {
      if (tile.getX() >= entities.get(0).getX() - Game.SIZE.getWidth()/2 -64 && tile.getX() <= entities.get(0).getX()+ (int) Game.SIZE.getWidth() / 2  + 64) {
        if (tile.getY() >= entities.get(0).getY() - Game.SIZE.getHeight()/3 && tile.getY() <= entities.get(0).getY() + Game.SIZE.getHeight()/3 * 2 + 64 ) {
          tile.tick();
          tilesTicked++;
        }
      }
    }

  }

  public void addEntity(Entity entity) {
    log.debug("entity added : " + entity.id);
    entities.add(entity);
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

  public void createLevel(BufferedImage levelImage) {
    int width = levelImage.getWidth();
    int height = levelImage.getHeight();

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = levelImage.getRGB(x, y);

        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        int size = 64;
        if (red == 0 && green == 0 && blue == 0) addTile(new Wall(x * size, y * size, size, size, true, Id.wall, this));
        if (red == 0 && green == 0 && blue == 255) addEntity(new Player(x * size, y * size, size, size, true, Id.player, this));
        if (red == 255 && green == 0 && blue == 0) addEntity(new PinkVial(x * size, y * size, size, size, true, Id.pinkVial, this));

      }
// addEntity(new PinkVial( 100, 200, 64, 64, true, Id.pinkVial, this));
    }

    // for (int i = 0; i < 5*Game.WIDTH * Game.SCALE / 64 + 1; i++) {
// addTile(new Wall(i * 64, Game.HEIGHT * Game.SCALE - 64, 64, 64, true, Id.wall, this));
//
// if (i > 3 && i < 14) addTile(new Wall(i * 64, 4*64, 64, 64, true, Id.wall, this));
// }
// addTile(new Wall(20*64, 6*64, 64, 64, true, Id.wall, this));
// addEntity(new Player((int)Game.SIZE.getWidth()/2, 200, 64, 64, true, Id.player, this));
  }
}
