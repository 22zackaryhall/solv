package entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import core.BasicGame
import core.Const
import core.world
import math.AABB
import util.Gfx
import util.Inputer
import java.util.*
import kotlin.concurrent.schedule


class Hero(var basicGame: BasicGame, ww: world, poss: Vector2, size: Vector2) : Entity() {

    var w: world

    var step: Music
    var pushPullSound: Music
    var hand: Rectangle
    var lastDir: Vector2

    var pushpulling = false
    var pressedSpace = false
    var shadowHero = false
    var grabbedSomething = false
    var killed = false
    var ass = false

    var thingBeingGrabbed: StaticEntity? = null

    var textureAlpha = 1f

    // what lastDir should have been but lastDir is now a vector tied texture in more complex code
    // this is the ACTUAL last direction the player was facing
    var previousDir: Vector2

    var canMove = true

    var sprite: Sprite
    var up_texture: Texture
    var down_texture: Texture
    var left_texture: Texture
    var right_texture: Texture

    var up_grab_texture: Texture
    var down_grab_texture: Texture
    var left_grab_texture: Texture
    var right_grab_texture: Texture

    var oceanSprite: Sprite // sprite of ocean with ocean shader that will be visible on camera and follow player
    var vertexShader: String? = null
    var fragmentShader: String? = null
    var shaderProgram: ShaderProgram? = null

    var levitating = false

    init {
        this.g = basicGame
        this.pos = poss
        spritePos.set(pos.x, pos.y)
        this.size = size
        this.w = ww
        vel = Const.TILESIZE + 0f

        hand = Rectangle(0f, 0f, size.x / 2, size.y / 2)
        lastDir = Vector2()
        previousDir = Vector2()

        up_texture = Texture(Gdx.files.internal("sprites/player_up_idle.png"))
        down_texture = Texture(Gdx.files.internal("sprites/player_down_idle.png"))
        left_texture = Texture(Gdx.files.internal("sprites/player_left_idle.png"))
        right_texture = Texture(Gdx.files.internal("sprites/player_right_idle.png"))

        up_grab_texture = Texture(Gdx.files.internal("sprites/player_up_grab.png"))
        down_grab_texture = Texture(Gdx.files.internal("sprites/player_down_grab.png"))
        left_grab_texture = Texture(Gdx.files.internal("sprites/player_left_grab.png"))
        right_grab_texture = Texture(Gdx.files.internal("sprites/player_right_grab.png"))

        step = Gdx.audio.newMusic(Gdx.files.internal("sounds/walking/step_0.ogg"))
        step.volume = .02f

        pushPullSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/push_pull.mp3"))

        lastDir.x = 0f
        lastDir.y = 1f

        sprite = Sprite(up_texture)

        oceanSprite = Sprite(Texture("white_pixel.png"))
        vertexShader = Gdx.files.internal("shaders/ocean.v").readString()
        fragmentShader = Gdx.files.internal("shaders/ocean.f").readString()
        shaderProgram = ShaderProgram(vertexShader, fragmentShader)
        ShaderProgram.pedantic = false // don't let shaders crash program if they don't compile
    }

    // Change textures to be that of the shadow her
    fun initShadow() {
        up_texture = Texture(Gdx.files.internal("sprites/red_up_idle.png"))
        down_texture = Texture(Gdx.files.internal("sprites/red_down_idle.png"))
        left_texture = Texture(Gdx.files.internal("sprites/red_left_idle.png"))
        right_texture = Texture(Gdx.files.internal("sprites/red_right_idle.png"))

        up_grab_texture = Texture(Gdx.files.internal("sprites/red_up_grab.png"))
        down_grab_texture = Texture(Gdx.files.internal("sprites/red_down_grab.png"))
        left_grab_texture = Texture(Gdx.files.internal("sprites/red_left_grab.png"))
        right_grab_texture = Texture(Gdx.files.internal("sprites/red_right_grab.png"))
    }

