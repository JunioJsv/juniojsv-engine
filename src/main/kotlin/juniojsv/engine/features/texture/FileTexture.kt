package juniojsv.engine.features.texture

import juniojsv.engine.features.utils.Resource
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import java.nio.file.Path

open class FileTexture(file: String) : Texture() {
    val fileName = Path.of(file).fileName.toString()

    init {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)

        val (pixels, width, height) = getRawTexture(Resource.get(file))

        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0, GL11.GL_RGBA,
            width, height, 0,
            GL11.GL_RGBA,
            GL11.GL_UNSIGNED_BYTE,
            pixels
        )

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)

        GL11.glTexParameteri(
            GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT
        )
        GL11.glTexParameteri(
            GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT
        )
        GL11.glTexParameteri(
            GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST
        )
        GL11.glTexParameteri(
            GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR
        )
        GL11.glTexParameteri(
            GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST
        )
    }
}