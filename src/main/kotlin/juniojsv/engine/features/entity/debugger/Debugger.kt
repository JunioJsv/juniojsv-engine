package juniojsv.engine.features.entity.debugger

import juniojsv.engine.features.context.WindowContext
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
        CubeMesh.create(),
        debugShaderProgram,
        isDebuggable = false
    )

    private val ellipsoids = MultiBeing(
        SphereMesh(.5f).create(),
        debugShaderProgram,
        isDebuggable = false
    )

    override fun render(context: WindowContext) {
        val defaultDepthFunction = GL11.glGetInteger(GL11.GL_DEPTH_FUNC)
        val defaultPolygonMode = GL11.glGetInteger(GL30.GL_POLYGON_MODE)
        GL11.glDepthFunc(GL11.GL_LEQUAL)
        GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE)
        val debugBeings = context.render.debugBeings.groupBy { it::class.java }

        rectangles.apply {
            val beings = debugBeings[DebugRectangle::class.java]?.map {
                it as DebugRectangle
                BaseBeing(
                    debugColorTexture,
                    position = it.position,
                    scale = Vector3f().apply {
                        val halfWidth = it.width / 2
                        val halfHeight = it.height / 2
                        val halfDepth = it.depth / 2
                        set(halfWidth, halfHeight, halfDepth)
                    }
                )
            } ?: return@apply
            update(beings)
            render(context)
        }

        ellipsoids.apply {
            val beings = debugBeings[DebugEllipsoid::class.java]?.map {
                it as DebugEllipsoid
                BaseBeing(
                    debugColorTexture,
                    position = it.position,
                    scale = it.radius
                )
            } ?: return@apply
            update(beings)
            render(context)
        }

        GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, defaultPolygonMode)
        GL11.glDepthFunc(defaultDepthFunction)
    }
}