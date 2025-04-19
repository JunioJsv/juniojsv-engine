package juniojsv.engine.features.utils

import juniojsv.engine.extensions.BulletTransform
import juniojsv.engine.extensions.toJoml
import juniojsv.engine.extensions.toVecmath
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import javax.vecmath.Quat4f

data class Transform(
    val position: Vector3f = Vector3f(),
    val rotation: Quaternionf = Quaternionf(),
    val scale: Vector3f = Vector3f(Scale.METER.length(1f))
) {

    constructor(transform: Transform) : this() {
        set(transform)
    }

    constructor(transform: BulletTransform) : this() {
        set(transform)
    }

    val previous by lazy { Transform().also { it.set(this) } }

    fun interpolate(next: Transform, t: Float): Transform {
        position.lerp(next.position, t)
        rotation.slerp(next.rotation, t)
        scale.lerp(next.scale, t)
        return this
    }

    fun setAsPrevious(): Transform {
        previous.set(this)
        return previous
    }

    fun set(source: Transform): Transform {
        position.set(source.position)
        rotation.set(source.rotation)
        scale.set(source.scale)
        return this
    }

    fun set(source: BulletTransform): Transform {
        // BulletTransform don't support scale
        position.set(source.origin.x, source.origin.y, source.origin.z)
        val rotation = Quat4f()
        source.getRotation(rotation)
        this.rotation.set(rotation.toJoml())
        return this
    }

    fun toBulletTransform(): BulletTransform {
        // BulletTransform don't support scale
        val transform = BulletTransform().apply {
            setIdentity()
            origin.set(position.x, position.y, position.z)
            setRotation(rotation.toVecmath())
        }
        return transform
    }

    fun transformation(): Matrix4f = Matrix4f()
        .apply {
            translate(position)
            rotate(rotation)
            scale(scale)
        }

    fun isRotated(): Boolean {
        return rotation.x != 0f || rotation.y != 0f || rotation.z != 0f || rotation.w != 1f
    }
}