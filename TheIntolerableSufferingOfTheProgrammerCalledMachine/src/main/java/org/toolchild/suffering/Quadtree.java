package org.toolchild.suffering;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toolchild.suffering.gameobject.entity.Entity;

public class Quadtree {
  private static final Logger   log              = LogManager.getLogger(Quadtree.class);

  private int          MAX_OBJECTS = 1;
  private int          MAX_LEVELS  = 100;

  private int          level;
  private List<Entity> objects;
  private Entity       entity;
  private Quadtree[]   nodes;

  /*
   * Constructor
   */
  public Quadtree(int pLevel, Entity entity) {
    this.level = pLevel;
    this.objects = new ArrayList<Entity>();
    this.entity = entity;
    this.nodes = new Quadtree[4];
  }

  public void clear() {
    this.objects.clear();

    for (int i = 0; i < this.nodes.length; i++) {
      if (this.nodes[i] != null) {
        this.nodes[i].clear();
        this.nodes[i] = null;
      }
    }
  }

  /*
   * Splits the node into 4 subnodes
   */
  private void split(Entity entity) {
    int subWidth = this.entity.getWidth() / 2;
    int subHeight = this.entity.getHeight() / 2;
    int x = this.entity.getX();
    int y = this.entity.getY();
    // public Entity(int x, int y, int width, int height, Id id, Handler handler, BufferedImage[] bufferedImages);
    this.nodes[0] = new Quadtree(this.level + 1, entity);
    this.nodes[1] = new Quadtree(this.level + 1, entity);
    this.nodes[2] = new Quadtree(this.level + 1, entity);
    this.nodes[3] = new Quadtree(this.level + 1, entity);
  }

  /*
   * Determine which node the object belongs to. -1 means object cannot completely fit within a child node and is part of the parent node
   */
  private int getIndex(Entity entity) {
    int index = -1;
    double verticalMidpoint = this.entity.getX() + (this.entity.getWidth() / 2);
    double horizontalMidpoint = this.entity.getY() + (this.entity.getHeight() / 2);

    // Object can completely fit within the top quadrants
    boolean topQuadrant = (entity.getY() < horizontalMidpoint && entity.getY() + entity.getHeight() < horizontalMidpoint);
    // Object can completely fit within the bottom quadrants
    boolean bottomQuadrant = (entity.getY() > horizontalMidpoint);

    // Object can completely fit within the left quadrants
    if (entity.getX() < verticalMidpoint && entity.getX() + entity.getWidth() < verticalMidpoint) {
      if (topQuadrant) {
        index = 1;
      } else if (bottomQuadrant) {
        index = 2;
      }
    }
    // Object can completely fit within the right quadrants
    else if (entity.getX() > verticalMidpoint) {
      if (topQuadrant) {
        index = 0;
      } else if (bottomQuadrant) {
        index = 3;
      }
    }

    return index;
  }

  /*
   * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
   */
  public void insert(Entity entity) {
    if (this.nodes[0] != null) {
      int index = getIndex(entity);

      if (index != -1) {
        this.nodes[index].insert(entity);

        return;
      }
    }

    this.objects.add(entity);

    if (this.objects.size() > this.MAX_OBJECTS && this.level < this.MAX_LEVELS) {
      if (this.nodes[0] == null) {
        split(entity);
      }

      int i = 0;
      while (i < this.objects.size()) {
        int index = getIndex(this.objects.get(i));
        if (index != -1) {
          this.nodes[index].insert(this.objects.remove(i));
        } else {
          i++;
        }
      }
    }
//    log.info(this.objects.size());
  }
  
  /*
   * Return all objects that could collide with the given object
   */
  public List retrieve(List returnObjects, Entity entity) {
     int index = getIndex(entity);
     if (index != -1 && nodes[0] != null) {
       nodes[index].retrieve(returnObjects, entity);
     }
   
     returnObjects.addAll(objects);
   
     return returnObjects;
   }

}