package juniojsv.engine.features.entities

import com.bulletphysics.collision.dispatch.CollisionWorld
import com.bulletphysics.collision.shapes.ConvexShape
import com.bulletphysics.dynamics.ActionInterface
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.linearmath.IDebugDraw
import juniojsv.engine.extensions.VecmathVector3f
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.render.Render
import juniojsv.engine.features.utils.*
import org.joml.Vector3f
import com.bulletphysics.linearmath.Transform as BulletTransform

class PlayerController(entity: Entity) : ActionInterface() {
    private val body = entity.physics?.collisionObject as? RigidBody ?: error("Player requires a RigidBody")
    private val acceleration = Scale.METER.length(50f)
    private val maxVelocity = Scale.METER.length(5f)
    private val jumpVelocity = Scale.METER.length(5f)
    private val airControlFactor = 0.2f
    private val mass = entity.physics?.config?.mass ?: error("Player requires mass")

    private val transform = BulletTransform()
    private val linearVelocity = VecmathVector3f()

    private val impulseAccumulator = VecmathVector3f()
    private var needsJump = false

    init {
        body.setSleepingThresholds(0f, 0f)
        body.activationState = RigidBody.DISABLE_DEACTIVATION
    }

    override fun updateAction(world: CollisionWorld, deltaTimeStep: Float) {
        if (!body.isActive) return
        body.getWorldTransform(transform)
        body.getLinearVelocity(linearVelocity)

        val isOnGround = isOnGround(world)

        val effectiveImpulse = VecmathVector3f(impulseAccumulator)

        if (!isOnGround) {
            effectiveImpulse.x *= airControlFactor
            effectiveImpulse.z *= airControlFactor
        }

        body.applyCentralImpulse(VecmathVector3f(effectiveImpulse.x, 0f, effectiveImpulse.z))

        if (isOnGround && needsJump) {
            val currentVerticalVelocity = linearVelocity.y
            val requiredImpulseY = (jumpVelocity - currentVerticalVelocity) * mass
            if (requiredImpulseY > 0) {
                body.applyCentralImpulse(VecmathVector3f(0f, requiredImpulseY, 0f))
            }
        }

        body.getLinearVelocity(linearVelocity)

        val horizontalVelocity = VecmathVector3f(linearVelocity.x, 0f, linearVelocity.z)
        if (horizontalVelocity.lengthSquared() > maxVelocity * maxVelocity) {
            horizontalVelocity.normalize()
            horizontalVelocity.scale(maxVelocity)
            linearVelocity.x = horizontalVelocity.x
            linearVelocity.z = horizontalVelocity.z
        }

        body.setLinearVelocity(linearVelocity)

        impulseAccumulator.scale(0f)
        needsJump = false
    }

    override fun debugDraw(debugDrawer: IDebugDraw?) {}

    private fun isOnGround(world: CollisionWorld): Boolean {
        val shape = body.collisionShape as? ConvexShape ?: return false

        val from = BulletTransform()
        val to = BulletTransform()
        body.getWorldTransform(from)
        to.set(from)

        val checkDistance = Scale.CENTIMETER.length(10f)
        to.origin.y -= checkDistance

        val callback = CollisionWorld.ClosestConvexResultCallback(from.origin, to.origin)
        world.convexSweepTest(shape, from, to, callback)

        return callback.hasHit() && callback.hitNormalWorld.y > 0.7f
    }

    private fun activate() {
        if (!body.isActive) body.activate(true)
    }

    fun jump() {
        needsJump = true
        activate()
    }

    fun addMovementInput(direction: VecmathVector3f) {
        val impulse = VecmathVector3f(direction)
        impulse.scale(acceleration * mass)
        impulseAccumulator.add(impulse)
        activate()
    }
}

class Player(
    transform: Transform,
    private val boundary: BoundaryEllipsoid = BoundaryEllipsoid(Vector3f(.5f, 1f, .5f))
) : Render(), IMovable {

    private val entity = Entity(
        transform,
        physics = PhysicsConfig(
            restitution = 0f,
            friction = 1.0f,
            linearDamping = 0.1f,
            angularDamping = 1.0f,
            angularFactor = 0f,
            mass = 80f
        )
    )

    lateinit var camera: Camera
        private set

    val cameraName = "player_camera_${this.hashCode()}" // Unique name

    private lateinit var controller: PlayerController

    // Reusable objects to avoid allocations
    private val directionJOML = Vector3f()
    private val directionVecmath = VecmathVector3f()

    override fun setup(context: IWindowContext) {
        super.setup(context)

        camera = context.camera.getOrPut(cameraName) { window ->
            Camera(entity.transform.position, window, this)
        }

        entity.physics?.createRigidBody(context, boundary)
        controller = PlayerController(entity)
        context.physics.addListener(controller)
    }

    /**
     * Translates user input intentions (movements) into physics actions.
     * Called by the input handling system.
     */
    override fun move(movements: Set<MovementDirection>) {
        if (!didSetup || movements.isEmpty()) return

        val forward = camera.forward().apply { y = 0f }.normalize()
        val right = camera.right().apply { y = 0f }.normalize()

        directionJOML.zero()

        for (movement in movements) {
            when (movement) {
                MovementDirection.FORWARD -> directionJOML.add(forward)
                MovementDirection.BACKWARD -> directionJOML.sub(forward)
                MovementDirection.RIGHT -> directionJOML.add(right)
                MovementDirection.LEFT -> directionJOML.sub(right)
                MovementDirection.UP -> controller.jump()
                MovementDirection.DOWN -> {}
            }
        }

        if (directionJOML.lengthSquared() > 0.001f) {
            directionJOML.normalize()
            directionVecmath.set(directionJOML.x, directionJOML.y, directionJOML.z)
            controller.addMovementInput(directionVecmath)
        }
    }


    override fun dispose() {
        super.dispose()
        if (didSetup) {
            context.physics.removeListener(controller)
        }
        entity.dispose(context)
        context.camera.remove(cameraName)
    }
}