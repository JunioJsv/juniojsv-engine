package juniojsv.engine.features.utils

import juniojsv.engine.features.entity.debugger.IDebugBeing
import org.joml.Vector3f

interface IBoundaryShape {
    fun isInsideFrustum(frustum: Frustum, position: Vector3f, scale: Vector3f): Boolean
    fun getDebugBeing(position: Vector3f, scale: Vector3f): IDebugBeing
}