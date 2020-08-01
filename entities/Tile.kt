package entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import core.BasicGame
import core.Const
import util.Gfx
import util.Inputer
import java.util.*

class Tile(var x: Float, var y: Float, var size: Float, var id: Int, internal var tex: ArrayList<String>, internal var game: BasicGame) {

    lateinit var sprite: Sprite

    init {
        init()
    }

    internal fun init() {
        var path: String? = null

        for (i in tex.indices) {
            val a = tex[i]
            val split = a.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val sid = Integer.parseInt(split[0])
            val spath = split[1]

            if (sid == id) {
                path = spath
                break
            }
        }

        path = "empty.png"

        if (id == Const.GRASS_0_ID)
            path = "grass_0.png"
        if (id == Const.GRASS_2_ID)
            path = "grass_2.png"
        if (id == Const.FLOOR_1_ID)
            path = "floor1.png"
        if (id == Const.FLOOR_2_ID)
            path = "floor2.png"
        if (id == Const.FLOOR_3_ID)
            path = "floor3.png"
        if (id == Const.FLOOR_4_ID)
            path = "floor4.png"
        if (id == Const.FLOOR_5_ID)
            path = "floor5.png"
        if (id == Const.FLOOR_6_ID)
            path = "floor6.png"
        if (id == Const.FLOOR_7_ID)
            path = "floor7.png"
        if (id == Const.FLOOR_8_ID)
            path = "floor8.png"
        if (id == Const.FLOOR_9_ID)
            path = "floor9.png"
        if (id == Const.FLOOR_10_ID)
            path = "floor10.png"
        if (id == Const.FLOOR_11_ID)
            path = "floor11.png"
        if (id == Const.FLOOR_12_ID)
            path = "floor12.png"
        if (id == Const.FLOOR_13_ID)
            path = "floor13.png"
        if (id == Const.FLOOR_14_ID)
            path = "floor14.png"
        if (id == Const.GRASS_1_ID)
            path = "grass_1.png"

        if (id == Const.SPACE_ENTRANCE)
            path = "space_entrance.png"

        if (id == Const.WATER_ID)
            path = "water.png"
        if (id == Const.DOCK_0)
            path = "dock_0.png"
        if (id == Const.SAND_0)
            path = "sand_0.png"

        if (id == Const.TOWER_BRICK)
            path = "tower_brick.png"
        if (id == Const.TOWER_BRICK_DARK)
            path = "tower_brick_dark.png"

        if (path != null) {
            sprite = Sprite(Texture(Gdx.files.internal("tiles/" + path)))
            sprite.setPosition(x, y)
            sprite.setSize(size, size)
        }
    }

    fun update() {
        if (Const.debugging) {
            if (game.editMode == 1 && game.editorMode && Gdx.input.isButtonPressed(Input.Buttons.RIGHT)
                    && Inputer.mousePos.x > x && Inputer.mousePos.x < x + bounds().width
                    && Inputer.mousePos.y > y && Inputer.mousePos.y < y + bounds().height) {
                this.id = 0
                init()
            }

            if (game.editMode == 1 && game.editorMode && Gdx.input.isButtonPressed(Input.Buttons.LEFT)
                    && Inputer.mousePos.x > x && Inputer.mousePos.x < x + bounds().width
                    && Inputer.mousePos.y > y && Inputer.mousePos.y < y + bounds().height) {
                id = game.currentEditorId
                init()
            }
        }
    }

    fun render() {
        if ((x + size > Gfx.cam.position.x - Gfx.cam.viewportWidth / 2f &&
                x < Gfx.cam.position.x + Gfx.cam.viewportWidth / 2f &&
                y + size > Gfx.cam.position.y - Gfx.cam.viewportHeight / 2f &&
                y < Gfx.cam.position.y + Gfx.cam.viewportHeight / 2f) || (Const.CURRENT_FILE == "space_2")) {

            Gfx.drawSprite(sprite)

            if (Const.debugging) {
                Gfx.sr.begin(ShapeRenderer.ShapeType.Line)
                Gfx.setColor(Color.SKY)
                Gfx.sr.rect(bounds().x, bounds().y, bounds().width, bounds().height)
                Gfx.sr.end()
            }

        }
    }

    fun dispose() {
        sprite.getTexture().dispose()
    }

    fun bounds(): Rectangle {
        return Rectangle(x, y, size, size)
    }
}
