package entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import util.Gfx

class LaserButton(p: Vector2, id: Int, on: Boolean, game: BasicGame) : Triggerable(id, on, game) {


    var ont: Texture
    var offt: Texture

    init {
        pos = p
        size.set(Const.TILESIZE.toFloat(), Const.TILESIZE.toFloat())

        ont = Texture(Gdx.files.internal("sprites/laser_button_on.png"))
        offt = Texture(Gdx.files.internal("sprites/laser_button_off.png"))

    }

    override fun update() {
        on = false

        // LASER BEAMS TOUCHING //
        for (t in game.map.currentEntityFloor.entities) {
            // laser shooter entity
            if (t.id == Const.LASER_UP_ID || t.id == Const.LASER_DOWN_ID || t.id == Const.LASER_RIGHT_ID || t.id == Const.LASER_LEFT_ID || t.id == Const.MOVING_LASER_LR_DOWN_ID || t.id == Const.MOVING_LASER_LR_UP_ID || t.id == Const.MOVING_LASER_UD_RIGHT_ID || t.id == Const.MOVING_LASER_UD_LEFT_ID) {
                // laser beams touching?
                // laser beams produced by reflection touching?
                if (t.beam?.temp!!.overlaps(this.bounds()))
                    on = true

                // NOTE: try to have a for loop do this instead?
                // currently i make 2 if statements for every iteration beams. Currently it can check collision of a beam that has
                // reflected 2 times max. I could just hardcode it texture to about 15 times and in teh case that there is
                // a level editor, people will be like "oh, it stops at this amount so it doesn't get slow or something". :) i smart
                if (t.beam?.beam?.temp != null) {
                    if (t.beam?.beam!!.temp.overlaps(this.bounds()))
                        on = true
                    else if (t.beam?.beam?.beam?.temp != null)
                        if (t.beam?.beam?.beam!!.temp.overlaps(this.bounds()))
                            on = true
                }
            }
        }

        if (on) {
            for (e in game.map.trigerables) {
                if (e.id == this.id && e.javaClass != LaserButton::class.java && doorCanBlockHere(e))
                    e.on = !e.defaultState
            }
        } else {
            for (e in game.map.trigerables)
                if (e.id == this.id && e.javaClass != LaserButton::class.java && doorCanBlockHere(e)) {
                    e.on = e.defaultState
                }
        }

    }

    // checks if door can be activated in current position, meaning
    fun doorCanBlockHere(e: Triggerable): Boolean {
        for (t in game.map.currentEntityFloor.entities)
            if (t.id != Const.EMPTY_ID && t.bounds().overlaps(e.bounds()))
                return false
        for (t in game.map.trigerables)
            if (t != e && t.bounds().overlaps(e.bounds()))
                return false
        if (e.bounds().overlaps(game.player.bounds()))
            return false
        if (e.bounds().overlaps(game.shadow.bounds()))
            return false
        return true
    }

    override fun render() {
        Gfx.sb.setColor(Color.WHITE)
        Gfx.sb.begin()
        if (on)
            Gfx.sb.draw(ont, pos.x, pos.y, size.x, size.y)
        else
            Gfx.sb.draw(offt, pos.x, pos.y, size.x, size.y)
        Gfx.sb.end()
    }

    override fun dispose() {
        ont.dispose()
        offt.dispose()
        alive = false
    }

    override fun trigger() {
        for (e in game.map.trigerables) {
            if (e != this && e.id == this.id) {
                e.on = this.on
                break
            }
        }
    }

}
