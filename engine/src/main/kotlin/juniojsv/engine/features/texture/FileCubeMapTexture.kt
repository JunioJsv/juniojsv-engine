package juniojsv.engine.features.texture

import juniojsv.engine.features.utils.Resource
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import kotlin.properties.Delegates

class FileCubeMapTexture(files: Array<String>) : Texture() {
    var width by Delegates.notNull<Int>()
    var height by Delegates.notNull<Int>()

    lateinit var faceWithMaxPixelLuminance: IndexedValue<PixelLuminance>
        private set

    init {
        val type = getType()
        GL11.glBindTexture(type, id)
        files.forEachIndexed { index, file ->
            val texture = getRawTexture(Resource.get(file))
            GL11.glTexImage2D(
                GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + index,
                0, GL11.GL_RGBA, texture.width, texture.height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, texture.pixels
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

    override fun getBindType(): Int {
        return GL30.GL_TEXTURE_BINDING_CUBE_MAP
    }

    override fun getType(): Int {
        return GL30.GL_TEXTURE_CUBE_MAP
    }
}