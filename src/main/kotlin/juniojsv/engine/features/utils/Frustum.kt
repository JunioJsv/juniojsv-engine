package juniojsv.engine.features.utils

import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.sqrt

class Frustum {
    private val planes = Array(6) { Vector4f() }

    constructor()

    constructor(projectionView: Matrix4f) {
        update(projectionView)
    }


    fun update(projectionView: Matrix4f) {
        with(projectionView) {
            planes[0].set(m03() + m00(), m13() + m10(), m23() + m20(), m33() + m30()) // Left
            planes[1].set(m03() - m00(), m13() - m10(), m23() - m20(), m33() - m30()) // Right
            planes[2].set(m03() + m01(), m13() + m11(), m23() + m21(), m33() + m31()) // Bottom
            planes[3].set(m03() - m01(), m13() - m11(), m23() - m21(), m33() - m31()) // Top
            planes[4].set(m03() + m02(), m13() + m12(), m23() + m22(), m33() + m32()) // Near
            planes[5].set(m03() - m02(), m13() - m12(), m23() - m22(), m33() - m32()) // Far
        }

        for (plane in planes) {
            val length = sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z)
            if (length > 1e-6f) {
                plane.div(length)
            } else {
                plane.set(0f, 0f, 0f, 0f)
            }
        }
    }

    fun isSphereInside(position: Vector3f, radius: Float): Boolean {
        for (plane in planes) {
            val distance = plane.x * position.x + plane.y * position.y + plane.z * position.z + plane.w
            if (distance < -radius) return false
        }
        return true
    }
}
