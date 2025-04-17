package juniojsv.engine.features.render

import juniojsv.engine.Config
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entity.Entity
import juniojsv.engine.features.utils.BoundaryBox
import juniojsv.engine.features.utils.BoundaryEllipsoid
import juniojsv.engine.features.utils.IBoundaryShape
import juniojsv.engine.features.utils.Transform
import juniojsv.engine.features.utils.factories.CubeMesh
import juniojsv.engine.features.utils.factories.ShadersProgramFactory
import juniojsv.engine.features.utils.factories.SphereMesh
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

class TransformableShape(transform: Transform, val boundary: IBoundaryShape) {
    val entity: Entity = Entity()

    init {
        update(transform)
    }

    fun update(transform: Transform) {
        entity.transform.set(boundary.createShapeTransform(transform))
    }
}

class DebugShapeRender : Render() {
    private val shader = ShadersProgramFactory.create("DEBUGGER")
    private val color = Vector3f(1f, 1f, 0f)

    private val boxes =
        MultiEntityRender(
            CubeMesh.create(),
            shader,
            isDebuggable = false,
            isPhysicsEnabled = false,
            isShaderOverridable = false,
            glMode = GL30.GL_LINE_STRIP
        ).apply { uniforms["uColor"] = color }

    private val ellipsoids =
        MultiEntityRender(
            SphereMesh(.5f).create(),
            shader,
            isDebuggable = false,
            isPhysicsEnabled = false,
            isShaderOverridable = false,
            glMode = GL30.GL_LINE_STRIP
        ).apply { uniforms["uColor"] = color }

    fun add(shape: TransformableShape) {
        when (shape.boundary) {
            is BoundaryEllipsoid -> ellipsoids.add(shape.entity)
            is BoundaryBox -> boxes.add(shape.entity)
        }
    }

    fun remove(shape: TransformableShape) {
        when (shape.boundary) {
            is BoundaryEllipsoid -> ellipsoids.remove(shape.entity)
            is BoundaryBox -> boxes.remove(shape.entity)
        }
    }

    override fun render(context: IWindowContext) {
        super.render(context)

        GL30.glLineWidth(1.5f)
        boxes.apply { isEnabled = Config.isDebug }.render(context)
        ellipsoids.apply { isEnabled = Config.isDebug }.render(context)
    }

    override fun dispose() {
        boxes.dispose()
        ellipsoids.dispose()
    }
}