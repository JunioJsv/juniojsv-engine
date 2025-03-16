package juniojsv.engine.features.context

import juniojsv.engine.features.entity.Camera
import juniojsv.engine.features.utils.Frustum
import juniojsv.engine.features.window.Window
import org.joml.Matrix4f
import org.joml.Vector3f

interface ICameraContext {
    val instance: Camera
    val projection: Matrix4f
    val view: Matrix4f
    val frustum: Frustum
}

class CameraContext(window: Window) : ICameraContext {
    private val camera = Camera(Vector3f(), window)
    override lateinit var projection: Matrix4f
        private set
    override lateinit var view: Matrix4f
        private set
    override var frustum = Frustum()
        private set

    override val instance: Camera
        get() = camera

    fun onPreRender() {
        projection = camera.projection()
        view = camera.view()
        val projectionView = Matrix4f()
        projection.mul(view, projectionView)
        frustum.update(projectionView)
    }
}