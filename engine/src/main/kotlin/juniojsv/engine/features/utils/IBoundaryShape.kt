package juniojsv.engine.features.utils

import com.bulletphysics.collision.shapes.CollisionShape
import juniojsv.engine.features.entity.debugger.IDebugBeing
import org.joml.Vector3f

interface IBoundaryShape {
    fun isInsideFrustum(frustum: Frustum, transform: Transform): Boolean
    fun getDebugBeing(transform: Transform): IDebugBeing
    fun getCollisionShape(scale: Vector3f): CollisionShape
}