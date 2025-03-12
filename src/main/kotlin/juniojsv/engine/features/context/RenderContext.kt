package juniojsv.engine.features.context

import juniojsv.engine.features.entity.Light
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.CubeMapTexture
import juniojsv.engine.features.texture.Texture
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

data class RenderState(
    var light: Light? = null
)

class RenderContext {
    var state = RenderState()
        private set

    private val textureUnits = listOf(
        GL13.GL_TEXTURE0, GL13.GL_TEXTURE1, GL13.GL_TEXTURE2, GL13.GL_TEXTURE3,
        GL13.GL_TEXTURE4, GL13.GL_TEXTURE5, GL13.GL_TEXTURE6, GL13.GL_TEXTURE7,
        GL13.GL_TEXTURE8, GL13.GL_TEXTURE9, GL13.GL_TEXTURE10, GL13.GL_TEXTURE11,
        GL13.GL_TEXTURE12, GL13.GL_TEXTURE13, GL13.GL_TEXTURE14, GL13.GL_TEXTURE15
    )


    fun onPreRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
    }

    fun setTextures(textures: Set<Texture>) {
        assert(textures.size <= textureUnits.size)
        textures.forEachIndexed { index, texture ->
            GL30.glActiveTexture(textureUnits[index])
            val currentTexture =
                GL30.glGetInteger(
                    if (texture is CubeMapTexture) GL30.GL_TEXTURE_BINDING_CUBE_MAP
                    else GL30.GL_TEXTURE_BINDING_2D
                )
            if (texture.id != currentTexture)
                GL30.glBindTexture(
                    if (texture is CubeMapTexture) GL30.GL_TEXTURE_CUBE_MAP
                    else GL11.GL_TEXTURE_2D,
                    texture.id
                )
        }
        val currentShadersProgram = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM)
        if (currentShadersProgram != 0) {
            ShadersProgram(currentShadersProgram)
                .putUniform("textures", IntArray(textures.size) { it })
        }
        GL30.glActiveTexture(textureUnits[0])
    }

    fun setShaderProgram(shader: ShadersProgram?) {
        val currentShadersProgram = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM)
        if (shader?.id == currentShadersProgram) return
        GL20.glUseProgram(shader?.id ?: 0)
    }

    fun setMesh(mesh: Mesh?) {
        val currentVao = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING)
        if (mesh?.vao == currentVao) return
        GL30.glBindVertexArray(mesh?.vao ?: 0)
        if (mesh != null) {
            GL20.glEnableVertexAttribArray(0)
            GL20.glEnableVertexAttribArray(1)
            GL20.glEnableVertexAttribArray(2)
        }
    }

    fun setLight(light: Light?) {
        if (light == state.light) return
        state = state.copy(light = light)
    }
}