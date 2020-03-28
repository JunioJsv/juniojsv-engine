package juniojsv.engine.utils

import juniojsv.engine.effects.Effect
import juniojsv.engine.effects.Shader
import juniojsv.engine.effects.ShaderType
import juniojsv.engine.essentials.Shell
import juniojsv.engine.essentials.Texture
import juniojsv.engine.extensions.toBuffer
import java.io.InputStream

object Effects {
    val DEFAULT_EFFECT by lazy {
        Effect(
                Shader("effects/default.vert", ShaderType.VERTEX),
                Shader("effects/default.frag", ShaderType.FRAGMENT)
        )
    }
    val DEPTH_EFFECT by lazy {
        Effect(
                Shader("effects/default.vert", ShaderType.VERTEX),
                Shader("effects/depth.frag", ShaderType.FRAGMENT)
        )
    }
    val SKYBOX_EFFECT by lazy {
        Effect(
                Shader("effects/skybox.vert", ShaderType.VERTEX),
                Shader("effects/skybox.frag", ShaderType.FRAGMENT)
        )
    }
}

object Textures {
    val UV_TEXTURE by lazy {
        Texture.TwoDimension("textures/uv.jpeg")
    }
    val GROUND_TEXTURE by lazy {
        Texture.TwoDimension("textures/ground.jpg")
    }
    val SKYBOX_DEFAULT_CUBEMAP by lazy {
        Texture.CubeMap(
                arrayOf(
                        "textures/right.png",
                        "textures/left.png",
                        "textures/top.png",
                        "textures/bottom.png",
                        "textures/front.png",
                        "textures/back.png"
                )
        )
    }
}

object Shells {
    val SPHERE_SHELL by lazy {
        Shell.WaveFront("shells/sphere.obj")
    }

    val CUBEMAP_SHELL by lazy {
        object : Shell() {
            private val vertices = floatArrayOf(
                    -1.0f, 1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f,

                    -1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,

                    1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,

                    -1.0f, -1.0f, 1.0f,
                    -1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,

                    -1.0f, 1.0f, -1.0f,
                    1.0f, 1.0f, -1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, 1.0f, -1.0f,

                    -1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, 1.0f,
                    1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, 1.0f,
                    1.0f, -1.0f, 1.0f
            )

            init {
                indicesCount = vertices.size / 3
                bindAll(vertices.toBuffer(), null, null, null)
            }

            override fun decode(stream: InputStream, onSuccess: (vertices: FloatArray, uvCoordinates: FloatArray?, normals: FloatArray?, indices: IntArray?) -> Unit) {
                TODO("Not yet implemented")
            }
        }
    }
}