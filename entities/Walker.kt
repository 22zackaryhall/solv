package entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import util.Gfx
import java.util.*
import kotlin.concurrent.schedule


class Walker(p: Vector2, id: Int, on: Boolean, game: BasicGame, var directionChar: String) : Triggerable(id, on, game) {

    var view: Rectangle = Rectangle()
    val maxLength = Const.TILESIZE * 15

    var t = Timer()

    init {
        pos = p
        spritePos.set(pos.x, pos.y)
        size.set(Const.TILESIZE.toFloat(), Const.TILESIZE.toFloat())
        this.on = on

        view.height = .5f
        view.width = .5f

        vel = Const.TILESIZE + 0f

    }

    fun step() {
        t.schedule(Const.MOVEMENT_DELAY * 2) {
            if (alive) {
                if (on && directionChar == "u") {
                    pos.y += vel
                    checkBounds(directionChar)
                    step()
                }
                if (on && directionChar == "d") {
                    pos.y -= vel
                    checkBounds(directionChar)
                    step()
                }
                if (on && directionChar == "l") {
                    pos.x -= vel
                    checkBounds(directionChar)
                    step()
                }
                if (on && directionChar == "r") {
                    pos.x += vel
                    checkBounds(directionChar)
                    step()
                }
            }
        }
    }

    override fun update() {

        spritePos.x += (pos.x - spritePos.x) * Const.SPRITE_LERP
        spritePos.y += (pos.y - spritePos.y) * Const.SPRITE_LERP


        // facing left
        if (directionChar == "l") {
            view.y = centerPos().y - view.height / 2f

            view.width = 0f
            for (i in 0..maxLength) {
                view.width = i + 0f
                view.x = spritePos.x - view.width

                var found = false
                for (e in game.map.currentEntityFloor.entities)
                    if (e.id != Const.EMPTY_ID && view.overlaps(e.bounds()))
                        found = true
                for (e in game.map.trigerables)
                    if (e.javaClass != Button::class.java && view.overlaps(e.bounds()))
                        found = true
                if (view.overlaps(game.player.spriteBounds())) {
                    found = true
                    if (!on) {
                        on = true
                        step()
                    }
                }
                if (found)
                    break
            }
        }


        // facing down
        if (directionChar == "d") {
            view.x = centerPos().x - view.width / 2f

            view.height = 0f
            for (i in 0..maxLength) {
                view.height = i + 0f
                view.y = spritePos.y - view.height

                var found = false
                for (e in game.map.currentEntityFloor.entities)
                    if (e.id != Const.EMPTY_ID && view.overlaps(e.bounds()))
                        found = true
                for (e in game.map.trigerables)
                    if (e.javaClass != Button::class.java && view.overlaps(e.bounds()))
                        found = true
                if (view.overlaps(game.player.spriteBounds())) {
                    found = true
                    if (!on) {
                        on = true
                        step()
                    }
                }
                if (found)
                    break
            }
        }


    }

    fun checkBounds(direction: String) {

        if (direction == "l") {
            if (bounds().overlaps(game.player.bounds()))
                pos.x += vel
            else {
                // Check for entities
                for (e in game.map.currentEntityFloor.entities)
                    if (e.id != Const.EMPTY_ID && e.bounds().overlaps(bounds()))
                        pos.x += vel

                // Check for trigerables
                for (e in game.map.trigerables) {
                    if (e != this && !e.on && e.javaClass != Button::class.java && e.pos.x == pos.x &&
                            e.pos.y == pos.y) {
                        pos.x += vel
                        println(e.javaClass)
                    }
                }
            }
        }

        if (direction == "r") {
            if (bounds().overlaps(game.player.bounds()))
                pos.x -= vel
            else
                for (e in game.map.currentEntityFloor.entities)
                    if (e.id != Const.EMPTY_ID && e.bounds().overlaps(bounds()))
                        pos.x -= vel
        }
        if (direction == "u") {
            if (bounds().overlaps(game.player.bounds()))
                pos.y -= vel
            else
                for (e in game.map.currentEntityFloor.entities)
                    if (e.id != Const.EMPTY_ID && e.bounds().overlaps(bounds()))
                        pos.y -= vel
        }

        if (direction == "d") {
            if (bounds().overlaps(game.player.bounds()))
                pos.y += vel
            else
                for (e in game.map.currentEntityFloor.entities)
                    if (e.id != Const.EMPTY_ID && e.bounds().overlaps(bounds()))
                        pos.y += vel
        }
    }

    override fun render() {
        Gfx.setColor(Color(0f, 150 / 255f, 136 / 255f, 1f))
        Gfx.fillRect(spritePos.x, spritePos.y, size.x, size.y)

        Gfx.setColor(Color.RED)
        Gfx.fillRect(view.x, view.y, view.width, view.height)
    }

    override fun dispose() {
        alive = false
    }

    override fun trigger() {}

}
