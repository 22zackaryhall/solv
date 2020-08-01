package entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import core.BasicGame
import core.Const
import util.Gfx

class LaserBeam(c: BasicGame, statE: StaticEntity) : Entity() {

    var temp: Rectangle = Rectangle()

    var direction = 'z'

    var core: BasicGame
    var currentEntity: StaticEntity
    var currentTriggerable: Triggerable? = null
    var currentShadowHero: Hero? = null

    var beam: LaserBeam? = null

    var maxLength = Const.TILESIZE * 20

    var found = false
    var foundStaticEntity = false
    var foundTriggerable = false
    var foundShadowHero = false

    init {
        this.core = c
        this.currentEntity = statE
        pos.set(currentEntity.centerPos().x, currentEntity.centerPos().y)

        if (currentEntity.id == Const.LASER_UP_ID || currentEntity.id == Const.MOVING_LASER_LR_UP_ID)
            direction = 'u'
        if (currentEntity.id == Const.LASER_DOWN_ID || currentEntity.id == Const.MOVING_LASER_LR_DOWN_ID)
            direction = 'd'
        if (currentEntity.id == Const.LASER_LEFT_ID || currentEntity.id == Const.MOVING_LASER_UD_LEFT_ID)
            direction = 'l'
        if (currentEntity.id == Const.LASER_RIGHT_ID || currentEntity.id == Const.MOVING_LASER_UD_RIGHT_ID)
            direction = 'r'
    }

    override fun update() {
        pos.set(currentEntity.centerPos().x, currentEntity.centerPos().y)
    }

    override fun render() {
        found = false
        foundStaticEntity = false
        foundTriggerable = false
        foundShadowHero = false

        // put long rect to check what it collides into
        temp.x = pos.x
        temp.y = pos.y
        temp.width = 2f
        temp.height = 2f

        // Increases laser height till it touches something
        // laser distance has a max of 10 tiles
        for (i in 1..maxLength) {
            if (!found) {
                beam = null

                if (direction == ('u'))
                    temp.height = i + 0f
                if (direction == ('d')) {
                    temp.height = i + 0f
                    temp.y = centerPos().y - temp.height
                }
                if (direction == ('l')) {
                    temp.width = i + 0f
                    temp.x = centerPos().x - temp.width
                }
                if (direction == ('r')) {
                    temp.width = i + 0f
                    temp.x = centerPos().x
                }

                for (e in core.map.currentEntityFloor.entities)
                    checkCollisionStatics(e)
                for (e in core.map.trigerables)
                    checkCollisionTriggerables(e)

                checkCollisionShadowHero()

                if (temp.overlaps(core.player.bounds()) && !core.player.killed && !found) {
//                    core.screenHidden = false
//                    core.screenHiderFadingIn = true
                    core.player.respawn()
                }
            }
        }

        beam?.render()

        Gfx.setColor(Color.ORANGE)

        if (foundStaticEntity) {
            // beam does collisions in centers because it moves texture tile by tile to check.
            // this *renders* it on the side to look more realistic
            if (direction == ('u'))
                Gfx.fillRect(temp.x - (temp.width / 2), temp.y + (currentEntity.size.y / 2), temp.width, temp.height - (currentEntity.size.y / 2))
            if (direction == ('d'))
                Gfx.fillRect(temp.x - (temp.width / 2), temp.y, temp.width, temp.height - (currentEntity.size.y / 2))
            if (direction == ('l'))
                Gfx.fillRect(temp.x, temp.y - (temp.height / 2), temp.width - (currentEntity.size.x / 2), temp.height)
            if (direction == ('r'))
                Gfx.fillRect(temp.x + (currentEntity.size.x / 2), temp.y - (temp.height / 2), temp.width, temp.height)

        } else if (foundTriggerable) {
            // beam does collisions in centers because it moves texture tile by tile to check.
            // this *renders* it on the side to look more realistic
            if (direction == ('u'))
                Gfx.fillRect(temp.x - (temp.width / 2), temp.y + (currentTriggerable!!.size.y / 2), temp.width, temp.height - (currentEntity.size.y / 2))
            if (direction == ('d'))
                Gfx.fillRect(temp.x - (temp.width / 2), temp.y, temp.width, temp.height - (currentTriggerable!!.size.y / 2))
            if (direction == ('l'))
                Gfx.fillRect(temp.x, temp.y - (temp.height / 2), temp.width - (currentTriggerable!!.size.x / 2), temp.height)
            if (direction == ('r'))
                Gfx.fillRect(temp.x + (currentTriggerable!!.size.x / 2), temp.y - (temp.height / 2), temp.width - (currentEntity.size.x / 2), temp.height)
        } else if (foundShadowHero) {
            if (direction == ('u'))
                Gfx.fillRect(temp.x, temp.y + (currentShadowHero!!.size.y / 2), temp.width, temp.height)
            if (direction == ('d'))
                Gfx.fillRect(temp.x, temp.y - (currentShadowHero!!.size.y / 2), temp.width, temp.height)
            if (direction == ('l'))
                Gfx.fillRect(temp.x, temp.y, temp.width, temp.height)
            if (direction == ('r'))
                Gfx.fillRect(temp.x + (currentShadowHero!!.size.x / 2), temp.y, temp.width - (currentShadowHero!!.size.x), temp.height)
        }

    }

    fun checkCollisionStatics(e: StaticEntity) {
        if (e != currentEntity && temp.overlaps(e.bounds()) && e.id != Const.EMPTY_ID) {

            if (e.id == Const.REFLECTOR_DOWNRIGHT_ID) {
                if (beam == null) {
                    beam = LaserBeam(core, e)
                }

                beam?.alive = true
                beam?.currentEntity = e

                if (direction == 'u')
                    beam?.direction = 'r'
                if (direction == 'l') {
                    beam?.direction = 'd'
                }
            }

            if (e.id == Const.REFLECTOR_DOWNLEFT_ID) {
                if (beam == null)
                    beam = LaserBeam(core, e)

                beam?.alive = true
                beam?.currentEntity = e

                if (direction == 'u')
                    beam?.direction = 'l'
                if (direction == 'r')
                    beam?.direction = 'd'
            }

            if (e.id == Const.REFLECTOR_UPLEFT_ID) {
                if (beam == null)
                    beam = LaserBeam(core, e)

                beam?.alive = true
                beam?.currentEntity = e

                if (direction == 'r')
                    beam?.direction = 'u'
                if (direction == 'd')
                    beam?.direction = 'l'
            }

            if (e.id == Const.REFLECTOR_UPRIGHT_ID) {
                if (beam == null)
                    beam = LaserBeam(core, e)

                beam?.alive = true
                beam?.currentEntity = e

                if (direction == 'l')
                    beam?.direction = 'u'
                if (direction == 'd')
                    beam?.direction = 'r'
            }

            found = true
            foundStaticEntity = true
        }
    }

    fun checkCollisionTriggerables(e: Triggerable) {
        if (e.javaClass == Door::class.java && e.on) {
        } else if (temp.overlaps(e.bounds()) && e.javaClass != Button::class.java) {
            currentTriggerable = e
            found = true
            foundTriggerable = true
        }
    }

    fun checkCollisionShadowHero() {
        if (temp.overlaps(core.shadow.bounds())) {
            currentShadowHero = core.shadow
            found = true
            foundShadowHero = true
        }
    }

    override fun dispose() {
        beam?.dispose()
        alive = false
        beam?.alive = false
    }

}
