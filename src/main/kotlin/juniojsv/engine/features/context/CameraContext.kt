package juniojsv.engine.features.context

import juniojsv.engine.features.entity.Camera
import juniojsv.engine.features.utils.Frustum
import juniojsv.engine.features.window.Window
import org.joml.Matrix4f
import org.joml.Vector3f

class CameraContext(window: Window) {
    private val camera = Camera(Vector3f(), window)
    lateinit var projection: Matrix4f
    lateinit var view: Matrix4f
    lateinit var frustum: Frustum

    val instance: Camera
        get() = camera

    internal fun onPreRender() {
        projection = camera.projection()
        view = camera.view()
        val projectionView = Matrix4f()
        projection.mul(view, projectionView)
        frustum = Frustum(projectionView)
    }
}