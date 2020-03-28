package juniojsv.engine.entities

import org.joml.Vector3f

class Light(override val position: Vector3f, val color: Vector3f = Vector3f(1f)) : Entity() {
    override fun rotate(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean) {
        throw UnsupportedOperationException("Don't use this!")
    }
}