    fun renderOceanSprite() {
        time += Gdx.graphics.deltaTime

        shaderProgram!!.begin()
        shaderProgram!!.setUniformf("time", time)

        Gfx.sb.shader = shaderProgram
        Gfx.sb.begin()
        Gfx.sb.draw(oceanSprite, Gfx.cam.position.x - Gfx.cam.viewportWidth / 2, Gfx.cam.position.y - Gfx.cam.viewportHeight / 2, Gfx.cam.viewportWidth, Gfx.cam.viewportHeight)
        Gfx.sb.end()
        Gfx.sb.shader = null
        shaderProgram!!.end()

    }

    override fun update() {
        invincible = Const.debugging

        if (!killed) {
            if (!frozen) {
                keyDir()
                dir.nor()
                movement()
            }

            // sets position of hands based off direction facing
            if (lastDir.x > 0)
                hand.x = pos.x + size.x
            else if (lastDir.x < 0)
                hand.x = pos.x - hand.width
            else
                hand.x = pos.x + (size.x / 2) - (hand.width / 2)

            if (lastDir.y > 0)
                hand.y = pos.y + size.y
            else if (lastDir.y < 0)
                hand.y = pos.y - hand.height
            else
                hand.y = pos.y + (hand.height / 2)

            pushpulling = false

            // push and pull objects
            if (Inputer.pressedKey(Input.Keys.SPACE) && !pressedSpace && !frozen && !shadowHero && canMove) {
                grabbedSomething = false
                for (t in w.currentEntityFloor.entities) {
                    if ((t.id == Const.ROCK_ID || (t.id == Const.BONE_ID && t.movableBone))
                            || t.id == Const.REFLECTOR_DOWNRIGHT_ID || t.id == Const.REFLECTOR_DOWNLEFT_ID
                            || t.id == Const.REFLECTOR_UPRIGHT_ID || t.id == Const.REFLECTOR_UPLEFT_ID) {
                        if (hand.overlaps(t.bounds())) {
                            thingBeingGrabbed = t
                            grabbedSomething = true
                            pushpulling = true

                        }
                    }
                }
                if (!grabbedSomething)
                    pressedSpace = true
            }

            // Set texture to grabbing type based off direction facing
            if (Inputer.pressedKey(Input.Keys.SPACE) && pushpulling) {
                if (lastDir.x > 0 && sprite.texture != right_grab_texture) {
                    sprite.texture = right_grab_texture;
                } else if (lastDir.x < 0 && sprite.texture != left_grab_texture) {
                    sprite.texture = left_grab_texture;
                } else if (lastDir.y > 0 && sprite.texture != up_grab_texture) {
                    sprite.texture = up_grab_texture;
                } else if (lastDir.y < 0 && sprite.texture != down_grab_texture) {
                    sprite.texture = down_grab_texture;
                }
            }
            // Default texture when not holding on to something
            else {
                if (lastDir.x > 0 && sprite.texture != right_texture) {
                    sprite.texture = right_texture;
                } else if (lastDir.x < 0 && sprite.texture != left_texture) {
                    sprite.texture = left_texture;
                } else if (lastDir.y > 0 && sprite.texture != up_texture) {
                    sprite.texture = up_texture;
                } else if (lastDir.y < 0 && sprite.texture != down_texture) {
                    sprite.texture = down_texture;
                }
            }

            if (!Inputer.pressedKey(Input.Keys.SPACE))
                pressedSpace = false
        }

        if (killed && textureAlpha <= 0.0f) {
            basicGame.loadMap(Const.CURRENT_FILE)
            w = basicGame.map
            pos = basicGame.map.spawnPos
            spritePos.set(pos.x, pos.y)
            killed = false
            textureAlpha = 1f
            resetLevel()
        }
    }

