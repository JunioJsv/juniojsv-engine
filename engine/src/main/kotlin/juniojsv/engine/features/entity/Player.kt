package juniojsv.engine.features.entity

import com.bulletphysics.collision.dispatch.CollisionWorld
import com.bulletphysics.collision.shapes.ConvexShape
import com.bulletphysics.dynamics.ActionInterface
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.linearmath.IDebugDraw
import juniojsv.engine.extensions.toVecmath
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.utils.*
import org.joml.Vector3f
import com.bulletphysics.linearmath.Transform as BulletTransform
import javax.vecmath.Vector3f as VecmathVector3f

class PlayerController(private val body: RigidBody) : ActionInterface() {
    private val acceleration = Scale.METER.length(1f)
    private val maxVelocity = Scale.METER.length(10f)

    private val linearVelocity = VecmathVector3f()
    private val gravity = VecmathVector3f()
    private val transform = BulletTransform()

    private val impulse = VecmathVector3f()
    private var needJump = false

    override fun updateAction(world: CollisionWorld, deltaTimeStep: Float) {
        body.getWorldTransform(transform)
        body.getGravity(gravity)
        body.getLinearVelocity(linearVelocity)

        val isOnGround = isOnGround(world)

        if (!isOnGround) {
            val airControlFactor = .3f
            impulse.x *= airControlFactor
            impulse.y *= airControlFactor
            impulse.z *= airControlFactor
        }

        body.applyCentralImpulse(impulse.apply { y = 0f })

        if (isOnGround && needJump) {
            val jumpForce = (-1 * gravity.y) * .5f
            val jumpImpulse = VecmathVector3f(0f, jumpForce, 0f)
            body.applyCentralImpulse(jumpImpulse)
        }

        body.getLinearVelocity(linearVelocity)
        linearVelocity.x = linearVelocity.x.coerceIn(-maxVelocity, maxVelocity)
        linearVelocity.z = linearVelocity.z.coerceIn(-maxVelocity, maxVelocity)
        body.setLinearVelocity(linearVelocity)

        impulse.scale(0f)
        needJump = false
    }

    private fun isOnGround(world: CollisionWorld): Boolean {
        val shape = body.collisionShape as ConvexShape? ?: return false

        val from = BulletTransform()
        val to = BulletTransform()

        body.getWorldTransform(from)
        to.set(from)

        to.origin.y -= Scale.CENTIMETER.length(10f)

        val callback = CollisionWorld.ClosestConvexResultCallback(
            from.origin,
            to.origin
        )

        world.convexSweepTest(shape, from, to, callback)

        return callback.hasHit() && callback.hitNormalWorld.y > 0.5f
    }

    private fun activate() {
        if (!body.isActive) body.activate()
    }

    fun jump() {
        needJump = true
        activate()
    }

    override fun debugDraw(debugDrawer: IDebugDraw?) {}

    fun move(direction: VecmathVector3f) {
        impulse.set(VecmathVector3f(direction).apply { scale(acceleration) })
        activate()
    }
}

class Player(
    transform: Transform,
    private val boundary: BoundaryEllipsoid = BoundaryEllipsoid(Vector3f(.5f, 1f, .5f))
) : Render(), IMovable {

    private val being = BaseBeing(
        transform,
        restitution = 0f,
        friction = 1.5f,
        linearDamping = 0f,
        angularDamping = 1f,
        angularFactor = 0f,
        mass = 1f
    )
    private lateinit var camera: Camera
    val cameraName = toString()

    private lateinit var controller: PlayerController

    override fun setup(context: IWindowContext) {
        super.setup(context)
        camera = context.camera.getOrPut(cameraName) { window ->
            Camera(being.transform.position, window).also { camera ->
                camera.parent = this
            }
        }
        being.setAsRigidBody(context, boundary)
        val body = being.collisionObject as? RigidBody ?: return
        controller = PlayerController(body)
        context.physics.world.addAction(controller)
    }

    override fun move(movements: Set<MovementDirection>) {
        if (!didSetup) return
        if (movements.isEmpty()) return

        val forward = camera.forward()
        val right = camera.right()

        val direction = Vector3f()

        for (movement in movements) {
            when (movement) {
                MovementDirection.FORWARD -> direction.add(forward)
                MovementDirection.BACKWARD -> direction.sub(forward)
                MovementDirection.RIGHT -> direction.add(right)
                MovementDirection.LEFT -> direction.sub(right)
                MovementDirection.UP -> controller.jump()
                else -> {}
            }
        }

        if (direction.lengthSquared() > 0f) {
            direction.normalize()
            controller.move(direction.toVecmath())
        }
    }

    override fun dispose() {
        super.dispose()
        being.dispose(context)
    }
}
