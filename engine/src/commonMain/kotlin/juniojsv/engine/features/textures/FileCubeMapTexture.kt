package juniojsv.engine.features.textures

import juniojsv.engine.features.utils.Resources
import juniojsv.engine.platforms.GL
import juniojsv.engine.platforms.constants.*
import kotlin.properties.Delegates

class FileCubeMapTexture(files: Array<String>) : Texture() {
    var width by Delegates.notNull<Int>()
    var height by Delegates.notNull<Int>()

    lateinit var faceWithMaxPixelLuminance: IndexedValue<PixelLuminance>
        private set

    init {
        val type = getType()
        GL.glBindTexture(type, id)
        files.forEachIndexed { index, file ->
            val texture = Resources.texture(file)
            GL.glTexImage2D(
                GL_TEXTURE_CUBE_MAP_POSITIVE_X + index,
                0, GL_RGBA, texture.width, texture.height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, texture.pixels
            )

            val (width, height) = texture.width to texture.height

            // Verify only 1000 pixels per face
            val maxPixelLuminance = texture.getMaxPixelLuminance((width * height) / 1000)
            if (index == 0) {
                this.width = texture.width
                this.height = texture.height
                faceWithMaxPixelLuminance = IndexedValue(index, maxPixelLuminance)
            } else if (maxPixelLuminance.luminance > faceWithMaxPixelLuminance.value.luminance) {
                faceWithMaxPixelLuminance = IndexedValue(index, maxPixelLuminance)
            }

            texture.dispose()
        }
        GL.glTexParameteri(
            type, GL_TEXTURE_MIN_FILTER, GL_LINEAR
        )
        GL.glTexParameteri(
            type, GL_TEXTURE_MAG_FILTER, GL_LINEAR
        )
        GL.glTexParameteri(
            type, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE
        )
        GL.glTexParameteri(
            type, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE
        )
        GL.glTexParameteri(
            type, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE
        )
        GL.glBindTexture(type, 0)
    }

    override fun getBindType(): Int {
        return GL_TEXTURE_BINDING_CUBE_MAP
    }

    override fun getType(): Int {
        return GL_TEXTURE_CUBE_MAP
    }
}