package juniojsv.engine.extensions

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

fun Vector3f.toVecmath(): javax.vecmath.Vector3f =
    VecmathVector3f(x, y, z)

fun VecmathVector3f.toJoml(): Vector3f =
    Vector3f(x, y, z)

fun Matrix4f.toVecmath(): VecmathMatrix4f {
    val vals = FloatArray(16)
    this.get(vals)
    return VecmathMatrix4f(
        vals[0], vals[4], vals[8], vals[12],
        vals[1], vals[5], vals[9], vals[13],
        vals[2], vals[6], vals[10], vals[14],
        vals[3], vals[7], vals[11], vals[15]
    )
}

fun VecmathMatrix4f.toJoml(): Matrix4f {
    return Matrix4f(
        m00, m10, m20, m30,
        m01, m11, m21, m31,
        m02, m12, m22, m32,
        m03, m13, m23, m33
    )
}

fun Quaternionf.toVecmath(): javax.vecmath.Quat4f {
    return javax.vecmath.Quat4f(x, y, z, w)
}

fun VecmathQuaternion4f.toJoml(): Quaternionf {
    return Quaternionf(x, y, z, w)
}