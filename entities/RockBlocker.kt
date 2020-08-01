package entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import util.Gfx

class RockBlocker(p: Vector2, id: Int, game: BasicGame, var neededIds: ArrayList<Int>) : Triggerable(id, false, game) {

    var sprite: Sprite

    init {
        pos = p
        size.set(Const.TILESIZE.toFloat(), Const.TILESIZE.toFloat())
        sprite = Sprite(Texture("tiles/rockblocker_lr.png"))
    }

    override fun update() {

    }

    override fun render() {
        Gfx.sb.color = Color.WHITE
        Gfx.sb.begin()
        Gfx.sb.draw(sprite, pos.x, pos.y, size.x, size.y)
        Gfx.sb.end()
    }

    override fun dispose() {
        sprite.texture.dispose()
    }

    override fun trigger() {}

}
