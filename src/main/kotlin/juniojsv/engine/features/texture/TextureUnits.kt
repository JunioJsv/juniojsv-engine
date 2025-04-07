package juniojsv.engine.features.texture

import juniojsv.engine.features.shader.ShadersProgram
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30

class TextureUnits {
    companion object {
        private val units = arrayOf(
            GL13.GL_TEXTURE0,
            GL13.GL_TEXTURE1,
            GL13.GL_TEXTURE2,
            GL13.GL_TEXTURE3,
            GL13.GL_TEXTURE4,
            GL13.GL_TEXTURE5,
            GL13.GL_TEXTURE6,
            GL13.GL_TEXTURE7,
            GL13.GL_TEXTURE8,
            GL13.GL_TEXTURE9,
            GL13.GL_TEXTURE10,
            GL13.GL_TEXTURE11,
            GL13.GL_TEXTURE12,
            GL13.GL_TEXTURE13,
            GL13.GL_TEXTURE14,
            GL13.GL_TEXTURE15
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
         * Binds a texture to a specific texture unit.
         *
         * @param index The index of the texture unit to bind to.
         * @param texture The texture to bind.
         * @return `true` if the texture was bound, `false` if it was already bound.
         * @throws NoSuchElementException if the index is out of bounds.
         */
        fun bind(index: Int, texture: Texture): Boolean {
            if (index > units.size - 1) {
                throw NoSuchElementException("Texture unit $index not found, units available 0..${units.size}")
            }
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
         * Binds a texture to a specific texture unit and sets the corresponding uniform in the shader program.
         *
         * @param index The index of the texture unit to bind to.
         * @param texture The texture to bind.
         * @param uniform The name of the uniform in the shader program to set.
         * @return `true` if the texture was bound, `false` if it was already bound.
         * @throws NoSuchElementException if the index is out of bounds.
         */
        fun bind(index: Int, texture: Texture, uniform: String): Boolean {
            val didBind = bind(index, texture)
            ShadersProgram.putUniform(uniform, index)
            return didBind
        }


        /**
         * Binds a texture to the first available texture unit that does not already contain a texture of the same type.
         *
         * @param texture The texture to bind.
         * @param uniform The name of the uniform in the shader program to set.
         * @return `true` if the texture was bound, `false` if it was already bound or if no suitable unit was found.
         * @throws NoSuchElementException if no suitable texture unit is found.
         * @see bind(index: Int, texture: Texture, uniform: String)
         */
        fun bind(texture: Texture, uniform: String): Boolean {
            val index = units.indexOfFirst { unit -> bindings[unit]?.let { !it.contains(texture.getType()) } ?: true }
            return bind(index, texture, uniform)
        }

        /**
         * Binds a set of textures to consecutive texture units and sets the corresponding uniform in the shader program.
         *
         * @param textures The set of textures to bind.
         * @param uniform The name of the uniform in the shader program to set. Defaults to "textures".
         * @return The number of textures that were bound.
         * @throws NoSuchElementException if there are not enough texture units to bind all textures.
         */
        fun bind(textures: Set<Texture>, uniform: String = "uTextures"): Int {
            if (textures.isEmpty()) {
                unbindAll()
            }
            val count = textures.withIndex().count { (index, texture) ->
                bind(index, texture)
            }

            ShadersProgram.putUniform(uniform, IntArray(count) { it })

            return count
        }
    }
}