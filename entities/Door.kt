package entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import util.Gfx

class Door(p: Vector2, id: Int, o: Boolean, game: BasicGame) : Triggerable(id, o, game) {

    var sprite: Sprite

    var maxSize: Vector2 = Vector2()

    var direction = 'd'
    var minSize = 2f

    var animationSpeed = 6f

    var activationSound: Sound
    var playedSound = false

    init {
        pos = p
        size.set(Const.TILESIZE.toFloat(), Const.TILESIZE.toFloat())
        this.on = o
        defaultState = on
        sprite = Sprite(Texture("sprites/door_lr.png"))
        sprite.setSize(size.x, size.y)
        activationSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Car_Door_Locking.mp3"))
    }

    override fun update() {
        if (on != defaultState && !playedSound) {
            if (Const.MUTE_SAFE) {
                activationSound.play()
            }
            playedSound = true
        }

        if (on == defaultState && playedSound) {
            if (Const.MUTE_SAFE) {
                activationSound.play()
            }
            playedSound = false
        }
    }

    override fun render() {

        Gfx.sb.color = Color.WHITE
        Gfx.sb.begin()
        Gfx.sb.draw(sprite, pos.x, pos.y, size.x, size.y)
        Gfx.sb.end()

        if (bounds().overlaps(game.player.bounds()) || bounds().overlaps(game.shadow.bounds())) {
            on = true
        } else {
            if (direction == 'd' && on == defaultState && size.y < maxSize.y) {
                size.y += animationSpeed
            } else if (direction == 'd' && on != defaultState && size.y > minSize)
                size.y -= animationSpeed

            if (direction == 'u' && on == defaultState && size.y < maxSize.y) {
                size.y += animationSpeed
            } else if (direction == 'u' && on != defaultState && size.y > minSize)
                size.y -= animationSpeed

            if (direction == 'l' && on == defaultState && size.x < maxSize.x) {
                size.x += animationSpeed
            } else if (direction == 'l' && on != defaultState && size.x > minSize)
                size.x -= animationSpeed

            if (direction == 'r' && on == defaultState && size.x < maxSize.x) {
                size.x += animationSpeed
            } else if (direction == 'r' && on != defaultState && size.x > minSize)
                size.x -= animationSpeed
        }
    }

    override fun dispose() {
        alive = false
        activationSound.dispose()
    }

    override fun trigger() {}

    fun resize(d: Char, length: Int) {
        direction = d
        if (direction == 'r') {
            maxSize.x = Const.TILESIZE * length + 0f
            if (on)
                size.x = minSize
            else
                size.x = maxSize.x
            if (length == 1)
                sprite = Sprite(Texture("sprites/door_lr_1x1.png"))
            else
                sprite = Sprite(Texture("sprites/door_lr.png"))
            sprite.setSize(size.x, size.y)
        }
        if (direction == 'l') {
            maxSize.x = Const.TILESIZE * length + 0f
            if (on)
                size.x = minSize
            else
                size.x = maxSize.x
            size.y = Const.TILESIZE + 0f
            pos.x -= maxSize.x
            if (length == 1)
                sprite = Sprite(Texture("sprites/door_lr_1x1.png"))
            else
                sprite = Sprite(Texture("sprites/door_lr.png"))
            sprite.setSize(size.x, size.y)

        }
        if (direction == 'u') {
            size.x = Const.TILESIZE + 0f
            maxSize.y = Const.TILESIZE * length + 0f
            if (on)
                size.y = minSize
            else
                size.y = maxSize.y
            if (length == 1)
                sprite = Sprite(Texture("sprites/door_ud_1x1.png"))
            else
                sprite = Sprite(Texture("sprites/door_ud.png"))
            sprite.setSize(size.x, size.y)

        }
        if (direction == 'd') {
            size.x = Const.TILESIZE + 0f
            maxSize.y = Const.TILESIZE * length + 0f
            if (on)
                size.y = minSize
            else
                size.y = maxSize.y
            pos.y -= maxSize.y - Const.TILESIZE
            if (length == 1)
                sprite = Sprite(Texture("sprites/door_ud_1x1.png"))
            else
                sprite = Sprite(Texture("sprites/door_ud.png"))
            sprite.setSize(size.x, size.y)
        }
    }

    override fun bounds(): Rectangle {
        return Rectangle(pos.x, pos.y, size.x, size.y)
    }

}
