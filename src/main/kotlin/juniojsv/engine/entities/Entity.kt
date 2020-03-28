package juniojsv.engine.entities

import org.joml.Vector3f

abstract class Entity {
    open val position: Vector3f = Vector3f(1f)
    open val rotation: Vector3f = Vector3f(1f)
    open var scale: Float = 1f

    open fun move(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean = false) {
        with(position) {
            x = if (increment) x + offsetX else offsetX
            y = if (increment) y + offsetY else offsetY
            z = if (increment) z + offsetZ else offsetZ
        }
    }

    open fun rotate(offsetX: Float, offsetY: Float, offsetZ: Float, increment: Boolean = false) {
        with(rotation) {
            x += if (increment) x + offsetX else offsetX
            y += if (increment) y + offsetY else offsetY
            z += if (increment) z + offsetZ else offsetZ
        }
    }
}