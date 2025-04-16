package juniojsv.engine.features.texture

import juniojsv.engine.features.shader.ShadersProgram
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30

class TextureUnits {
    companion object {
        /**
         * Array of supported texture types.
         */
        val TYPES = arrayOf(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_CUBE_MAP)

        /**
         * Array of available texture units.
         */
        private val units = Array(32) { GL13.GL_TEXTURE0 + it }

        /**
         * Retrieves the current bindings for a specific bind type across all texture units.
         *
         * @param bindType The type of binding to query (e.g., GL30.GL_TEXTURE_BINDING_2D).
         * @return A list of integers representing the bound texture IDs for each unit.
         */
        fun getBindings(bindType: Int): List<Int> {
            val bindings = units.map { unit ->
                GL30.glActiveTexture(unit)
                GL30.glGetInteger(bindType)
            }
            GL30.glActiveTexture(units[0])
            return bindings
        }

        /**
         * Unbinds all textures from all texture units.
         */
        fun unbindAll() {
            units.forEach { unit ->
                GL30.glActiveTexture(unit)
                TYPES.forEach { type -> GL30.glBindTexture(type, 0) }
            }
            GL30.glActiveTexture(units[0])
        }

        /**
         * Binds a texture to a specific texture unit.
         *
         * @param index The index of the texture unit to bind to.
         * @param texture The texture to bind.
         * @return `true` if the texture was bound, `false` if it was already bound.
         * @throws NoSuchElementException if the specified texture unit index is out of bounds.
         */
        fun bind(index: Int, texture: Texture): Boolean {
            if (index < 0 || index > units.size - 1) {
                throw NoSuchElementException("Texture unit $index not found, units available 0..${units.size}")
            }
            val unit = units[index]
            GL30.glActiveTexture(unit)
            val isAlreadyBound = texture.id == GL30.glGetInteger(texture.getBindType())
            if (isAlreadyBound) return false
            val type = texture.getType()
            GL30.glBindTexture(type, texture.id)
            GL30.glActiveTexture(units[0])
            return true
        }

        /**
         * Binds a texture to a specific texture unit and sets the corresponding uniform in the shader program.
         *
         * @param index The index of the texture unit to bind to.
         * @param texture The texture to bind.
         * @param uniform The name of the uniform in the shader program.
         * @return `true` if the texture was bound, `false` if it was already bound.
         */
        fun bind(index: Int, texture: Texture, uniform: String): Boolean {
            val didBind = bind(index, texture)
            ShadersProgram.putUniform(uniform, index)
            return didBind
        }

        /**
         * Binds a texture to an available texture unit, or returns the index if it's already bound.
         * @param texture The texture to bind.
         * @param uniform The name of the uniform in the shader program.
         * @return The index of the texture unit the texture is bound to, or -1 if it could not be bound.
         */
        fun bind(texture: Texture, uniform: String? = null): Int {
            val bindings = getBindings(texture.getBindType())
            var index = bindings.indexOfFirst { id -> texture.id == id }
            if (index != -1) {
                if (uniform != null) ShadersProgram.putUniform(uniform, index)
                return index
            }

            index = bindings.indexOfFirst { id -> id == 0 }

            val didBind = if (uniform != null) bind(index, texture, uniform) else bind(index, texture)
            return if (didBind) index else -1
        }

        /**
         * Binds a set of textures to available texture units and sets the corresponding uniform array in the shader program.
         *
         * @param textures The set of textures to bind.
         * @param uniform The name of the uniform array in the shader program.
         * @return A list of the texture unit indexes the textures were bound to.
         */
        fun bind(textures: Set<Texture>, uniform: String = "uTextures"): List<Int> {
            if (textures.isEmpty()) {
                unbindAll()
            }

            val indexes = textures.map { bind(it) }

            ShadersProgram.putUniform(uniform, IntArray(indexes.size) { indexes[it] })

            return indexes
        }
    }
}