package juniojsv.engine.features.debugger

import juniojsv.engine.Config
import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.entity.MultiBeing
import juniojsv.engine.features.entity.Render
import juniojsv.engine.features.utils.BoundaryBox
import juniojsv.engine.features.utils.BoundaryEllipsoid
import juniojsv.engine.features.utils.Transform
import juniojsv.engine.features.utils.factories.CubeMesh
import juniojsv.engine.features.utils.factories.ShadersProgramFactory
import juniojsv.engine.features.utils.factories.SphereMesh
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

sealed class Debugger {
    val being: BaseBeing = BaseBeing()

    class Box(transform: Transform, private val boundary: BoundaryBox) : Debugger() {
        init {
            set(transform)
        }

        override fun set(transform: Transform) {
            val scale = Vector3f(boundary.extents).apply {
                mul(transform.scale)
                div(2f)
            }
            being.transform.also {
                it.position.set(transform.position)
                it.rotation.set(transform.rotation)
                it.scale.set(scale)
            }
        }
    }

    class Ellipsoid(transform: Transform, private val boundary: BoundaryEllipsoid) : Debugger() {
        init {
            set(transform)
        }

        override fun set(transform: Transform) {
            val scale = Vector3f(boundary.radius).mul(transform.scale)
            being.transform.also {
                it.position.set(transform.position)
                it.rotation.set(transform.rotation)
                it.scale.set(scale)
            }
        }
    }

    abstract fun set(transform: Transform)
}


class DebuggerRender : Render() {
    private val shader = ShadersProgramFactory.create("DEBUGGER")
    private val color = Vector3f(1f, 1f, 0f)

    private val boxes =
        MultiBeing(
            CubeMesh.create(),
            shader,
            isDebuggable = false,
            isPhysicsEnabled = false,
            isShaderOverridable = false,
            glMode = GL30.GL_LINE_STRIP
        ).apply { uniforms["uColor"] = color }

    private val ellipsoids =
        MultiBeing(
            SphereMesh(.5f).create(),
            shader,
            isDebuggable = false,
            isPhysicsEnabled = false,
            isShaderOverridable = false,
            glMode = GL30.GL_LINE_STRIP
        ).apply { uniforms["uColor"] = color }

    fun add(debugger: Debugger) {
        when (debugger) {
            is Debugger.Box -> boxes.add(debugger.being)
            is Debugger.Ellipsoid -> ellipsoids.add(debugger.being)
        }
    }

    fun remove(debugger: Debugger) {
        when (debugger) {
            is Debugger.Box -> boxes.remove(debugger.being)
            is Debugger.Ellipsoid -> ellipsoids.remove(debugger.being)
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