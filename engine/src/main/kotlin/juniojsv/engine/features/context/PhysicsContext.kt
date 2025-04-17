package juniojsv.engine.features.context

import com.bulletphysics.collision.broadphase.DbvtBroadphase
import com.bulletphysics.collision.dispatch.CollisionDispatcher
import com.bulletphysics.collision.dispatch.CollisionObject
import com.bulletphysics.collision.dispatch.CollisionWorld
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration
import com.bulletphysics.dynamics.ActionInterface
import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver
import com.bulletphysics.linearmath.IDebugDraw
import juniojsv.engine.features.entity.PhysicsComponent
import juniojsv.engine.features.utils.FrameTimer
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import javax.vecmath.Vector3f
import kotlin.concurrent.thread

interface IPhysicsContext {
    var speed: Float
    fun addController(controller: ActionInterface)
    fun removeController(controller: ActionInterface)
    fun addCollisionObject(collisionObject: CollisionObject)
    fun removeCollisionObject(collisionObject: CollisionObject)
}

class PhysicsContext : IPhysicsContext {
    private val broadphase = DbvtBroadphase()
    private val collisionConfiguration = DefaultCollisionConfiguration()
    private val dispatcher = CollisionDispatcher(collisionConfiguration)
    private val solver = SequentialImpulseConstraintSolver()
    private val world = DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration)

    @Volatile
    override var speed = 1f

    private val commands = ConcurrentLinkedQueue<() -> Unit>()
    private var running = false
    private var thread: Thread? = null

    private val timer = FrameTimer()

    companion object {
        private val logger = LoggerFactory.getLogger(PhysicsContext::class.java)
    }

    init {
        world.setGravity(Vector3f(0f, -9.81f, 0f))
    }

    fun start() {
        if (running) return
        running = true
        val name = "physics"
        thread = thread(name = name) {
            logger.info("Starting $name thread.")
            timer.reset()
            val action = object : ActionInterface() {
                override fun updateAction(collisionWorld: CollisionWorld, deltaTimeStep: Float) {
                    val collisionObjects = world.collisionObjectArray
                    for (i in 0 until collisionObjects.size) {
                        val collisionObject = collisionObjects.getQuick(i)
                        val physicsComponent = collisionObject.userPointer as? PhysicsComponent
                        physicsComponent?.transform?.setAsPrevious()
                        physicsComponent?.syncWithCollisionObject()
                    }
                }

                override fun debugDraw(debugDrawer: IDebugDraw?) {}
            }
            world.addAction(action)
            while (running) {
                val fpsWasUpdated = timer.update()
                val deltaTime = timer.deltaTime.toFloat()

                var command = commands.poll()
                while (command != null) {
                    command()
                    command = commands.poll()
                }

                world.stepSimulation(deltaTime * speed, 3, 1 / 60f)

                if (fpsWasUpdated) logStats()

                Thread.sleep(1)
            }
            world.removeAction(action)
            logger.info("Stopping $name thread.")
        }
    }

    override fun addController(controller: ActionInterface) {
        commands.add { world.addAction(controller) }
    }

    override fun removeController(controller: ActionInterface) {
        commands.add { world.removeAction(controller) }
    }

    override fun addCollisionObject(collisionObject: CollisionObject) {
        commands.add {
            when (collisionObject) {
                is RigidBody -> world.addRigidBody(collisionObject)
                else -> world.addCollisionObject(collisionObject)
            }
        }
    }

    override fun removeCollisionObject(collisionObject: CollisionObject) {
        commands.add {
            when (collisionObject) {
                is RigidBody -> world.removeRigidBody(collisionObject)
                else -> world.removeCollisionObject(collisionObject)
            }
        }
    }

    private fun logStats() {
        val collisionObjects = world.collisionObjectArray
        val controllers = world.numActions
        logger.info(
            """
            |Physics Stats:
            |  - Speed: $speed
            |  - Collision Objects: ${collisionObjects.size}
            |  - Controllers: $controllers
            |  - FPS: ${timer.getAverageFpsRounded()}
            """.trimMargin()
        )
    }

    fun stop() {
        running = false
        thread?.join()
    }
}
