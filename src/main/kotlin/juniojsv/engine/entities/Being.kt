package juniojsv.engine.entities

import juniojsv.engine.effects.Effect
import juniojsv.engine.essentials.Shell
import juniojsv.engine.essentials.Texture
import org.joml.Matrix4f
import org.joml.Vector3f

class Being(
        val shell: Shell,
        val texture: Texture.TwoDimension?,
        val effect: Effect?,
        override val position: Vector3f = Vector3f(0f),
        override val rotation: Vector3f = Vector3f(0f),
        override var scale: Float = 1f
) : Entity() {
    fun transformation(): Matrix4f = Matrix4f()
            .apply {
                translate(position)
                rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), 1f, 0f, 0f)
                rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), 0f, 1f, 0f)
                rotate(Math.toRadians(rotation.z.toDouble()).toFloat(), 0f, 0f, 1f)
                scale(scale)
            }
}