    fun movement() {
        // PUSH //
        // checked first so it uses original directions
        if (pushpulling && Inputer.tappedKey(Input.Keys.UP) && previousDir.y == 1f && thingBeingGrabbed != null && canMove) {
            thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(vel))
            checkCollisionPushPullEntity("u")
        }
        // texture
        if (dir.y == 1f) {

            // resets direction. it will be set back
            dir.x = previousDir.x
            dir.y = 0f
            lastDir.x = 0f
            lastDir.y = 0f

            // if you're facing texture instead
            if (thingBeingGrabbed != null && hand.y > pos.y && hand.x > pos.x && hand.x < pos.x + size.x)
                lastDir.y = 1f
            // if you're facing down instead
            if (thingBeingGrabbed != null && hand.y < pos.y && hand.x > pos.x && hand.x < pos.x + size.x)
                lastDir.y = -1f
            if (thingBeingGrabbed != null && hand.x < pos.x)
                lastDir.x = -1f
            if (thingBeingGrabbed != null && hand.x >= pos.x + size.x)
                lastDir.x = 1f

            // normal movement
            val previousY = pos.y
            if (!pushpulling) {
                dir.x = 0f
                dir.y = 1f
                lastDir.x = 0f
                lastDir.y = dir.y
                pos.y += dir.y * vel
            }

            checkCollision("u")
            if (pos.y != previousY) {
                step.stop()
                step.play()
            }

        }
        if (pushpulling && previousDir.y == 1f && Inputer.tappedKey(Input.Keys.UP)) {
            dir.x = 0f
            dir.y = 1f
            lastDir.x = 0f
            lastDir.y = dir.y
            pos.y += vel
            checkCollision("u")
            if (shadowHero) println("SDFSD")
        }

