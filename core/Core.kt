package core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import entities.Hero
import util.Gfx
import util.Inputer
import util.Shaker
import java.util.*
import kotlin.concurrent.schedule

class Core : BasicGame() {

    val lerp = .1f
    lateinit var shaker: Shaker
    var hiderAlpha = 0f
    var quoteView = false
    var pauseMenu = false
    var pauseIndex = 0
    var pauseIndexMax = 3
    var spaceHandMaxY = (9f * Const.TILESIZE) + 4
    var spaceHandMinY = (9f * Const.TILESIZE) - 4
    var spaceHandLeftUp = false
    var spaceHandRightUp = true
    lateinit var pageFlipSound: Sound

    lateinit var vignette: Texture

    lateinit var brainStatue: Texture
    lateinit var puzzlePage: Texture
    lateinit var andromedaPage: Texture
    lateinit var leoPage: Texture
    lateinit var geminiPage: Texture
    lateinit var docksPage: Texture

    lateinit var bookTextColor: Color

    var spaceHeadUp = false
    var spaceHeadY: Float = 0f

    lateinit var heart: Texture

    override fun create() {
        Gfx.initOrtho(1280f, 270f, false)
        Gfx.cam.zoom = Const.ZOOM
        Gfx.initFont(56, "fonts/youngserif-regular.ttf")
        Gfx.initFont2(24, "fonts/youngserif-regular.ttf")
        Const.defaultMatrix = Gfx.sb.projectionMatrix
        vignette = Texture("vignette.png")
        shaker = Shaker(Gfx.cam)
        theme = Gdx.audio.newMusic(Gdx.files.internal("music/love.mp3"))
        pageFlipSound = Gdx.audio.newSound(Gdx.files.internal("music/page_flip.mp3"))
        loadMap(Const.CURRENT_FILE)
        player = Hero(this,
                map,
                Vector2(map.spawnPos.x, map.spawnPos.y),
                Vector2(Const.TILESIZE - 0f, Const.TILESIZE + 0f))

        if (Const.FIRST_TIME_PLAYING) {
            loadMap(Const.CURRENT_FILE)
            player.w = map
            player.pos.x = map.spawnPos.x
            player.pos.y = map.spawnPos.y
        }

        if (Const.MOVING_CAMERA_X)
            Gfx.cam.position.x = player.pos.x + (player.size.x / 2);
        if (Const.MOVING_CAMERA_Y)
            Gfx.cam.position.y = player.pos.y + (player.size.y / 2);

        // Delay so unlocked areadoors don't play their sound on spawn
        val t = Timer();
        t.schedule(300) {
            Const.MUTE_SAFE = true
        }

        bookSpine = Sprite(Texture("sprites/book_spine.png"))

        vertexShader = Gdx.files.internal("shaders/menu.v").readString()
        fragmentShader = Gdx.files.internal("shaders/menu.f").readString()
        shaderProgram = ShaderProgram(vertexShader, fragmentShader)
        ShaderProgram.pedantic = false // let shaders crash program if they don't compile?

        brainStatue = Texture("sprites/brain_statue.png")
        puzzlePage = Texture("sprites/paper_puzzle.png")
        andromedaPage = Texture("sprites/page_andromeda.png")
        leoPage = Texture("sprites/page_leo.png")
        geminiPage = Texture("sprites/page_gemini.png")
        docksPage = Texture("sprites/page_docks.png")

        // Add in the different quotes
        Const.QUOTES.add("It is the struggle itself towards heights\nthat fills a man's heart.\n\n- Albert Camus")
        Const.QUOTES.add("What would life be if we had no courage\nto attempt anything?\n\n- Vincent Van Gogh")
        Const.QUOTES.add("To live is to suffer, to survive is to\nfind some meaning in the suffering.\n\n- Friedrich Nietzsche")
        Const.QUOTES.add("Life is 10% what happens to you and 90%\nhow you react to it.\n\n- Charles R. Swindoll")

        bookTextColor = Color.DARK_GRAY

        spaceHeadY = 8f * Const.TILESIZE

	heart = Texture("sprites/heart.png")
    }

