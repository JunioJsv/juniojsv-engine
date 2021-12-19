package juniojsv.engine.features.utils

import juniojsv.engine.features.mesh.IMeshProvider
import juniojsv.engine.features.mesh.Mesh
import juniojsv.engine.features.shader.Shader
import juniojsv.engine.features.shader.ShaderType
import juniojsv.engine.features.shader.ShadersProgram
import juniojsv.engine.features.texture.CubeMapTexture
import juniojsv.engine.features.texture.TwoDimensionTexture

object Shaders {
    val DEFAULT_EFFECT by lazy {
        ShadersProgram(
            Shader("shaders/default.vert", ShaderType.VERTEX),
            Shader("shaders/default.frag", ShaderType.FRAGMENT)
        )
    }
    val DEPTH_EFFECT by lazy {
        ShadersProgram(
            Shader("shaders/default.vert", ShaderType.VERTEX),
            Shader("shaders/depth.frag", ShaderType.FRAGMENT)
        )
    }
    val SKYBOX_EFFECT by lazy {
        ShadersProgram(
            Shader("shaders/skybox.vert", ShaderType.VERTEX),
            Shader("shaders/skybox.frag", ShaderType.FRAGMENT)
        )
    }
}

object Textures {
    val UV_TEXTURE by lazy {
        TwoDimensionTexture("textures/uv.jpeg")
    }
    val GROUND_TEXTURE by lazy {
        TwoDimensionTexture("textures/ground.jpg")
    }
    val SKYBOX_DEFAULT_CUBEMAP by lazy {
        CubeMapTexture(
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

object Mesh {
    val CUBEMAP_SHELL by lazy {
        object : IMeshProvider {
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

            override fun decode(onSuccess: (mesh: Mesh) -> Unit) {
                onSuccess(Mesh(vertices))
            }
        }
    }
}