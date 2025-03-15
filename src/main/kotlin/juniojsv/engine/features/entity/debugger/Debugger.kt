package juniojsv.engine.features.entity.debugger

import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.entity.BaseBeing
import juniojsv.engine.features.entity.IRender
import juniojsv.engine.features.entity.MultiBeing
import juniojsv.engine.features.utils.factories.ColorTextureFactory
import juniojsv.engine.features.utils.factories.ShaderProgramFactory
import juniojsv.engine.features.utils.factories.ShaderPrograms
import juniojsv.engine.features.utils.factories.SphereMesh
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

class Debugger : IRender {
    private val debugColorTexture = ColorTextureFactory.create(Vector3f(1f, 1f, 0f))

    private val spheres = MultiBeing(
        SphereMesh(.5f).create(),
        ShaderProgramFactory.create(ShaderPrograms.DEFAULT_INSTANCED_DEBUG),
        isDebuggable = false
    )

    override fun render(context: WindowContext) {
        val defaultDepthFunction = GL11.glGetInteger(GL11.GL_DEPTH_FUNC)
        val defaultPolygonMode = GL11.glGetInteger(GL30.GL_POLYGON_MODE)
        GL11.glDepthFunc(GL11.GL_LEQUAL)
        GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE)

        spheres.apply {
            val beings = context.render.debugBeings.filterIsInstance<DebugSphere>().map {
                BaseBeing(
                    debugColorTexture,
                    position = it.position,
                    scale = it.radius
                )
            }
            println(beings.size)
            update(beings)
            render(context)
        }

        GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, defaultPolygonMode)
        GL11.glDepthFunc(defaultDepthFunction)
    }
}