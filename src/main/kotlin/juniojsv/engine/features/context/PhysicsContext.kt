package juniojsv.engine.features.context

import com.bulletphysics.collision.broadphase.DbvtBroadphase
import com.bulletphysics.collision.dispatch.CollisionDispatcher
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration
import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver
import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.window.Window
import javax.vecmath.Vector3f

interface IPhysicsContext {
    val world: DiscreteDynamicsWorld
    var speed: Float
}

class PhysicsContext(private val window: Window) : IPhysicsContext {
    private val broadphase = DbvtBroadphase()
    private val collisionConfiguration = DefaultCollisionConfiguration()
    private val dispatcher = CollisionDispatcher(collisionConfiguration)
    private val solver = SequentialImpulseConstraintSolver()
    override val world = DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration)
    override var speed = 1f

    init {
        world.setGravity(Vector3f(0f, -9.81f, 0f))
    }

    fun onPreRender() {
        val deltaTime = window.context.time.deltaInSeconds.toFloat()
        world.stepSimulation(deltaTime * speed, 1)
        world.collisionObjectArray.forEach {
            val being = it.userPointer as? BaseBeing?
            being?.applyRigidBodyTransform()
        }
    }
}