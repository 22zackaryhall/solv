package entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import core.Const
import util.Gfx

class Spawn(startPos: Vector2) : Entity() {

    init {
        pos.x = startPos.x
        pos.y = startPos.y
        size.x = Const.TILESIZE.toFloat()
        size.y = Const.TILESIZE.toFloat()
    }

    override fun update() {}

    override fun render() {
        if (Const.debugging) {
            Gfx.setColor(Color.PURPLE)
            Gfx.fillRect(pos.x, pos.y, size.x, size.y)
        }
    }

    override fun dispose() {
        alive = false
    }

    fun checkCollision(dir: String?) {}

}