package juniojsv.engine.features.utils

import juniojsv.engine.features.entity.debugger.DebugRectangle
import juniojsv.engine.features.entity.debugger.IDebugBeing
import org.joml.Vector3f

data class BoundaryRectangle(val width: Float, val height: Float, val depth: Float) : IBoundaryShape {
    override fun isInsideFrustum(frustum: Frustum, position: Vector3f, scale: Vector3f): Boolean {
        return frustum.isRectangleInside(position, width * scale.x, height * scale.y, depth * scale.z)
    }

    override fun getDebugBeing(position: Vector3f, scale: Vector3f): IDebugBeing {
        return DebugRectangle(position, width * scale.x, height * scale.y, depth * scale.z)
    }
}
