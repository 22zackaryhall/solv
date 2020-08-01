package entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import util.Gfx
import java.util.*
import kotlin.concurrent.schedule

class LevelSwitcher(p: Vector2, game: BasicGame, var exitPath: String, var specificLocation: Boolean, var targetLocation: Vector2) : Triggerable(0, false, game) {

    val t = Timer()

    val levitationTimer = Timer()
    var finishedLevitating = true

    init {
        pos = p
        size.set(Const.TILESIZE + 0f, Const.TILESIZE + 0f)
    }

    override fun update() {
        // When in space_2
//        if (Const.CURRENT_FILE == "space_2" && Const.FINISHEDHANDSTUFF) {
//            if (bounds().contains(Vector2(game.player.pos.x + (game.player.size.x / 2f), game.player.pos.y + (game.player.size.y / 6)))) {
//                // Credits roll
//                if (exitPath == "end") {
//                    Const.CREDITS = true;
//                } else {
//                    nextLevel();
//                }
//            }
//        }

        if (bounds().contains(Vector2(game.player.pos.x + (game.player.size.x / 2f), game.player.pos.y + (game.player.size.y / 6)))) {
            // Credits roll
            if (exitPath == "end") {
                Const.CREDITS = true
                // Enter space_0 if not beaten
            } else if (exitPath == "space_0") {
                if (!Const.COMPLETEDSPACE) {
                    nextLevel()
                }

            } // Levitate player when in tower
            else if (exitPath.startsWith("tower") && Const.CURRENT_FILE != "overworld_6" && !game.player.levitating) {
                game.player.levitating = true
                finishedLevitating = false
                game.player.canMove = false
                game.shadow.canMove = false
                game.player.spritePos = this.pos.cpy()

                levitationTimer.schedule(1400) {
                    finishedLevitating = true;
                }

                // Change level after levitated in tower
            } else if (finishedLevitating) {
                nextLevel();
                if (game.player.levitating) game.player.levitating = false

            } else if(Const.CURRENT_FILE.startsWith("overworld")) {
                nextLevel()
            }
        }

        // take player to space_3 from overworld_4 if space world completed
        if (Const.COMPLETEDSPACE &&
                (bounds().contains(Vector2(game.player.pos.x + (game.player.size.x / 2f), game.player.pos.y + (game.player.size.y / 6)))) &&
                exitPath == "space_0") {
            exitPath = "space_3" // exitPath is reset on level gen anyways
            nextLevel()
        }
    }

    fun nextLevel() {
        Const.MUTE_SAFE = false;

        // delay to let player release movement keys
        game.screenHidden = false;
        game.screenHiderFadingIn = true;

        game.map.dispose();
        game.loadMap(exitPath);

        // ensures player uses data for collision/etc in new map
        game.player.w = game.map
        game.shadow.w = game.map

        game.player.invincible = true
        game.shadow.invincible = true

        if (!specificLocation)
            game.player.pos.set(game.map.spawnPos)
        else
            game.player.pos.set(targetLocation)

        game.shadow.pos.set(game.map.shadowspawnPos)

        game.player.spritePos.set(game.player.pos)
        game.shadow.spritePos.set(game.shadow.pos)

        game.player.invincible = false
        game.shadow.invincible = false

        game.player.canMove = false
        game.shadow.canMove = false

        t.schedule(200) {
            game.player.canMove = true
            game.shadow.canMove = true
            Const.MUTE_SAFE = true
        }

        Gfx.cam.position.x = game.player.pos.x
        Gfx.cam.position.y = game.player.pos.y
    }

    override fun render() {
        if (Const.debugging) {
            Gfx.setColor(Color(255f / 255f, 235 / 255f, 59 / 255f, 1f))
            Gfx.fillRect(pos.x, pos.y, size.x, size.y)
        }
    }

    override fun dispose() {
        alive = false
    }

    override fun trigger() {

    }

}
