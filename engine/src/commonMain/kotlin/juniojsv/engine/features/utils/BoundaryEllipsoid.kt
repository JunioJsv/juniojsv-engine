package juniojsv.engine.features.utils

import com.bulletphysics.collision.shapes.CollisionShape
import com.bulletphysics.collision.shapes.ConvexHullShape
import com.bulletphysics.util.ObjectArrayList
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class BoundaryEllipsoid(val radius: Vector3f, val details: Float = .5f) : IBoundaryShape {
    override fun isInsideFrustum(frustum: Frustum, transform: Transform): Boolean {
        val scale = transform.scale
        val position = transform.position
        val radius = Vector3f(radius).mul(scale)

        val isSphere = radius.x == radius.y && radius.y == radius.z

        if (isSphere) return frustum.isSphereInside(position, radius.x)

        if (transform.isRotated()) {
            return frustum.isEllipsoidInside(position, radius, transform.rotation)
        }

        return frustum.isEllipsoidInside(position, radius)
    }

    private fun createEllipsoidShape(radius: Vector3f): ConvexHullShape {
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

    override fun createCollisionShape(scale: Vector3f): CollisionShape {
        return createEllipsoidShape(Vector3f(radius).mul(scale))
    }

    override fun createShapeTransform(transform: Transform): Transform {
        val scale = Vector3f(radius).mul(transform.scale)
        return Transform(transform.position, transform.rotation, scale)
    }
}
