package juniojsv.engine

import org.joml.Vector3f

class Light(
    val position: Vector3f = Vector3f(0f, 0f, -.5f),
    val color: Vector3f = Vector3f(1f)
) {
    fun move(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean = false) {
        with(position) {
            x = if (increment) x + offsetX else offsetX
            y = if (increment) y + offsetY else offsetY
            z = if (increment) z + offsetZ else offsetZ
        }
    }
}