    var time = 0f
    fun update() {
        input()
        time += Gdx.graphics.deltaTime
        if (!pauseMenu && !quoteView) {
            Gfx.cam.update()

            // Camera movement based off cam_follow
            // X movement
            if (Const.MOVING_CAMERA_X) {
                Gfx.cam.position.x += (player.pos.x - Gfx.cam.position.x) * lerp

                // max x
                if (Const.HAS_MAX_X) {
                    if (Gfx.cam.position.x > Const.MAX_X) {
                        Gfx.cam.position.x = Const.MAX_X
                    }
                }
                // min x
                if (Const.HAS_MIN_X) {
                    if (Gfx.cam.position.x < Const.MIN_X) {
                        Gfx.cam.position.x = Const.MIN_X
                    }
                }
            }

            // Y movement
            if (Const.MOVING_CAMERA_Y) {
                Gfx.cam.position.y += (player.pos.y - Gfx.cam.position.y) * lerp

                // max y
                if (Const.HAS_MAX_Y) {
                    if (Gfx.cam.position.y > Const.MAX_Y) {
                        Gfx.cam.position.y = Const.MAX_Y
                    }
                }
                // min y
                if (Const.HAS_MIN_Y) {
                    if (Gfx.cam.position.y < Const.MIN_Y) {
                        Gfx.cam.position.y = Const.MIN_Y
                    }
                }
            }

            // Render everything in the world
            if (!screenHidden && !bookOpen) {
                // update entities
                map.currentTileFloor.update();
                map.currentEntityFloor.update();
                map.updateTriggerables();

                if (!screenHidden) {
                    player.update();
                    if (Const.SHADOW_HERO_LEVEL) {
                        shadow.update();
                    }
                }

                // Audio
                if (Const.MUTED && theme.isPlaying)
                    theme.stop()
                if (!Const.MUTED && !theme.isPlaying)
                    theme.play()
            }

            // Space world particles
            if (Const.CURRENT_FILE.startsWith("space")) {
                for (s in spaceParticles)
                    s.update()
            }
        }

        // If player is in space_3, they have completed space world
        if (Const.CURRENT_FILE == "space_3") {
            Const.COMPLETEDSPACE = true
        }

        // Make space hands float up and down
        if (Const.CURRENT_FILE == "space_2") {
            if (spaceHandLeftUp) space2HandLeft.setY(space2HandLeft.getY() + .4f)
            if (!spaceHandLeftUp) space2HandLeft.setY(space2HandLeft.getY() - .3f)
            if (space2HandLeft.getY() > spaceHandMaxY) spaceHandLeftUp = false
            if (space2HandLeft.getY() < spaceHandMinY) spaceHandLeftUp = true

            if (spaceHandRightUp) space2HandRight.setY(space2HandRight.getY() + .3f)
            if (!spaceHandRightUp) space2HandRight.setY(space2HandRight.getY() - .4f)
            if (space2HandRight.getY() > spaceHandMaxY) spaceHandRightUp = false
            if (space2HandRight.getY() < spaceHandMinY) spaceHandRightUp = true
        }

        // Make space head float up and down
        if (Const.CURRENT_FILE == "space_2") {
            if (spaceHeadUp) spaceHeadY += .4f
            if (!spaceHeadUp) spaceHeadY -= .4f
            if (spaceHeadY > (8f * Const.TILESIZE) + (Const.TILESIZE/2f)) spaceHeadUp = false
            if (spaceHeadY < (8f * Const.TILESIZE) - (Const.TILESIZE/4f)) spaceHeadUp = true
        }

        // overworld_5 water collision
        if (Const.CURRENT_FILE == "overworld_5") {
            var rect = Rectangle(0f, 9f * Const.TILESIZE, Const.TILESIZE.toFloat(), Const.TILESIZE * 2f);
            if (player.bounds().overlaps(rect)) {
                player.pos.x += Const.TILESIZE.toFloat()
                quoteView = true
                Const.QUOTE_CURRENT = Const.QUOTES.get(MathUtils.random(0, Const.QUOTES.size - 1))
            }
        }
    }

