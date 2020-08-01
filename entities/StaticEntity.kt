package entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import math.Vec2f
import util.Gfx
import util.Inputer

class StaticEntity(internal var game: BasicGame, var x: Float, var y: Float, var id: Int) {

    var size = Vec2f()
    var bsize = Vec2f()
    var path: String? = null
    lateinit internal var sprite: Sprite
    var beam: LaserBeam? = null

    // for moving laser beam
    var LR = false
    var UD = false
    var moveSpeed = 1f

    var spritePos: Vector2

    // for bone logic of overworld_3
    var movableBone = false

    init {
        init()
        spritePos = Vector2(x, y)
    }

    private fun init() {
        // Stuff underneath the map that is depricated now :)
        /*
        for (i in tex.indices) {
            val a = tex[i]
            val split = a.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            // id:path
            val sid = Integer.parseInt(split[0])
            val spath = split[1]

            // size,size
            val sizes = split[2]
            val ssplit = sizes.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val sizew = Integer.parseInt(ssplit[0])
            val sizeh = Integer.parseInt(ssplit[1])
            size = Vec2f(sizew * tsize, sizeh * tsize)

            // bounds
            val bsizes = split[3]
            val sbsizes = bsizes.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val sizewb = Integer.parseInt(sbsizes[0])
            val sizehb = Integer.parseInt(sbsizes[1])
            bsize = Vec2f(sizewb.toFloat(), sizehb.toFloat())

            if (sid == id) {
                path = spath
                break
            }
        }
*/
        size.x = Const.TILESIZE.toFloat()
        size.y = Const.TILESIZE.toFloat()
        bsize.x = size.x
        bsize.y = size.y

        path = "wall.png"

        if (id == Const.ROCK_ID)
            path = "rock0.png"

        if (id == Const.REFLECTOR_DOWNLEFT_ID)
            path = "downleft.png"
        if (id == Const.REFLECTOR_UPLEFT_ID)
            path = "upleft.png"
        if (id == Const.REFLECTOR_UPRIGHT_ID)
            path = "upright.png"
        if (id == Const.REFLECTOR_DOWNRIGHT_ID)
            path = "downright.png"

        if (id == Const.LASER_UP_ID || id == Const.LASER_DOWN_ID || id == Const.LASER_LEFT_ID || id == Const.LASER_RIGHT_ID)
            path = "laser_shooter.png"

        if (id == Const.MOVING_LASER_LR_UP_ID || id == Const.MOVING_LASER_LR_DOWN_ID) {
            LR = true
            path = "laser_shooter.png"
        }
        if (id == Const.MOVING_LASER_UD_LEFT_ID || id == Const.MOVING_LASER_UD_RIGHT_ID) {
            UD = true
            path = "laser_shooter.png"
        }

        if (id == Const.WALL_DETAIL)
            path = "wall_detail.png"
        if (id == Const.WALL_UP)
            path = "wall_up.png"
        if (id == Const.WALL_DOWN)
            path = "wall_down.png"
        if (id == Const.WALL_LEFT)
            path = "wall_left.png"
        if (id == Const.WALL_RIGHT)
            path = "wall_right.png"


        if (id == Const.WALL_UPLEFT)
            path = "wall_upleft.png"
        if (id == Const.WALL_UPRIGHT)
            path = "wall_upright.png"
        if (id == Const.WALL_DOWNLEFT)
            path = "wall_downleft.png"
        if (id == Const.WALL_DOWNRIGHT)
            path = "wall_downright.png"

        if (id == Const.WALL_UPDOWN)
            path = "wall_updown.png"

        if (id == Const.WALL_LEFTRIGHT)
            path = "wall_leftright.png"

        if (id == Const.WALL_WATER_DOWNRIGHT2_ID)
            path = "wall_water_downright2.png"
        if (id == Const.WALL_WATER_DOWNLEFT2_ID)
            path = "wall_water_downleft2.png"

        if (id == Const.WALL_2_ID)
            path = "wall2.png"
        if (id == Const.WALL_2_UP_ID)
            path = "wall2_up.png"
        if (id == Const.WALL_2_DOWN_ID)
            path = "wall2_down.png"
        if (id == Const.WALL_2_LEFT_ID)
            path = "wall2_left.png"
        if (id == Const.WALL_2_RIGHT_ID)
            path = "wall2_right.png"

        if (id == Const.WALL_2_UPRIGHT_ID)
            path = "wall2_upright.png"
        if (id == Const.WALL_2_UPLEFT_ID)
            path = "wall2_upleft.png"
        if (id == Const.WALL_2_DOWNRIGHT_ID)
            path = "wall2_downright.png"
        if (id == Const.WALL_2_DOWNLEFT_ID)
            path = "wall2_downleft.png"

        if (id == Const.WALL_2_UPRIGHT2_ID)
            path = "wall2_upright2.png"
        if (id == Const.WALL_2_UPLEFT2_ID)
            path = "wall2_upleft2.png"
        if (id == Const.WALL_2_DOWNRIGHT2_ID)
            path = "wall2_downright2.png"
        if (id == Const.WALL_2_DOWNLEFT2_ID)
            path = "wall2_downleft2.png"

        if (id == Const.WALL_INVISIBLE)
            path = "rock0.png"

        if (id == Const.WALL_2_UPDOWN)
            path = "wall2_updown.png"
        if (id == Const.WALL_2_LEFTRIGHT)
            path = "wall2_leftright.png"
        if (id == Const.WALL_2_UPDOWNLEFT)
            path = "wall2_updownleft.png"
        if (id == Const.WALL_2_UPDOWNRIGHT)
            path = "wall2_updownright.png"
        if (id == Const.WALL_2_LEFTRIGHTUP)
            path = "wall2_leftrightup.png"
        if (id == Const.WALL_2_LEFTRIGHTDOWN)
            path = "wall2_leftrightdown.png"

        if (path != null) {
            sprite = Sprite(Texture(Gdx.files.internal("tiles/" + path!!)))
            sprite.setPosition(x, y)
            sprite.setSize(size.x, size.y)
            //			sprite.flip(false, true);
            //  if (size.y > Const.TILESIZE) {
            //     sprite.setPosition(sprite.x, sprite.y - Const.TILESIZE * bsize.y)
            //  }
        } else {
            println("unknow path: " + path!!)
        }

        if (id == Const.LASER_UP_ID || id == Const.MOVING_LASER_LR_UP_ID ||
                id == Const.LASER_DOWN_ID || id == Const.MOVING_LASER_LR_DOWN_ID ||
                id == Const.LASER_LEFT_ID || id == Const.MOVING_LASER_UD_LEFT_ID ||
                id == Const.LASER_RIGHT_ID || id == Const.MOVING_LASER_UD_RIGHT_ID)
            initLaser()
    }


