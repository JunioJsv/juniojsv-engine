package juniojsv.engine.features.entity

import com.bulletphysics.collision.dispatch.CollisionObject
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.linearmath.DefaultMotionState
import juniojsv.engine.extensions.VecmathVector3f
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.debugger.Debugger
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.utils.IBoundaryShape
import juniojsv.engine.features.utils.Transform

class BaseBeing(
    val transform: Transform = Transform(),
    val texture: Texture? = null,
    val textureScale: Float = 1f,
    val mass: Float = 10f,
    val restitution: Float = .3f,
    val friction: Float = .5f,
    val linearDamping: Float = 0f,
    val angularDamping: Float = .5f,
    val angularFactor: Float = 1f,
    var isEnabled: Boolean = true
) {
    var collisionObject: CollisionObject? = null
        private set

    var debugger: Debugger? = null
        private set

    fun applyCollisionObjectTransform() {
        collisionObject?.also {
            transform.set(it)
            debugger?.set(transform)
        }
    }

    fun createRigidBody(render: BeingRender) {
        val context = render.context
        val boundary = render.mesh.boundary ?: return

        createRigidBody(context, boundary)
    }

    fun createRigidBody(context: IWindowContext, boundary: IBoundaryShape) {
        disposeCollisionObject(context)
        val scale = transform.scale
        val shape = boundary.createCollisionShape(scale)
        val inertia = VecmathVector3f()
        if (mass > 0f) shape.calculateLocalInertia(mass, inertia)
        val transform = transform.toBulletTransform()
        val config = RigidBodyConstructionInfo(mass, DefaultMotionState(transform), shape, inertia)
        config.additionalDamping = true
        config.restitution = restitution
        config.friction = friction
        config.linearDamping = linearDamping
        config.angularDamping = angularDamping

        val body = RigidBody(config).also { it.userPointer = this }
        body.angularFactor = angularFactor

        context.physics.addCollisionObject(body)
        collisionObject = body
    }

    fun createDebugger(render: BeingRender) {
        val context = render.context
        val boundary = render.mesh.boundary ?: return

        disposeDebugger(context)
        val debugger = boundary.createDebugger(transform)
        context.debugger.add(debugger)
        this.debugger = debugger
    }

    fun isInsideFrustum(render: BeingRender): Boolean {
        val context = render.context
        val boundary = render.mesh.boundary ?: return true

        return boundary.isInsideFrustum(context.camera.frustum, transform)
    }

    private fun disposeCollisionObject(context: IWindowContext) {
        collisionObject?.also {
            collisionObject = null
            context.physics.removeCollisionObject(it)
        }
    }

    private fun disposeDebugger(context: IWindowContext) {
        debugger?.also {
            debugger = null
            context.debugger.remove(it)
        }
    }

    fun dispose(context: IWindowContext) {
        disposeCollisionObject(context)
        disposeDebugger(context)
    }
}
