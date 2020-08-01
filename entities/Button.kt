package entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import util.Gfx

class Button(p: Vector2, id: Int, on: Boolean, game: BasicGame) : Triggerable(id, on, game) {

    var sprite: Sprite

    init {
        pos = p
        size.set(Const.TILESIZE.toFloat(), Const.TILESIZE.toFloat())
        this.on = on
        this.defaultState = on
        sprite = Sprite(Texture("sprites/button.png"))
        sprite.setSize(size.x, size.y)
        sprite.setPosition(spritePos.x, spritePos.y)
    }

    override fun update() {
        on = false

        // PLAYER ON BUTTON //
        if (game.player.bounds().overlaps(bounds()) || game.shadow.bounds().overlaps(bounds())) {
            on = true
        }

        // OBJECT ON BUTTON //
        for (e in game.map.currentEntityFloor.entities)
            if (e.id != 0 && e.bounds().overlaps(bounds()))
                on = true

        // WALKER ON BUTTON //
        for (e in game.map.trigerables)
            if (e.javaClass == Walker::class.java && e.bounds().overlaps(bounds()))
                on = true

        // objects on triggerables
        // i think this deals with rocks on doors and such
        if (on) {
            for (e in game.map.trigerables) {
                if (e.id == this.id && doorCanBlockHere(e)) {
                    e.on = !e.defaultState
                }
                // enable solutiontiles if any with same id
                if (e is SolutionTile && e.id == this.id) {
                    e.activated = true
                }
            }
        } else {
            for (e in game.map.trigerables)
                if (e.id == this.id) {
                    if (e is Door && !doorCanBlockHere(e)) {
                        e.on = true
                    } else {
                        e.on = e.defaultState
                    }
                    if (e is SolutionTile) {
                        e.activated = false
                    }
                }
        }
    }

    // checks if door can be activated in current position, meaning
    fun doorCanBlockHere(e: Triggerable): Boolean {
        for (t in game.map.currentEntityFloor.entities)
            if (t.id != Const.EMPTY_ID && t.bounds().overlaps(e.bounds()))
                return false
        for (t in game.map.trigerables)
            if (t != e && t.bounds().overlaps(e.bounds())) {
                return false
            }
        if (e.bounds().overlaps(game.player.bounds())) {
            return false
        }
        if (e.bounds().overlaps(game.shadow.bounds()))
            return false
        return true
    }

    override fun render() {
        if (on)
            Gfx.sb.color = Color(1f, 152 / 255f, 0f, 1f)
        else
            Gfx.sb.color = Color(183 / 255f, 28 / 255f, 28 / 255f, 1f)
//        Gfx.fillRect(pos.x, pos.y, size.x, size.y)

        Gfx.sb.begin()
        Gfx.sb.draw(sprite, pos.x, pos.y, size.x, size.y)
//        sprite.draw(Gfx.sb)
        Gfx.sb.end()
    }

    override fun dispose() {
        alive = false
    }

    override fun trigger() {}

}