    override fun render() {
        if (!Const.CURRENT_FILE.startsWith("space_complete")) {
            update()
        }

        if (pauseMenu) {

            Gfx.setClearColor(Color(24 / 255f, 59 / 255f, 116 / 255f, 1f))
            Gfx.update()

            Gfx.font.color = (Color.BLACK)
            if (pauseIndex == 0) Gfx.font.color = Color.WHITE
            Gfx.drawTextUI("Resume", Gfx.font, Gfx.cam.viewportWidth / 2f - 100f, Gfx.cam.viewportHeight - (Gfx.font.getLineHeight() * 3));
            Gfx.font.color = (Color.BLACK)
            if (pauseIndex == 1) Gfx.font.color = Color.WHITE
            Gfx.drawTextUI("Options", Gfx.font, Gfx.cam.viewportWidth / 2f - 100f, Gfx.cam.viewportHeight - (Gfx.font.getLineHeight() * 4));
            Gfx.font.color = (Color.BLACK)
            if (pauseIndex == 2) Gfx.font.color = Color.WHITE
            Gfx.drawTextUI("Credits", Gfx.font, Gfx.cam.viewportWidth / 2f - 100f, Gfx.cam.viewportHeight - (Gfx.font.getLineHeight() * 5));
            Gfx.font.color = (Color.BLACK)
            if (pauseIndex == 3) Gfx.font.color = Color.WHITE
            Gfx.drawTextUI("Quit", Gfx.font, Gfx.cam.viewportWidth / 2f - 100f, Gfx.cam.viewportHeight - (Gfx.font.getLineHeight() * 6));
            Gfx.font.color = Color.BLACK

            Gfx.uisb.begin()
            Gfx.uisb.draw(vignette, 0f, 0f, Gfx.cam.viewportWidth.toFloat(), Gfx.cam.viewportHeight.toFloat())
            Gfx.uisb.end()
        } else if (!pauseMenu && !quoteView) {
            Gfx.update()
            // Background colors
            if (Const.CURRENT_FILE.startsWith("space")) {
                Gfx.setClearColor(Color.BLACK)
                for (s in spaceParticles)
                    s.render()
            } else if (
            Const.CURRENT_FILE.startsWith("tower") ||
                    Const.CURRENT_FILE == "cave_0" ||
                    Const.CURRENT_FILE == "cave_1") {
                Gfx.setClearColor(Color(16 / 255f, 16 / 255f, 16 / 255f, 1f))
            } else {
                Gfx.setClearColor(Color(10 / 255f, 90 / 255f, 176 / 255f, 1f))
            }

            if (Const.CURRENT_FILE == "space_2") {
                // render brain statue of space_2
                Gfx.sb.begin()
                Gfx.sb.draw(brainStatue, (6f * Const.TILESIZE) - (brainStatue.getWidth() / 2) + (Const.TILESIZE / 2), spaceHeadY);
                Gfx.sb.end()
            }

            // Render entities
            if (Const.CURRENT_FILE == "overworld_6") {
                Gfx.drawSprite(towerSprite);
            }
            if (Const.CURRENT_FILE.startsWith("overworld")) {
                player.renderOceanSprite()
            }

            map.currentTileFloor.render()
            map.renderTriggerableSolutionTiles();
            map.renderTriggerables()


            map.currentEntityFloor.render()

            // NOTE: demospecific code to not render player at end of demo
            if (!Const.CURRENT_FILE.startsWith("space_complete")) {
                player.render()
            }

            map.renderTriggerableDoors();

            player.renderHand()


            // Render the sort of tutorial in cave_0
            if (Const.CURRENT_FILE.startsWith("cave")) {
                Gfx.drawText("Hold\nSpacebar\nto grab", -340f, 670f, Color.DARK_GRAY)
            }

	    // Draw the heart
	    if (Const.CURRENT_FILE.equals("tower_8")) {
		    Gfx.sb.begin()
		    Gfx.sb.draw(heart, Const.TILESIZE*3f, 230f)
		    Gfx.sb.end()
	    }

            if (Const.SHADOW_HERO_LEVEL)
                shadow.render()

            // Spawn Text
            if (showingSpawnText && spawnTextContents != "") {
                // translucent background
                Gdx.gl.glEnable(GL20.GL_BLEND)
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
                Gfx.setColorUI(Color(0f, 0f, 0f, spawnTextAlpha))
                Gfx.fillRectUI(0f, Gfx.cam.viewportHeight - ((Gfx.font.lineHeight / 2) * 1.5f) - ((Gfx.font.lineHeight * 2f) / 2f), Gfx.cam.viewportWidth + 0f, Gfx.font.lineHeight * 1.5f)

                // The text
                Gfx.uisb.begin()
                Gfx.font.setColor(Color(1f, 1f, 1f, spawnTextAlpha))
                Gfx.font.draw(Gfx.uisb, spawnTextContents, Gfx.cam.viewportWidth / 2 - (spawnTextStringWidth / 2), Gfx.cam.viewportHeight - ((Gfx.font.lineHeight / 2) * 1.5f))
                Gfx.uisb.end()

                // Alter alpha
                if (spawnTextAlpha > 0.7) {
                    spawnTextAlpha -= .003f
                } else {
                    spawnTextAlpha -= .008f
                }
            }
        } else if (quoteView) {
            Gfx.setClearColor(Color.BLACK)
            Gfx.update()

            // Quote text
            Gfx.uisb.begin()
            Gfx.font.setColor(Color(1f, 1f, 1f, spawnTextAlpha))
            Gfx.font.draw(Gfx.uisb, Const.QUOTE_CURRENT, 60f, Gfx.cam.viewportHeight - ((Gfx.font.lineHeight / 2) * 2.5f))
            Gfx.uisb.end()
        }

        if (Const.debugging)
            renderDebug()

        // Render book
        if (bookOpen) {
            val bookWidth: Float = 700f
            val bookHeight: Float = Gfx.cam.viewportHeight
            val bookX: Float = Gfx.cam.viewportWidth / 2 - bookWidth / 2
            val paddingWidth = 20f

            // Translucent background
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            Gfx.setColorUI(Color(0f, 0f, 0f, .7f))
            Gfx.fillRectUI(0f, 0f, Gfx.cam.viewportWidth + 0f, Gfx.cam.viewportHeight + 0f)
            // Render cover and back
            Gfx.setColorUI(Color(84 / 255f, 69 / 255f, 44 / 255f, 1f))
            Gfx.fillRectUI(bookX + bookWidth, 0f, paddingWidth, bookHeight)
            Gfx.fillRectUI(bookX - paddingWidth, 0f, paddingWidth, bookHeight)
            // Render page
            Gfx.setColorUI(Color(240f / 255, 238f / 255, 230f / 255, 1f))
            Gfx.fillRectUI(bookX, 0f, bookWidth, bookHeight)
            // Left page
            Gfx.setColorUI(Color(214f / 255, 212f / 255, 205f / 255, 1f))
            Gfx.fillRectUI(bookX - paddingWidth, 0f, -bookX + paddingWidth, bookHeight)
            // Spine
            Gfx.uisb.begin()
            Gfx.uisb.draw(bookSpine, bookX - 120f, 0f, 120f, Gfx.cam.viewportHeight)
            Gfx.uisb.end()
            // Page number
            Gfx.drawTextUI("" + currentBookPage, Gfx.font2, (bookX + bookWidth) - 24f, bookHeight - (paddingWidth * .5f), Color.GRAY)
            // Text on page
            if (currentBookPage == 1 && Const.BOOKS.contains(1)) {
                Gfx.uisb.begin();
                Gfx.uisb.draw(andromedaPage, bookX, paddingWidth + (bookHeight / 4f), bookWidth, bookHeight / 2f)
                Gfx.uisb.end()
            } else if (currentBookPage == 2 && Const.BOOKS.contains(2)) {
                Gfx.uisb.begin();
                Gfx.uisb.draw(leoPage, bookX, paddingWidth + (bookHeight / 4f), bookWidth, bookHeight / 2f)
                Gfx.uisb.end()
            } else if (currentBookPage == 3 && Const.BOOKS.contains(3)) {
                Gfx.uisb.begin();
                Gfx.uisb.draw(geminiPage, bookX, paddingWidth + (bookHeight / 4f), bookWidth, bookHeight / 2f)
                Gfx.uisb.end()
            } else if (currentBookPage == 4 && Const.BOOKS.contains(4)) {
                Gfx.uisb.begin();
                Gfx.uisb.draw(docksPage, bookX, paddingWidth + (bookHeight / 4f), bookWidth, bookHeight / 2f)
                Gfx.uisb.end()
            } else if (currentBookPage == 5 && Const.BOOKS.contains(5)) {
                Gfx.uisb.begin();
                Gfx.uisb.draw(puzzlePage, bookX, paddingWidth + (bookHeight / 4f), bookWidth, bookHeight / 2f)
                Gfx.uisb.end()
            }

            // Letter B at bottom of every page
            Gfx.font.setColor(Color.GRAY);
            Gfx.drawTextUI("B", bookX + (bookWidth / 2) - 45f, Gfx.font.lineHeight.toFloat() / 1.3f)
        }


        // fading out
        if (!screenHiderFadingIn && screenHidden) {
            if (hiderAlpha > 0f) {
                hiderAlpha -= .02f
            } else {
                hiderAlpha = 0f
                screenHidden = false
            }
        }

        // fade in
        if (screenHiderFadingIn && !screenHidden) {
            if (hiderAlpha < 1f) {
                //hiderAlpha += .02f
                hiderAlpha = 1f
            } else {
                hiderAlpha = 1f
                screenHidden = true
                screenHiderFadingIn = false // to ensure it fades back out
            }
        }

        // Screen hider
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gfx.setColorUI(Color(0f, 0f, 0f, hiderAlpha));
        Gfx.fillRectUI(0f, 0f, Gfx.cam.viewportWidth + 0f, Gfx.cam.viewportHeight + 0f);

        // End screen
        if (Const.CURRENT_FILE.startsWith("space_complete")) {
//                Gfx.setClearColor(Color(5 / 255f, 36 / 255f, 37 / 255f, 1f));
            Gfx.drawTextUI("Thanks for playing", Gfx.cam.viewportWidth / 2f - 270f, Gfx.cam.viewportHeight - Gfx.font.lineHeight * 2);
        }

        // secret place
        if (Const.CURRENT_FILE == "place") {
            Gfx.setClearColor(Color(7 / 255f, 51 / 255f, 117 / 255f, 1f));
        }
    }

