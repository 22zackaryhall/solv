package entities;

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import util.Gfx

class BookPage(p: Vector2, game: BasicGame, nid: Int) : Triggerable(nid, false, game) {

    var sprite: Sprite
    var text = "There is no terxt"

    init {
        pos = p.cpy()
        size.set(Const.TILESIZE.toFloat(), Const.TILESIZE.toFloat())

        sprite = Sprite(Texture("sprites/page.png"))
        sprite.setPosition(pos.x, pos.y)
        sprite.setSize(size.x, size.y)
        spritePos = pos.cpy()
    }

    override fun update() {}

    override fun render() {
        if (alive) {

            Gfx.sb.begin()
            Gfx.sb.draw(sprite, spritePos.x, spritePos.y, size.x, size.y)
            Gfx.sb.end()
        }
    }

    override fun dispose() {
        sprite.texture.dispose()
    }

    override fun trigger() {}
}
