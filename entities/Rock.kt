package entities

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import util.Gfx

class Rock(p: Vector2, game: BasicGame) : Triggerable(6969, false, game) {

    var sprite: Sprite

    init {
        pos = p.cpy()
        size.set(Const.TILESIZE.toFloat(), Const.TILESIZE.toFloat())

        sprite = Sprite(Texture("tiles/rock0.png"))
        sprite.setPosition(pos.x, pos.y)
        sprite.setSize(size.x, size.y)
        spritePos = pos.cpy()
    }

    override fun update() {
        spritePos.x += (pos.x - spritePos.x) * Const.SPRITE_LERP
        spritePos.y += (pos.y - spritePos.y) * Const.SPRITE_LERP
    }

    override fun render() {
        Gfx.drawSprite(sprite, spritePos.x, spritePos.y, size.x, size.y)
    }

    override fun dispose() {
        sprite.texture.dispose()
    }

    override fun trigger() {}
}