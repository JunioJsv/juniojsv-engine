package juniojsv.engine

import org.joml.Vector3f

class Light(
    private val view: View,
    val position: Vector3f = Vector3f(0f, 0f, -.5f),
    val color: Vector3f = Vector3f(1f)
) {

    fun move(dx: Float, dy: Float, dz: Float, increment: Boolean = false) {
        with(position) {
            x = if (increment) x + dx else dx
            y = if (increment) y + dy else dy
            z = if (increment) z + dz else dz
        }
    }

}