package entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import util.Gfx
import java.util.*

class AreaDoor(p: Vector2, game: BasicGame, nsize: Vector2, needed: ArrayList<Int>) : Triggerable(0, false, game) {

    var sprite: Sprite

    var reuiredSolved: ArrayList<Int> = arrayListOf()

    var activationSound: Sound
    var playedSound = false
    var open = false

    init {
        pos = p
        size.set(nsize.x, nsize.y)
        defaultState = false
        sprite = Sprite(Texture("sprites/door_lr.png"))
        sprite.setSize(size.x, size.y)
        activationSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Car_Door_Locking.mp3"))
        reuiredSolved = needed
    }

    override fun update() {
        if (open != defaultState && !playedSound) {
            if (Const.MUTE_SAFE)
                activationSound.play()
            playedSound = true
        }

        if (open == defaultState && playedSound) {
            if (Const.MUTE_SAFE)
                activationSound.play()
            playedSound = false
        }

        // See if all are solved
        var solved = 0
        for (t in game.map.trigerables) {
            if (t is SolutionTile) {
                if (reuiredSolved.contains(t.id) && t.on)
                    solved++
            }
        }
        if (solved == reuiredSolved.size)
            open = true
    }

    override fun render() {
        if (!open) {
            Gfx.sb.color = Color.WHITE
            Gfx.sb.begin()
            Gfx.sb.draw(sprite, pos.x, pos.y, size.x, size.y)
            Gfx.sb.end()
        }
    }

    override fun dispose() {
        alive = false
        activationSound.dispose()
    }

    override fun trigger() {

    }

}