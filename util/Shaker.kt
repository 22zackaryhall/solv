package util

import com.badlogic.gdx.graphics.OrthographicCamera
import java.util.*

class Shaker(private val cam: OrthographicCamera) {

    private var time: Float = 0.toFloat()
    private var random: Random? = null
    private var x: Float = 0.toFloat()
    private var y: Float = 0.toFloat()
    private var current_time: Float = 0.toFloat()
    private var power: Float = 0.toFloat()
    private var current_power: Float = 0.toFloat()

    init {
        time = 0f
        current_time = 0f
        power = 0f
        current_power = 0f
    }

    fun shake(power: Float, time: Float) {
        random = Random()
        this.power = power
        this.time = time
        this.current_time = 0f
    }

    fun tick(delta: Float) {
        if (current_time < time) {
            current_time += delta * 1000
            current_power = power * ((time - current_time) / time)

            x = (random!!.nextFloat() - 0.5f) * 2f * current_power
            y = (random!!.nextFloat() - 0.5f) * 2f * current_power

            cam.translate(-x, -y)

        }
    }
}