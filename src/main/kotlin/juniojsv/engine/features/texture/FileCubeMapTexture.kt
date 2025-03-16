package juniojsv.engine.features.texture

import juniojsv.engine.features.utils.Resource
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class FileCubeMapTexture(files: Array<String>) : Texture() {

    init {
        val type = getTextureType()
        GL11.glBindTexture(type, id)
        files.forEachIndexed { index, file ->
            val texture = getRawTexture(Resource.get(file))
            GL11.glTexImage2D(
                GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + index,
                0, GL11.GL_RGBA, texture.width, texture.height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, texture.pixels
            )
            texture.dispose()
        }
        GL11.glTexParameteri(
            type, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR
        )
        GL11.glTexParameteri(
            type, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR
        )
        GL11.glTexParameteri(
            type, GL11.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE
        )
        GL11.glTexParameteri(
            type, GL11.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE
        )
        GL11.glTexParameteri(
            type, GL20.GL_TEXTURE_WRAP_R, GL20.GL_CLAMP_TO_EDGE
        )
        GL11.glBindTexture(type, 0)
    }

    override fun getTextureBindType(): Int {
        return GL30.GL_TEXTURE_BINDING_CUBE_MAP
    }

    override fun getTextureType(): Int {
        return GL30.GL_TEXTURE_CUBE_MAP
    }
}