package juniojsv.engine.features.context

import juniojsv.engine.features.entities.Camera
import juniojsv.engine.features.utils.Frustum
import juniojsv.engine.features.window.PlatformWindow
import org.joml.Matrix4f
import org.joml.Vector3f

interface ICameraContext {
    val instance: Camera
    val projection: Matrix4f
    val previousProjection: Matrix4f
    val view: Matrix4f
    val previousView: Matrix4f
    val frustum: Frustum
    fun setAsCurrent(name: String)
    fun getOrPut(name: String, defaultValue: (window: PlatformWindow) -> Camera): Camera
    fun remove(name: String)
    fun next()
}

class CameraContext(private val window: PlatformWindow) : ICameraContext {
    private val defaultCamera = Camera(Vector3f(), window)
    private var camera = "main"
    private val cameras = mutableMapOf(
        camera to defaultCamera
    )


    override fun setAsCurrent(name: String) {
        camera = name
    }

    override fun next() {
        val keys = cameras.keys.toList()
        val currentCameraIndex = keys.indexOf(camera)
        val nextCameraIndex = (currentCameraIndex + 1) % keys.size
        camera = keys[nextCameraIndex]
    }

    override fun remove(name: String) {
        cameras.remove(name)
    }

    override fun getOrPut(name: String, defaultValue: (windows: PlatformWindow) -> Camera): Camera {
        return cameras.getOrPut(name) { defaultValue(window) }
    }

    override val instance: Camera
        get() = cameras.getOrDefault(camera, defaultCamera)

    override var projection: Matrix4f = instance.view()
        private set
    override var previousProjection: Matrix4f = Matrix4f(projection)
        private set

    override var view: Matrix4f = instance.view()
        private set
    override var previousView: Matrix4f = Matrix4f(view)
        private set

    private val projectionView = Matrix4f()

    override var frustum = Frustum()
        private set

    fun onPreRender() {
        projection.set(instance.projection())
        view.set(instance.view())

        projection.mul(view, projectionView)
        frustum.update(projectionView)
    }

    fun onPostRender() {
        previousProjection.set(projection)
        previousView.set(view)
    }
}