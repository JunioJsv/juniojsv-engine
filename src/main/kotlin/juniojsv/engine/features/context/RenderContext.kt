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
    val mesh: Mesh? = null,
    val shader: ShadersProgram? = null,
    val textures: Set<Texture> = setOf(),
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
        if (textures == state.textures) return
        assert(textures.size <= textureUnits.size)
        textures.mapIndexed { index, texture ->
            val target = if (texture is CubeMapTexture) GL30.GL_TEXTURE_CUBE_MAP else GL11.GL_TEXTURE_2D
            GL30.glActiveTexture(textureUnits[index])
            GL30.glBindTexture(target, texture.id)
        }
        state.shader?.putUniform("textures", IntArray(textures.size) { it })
        GL30.glActiveTexture(textureUnits[0])
        state = state.copy(textures = textures)
    }

    fun setShaderProgram(shader: ShadersProgram?) {
        if (shader == state.shader) return
        GL20.glUseProgram(shader?.id ?: 0)
        state = state.copy(shader = shader)
    }

    fun setMesh(mesh: Mesh?) {
        if (mesh == state.mesh) return
        GL30.glBindVertexArray(mesh?.vao ?: 0)
        if (mesh != null) {
            GL20.glEnableVertexAttribArray(0)
            GL20.glEnableVertexAttribArray(1)
            GL20.glEnableVertexAttribArray(2)
        }
        state = state.copy(mesh = mesh)
    }

    fun setLight(light: Light?) {
        if (light == state.light) return
        state = state.copy(light = light)
    }
}