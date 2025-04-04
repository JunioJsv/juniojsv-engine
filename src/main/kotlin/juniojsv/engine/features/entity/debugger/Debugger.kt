package juniojsv.engine.features.entity.debugger

import juniojsv.engine.features.context.IWindowContext
import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.entity.IRender
import juniojsv.engine.features.entity.MultiBeing
import juniojsv.engine.features.utils.factories.*
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

class Debugger : IRender {
    private val debugColorTexture = ColorTextureFactory.create(Vector3f(1f, 1f, 0f))
    private val debugShaderProgram = ShaderProgramFactory.create(ShaderPrograms.DEFAULT_INSTANCED_DEBUG)

    private val rectangles = MultiBeing(
        CubeMesh.create(), debugShaderProgram, isDebuggable = false, isPhysicsEnabled = false
    )

    private val ellipsoids = MultiBeing(
        SphereMesh(.5f).create(), debugShaderProgram, isDebuggable = false, isPhysicsEnabled = false
    )

    override fun render(context: IWindowContext) {
        val defaultDepthFunction = GL11.glGetInteger(GL11.GL_DEPTH_FUNC)
        val defaultPolygonMode = GL11.glGetInteger(GL30.GL_POLYGON_MODE)
        GL11.glDepthFunc(GL11.GL_LEQUAL)
        GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE)
        val debugBeings = context.render.debugBeings.groupBy { it::class.java }

        rectangles.apply {
            val beings = debugBeings[DebugRectangle::class.java]?.map {
                it as DebugRectangle
                BaseBeing(
                    it.transform,
                    debugColorTexture
                )
            } ?: return@apply
            update(beings)
            render(context)
        }

        ellipsoids.apply {
            val beings = debugBeings[DebugEllipsoid::class.java]?.map {
                it as DebugEllipsoid
                BaseBeing(
                    it.transform,
                    debugColorTexture
                )
            } ?: return@apply
            update(beings)
            render(context)
        }

        GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, defaultPolygonMode)
        GL11.glDepthFunc(defaultDepthFunction)
    }
}