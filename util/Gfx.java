package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import java.util.ArrayList;

public class Gfx {

    public static ShapeRenderer sr = new ShapeRenderer();
    public static ShapeRenderer uisr = new ShapeRenderer();

    public static SpriteBatch sb = new SpriteBatch();
    public static SpriteBatch uisb = new SpriteBatch();

    public static OrthographicCamera cam = new OrthographicCamera();

    public static ArrayList<Texture> textures = new ArrayList<Texture>();
    public static ArrayList<Sprite> sprites = new ArrayList<Sprite>();

    public static BitmapFont font = new BitmapFont();
    public static BitmapFont font2 = new BitmapFont();

    public static Color clearColor = Color.DARK_GRAY;

    public static void initOrtho(float w, float h, boolean flipped) {
        cam = new OrthographicCamera(w, h);
        cam.setToOrtho(flipped);
    }

    public static void initFont(int size, String file) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal(file));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        parameter.size = size;
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    public static void initFont2(int size, String file) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal(file));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        parameter.size = size;
        font2 = generator.generateFont(parameter);
        generator.dispose();
    }


    public static void loadImage(String s) {
        textures.add(new Texture(Gdx.files.internal(s)));
    }

    public static void loadSprite(String s) {
        sprites.add(new Sprite(new Texture(Gdx.files.internal(s))));
    }

    public static void update() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        cam.update();
        Inputer.mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(Inputer.mousePos);
    }

    public static void setClearColor(Color c) {
        clearColor = c;
    }

    public static void setColor(Color c) {
        sr.setColor(c);
    }

    public static void setColorUI(Color c) {
        uisr.setColor(c);
    }

    public static void fillCircle(float x, float y, float r) {
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeType.Filled);
        sr.circle(x, y, r);
        sr.end();
    }

    public static void fillCircleUI(float x, float y, float r) {
        uisr.begin(ShapeType.Filled);
        uisr.circle(x, y, r);
        uisr.end();
    }

    public static void drawCircle(float x, float y, float r) {
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeType.Line);
        sr.circle(x, y, r);
        sr.end();
    }

    public static void drawCircleUI(float x, float y, float r) {
        uisr.begin(ShapeType.Line);
        uisr.circle(x, y, r);
        uisr.end();
    }

    public static void fillRect(float x, float y, float w, float h) {
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeType.Filled);
        sr.rect(x, y, w, h);
        sr.end();
    }

    public static void fillRectUI(float x, float y, float w, float h) {
        uisr.begin(ShapeType.Filled);
        uisr.rect(x, y, w, h);
        uisr.end();
    }

    public static void drawRect(float x, float y, float w, float h) {
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeType.Line);
        sr.rect(x, y, w, h);
        sr.end();
    }

    public static void drawRectUI(float x, float y, float w, float h) {
        uisr.begin(ShapeType.Line);
        uisr.rect(x, y, w, h);
        uisr.end();
    }

    public static void drawSprite(Sprite s, float x, float y, float w, float h) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        s.setPosition(x, y);
        s.setSize(w, h);
        s.draw(sb);
        sb.end();
    }

    public static void drawSprite(Sprite s, float x, float y) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        s.setPosition(x, y);
        s.draw(sb);
        sb.end();
    }

    public static void drawSprite(Sprite s) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        s.draw(sb);
        sb.end();
    }

    public static void drawSprite(String texture, float x, float y, float w, float h) {
        sb.setProjectionMatrix(cam.combined);
        boolean hasSprite = false;
        for (Sprite s : sprites) {
            Texture t = s.getTexture();
            String path = ((FileTextureData) t.getTextureData())
                    .getFileHandle().path();
            if (path == texture) {
                hasSprite = true;
                sb.begin();
                s.setPosition(x, y);
                s.setSize(w, h);
                s.draw(sb);
                sb.end();
            }
        }
        if (!hasSprite) {
            Logger.textureNotFound(texture);
        }
    }

    public static void drawSpriteUI(String texture, float x, float y, float w, float h) {
        sb.setProjectionMatrix(cam.combined);
        boolean hasSprite = false;
        for (Sprite s : sprites) {
            Texture t = s.getTexture();
            String path = ((FileTextureData) t.getTextureData())
                    .getFileHandle().path();
            if (path == texture) {
                hasSprite = true;
                uisb.begin();
                s.setPosition(x, y);
                s.setSize(w, h);
                s.draw(uisb);
                uisb.end();
            }
        }
        if (!hasSprite) {
            Logger.textureNotFound(texture);

        }
    }

    public static void drawSpriteUI(String texture, float x, float y, float w, float h, Color c) {
        sb.setProjectionMatrix(cam.combined);
        boolean hasSprite = false;
        for (Sprite s : sprites) {
            Texture t = s.getTexture();
            String path = ((FileTextureData) t.getTextureData())
                    .getFileHandle().path();
            if (path == texture) {
                hasSprite = true;
                uisb.begin();
                s.setPosition(x, y);
                s.setSize(w, h);
                s.setColor(c);
                s.draw(uisb);
                uisb.end();
            }
        }
        if (!hasSprite) {
            Logger.textureNotFound(texture);

        }
    }

    public static void drawSprite(String texture, float x, float y, float w, float h, Color c) {
        sb.setProjectionMatrix(cam.combined);
        boolean hasSprite = false;
        for (Sprite s : sprites) {
            Texture t = s.getTexture();
            String path = ((FileTextureData) t.getTextureData())
                    .getFileHandle().path();
            if (path == texture) {
                hasSprite = true;
                sb.begin();
                s.setColor(c);
                s.setPosition(x, y);
                s.setSize(w, h);
                s.draw(sb);
                sb.end();
            }
        }
        if (!hasSprite) {
            Logger.textureNotFound(texture);
        }
    }

    public static void drawSprite(String texture, float x, float y) {
        sb.setProjectionMatrix(cam.combined);
        boolean hasSprite = false;
        for (Sprite s : sprites) {
            Texture t = s.getTexture();
            String path = ((FileTextureData) t.getTextureData())
                    .getFileHandle().path();
            if (path == texture) {
                hasSprite = true;
                sb.begin();
                s.setPosition(x, y);
                s.draw(sb);
                sb.end();
            }
        }
        if (!hasSprite) {
            Logger.textureNotFound(texture);
        }
    }

    public static void drawImage(String texture, float x, float y, float w, float h) {
        sb.setProjectionMatrix(cam.combined);
        boolean hasTexture = false;
        for (Texture t : textures) {
            String path = ((FileTextureData) t.getTextureData())
                    .getFileHandle().path();
            if (path == texture) {
                t.setFilter(Texture.TextureFilter.MipMapLinearNearest,
                        Texture.TextureFilter.MipMapLinearNearest);
                hasTexture = true;
                sb.begin();
                sb.draw(t, x, y, w, h);
                sb.end();
            }
        }

        if (!hasTexture) {
            Logger.textureNotFound(texture);
        }
    }

    public static void drawImage(String texture, float x, float y, float w, float h, Color c) {
        sb.setProjectionMatrix(cam.combined);
        boolean hasTexture = false;
        for (Texture t : textures) {
            String path = ((FileTextureData) t.getTextureData())
                    .getFileHandle().path();
            if (path == texture) {
                Color pre = sb.getColor();
                sb.setColor(c);
                hasTexture = true;
                sb.begin();
                sb.draw(t, x, y, w, h);
                sb.end();
                sb.setColor(pre);
            }
        }

        if (!hasTexture) {
            Logger.textureNotFound(texture);
        }
    }

    public static void drawImageUI(String texture, float x, float y, float w, float h) {
        boolean hasTexture = false;
        for (Texture t : textures) {
            String path = ((FileTextureData) t.getTextureData())
                    .getFileHandle().path();
            if (path == texture) {
                hasTexture = true;
                uisb.begin();
                uisb.draw(t, x, y, w, h);
                uisb.end();
            }
        }

        if (!hasTexture) {
            Logger.textureNotFound(texture);
        }
    }

    public static void drawImageUI(String texture, float x, float y, float w, float h, Color c) {
        boolean hasTexture = false;
        for (Texture t : textures) {
            String path = ((FileTextureData) t.getTextureData())
                    .getFileHandle().path();
            if (path == texture) {
                Color pre = uisb.getColor();
                uisb.setColor(c);
                hasTexture = true;
                uisb.begin();
                uisb.draw(t, x, y, w, h);
                uisb.end();
                uisb.setColor(pre);
            }
        }

        if (!hasTexture) {
            Logger.textureNotFound(texture);
        }
    }

    public static Texture getImage(String texture) {
        for (Texture t : textures) {
            String path = ((FileTextureData) t.getTextureData())
                    .getFileHandle().path();
            if (path == texture) {
                return t;
            }
        }
        Logger.textureNotFound(texture);
        return null;
    }

    public static Sprite getSprite(String texture) {
        for (Sprite s : sprites) {
            String path = ((FileTextureData) s.getTexture().getTextureData())
                    .getFileHandle().path();
            if (path == texture) {
                return s;
            }
        }
        Logger.textureNotFound(texture);
        return null;
    }

    public static void drawLine(float x, float y, float x1, float y1) {
        sr.begin(ShapeType.Line);
        sr.line(x, y, x1, y1);
        sr.end();
    }

    public static void drawText(String s, float x, float y) {
        sb.begin();
        font.draw(sb, s, x, y);
        sb.end();
    }

    //    // center ui text open x with given y
    //    public static void allignTextX(String s, float y) {
    //        GlyphLayout gl = new GlyphLayout();
    //        gl.setText(Gfx.font, s);
    //
    //        uisb.begin();
    //        font.draw(uisb, s, Const.WIDTH / 2 - (gl.width / 2), y);
    //        uisb.end();
    //    }
    //
    //    // center ui text open y with given x
    //    public static void allignTextY(String s, float x) {
    //        GlyphLayout gl = new GlyphLayout();
    //        gl.setText(Gfx.font, s);
    //
    //        uisb.begin();
    //        font.draw(uisb, s, x, Const.HEIGHT / 2 - (gl.height / 2));
    //        uisb.end();
    //    }

    public static void drawText(String s, float x, float y, Color c) {
        font.setColor(c);
        sb.begin();
        font.draw(sb, s, x, y);
        sb.end();
        font.setColor(Color.WHITE);
    }

    public static void drawText(String s, BitmapFont currentFont, float x, float y, Color c) {
        font.setColor(c);
        sb.begin();
        currentFont.draw(sb, s, x, y);
        sb.end();
        currentFont.setColor(Color.WHITE);
    }

    public static void drawTextUI(String s, BitmapFont currentFont, float x, float y) {
        uisb.begin();
        currentFont.draw(uisb, s, x, y);
        uisb.end();
    }

    public static void drawTextUI(String s, float x, float y) {
        uisb.begin();
        font.draw(uisb, s, x, y);
        uisb.end();
    }

    public static void drawTextUI(String s, float x, float y, Color c) {
        font.setColor(c);
        uisb.begin();
        font.draw(uisb, s, x, y);
        uisb.end();
        font.setColor(Color.WHITE);
    }

    public static void drawTextUI(String s, BitmapFont currentFont, float x, float y, Color c) {
        currentFont.setColor(c);
        uisb.begin();
        currentFont.draw(uisb, s, x, y);
        uisb.end();
        currentFont.setColor(Color.WHITE);
    }

    public static void dispose() {
        font.dispose();
        uisb.dispose();
        uisr.dispose();
        sb.dispose();
        sr.dispose();
        for (Texture t : textures)
            t.dispose();
        textures.clear();
        for (Sprite s : sprites)
            s.getTexture().dispose();
        sprites.clear();
    }
}