    fun input() {
        // Open pause menu
        if (Inputer.tappedKey(Input.Keys.ESCAPE) && !bookOpen && !quoteView) {
            pauseMenu = !pauseMenu
            pauseIndex = 0
        }

        if (Inputer.tappedKey(Input.Keys.R) && !player.killed && !player.levitating) {
            screenHidden = false
            screenHiderFadingIn = true
            player.respawn()
        }

        if (pauseMenu) {
            if (Inputer.tappedKey(Input.Keys.UP)) {
                println(Const.CURRENT_FILE)
                if (pauseIndex == 0) pauseIndex = pauseIndexMax
                else {
                    pauseIndex--
                }
            }
            if (Inputer.tappedKey(Input.Keys.DOWN)) {
                if (pauseIndex == pauseIndexMax) pauseIndex = 0
                else {
                    pauseIndex++
                }
            }

            if (Inputer.tappedKey(Input.Keys.ENTER) || Inputer.tappedKey(Input.Keys.SPACE)) {
                // Resume Game
                if (pauseIndex == 0) pauseMenu = false
                // Quit game
                if (pauseIndex == 3) {
                    map.saveSaveData()
                    Gdx.app.exit()
                }
            }

        } else if (!pauseMenu) {

            // Exit quoteView
            if (Inputer.tappedKey(Input.Keys.ENTER) || Inputer.tappedKey(Input.Keys.SPACE)) {
                if (quoteView) {
                    quoteView = false
                    player.canMove = true
                }
            }

            // Open / Close book
            if (Inputer.tappedKey(Input.Keys.B) && !pauseMenu) {
                bookOpen = !bookOpen
                if (!bookOpen)
                    currentBookPage = 1
            }

            // Flip pages of the book
            if (bookOpen) {
                var lastBookPage = 6
                if (Inputer.tappedKey(Input.Keys.RIGHT)) {
                    if (currentBookPage < lastBookPage) {
                        currentBookPage++
                        pageFlipSound.play()
                    }
                }
                if (Inputer.tappedKey(Input.Keys.LEFT)) {
                    if (currentBookPage > 1) {
                        currentBookPage--
                        pageFlipSound.play()
                    }
                }
            }

            // Toggle debug mode
            if (Inputer.tappedKey(Input.Keys.SHIFT_RIGHT))
                Const.debugging = !Const.debugging

            if (Const.debugging) {
                if (Inputer.tappedKey(Input.Keys.F1))
                    editMode = 0
                if (Inputer.tappedKey(Input.Keys.F2))
                    editMode = 1

                if (Inputer.tappedKey(Input.Keys.F3)) {
                    println("Saving data")
                    map.save()
                }

                if (Inputer.tappedKey(Input.Keys.NUM_1))
                    currentEditorId++
                if (Inputer.tappedKey(Input.Keys.NUM_2))
                    currentEditorId--
            }
        }
    }

