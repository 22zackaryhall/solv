package util.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public abstract class UIItem {

    public Vector3 pos = new Vector3();

    public Vector2 size = new Vector2();

    public Color color = Color.TEAL;

    public String text;

    public GUIBox box;

    public UIItem(GUIBox box) {
        this.box = box;
        size.set(box.itemWidth, box.itemHeight);
    }

    abstract void update();

    abstract void render();

    abstract void dispose();

}
