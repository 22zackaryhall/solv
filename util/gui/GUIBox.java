package util.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import util.Gfx;

public class GUIBox {

    // name of box to be displayed at top of it
    public String title = "UI BOX";

    // height / width of item in box
    public float itemWidth = 120;
    public float itemHeight = 30;

    public float spacing = 5;

    public Vector2 size = new Vector2();

    // position can be projected
    public Vector3 pos = new Vector3();

    public Color color = Color.BLACK;

    Array<UIItem> items = new Array<UIItem>();

    public GUIBox(float x, float y) {
        pos.set(x, y, 0);
    }

    public void update() {

        size.set(itemWidth + (spacing * 2), itemHeight + (spacing) + (itemHeight * items.size));

        for (UIItem i : items) {
            i.pos.x = pos.x + spacing;
            i.pos.y = pos.y - (itemHeight * 2) - (itemHeight * items.indexOf(i, true));
            i.update();
        }
    }

    public void render() {
        Gfx.setColorUI(color);
        Gfx.fillRectUI(pos.x, pos.y, size.x, -size.y);
        Gfx.drawTextUI(title, pos.x, pos.y - Gfx.font.getLineHeight() / 2);

        for (UIItem i : items)
            i.render();
    }

    public void dispose() {
        for (UIItem i : items)
            i.dispose();
    }

    public void setItemSize(float w, float h) {
        for (UIItem i : items)
            i.size.set(itemWidth, itemHeight);

        itemWidth = w;
        itemHeight = h;
    }

    public void addItem(UIItem item) {
        item.size.set(itemWidth, itemHeight);
        items.add(item);
    }

}
