package juniojsv.engine.platforms

import juniojsv.engine.features.textures.RawTexture
import java.io.InputStream

interface IPlatformDecoders {
    fun texture(stream: InputStream): RawTexture
}

expect object PlatformDecoders : IPlatformDecoders