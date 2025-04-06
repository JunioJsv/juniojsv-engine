package juniojsv.engine.features.context

import juniojsv.engine.features.entity.Camera
import juniojsv.engine.features.utils.Frustum
import juniojsv.engine.features.window.Window
import org.joml.Matrix4f
import org.joml.Vector3f

interface ICameraContext {
    val instance: Camera
    val projection: Matrix4f
    val previousProjection: Matrix4f
    val view: Matrix4f
    val previousView: Matrix4f
    val frustum: Frustum
}

class CameraContext(window: Window) : ICameraContext {
    private val camera = Camera(Vector3f(), window)
    override var projection: Matrix4f = instance.view()
        private set
    override var previousProjection: Matrix4f = projection
        private set

    override var view: Matrix4f = instance.view()
        private set
    override var previousView: Matrix4f = view
        private set

    override var frustum = Frustum()
        private set

    override val instance: Camera
        get() = camera

    fun onPreRender() {
        projection = instance.projection()
        view = instance.view()
        val projectionView = Matrix4f()
        projection.mul(view, projectionView)
        frustum.update(projectionView)
    }

    fun onPostRender() {
        previousProjection = projection
        previousView = view
    }
}