    fun initLaser() {
        beam = LaserBeam(game, this)
    }

    fun update() {
        if (id != 0) {
            spritePos.x += (x - spritePos.x) * Const.SPRITE_LERP
            spritePos.y += (y - spritePos.y) * Const.SPRITE_LERP

            // for moving laser beams
            if (LR) {
                x += moveSpeed
                for (e in game.map.currentEntityFloor.entities) {
                    if (e != this && e.id != Const.EMPTY_ID && e.bounds().overlaps(this.bounds())) {
                        x -= moveSpeed
                        moveSpeed = -moveSpeed
                    }
                }
                for (t in game.map.trigerables) {
                    if (t.javaClass == Door::class.java && t.on != t.defaultState && t.bounds().overlaps(this.bounds())) {
                        x -= moveSpeed
                        moveSpeed = -moveSpeed
                    }
                }
                if (bounds().overlaps(game.player.bounds()) || bounds().overlaps(game.shadow.bounds())) {
                    x -= moveSpeed
                    moveSpeed = -moveSpeed
                }
            }
            if (UD) {
                y += moveSpeed
                for (e in game.map.currentEntityFloor.entities) {
                    if (e != this && e.id != Const.EMPTY_ID && e.bounds().overlaps(this.bounds())) {
                        y -= moveSpeed
                        moveSpeed = -moveSpeed
                    }
                }
                for (t in game.map.trigerables) {
                    if (t.javaClass == Door::class.java && t.on != t.defaultState && t.bounds().overlaps(this.bounds())) {
                        y -= moveSpeed
                        moveSpeed = -moveSpeed
                    }
                }
                if (bounds().overlaps(game.player.bounds()) || bounds().overlaps(game.shadow.bounds())) {
                    x -= moveSpeed
                    moveSpeed = -moveSpeed
                }
            }

        }
        if (Const.debugging) {
            if (game.editMode == 0 && game.editorMode && Gdx.input.isButtonPressed(Input.Buttons.RIGHT)
                    && Inputer.mousePos.x > x && Inputer.mousePos.x < x + bounds().width
                    && Inputer.mousePos.y > y && Inputer.mousePos.y < y + bounds().height) {
                this.id = 0
            }

            if (game.editMode == 0 && game.editorMode && Gdx.input.isButtonPressed(Input.Buttons.LEFT)
                    && Inputer.mousePos.x > x && Inputer.mousePos.x < x + bounds().width
                    && Inputer.mousePos.y > y && Inputer.mousePos.y < y + bounds().height) {
                id = game.currentEditorId
                init()
            }
        }

        if (id == Const.LASER_UP_ID || id == Const.MOVING_LASER_LR_UP_ID ||
                id == Const.LASER_DOWN_ID || id == Const.MOVING_LASER_LR_DOWN_ID ||
                id == Const.LASER_LEFT_ID || id == Const.MOVING_LASER_UD_LEFT_ID ||
                id == Const.LASER_RIGHT_ID || id == Const.MOVING_LASER_UD_RIGHT_ID) {
            beam?.update()
        }

    }