        // down
        // checked first so it uses original directions
        if (pushpulling && Inputer.tappedKey(Input.Keys.DOWN) && previousDir.y == -1f && thingBeingGrabbed != null && canMove) {
            thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(-vel))
            checkCollisionPushPullEntity("d")
        }
        if (dir.y == -1f) {

            // resets direction. it will be set back
            dir.x = previousDir.x
            dir.y = 0f
            lastDir.x = 0f
            lastDir.y = 0f

            // if you're facing texture instead
            if (thingBeingGrabbed != null && hand.y > pos.y && hand.x > pos.x && hand.x < pos.x + size.x)
                lastDir.y = 1f
            // if you're facing down instead
            if (thingBeingGrabbed != null && hand.y < pos.y && hand.x > pos.x && hand.x < pos.x + size.x)
                lastDir.y = -1f
            if (thingBeingGrabbed != null && hand.x < pos.x)
                lastDir.x = -1f
            if (thingBeingGrabbed != null && hand.x >= pos.x + size.x)
                lastDir.x = 1f

            // normal movement
            val previousY = pos.y
            if (!pushpulling) {
                dir.x = 0f
                dir.y = -1f
                lastDir.x = 0f
                lastDir.y = dir.y
                pos.y += dir.y * vel
            }

            checkCollision("d")
            if (pos.y != previousY) {
                step.stop()
                step.play()
            }

        }
        if (pushpulling && previousDir.y == -1f && Inputer.tappedKey(Input.Keys.DOWN)) {
            dir.x = 0f
            dir.y = -1f
            lastDir.x = 0f
            lastDir.y = dir.y
            pos.y += dir.y * vel
            checkCollision("d")
        }
        // left
        // checked first so it uses original directions
        if (pushpulling && Inputer.tappedKey(Input.Keys.LEFT) && previousDir.x == -1f && thingBeingGrabbed != null && canMove) {
            thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(-vel))
            checkCollisionPushPullEntity("l")
        }
        if (dir.x == -1f) {

            // resets direction. it will be set back
            dir.x = 0f
            dir.y = previousDir.y
            lastDir.x = 0f
            lastDir.y = 0f

            // if you're facing texture instead
            if (thingBeingGrabbed != null && hand.y > pos.y && hand.x > pos.x && hand.x < pos.x + size.x)
                lastDir.y = 1f
            // if you're facing down instead
            if (thingBeingGrabbed != null && hand.y < pos.y && hand.x > pos.x && hand.x < pos.x + size.x)
                lastDir.y = -1f
            if (thingBeingGrabbed != null && hand.x < pos.x)
                lastDir.x = -1f
            if (thingBeingGrabbed != null && hand.x >= pos.x + size.x)
                lastDir.x = 1f

            // normal movement
            val previousX = pos.x
            if (!pushpulling) {
                dir.x = -1f
                dir.y = 0f
                lastDir.x = dir.x
                lastDir.y = 0f
                pos.x += dir.x * vel
            }

            checkCollision("l")

            if (pos.x != previousX) {
                step.stop()
                step.play()
            }

        }
        if (pushpulling && previousDir.x == -1f && Inputer.tappedKey(Input.Keys.LEFT)) {
            dir.x = -1f
            dir.y = 0f
            lastDir.x = dir.x
            lastDir.y = 0f
            pos.x += dir.x * vel
            checkCollision("l")
        }
        // right
        // checked first so it uses original directions
        if (pushpulling && Inputer.tappedKey(Input.Keys.RIGHT) && previousDir.x == 1f && thingBeingGrabbed != null && canMove) {
            thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(vel))
            checkCollisionPushPullEntity("r")
        }
        if (dir.x == 1f) {

            // resets direction. it will be set back
            dir.x = 0f
            dir.y = previousDir.y
            lastDir.x = 0f
            lastDir.y = 0f

            // if you're facing texture instead
            if (thingBeingGrabbed != null && hand.y > pos.y && hand.x > pos.x && hand.x < pos.x + size.x)
                lastDir.y = 1f
            // if you're facing down instead
            if (thingBeingGrabbed != null && hand.y < pos.y && hand.x > pos.x && hand.x < pos.x + size.x)
                lastDir.y = -1f
            if (thingBeingGrabbed != null && hand.x < pos.x)
                lastDir.x = -1f
            if (thingBeingGrabbed != null && hand.x >= pos.x + size.x)
                lastDir.x = 1f

            // normal movement
            val previousX = pos.x
            if (!pushpulling) {
                dir.x = 1f
                dir.y = 0f
                lastDir.x = dir.x
                lastDir.y = 0f
                pos.x += dir.x * vel
            }

            checkCollision("r")

            if (pos.x != previousX) {
                step.stop()
                step.play()
            }
        }
        if (pushpulling && previousDir.x == 1f && Inputer.tappedKey(Input.Keys.RIGHT)) {
            dir.x = 1f
            dir.y = 0f
            lastDir.x = dir.x
            lastDir.y = 0f
            pos.x += dir.x * vel
            checkCollision("r")
        }

        // PULL //
        if (pushpulling) {
            // texture
            if (Inputer.tappedKey(Input.Keys.UP) && previousDir.y == -1f && canMove) {
                pos.y += vel

                checkCollision("u")
                if (pushpulling && thingBeingGrabbed != null) {
                    thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(vel))
                    checkCollisionPushPullEntity("u")
                    pushPullSound.stop()
                    pushPullSound.play()
                }
            }
            // down
            if (Inputer.tappedKey(Input.Keys.DOWN) && previousDir.y == 1f && canMove) {
                pos.y -= vel
                checkCollision("d")
                if (pushpulling && thingBeingGrabbed != null) {
                    thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(-vel))

                    checkCollisionPushPullEntity("d")
                }
            }
            // left
            if (Inputer.tappedKey(Input.Keys.LEFT) && previousDir.x == 1f && canMove) {
                pos.x -= vel
                checkCollision("l")
                if (pushpulling && thingBeingGrabbed != null) {
                    thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(-vel))
                    checkCollisionPushPullEntity("l")
                }
            }
            // right
            if (Inputer.tappedKey(Input.Keys.RIGHT) && previousDir.x == -1f && canMove) {
                pos.x += vel
                checkCollision("r")
                if (pushpulling && thingBeingGrabbed != null) {
                    thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(vel))
                    checkCollisionPushPullEntity("r")
                }
            }
        }
    }

    var time = 700f
    override fun render() {
        if (levitating) {
            spritePos.y += 5f
            sprite.texture = up_texture
        } else {
            spritePos.x += (pos.x - spritePos.x) * Const.SPRITE_LERP
            spritePos.y += (pos.y - spritePos.y) * Const.SPRITE_LERP
        }
        handSprite.x += (hand.x - handSprite.x) * Const.SPRITE_LERP
        handSprite.y += (hand.y - handSprite.y) * Const.SPRITE_LERP

        // slowly make player transparent on death
        if (killed && textureAlpha > 0.0f)
            textureAlpha -= .02f

        if (!shadowHero) {
            Gfx.sb.color = Color(1f, 1f, 1f, textureAlpha)
        }

        // draw player
        // Gfx.sb.begin()
        // Gfx.sb.draw(texture, spritePos.x, spritePos.y, size.x, size.y)
        // Gfx.sb.end()

        Gfx.sb.begin()
        Gfx.sb.draw(sprite, spritePos.x, spritePos.y, size.x, size.y)
        Gfx.sb.end()

        // reset color
        //        Gfx.sb.color = Color.WHITE

        // draw bounds
        if (Const.debugging) {
            Gfx.setColor(Color.RED)
            Gfx.drawRect(bounds().x, bounds().y, bounds().width, bounds().height)

            Gfx.setColor(Color.BLUE)
            Gfx.drawRect(pos.x, pos.y, size.x, size.y)
        }

    }

    var handSprite: Vector2 = Vector2()

    fun renderHand() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gfx.sr.begin(ShapeRenderer.ShapeType.Filled)

        for (e in basicGame.map.currentEntityFloor.entities)
            if ((e.id == Const.ROCK_ID || (e.id == Const.BONE_ID && e.movableBone)) && hand.overlaps(e.bounds())) {
                Gfx.setColor(Color(0f, 0f, 0f, .4f))
                //                Gfx.sr.circle(centerPos().x + (lastDir.x * Const.TILESIZE), centerPos().y + (lastDir.y * Const.TILESIZE), hand.width / 2)
                Gfx.sr.circle(e.spritePos.x + (e.size.x / 2), e.spritePos.y + (e.size.y / 2), hand.width / 2)
            }

        // draw grabbing hand
        if (pushpulling) {
            //            Gfx.setColor(Color.BLACK)
            //            Gfx.sr.circle(thingBeingGrabbed!!.centerPos().x, thingBeingGrabbed!!.centerPos().y, hand.width / 2)
        } else if (Const.debugging) {
            Gfx.sr.color = Color.LIGHT_GRAY
            Gfx.sr.rect(hand.x, hand.y, hand.width, hand.height)
        }
        Gfx.sr.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)

    }

    // treat direction as unit vector
    internal fun keyDir() {
        if (Inputer.tappedKey(Input.Keys.LEFT) && inASingleTileOnly() && !ass && canMove) {
            previousDir.x = lastDir.x
            previousDir.y = 0f

            ass = true
            var t = Timer()
            t.schedule(Const.PLAYER_MOVEMENT_DELAY) {
                ass = false
            }


            dir.x = -1f
            lastDir.x = -1f
            lastDir.y = 0f
            if (pushpulling) {
                if (previousDir.x == -1f)
                    sprite.texture = left_texture
            } else {
                if (shadowHero)
                    sprite.texture = right_texture
                else
                    sprite.texture = left_texture
            }
        } else if (Inputer.tappedKey(Input.Keys.RIGHT) && inASingleTileOnly() && !ass && canMove) {
            previousDir.x = lastDir.x
            previousDir.y = 0f

            ass = true
            var t = Timer()
            t.schedule(Const.PLAYER_MOVEMENT_DELAY) {
                ass = false
            }

            dir.x = 1f
            lastDir.x = 1f
            lastDir.y = 0f
            if (pushpulling) {
                if (previousDir.x == 1f)
                    sprite.texture = right_texture

            } else {
                if (shadowHero) {
                    sprite.texture = left_texture

                } else
                    sprite.texture = right_texture
            }
        } else
            dir.x = 0f


        if (Inputer.tappedKey(Input.Keys.UP) && inASingleTileOnly() && !ass && canMove) {
            previousDir.y = lastDir.y;
            previousDir.x = 0f;

            ass = true;
            var t = Timer();
            t.schedule(Const.PLAYER_MOVEMENT_DELAY) {
                ass = false;
            }
            dir.y = 1f;
            lastDir.y = 1f;
            lastDir.x = 0f;

            if (basicGame.player.pushpulling) {
                if (previousDir.y == 1f)
                    sprite.texture = up_texture
            } else {
                if (shadowHero)
                    sprite.texture = down_texture
                else
                    sprite.texture = up_texture
            }
        } else if (Inputer.tappedKey(Input.Keys.DOWN) && inASingleTileOnly() && !ass && canMove) {
            previousDir.y = lastDir.y
            previousDir.x = 0f

            ass = true
            var t = Timer()
            t.schedule(Const.PLAYER_MOVEMENT_DELAY) {
                ass = false
            }

            dir.y = -1f
            lastDir.y = -1f
            lastDir.x = 0f
            if (pushpulling) {
                if (previousDir.y == -1f)
                    sprite.texture = down_texture
            } else {
                if (shadowHero)
                    sprite.texture = up_texture
                else
                    sprite.texture = down_texture
            }
        } else
            dir.y = 0f

        if (shadowHero) {
            dir.x = -dir.x
            dir.y = -dir.y
        }
    }

    fun checkCollision(face: String) {
        if (!invincible) {
            // entity collision
            for (t in w.getEntityFloor(w.currentFloorId).entities)
                if (t.id != 0 && AABB.collides(t.bounds(), this.bounds())) {
                    if (face == "l")
                        pos.x += vel
                    if (face == "r")
                        pos.x -= vel
                    if (face == "u")
                        pos.y -= vel
                    if (face == "d")
                        pos.y += vel
                    // save when step on to rock blocker
                }

            for (e in w.trigerables) {
                // solutiontile 2
                if (e is SolutionTile && !e.solutiontile3 && e.invisible && e.activated && e.bounds().overlaps(bounds())) {
                    println("invisible solution tile. would be Saving data but that is commented ITS SAVING")
                    w.save()

                }
                // solutiontile 3
                if (e is SolutionTile && e.solutiontile3 && e.bounds().overlaps(bounds())) {
                    println("invisible solutiontile3. would save but saving is commented in hero.kt ITS SAVING")
                    w.save()
                }

                // book
                if (e is BookPage && e.alive && e.bounds().overlaps(bounds())) {
                    if (!Const.BOOKS.contains(e.id) && e.alive) {
                        e.alive = false
                        Const.BOOKS.add(e.id)
                        println("Collected book: " + e.id)
                        println("I would save this to save_data but this line is commented ITS SAVING")
                        basicGame.map.saveSaveData()
                        basicGame.bookOpen = true
                        basicGame.currentBookPage = e.id
                    }
                }


                if (e.javaClass != SolutionTile::class.java &&
                        e.javaClass != Button::class.java &&
                        e.javaClass != LevelSwitcher::class.java &&
                        !(e is RockBlocker) && !(e is BookPage) &&
                        AABB.collides(e.bounds(), this.bounds())) {
                    if (!(e is AreaDoor && e.open) && !(e is Door && e.on)) {
                        if (face == "l")
                            pos.x += vel
                        if (face == "r")
                            pos.x -= vel
                        if (face == "u")
                            pos.y -= vel
                        if (face == "d")
                            pos.y += vel
                    }
                    // size() > 0 indicates it's a saving rockblocker
                } else if (e is RockBlocker && e.neededIds.size > 0 && e.bounds().overlaps(bounds())) {
                    println("touched a saving rockblocker. This is gonna be your respawn position")
                    Const.LAST_ROCKBLOCKER_POS = pos.cpy()
                    val required = e.neededIds.size
                    var currentActive: Int = 0
                    var found = arrayListOf<Triggerable>();
                    // check if required triggerables are on
                    for (t in w.trigerables) {
                        if (!(t is Door) && !(t is AreaDoor)) {// NOTE: ignores doors
                            // triggerables are in their active state and part of needed
                            if (e.neededIds.contains(t.id))
                                println("its atorund")
                            if (!(t is RockBlocker) && !found.contains(t) && t.on != t.defaultState && e.neededIds.contains(t.id)) {
                                found.add(t);
                                currentActive++
                            }
                        }
                    }

                    // NOTE: should be == but had some bug where it was more than.
                    // Though, in theory this should still work fine because only trigs of the
                    // same id should increment currentActive
                    if (currentActive >= required) {
                        println("the puzzle is solved! would be saving but it's commented out ITS SAVING")
                        basicGame.map.save()
                    }
                }
            }
            // walkers
            for (e in w.trigerables)
                if (e.javaClass == Walker::class.java && bounds().overlaps(e.bounds())) {
                    if (face == "l")
                        pos.x += vel
                    if (face == "r")
                        pos.x -= vel
                    if (face == "u")
                        pos.y -= vel
                    if (face == "d")
                        pos.y += vel
                }
            if (bounds().overlaps(basicGame.player.bounds()) && basicGame.player != this || bounds().overlaps(basicGame.shadow.bounds()) && basicGame.shadow != this) {
                if (face == "l")
                    pos.x += vel
                if (face == "r")
                    pos.x -= vel
                if (face == "u")
                    pos.y -= vel
                if (face == "d")
                    pos.y += vel
            }
        }
    }

    fun checkCollisionPushPullEntity(face: String) {
        // entities
        for (t in w.getEntityFloor(w.currentFloorId).entities)
            if (t.id != 0 && t != thingBeingGrabbed && AABB.collides(t.bounds(), thingBeingGrabbed?.bounds())) {
                if (face == "l")
                    thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(vel))
                if (face == "r")
                    thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(-vel))
                if (face == "d")
                    thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(vel))
                if (face == "u")
                    thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(-vel))
            }
