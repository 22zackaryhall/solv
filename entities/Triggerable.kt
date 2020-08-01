package entities

import core.BasicGame

abstract class Triggerable(i: Int, var on: Boolean, var game: BasicGame) : Entity() {
    init {
        id = i
    }

    var defaultState = on
    abstract fun trigger()

}
