package core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import entities.Hero
import util.Audio
import util.Gfx
import java.util.*
import kotlin.concurrent.schedule

abstract class BasicGame : ApplicationAdapter() {

    var layout: GlyphLayout

    init {
        layout = GlyphLayout()
    }

    // Shaders for space_2
    var vertexShader: String? = null
    var fragmentShader: String? = null
    var shaderProgram: ShaderProgram? = null

    var tileIds = ArrayList<String>()
    var editorMode: Boolean = true
    var editMode = 1 // 0-entity mode 1-tile mode
    var currentEditorId = 1
    var currentBookPage = 1
    var bookOpen = false

    lateinit var map: world

    lateinit var player: Hero
    lateinit var shadow: Hero

    var screenHidden = false
    var screenHiderFadingIn = false

    // For spawning text
    var showingSpawnText = false
    var spawnTextContents = ""

    lateinit var theme: Music

    var spawnTextStringWidth = 1f
    var spawnTextStringHeight = 1f
    var spawnTextAlpha = 1f

    val t = Timer()

    var spaceParticles = arrayListOf<star>()
    lateinit var space2HandLeft: Sprite
    lateinit var space2HandRight: Sprite
    lateinit var bookSpine: Sprite

    lateinit var towerSprite: Sprite

    abstract override fun render()
    abstract override fun dispose()

    fun clean() {
        Audio.dispose()
        Gfx.dispose()
    }

    override fun create() {}

    override fun pause() {}

    override fun resume() {}

    fun loadMap(name: String) {
        Const.CURRENT_FILE = name
        spaceParticles.clear();

        // Tower for overworld_6
        if (name.contains("overworld_6")) {
            towerSprite = Sprite(Texture("sprites/tower.png"));
            towerSprite.setPosition(Const.TILESIZE * 6f - 200f, Const.TILESIZE * 11f);
            towerSprite.setSize(500f, 800f)
        }

        // Loads particles
        if (name.contains("space")) {
            for (i in 0..60) {
                spaceParticles.add(star(
                        MathUtils.random(0f, Gfx.cam.viewportWidth),
                        MathUtils.random(0f, Gfx.cam.viewportHeight),
                        spaceParticles))
            }
        }

        // Loads hands for space_2
        if (name.equals("space_2")) {
            space2HandLeft = Sprite(Texture("sprites/hand_left.png"))
            space2HandLeft.setPosition((3f * Const.TILESIZE) - space2HandLeft.getWidth(), (8f * Const.TILESIZE))
            space2HandRight = Sprite(Texture("sprites/hand_right.png"))
            space2HandRight.setPosition((9f * Const.TILESIZE) + Const.TILESIZE, (8f * Const.TILESIZE))
        } else {
            try {
                space2HandLeft.texture.dispose()
                space2HandRight.texture.dispose()
            } catch (e: Exception) { }
        }

        map = world(this)
        map.tfloors.clear()
        map.efloors.clear()
        map.trigerables.clear()

        if (Const.FIRST_TIME_PLAYING) {
            Const.FIRST_TIME_PLAYING = false
            map.loadSaveData()  // This changes Const.CURRENT_FILE
        }
        map.addTileFloor(TileFloor(0, Const.TILESIZE.toFloat(), Gdx.files.internal("maps/" + Const.CURRENT_FILE + ".map"), this))
        map.addEntityFloor(EntityFloor(0, Const.TILESIZE.toFloat(), Gdx.files.internal("maps/" + Const.CURRENT_FILE + ".ent"), this))
        map.loadTriggerables(Const.CURRENT_FILE + ".trig")
        map.setCurrentFloorId(0)

        shadow = Hero(this, map, Vector2(map.shadowspawnPos.x, map.shadowspawnPos.y), Vector2(Const.TILESIZE - 0f, Const.TILESIZE + 0f))
        shadow.shadowHero = true
        shadow.initShadow()
        Gfx.cam.zoom = Const.ZOOM

        if (Const.CURRENT_FILE == "space_2") {
            Gfx.cam.zoom = 1.2f;
        }
    }

    fun spawnText(text: String) {
        showingSpawnText = true
        spawnTextContents = text
        spawnTextAlpha = 1f

        layout.setText(Gfx.font, text)
        spawnTextStringWidth = layout.width // contains the width of the current set text
        spawnTextStringHeight = layout.height // contains the height of the current set text

        // NOTE(Jervac): This caused problems of restarting timer interfering with switching levels instantly. WTF was it supposed to do? Everything works without this code
        /*
        t.schedule(Const.SPAWN_TEXT_TIME) {
            showingSpawnText = false
                spawnTextContents = ""
                spawnTextAlpha = 1f
        }
        */
    }
}

class star(var x: Float, var y: Float, val list: ArrayList<star>) {
    var size = 5f
    var alive = true
    var vel = 0f
    var foreground = MathUtils.random(1, 10) <= 2
    var alpha = 1f
    var glower = MathUtils.random(1, 10) == 1
    var glowUp = false
    var glowDown = false

    val t = Timer()

    init {
        if (foreground)
            vel = MathUtils.random(2f, 3.5f)
        else {
            alpha = MathUtils.random(0.1f, 0.66f)
            vel = MathUtils.random(.5f, 1.4f)
        }

        for (s in list) {
            if (s.alive && s.bounds().overlaps(bounds())) {
                alive = false
            }
        }

        if (glower) {
            alive = true
            vel = 0f
            alpha = 0f
            size = MathUtils.random(size, size + 25f)
        }

        t.schedule(MathUtils.random(1000L, 8000L)) {
            glow(true)
        }
    }

    // @param: setGlowUp: Sets glowUp to true when true, glowDown to true when false
    fun glow(setGlowUp: Boolean) {
        if (setGlowUp) {
            glowUp = true
            glowDown = false
        } else {
            glowUp = false
            glowDown = true
        }

        // Take short time to fade back out
        if (glowUp) {
            t.schedule(MathUtils.random(1000L, 1200L)) {
                glow(false)
            }
            // Take longer to fade in
        } else {
            t.schedule(MathUtils.random(1000L, 5000L)) {
                glow(true)
            }
        }
    }

    fun update() {
        x += vel;
        if (x >= Gfx.cam.viewportWidth) x = 0f;
    }

    fun render() {
        if (alive) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            if (!glower) {
                Gfx.setColorUI(Color(1f, 1f, 1f, alpha))
                Gfx.fillRectUI(x, y, size, size)
                Gfx.setColorUI(Color(.21f, .2f, .2f, .21f))
                Gfx.fillCircleUI(x + (size * 1.5f) / 4, y + (size * 1.5f) / 4, size * 1.5f)

            } else if (glower) {
                Gfx.setColorUI(Color(.21f, .2f, .2f, alpha))

                if (glowUp && alpha < .4f) {
                    alpha += 0.01f
                }
                if (glowDown && alpha > 0f) {
                    alpha -= 0.01f
                }

                Gfx.fillCircleUI(x, y, size)
                Gfx.setColorUI(Color(.21f, .2f, .2f, alpha / 2f))
                Gfx.fillCircleUI(x, y, size / 4)
            }
        }
    }

    fun bounds(): Rectangle {
        return Rectangle(x, y, size, size)
    }
}