package entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.BasicGame;

public abstract class Entity {

    public BasicGame g;
    public int id;
    public boolean invincible;
    public boolean frozen;
    public boolean alive = true;

    public float vel;

    public Vector2 dir = new Vector2(0, 0);
    public Vector2 pos = new Vector2(0, 0);
    public Vector2 size = new Vector2(0, 0);
    public Vector2 spritePos = new Vector2(0, 0);

    public Entity() {
    }

    public abstract void render();

    public abstract void update();

    public abstract void dispose();

    public Rectangle bounds() {
        return new Rectangle(pos.x, pos.y, size.x, size.y);
    }

    public Rectangle bounds(float x, float y, float w, float h) {
        return new Rectangle(x, y, w, h);
    }

    public Vector2 centerPos() {
        return new Vector2(pos.x + (size.x / 2), pos.y + (size.y / 2));
    }

}
