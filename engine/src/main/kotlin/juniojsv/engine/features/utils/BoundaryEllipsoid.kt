package juniojsv.engine.features.utils

import com.bulletphysics.collision.shapes.CollisionShape
import com.bulletphysics.collision.shapes.ConvexHullShape
import com.bulletphysics.util.ObjectArrayList
import juniojsv.engine.features.entity.debugger.DebugEllipsoid
import juniojsv.engine.features.entity.debugger.IDebugBeing
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class BoundaryEllipsoid(val radius: Vector3f) : IBoundaryShape {
    override fun isInsideFrustum(frustum: Frustum, transform: Transform): Boolean {
        val scale = transform.scale
        val position = transform.position
        val radius = Vector3f(radius).mul(scale)

        val isSphere = radius.x == radius.y && radius.y == radius.z

        if (isSphere) return frustum.isSphereInside(position, radius.x)

        if (transform.hasRotation()) {
            return frustum.isEllipsoidInside(position, radius, transform.rotation)
        }

        return frustum.isEllipsoidInside(position, radius)
    }

    private fun createEllipsoidShape(radius: Vector3f, details: Float = 1.0f): ConvexHullShape {
        val sector = (36 * details).roundToInt()
        val stack = (18 * details).roundToInt()

        val points = ObjectArrayList<javax.vecmath.Vector3f>()

        val sectorStep = 2 * Math.PI / sector
        val stackStep = Math.PI / stack

        for (i in 0..stack) {
            val stackAngle = Math.PI / 2 - i * stackStep
            val xy = cos(stackAngle).toFloat()
            val z = sin(stackAngle).toFloat()

            for (j in 0..sector) {
                val sectorAngle = j * sectorStep
                val x = (xy * cos(sectorAngle)).toFloat()
                val y = (xy * sin(sectorAngle)).toFloat()

                points.add(
                    javax.vecmath.Vector3f(
                        x * radius.x,
                        y * radius.y,
                        z * radius.z
                    )
                )
            }
        }

        return ConvexHullShape(points)
    }

    override fun getDebugBeing(transform: Transform): IDebugBeing {
        return DebugEllipsoid(transform.copy(scale = Vector3f(radius).mul(transform.scale)))
    }

    override fun getCollisionShape(scale: Vector3f): CollisionShape {
        return createEllipsoidShape(Vector3f(radius).mul(scale), .5f)
    }
}
