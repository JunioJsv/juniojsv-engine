package juniojsv.engine.features.entity

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.render.MeshRenderer
import juniojsv.engine.features.render.TransformableShape
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.utils.Transform

data class PhysicsConfig(
    val mass: Float = 10f,
    val restitution: Float = .3f,
    val friction: Float = .5f,
    val linearDamping: Float = 0f,
    val angularDamping: Float = .5f,
    val angularFactor: Float = 1f,
)

data class MaterialConfig(
    val texture: Texture? = null,
    val scale: Float = 1f,
)

class Entity(
    val transform: Transform = Transform(),
    val material: MaterialConfig? = null,
    physics: PhysicsConfig? = null,
    var isEnabled: Boolean = true
) : PhysicsComponentListener {
    val physics = physics?.let { config ->
        PhysicsComponent(transform, config).also { it.addListener(this) }
    }

    var debugger: TransformableShape? = null
        private set

    override fun didTransformChanged() {
        debugger?.update(transform)
    }

    fun createRigidBody(render: MeshRenderer) {
        if (physics == null) return
        val context = render.context
        val boundary = render.mesh.boundary ?: return

        physics.createRigidBody(context, boundary)
    }

    fun createDebugger(render: MeshRenderer) {
        val context = render.context
        val boundary = render.mesh.boundary ?: return

        disposeDebugger(context)
        TransformableShape(transform, boundary).also {
            debugger = it
            context.debugger.add(it)
        }
    }

    fun isInsideFrustum(render: MeshRenderer): Boolean {
        val context = render.context
        val boundary = render.mesh.boundary ?: return true

        return boundary.isInsideFrustum(context.camera.frustum, transform)
    }

    private fun disposeDebugger(context: IWindowContext) {
        debugger?.also {
            debugger = null
            context.debugger.remove(it)
        }
    }

    fun dispose(context: IWindowContext) {
        physics?.dispose(context)
        disposeDebugger(context)
    }
}