// triggerables        
        for (e in w.trigerables) {
            if (e.javaClass != Button::class.java && e.javaClass != LevelSwitcher::class.java && !e.on && AABB.collides(e.bounds(), thingBeingGrabbed?.bounds())) {
                if (face == "l")
                    thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(vel))
                if (face == "r")
                    thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(-vel))
                if (face == "d")
                    thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(vel))
                if (face == "u")
                    thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(-vel))
            }
        }
        // player
        if (AABB.collides(thingBeingGrabbed?.bounds(), this.bounds())) {
            if (face == "l")
                thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(vel))
            if (face == "r")
                thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(-vel))
            if (face == "d")
                thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(vel))
            if (face == "u")
                thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(-vel))
        }
// shadow player
        if (AABB.collides(thingBeingGrabbed?.bounds(), basicGame.shadow.bounds())) {
            if (face == "l")
                thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(vel))
            if (face == "r")
                thingBeingGrabbed?.x = (thingBeingGrabbed?.x!!.plus(-vel))
            if (face == "d")
                thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(vel))
            if (face == "u")
                thingBeingGrabbed?.y = (thingBeingGrabbed?.y!!.plus(-vel))
        }

    }

    override fun dispose() {
        sprite.texture.dispose()
        step.dispose()
    }

    fun respawn() {
        if (!invincible) {
            killed = true
            textureAlpha = 1f

            // delay to let player release movement keys
            canMove = false
            val t = Timer()
            t.schedule(100) {
                canMove = true
            }
        }
    }

    private fun resetLevel() {
        basicGame.map.dispose()
        basicGame.loadMap(Const.CURRENT_FILE)

        // ensures player uses data for collision/etc in new map
        w = basicGame.map
        basicGame.shadow.w = basicGame.map

        basicGame.player.invincible = true
        basicGame.shadow.invincible = true

        // if you touched a rockblocker go to it. Otherwise, go to start of the level
        if (Const.LAST_ROCKBLOCKER_POS == null) {
            basicGame.player.pos.set(basicGame.map.spawnPos)
        } else {
            basicGame.player.pos.set(Const.LAST_ROCKBLOCKER_POS)
        }
        basicGame.player.spritePos.set(basicGame.player.pos)

        basicGame.shadow.pos.set(basicGame.map.shadowspawnPos)
        basicGame.shadow.spritePos.set(basicGame.shadow.pos)

        basicGame.player.invincible = false
        basicGame.shadow.invincible = false

        basicGame.player.canMove = false
        basicGame.shadow.canMove = false

        // delay to let player release movement keys
        val tt = Timer()
        tt.schedule(100) {
            basicGame.player.canMove = true
            basicGame.shadow.canMove = true
        }

        // don't show the same spawn text
        basicGame.showingSpawnText = false
    }

    override fun bounds(): Rectangle {
        return Rectangle(pos.x + 4f, pos.y + 2f, size.x - 8f, size.y - (Const.TILESIZE / 2))
    }

    // in position perfectly aligned in grid and only in a single tile?
    fun inASingleTileOnly(): Boolean {
        var touching = 0
        for (t in w.currentTileFloor.tiles) {
            if (t.bounds().overlaps(bounds()))
                touching++
        }

        return touching == 1
    }

    fun spriteBounds(): Rectangle {
        return Rectangle(spritePos.x + 1f, spritePos.y + 1f, size.x - 2f, size.y - 2f)
    }
}
