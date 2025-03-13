package juniojsv.engine.features.entity

import juniojsv.engine.features.context.WindowContext
import juniojsv.engine.features.utils.SphereBoundary
import juniojsv.engine.features.utils.factories.ColorTextureFactory
import juniojsv.engine.features.utils.factories.ShaderProgramFactory
import juniojsv.engine.features.utils.factories.ShaderPrograms
import juniojsv.engine.features.utils.factories.SphereMesh
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

class Debugger : IRender {
    private val debugColorTexture = ColorTextureFactory.create(Vector3f(1f, 1f, 0f))
    private val defaultInstancedShadersProgram = ShaderProgramFactory.create(ShaderPrograms.DEFAULT_INSTANCED)
    private val sphereMesh = SphereMesh.create()

    override fun render(context: WindowContext) {
        val beingsWithSphereBoundary = context.render.state.beings.filter { it.boundary is SphereBoundary }

        val defaultDepthFunction = GL11.glGetInteger(GL11.GL_DEPTH_FUNC)
        val defaultPolygonMode = GL11.glGetInteger(GL30.GL_POLYGON_MODE)
        GL11.glDepthFunc(GL11.GL_LEQUAL)
        GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE)
        MultiBeing(
            sphereMesh,
            defaultInstancedShadersProgram,
            beingsWithSphereBoundary.map {
                val effectiveScale = it.scale * (it.boundary as SphereBoundary).radius
                BaseBeing(
                    debugColorTexture,
                    position = it.position,
                    scale = effectiveScale
                )
            },
            isDebugger = true
        ).render(context)

        GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, defaultPolygonMode)
        GL11.glDepthFunc(defaultDepthFunction)
    }
}