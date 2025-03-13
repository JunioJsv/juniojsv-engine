package juniojsv.engine.features.texture

import juniojsv.engine.features.utils.Resource
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

open class FileCubeMapTexture(files: Array<String>) : Texture() {

    init {
        GL11.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, id)
        files.forEachIndexed { index, file ->
            val (pixels, width, height) = getRawTexture(Resource.get(file))
            GL11.glTexImage2D(
                GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + index,
                0, GL11.GL_RGBA, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels
            )
        }
        GL11.glTexParameteri(
            GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR
        )
        GL11.glTexParameteri(
            GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR
        )
        GL11.glTexParameteri(
            GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE
        )
        GL11.glTexParameteri(
            GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE
        )
        GL11.glTexParameteri(
            GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_WRAP_R, GL20.GL_CLAMP_TO_EDGE
        )
        GL11.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, 0)
    }

}