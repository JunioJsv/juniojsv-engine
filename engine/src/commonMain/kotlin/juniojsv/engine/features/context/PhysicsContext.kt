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
import juniojsv.engine.Config
import juniojsv.engine.features.entities.PhysicsComponent
import juniojsv.engine.features.utils.FrameTimer
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import javax.vecmath.Vector3f
import kotlin.concurrent.thread

interface IPhysicsContext {
    var speed: Float
    val lastUpdateTime: Double
    fun addListener(listener: ActionInterface)
    fun removeListener(listener: ActionInterface)
    fun addCollisionObject(collisionObject: CollisionObject)
    fun removeCollisionObject(collisionObject: CollisionObject)
}

class PhysicsContext() : IPhysicsContext {

    companion object {
        const val FIXED_TIME_STEP = 1f / 60f
        const val MAX_SUB_STEPS = 1
        private val logger = LoggerFactory.getLogger(PhysicsContext::class.java)
    }

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

    private var simulationTime = 0f
    private var interpolationTime = 0f
    private var lastInterpolationFactor = 0f
    private val timer = FrameTimer()

    override val lastUpdateTime: Double
        get() = timer.lastUpdate

    init {
        world.setGravity(Vector3f(0f, -9.81f, 0f))
    }

    private fun getPhysicsComponents(): List<PhysicsComponent> {
        val collisionObjects = world.collisionObjectArray
        val components = mutableListOf<PhysicsComponent>()
        for (i in 0 until collisionObjects.size) {
            val collisionObject = collisionObjects.getQuick(i)
            val physicsComponent = collisionObject.userPointer as? PhysicsComponent
            if (physicsComponent != null) components.add(physicsComponent)
        }
        return components
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
                    getPhysicsComponents().forEach { it.onSimulationUpdate() }
                }

                override fun debugDraw(debugDrawer: IDebugDraw?) {}
            }
            world.addAction(action)

            while (running) {
                val fpsWasUpdated = timer.update()
                val deltaTime = timer.deltaTime.toFloat()

                simulationTime += deltaTime.coerceAtMost(FIXED_TIME_STEP) * speed
                interpolationTime += deltaTime

                var command = commands.poll()
                while (command != null) {
                    command()
                    command = commands.poll()
                }

                val interpolationFactor = simulationTime / FIXED_TIME_STEP

                while (simulationTime >= FIXED_TIME_STEP) {
                    world.stepSimulation(FIXED_TIME_STEP, MAX_SUB_STEPS, FIXED_TIME_STEP)
                    simulationTime -= FIXED_TIME_STEP
                }

                if (interpolationTime >= FIXED_TIME_STEP && interpolationFactor < 1f) {
                    val deltaInterpolationFactor = interpolationFactor - lastInterpolationFactor
                    if (deltaInterpolationFactor > 0f) {
                        getPhysicsComponents().forEach {
                            it.onSimulationInterpolate(interpolationFactor)
                        }
                    }
                    lastInterpolationFactor = interpolationFactor
                    interpolationTime = 0f
                }

                if (fpsWasUpdated) logStats()

                Thread.yield()
            }

            world.removeAction(action)
            logger.info("Stopping $name thread.")
        }
    }

    override fun addListener(listener: ActionInterface) {
        commands.add { world.addAction(listener) }
    }

    override fun removeListener(listener: ActionInterface) {
        commands.add { world.removeAction(listener) }
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
        if (!Config.isDebug) return
        val collisionObjects = world.collisionObjectArray
        val listeners = world.numActions
        logger.info(
            """
            |Physics Stats:
            |  - Speed: $speed
            |  - Collision Objects: ${collisionObjects.size}
            |  - Listeners: $listeners
            |  - FPS: ${timer.getAverageFpsRounded()}
            """.trimMargin()
        )
    }

    fun stop() {
        running = false
        thread?.join()
    }
}