    var temp = Rectangle(0f, 0f, 0f, 0f)

    fun render() {

        // draw the sprite
        // plus minus size so it shows corner tiles and all
        if ((x + size.x > Gfx.cam.position.x - Gfx.cam.viewportWidth / 2f &&
                x < Gfx.cam.position.x + Gfx.cam.viewportWidth / 2f &&
                y + size.y > Gfx.cam.position.y - Gfx.cam.viewportHeight / 2f &&
                y < Gfx.cam.position.y + Gfx.cam.viewportHeight / 2f) || Const.CURRENT_FILE == "space_2") {

            if (id != Const.EMPTY_ID && id != Const.WALL_INVISIBLE)
                Gfx.drawSprite(sprite, spritePos.x, spritePos.y, size.x, size.y)

            if (Const.debugging) {
                Gfx.setColor(Color.BLACK)
                Gfx.drawRect(bounds().x, bounds().y, bounds().width, bounds().height)
            }
        }

        // LASER BEAM //
        if (id == Const.LASER_UP_ID || id == Const.MOVING_LASER_LR_UP_ID ||
                id == Const.LASER_DOWN_ID || id == Const.MOVING_LASER_LR_DOWN_ID ||
                id == Const.LASER_LEFT_ID || id == Const.MOVING_LASER_UD_LEFT_ID ||
                id == Const.LASER_RIGHT_ID || id == Const.MOVING_LASER_UD_RIGHT_ID) {
            beam?.render()
        }
    }

    fun dispose() {
        sprite.getTexture().dispose();
        beam?.dispose()
    }

    fun bounds(): Rectangle {
        return Rectangle(x, y, size.x, size.y)
    }

    fun centerPos(): Vector2 {
        return Vector2(x + (size.x / 2), y + (size.y / 2))
    }

}
