package entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import util.Gfx

class SolutionTile(p: Vector2, id: Int, game: BasicGame, previouslySolved: Boolean, invisible: Boolean) : Triggerable(id, false, game) {

    var sprite: Sprite

// NOTE: invisible tile id is id of object that needs to be activated in order for the
// solutiontile to be activated. Once activated, stepping on it causes a save

// solutiontile 3 needs whatever id you can get that's not taken
// it just saves when you touch it

    // returns if *on == true) when the game was saved
    var previouslySolved = false
    var invisible = false
    var activated = false // when activated it will save on touch
    var solutiontile3 = false

    init {
        this.previouslySolved = previouslySolved
        this.invisible = invisible
        pos = p
        size.set(Const.TILESIZE.toFloat(), Const.TILESIZE.toFloat())
        sprite = Sprite(Texture("tiles/solution_tile.png"))
        if (previouslySolved)
            on = true
    }

    override fun update() {
        if (invisible) {
            if (!on && game.player.bounds().overlaps(bounds()) && activated) {
                on = true
            }
        } else {
            if (!on && game.player.bounds().overlaps(bounds())) {
                on = true
            }
        }
    }

    override fun render() {
        if (!invisible) {
            Gfx.sb.color = Color.WHITE
            if (on)
                Gfx.sb.color = Color.RED
            Gfx.sb.begin()
            Gfx.sb.draw(sprite, pos.x, pos.y, size.x, size.y)
            Gfx.sb.end()
        }
    }

    override fun dispose() {
        sprite.texture.dispose()
    }

    override fun trigger() {

    }
}
