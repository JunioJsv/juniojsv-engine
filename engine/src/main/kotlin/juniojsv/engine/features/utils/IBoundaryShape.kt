package juniojsv.engine.features.utils

import com.bulletphysics.collision.shapes.CollisionShape
import juniojsv.engine.features.debugger.Debugger
import org.joml.Vector3f

interface IBoundaryShape {
    fun isInsideFrustum(frustum: Frustum, transform: Transform): Boolean
    fun createCollisionShape(scale: Vector3f): CollisionShape
    fun createDebugger(transform: Transform): Debugger
}