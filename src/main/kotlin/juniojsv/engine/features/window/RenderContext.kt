package juniojsv.engine.features.window

import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.CubeMapTexture
import juniojsv.engine.features.texture.Texture
import juniojsv.engine.features.texture.TwoDimensionTexture
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class RenderContext : IRenderContext {
    private var currentTexture: Texture? = null
    private var currentShaderProgram: ShadersProgram? = null
    private var currentMesh: Mesh? = null

    private var lastOnInitFrame = 0.0
    private var delta = 0.0

    private var frames = 0

    override fun setCurrentShaderProgram(shader: ShadersProgram?) {
        if (shader != null) {
            if (shader.id != currentShaderProgram?.id) {
                GL20.glUseProgram(shader.id)
                currentShaderProgram = shader
            }
        } else {
            GL20.glUseProgram(0)
            currentShaderProgram = null
        }
    }

    override fun setCurrentTexture(texture: Texture?) {
        fun getTarget(texture: Texture): Int {
            return if(texture is CubeMapTexture) GL30.GL_TEXTURE_CUBE_MAP else GL11.GL_TEXTURE_2D;
        }
        if (texture != null) {
            if (texture.id != currentTexture?.id) {
                GL11.glBindTexture(
                    getTarget(texture),
                    texture.id
                )
                currentTexture = texture
            }
        } else {
            currentTexture?.let {
                GL11.glBindTexture(getTarget(it), 0)
                currentTexture = null
            }
        }
    }

    override fun setCurrentMesh(mesh: Mesh?) {
        if (mesh != null) {
            if (mesh.id != currentMesh?.id) {
                GL30.glBindVertexArray(mesh.id)
                GL20.glEnableVertexAttribArray(0)
                GL20.glEnableVertexAttribArray(1)
                GL20.glEnableVertexAttribArray(2)
                currentMesh = mesh
            }
        } else {
            GL30.glBindVertexArray(0)
            currentMesh = null
        }
    }

    private fun getTime(): Double {
        return GLFW.glfwGetTime()
    }

    override fun getFramesCount(): Int = frames

    override fun getDelta(): Double = delta

    fun onInitFrame() {
        val onInit = getTime()
        delta = getTime() - lastOnInitFrame
        lastOnInitFrame = onInit
    }

    fun onPostFrame() {
        frames++
    }
}