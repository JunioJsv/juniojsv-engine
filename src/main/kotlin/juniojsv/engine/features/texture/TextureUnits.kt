package juniojsv.engine.features.texture

import juniojsv.engine.features.shader.ShadersProgram
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30

class TextureUnits {
    companion object {
        private val units = arrayOf(
            GL13.GL_TEXTURE0, GL13.GL_TEXTURE1, GL13.GL_TEXTURE2, GL13.GL_TEXTURE3,
            GL13.GL_TEXTURE4, GL13.GL_TEXTURE5, GL13.GL_TEXTURE6, GL13.GL_TEXTURE7,
            GL13.GL_TEXTURE8, GL13.GL_TEXTURE9, GL13.GL_TEXTURE10, GL13.GL_TEXTURE11,
            GL13.GL_TEXTURE12, GL13.GL_TEXTURE13, GL13.GL_TEXTURE14, GL13.GL_TEXTURE15
        )

        private val bindings = mapOf(
            *units.map { unit -> unit to mutableSetOf<Int>() }.toTypedArray()
        )

        /**
         * Unbinds all textures from all texture units.
         * This method iterates through each texture unit and unbinds any texture that is currently bound to it.
         */
        fun unbindAll() {
            bindings.forEach { (unit, types) ->
                GL30.glActiveTexture(unit)
                types.forEach { type ->
                    GL30.glBindTexture(type, 0)
                }
                types.clear()
            }
            GL30.glActiveTexture(units[0])
        }

        /**
         * Binds a [texture] to a specific [index] texture unit.
         *
         * @param index The index of the texture unit to bind to.
         * @param texture The texture to bind.
         * @return `true` if the texture was bound, `false` if it was already bound.
         */
        fun bind(index: Int, texture: Texture): Boolean {
            val unit = units[index]
            GL30.glActiveTexture(unit)
            val isAlreadyBound = texture.id == GL30.glGetInteger(texture.getBindType())
            if (isAlreadyBound) return false
            val type = texture.getType()
            GL30.glBindTexture(type, texture.id)
            bindings[unit]?.add(type)
            GL30.glActiveTexture(units[0])
            return true
        }

        /**
         * Binds a set of [textures] to texture units.
         *
         * @param textures The set of textures to bind.
         * @return The number of textures that were bound.
         * @throws Exception If the number of textures to bind exceeds the maximum number of available texture units.
         * @see unbindAll
         */
        fun bind(textures: Set<Texture>): Int {
            if (textures.isEmpty()) {
                unbindAll()
            }
            if (textures.size > units.size) {
                throw Exception("Too many textures to bind, max ${units.size}")
            }
            val count = textures.withIndex().count { (index, texture) ->
                bind(index, texture)
            }

            ShadersProgram.putUniform(
                "textures",
                IntArray(count) { it }
            )

            return count
        }
    }
}