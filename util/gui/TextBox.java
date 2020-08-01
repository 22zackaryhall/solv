package util.gui;

import com.badlogic.gdx.graphics.Color;
import util.Gfx;

public class TextBox extends UIItem {

    public TextBox(GUIBox box) {
        super(box);
        text = "Textbox";
    }

    public void update() {

    }

    public void render() {
        Gfx.setColorUI(color);
        Gfx.fillRectUI(pos.x, pos.y, size.x, size.y);
        Gfx.font.setColor(Color.WHITE);
        Gfx.drawTextUI(" " + text, pos.x, pos.y + Gfx.font.getLineHeight());
    }

    public void dispose() {

    }
}