    fun renderDebug() {
        Gfx.setColorUI(Color(12 / 255f, 12 / 255f, 12 / 255f, .3f))
        Gfx.fillRectUI(0f, 0f, 300f, Gfx.cam.viewportHeight)
        Gfx.drawTextUI("Tile ID: " + currentEditorId, 10f, Gfx.cam.viewportHeight - Gfx.font.lineHeight * 3, Color.WHITE)

        var tx = 0f
        var ty = 0f
        var path = ""

        for (t in map.currentTileFloor.tiles) {
            if (t.bounds().contains(Vector2(Inputer.mousePos.x, Inputer.mousePos.y))) {
                tx = t.x / Const.TILESIZE
                ty = t.y / Const.TILESIZE
            }
        }

        for (e in map.currentEntityFloor.entities)
            if (e.bounds().contains(Vector2(Inputer.mousePos.x, Inputer.mousePos.y)))
                path = e.path.toString()

        Gfx.drawTextUI("Tile Position: " + tx + "," + ty, 10f, Gfx.cam.viewportHeight - Gfx.font.lineHeight * 2, Color.WHITE)
        Gfx.drawTextUI("Level: " + map.path, 10f, Gfx.cam.viewportHeight - Gfx.font.lineHeight * 4, Color.WHITE)
        Gfx.drawTextUI("Path: " + path, 10f, Gfx.cam.viewportHeight - Gfx.font.lineHeight * 6, Color.WHITE)
        Gfx.drawTextUI("FPS: " + Gdx.graphics.framesPerSecond, 10f, Gfx.cam.viewportHeight - Gfx.font.lineHeight, Color.WHITE)
    }

    override fun resize(width: Int, height: Int) {
        // whenever our screen resizes, we need to update our uniform
//        shaderProgram!!.begin()
//        shaderProgram!!.setUniformf("resolution", width.toFloat(), height.toFloat())
//        shaderProgram!!.end()

    }

    override fun dispose() {
        // Save last location
        map.saveSaveData()
        Gdx.app.exit()

        clean()
        map.dispose()
        player.dispose()
        theme.dispose()
        if (Const.SHADOW_HERO_LEVEL)
            shadow.dispose()
        map.trigerables.clear()
        map.currentEntityFloor.entities.clear()
        map.currentTileFloor.tiles.clear()
        spaceParticles.clear()
    }
}
