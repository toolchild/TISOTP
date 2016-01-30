package org.toolchild.suffering;

import java.awt.Graphics;
import java.util.LinkedList;

import org.toolchild.suffering.entity.Entity;
import org.toolchild.suffering.tile.Tile;
import org.toolchild.suffering.tile.Wall;

public class Handler {
  public LinkedList<Entity> entities = new LinkedList<Entity>();
  public LinkedList<Tile> tiles = new LinkedList<Tile>();

  public Handler(){
    createLevel();
  }
  public void render(Graphics graphics) {
    for (Entity entity : entities) {
      entity.render(graphics);     
    }
    for (Tile tile : tiles) {
      tile.render(graphics);
    }
  }

  public void tick() {
    for (Entity entity : entities) {
      entity.tick();
    }
    for (Tile tile : tiles) {
      tile.tick();
    }
  }

  public void addEntity(Entity entity) {
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
  
  public void createLevel(){
   for (int i = 0; i < Game.WIDTH*Game.SCALE/64+1; i++) {
     addTile(new Wall(i*64, Game.HEIGHT*Game.SCALE-64,64,64,true,Id.wall,this));
     addTile(new Wall(i*64, 100,64,64,true,Id.wall,this));
   }
  }
}
