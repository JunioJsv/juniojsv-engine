package juniojsv.engine.features.context

import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.FileCubeMapTexture
import juniojsv.engine.features.texture.Texture
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30


class RenderContext {
    val state = RenderState()

    private val textureUnits = listOf(
        GL13.GL_TEXTURE0, GL13.GL_TEXTURE1, GL13.GL_TEXTURE2, GL13.GL_TEXTURE3,
        GL13.GL_TEXTURE4, GL13.GL_TEXTURE5, GL13.GL_TEXTURE6, GL13.GL_TEXTURE7,
        GL13.GL_TEXTURE8, GL13.GL_TEXTURE9, GL13.GL_TEXTURE10, GL13.GL_TEXTURE11,
        GL13.GL_TEXTURE12, GL13.GL_TEXTURE13, GL13.GL_TEXTURE14, GL13.GL_TEXTURE15
    )

    // Texture unit to Texture id
    private val textureBindings = mutableMapOf<Int, Texture>()


    fun onPreRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        state.beings.clear()
    }

    private fun getTextureBindType(texture: Texture): Int {
        return if (texture is FileCubeMapTexture) GL30.GL_TEXTURE_BINDING_CUBE_MAP else GL30.GL_TEXTURE_BINDING_2D
    }

    private fun getTextureType(texture: Texture): Int {
        return if (texture is FileCubeMapTexture) GL30.GL_TEXTURE_CUBE_MAP else GL11.GL_TEXTURE_2D
    }

    fun setTextures(textures: Set<Texture>) {
        if (textures.isEmpty()) clearTextureBindings()
        assert(textures.size <= textureUnits.size)
        val currentProgram = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM)
        val amountTextureBindings = textures.withIndex().count { (index, texture) ->
            val textureUnit = textureUnits[index]
            GL30.glActiveTexture(textureUnit)
            val currentTexture = GL30.glGetInteger(getTextureBindType(texture))
            val shouldBind = texture.id != currentTexture

            if (shouldBind) {
                GL30.glBindTexture(getTextureType(texture), texture.id)
                textureBindings[textureUnit] = texture
            }

            shouldBind
        }

        if (currentProgram != 0) {
            ShadersProgram.putUniform(
                currentProgram,
                "textures",
                IntArray(amountTextureBindings) { it }
            )
        }

        GL30.glActiveTexture(textureUnits[0])
    }

    private fun clearTextureBindings() {
        textureBindings.forEach { (unit, texture) ->
            GL30.glActiveTexture(unit)
            val currentTexture = GL30.glGetInteger(getTextureBindType(texture))
            val shouldUnbind = texture.id == currentTexture
            if (shouldUnbind) {
                GL30.glBindTexture(getTextureType(texture), 0)
            }
        }
        textureBindings.clear()
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
}