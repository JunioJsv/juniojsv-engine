package juniojsv.engine.features.utils

import juniojsv.engine.platforms.GLDebugMessageCallback
import juniojsv.engine.platforms.constants.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OpenGLESDebugCallback private constructor() : GLDebugMessageCallback {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(OpenGLESDebugCallback::class.java)
        private val INSTANCE by lazy { OpenGLESDebugCallback() }
        fun create(): OpenGLESDebugCallback {
            return INSTANCE
        }
    }

    override fun onMessage(
        source: Int,
        type: Int,
        id: Int,
        severity: Int,
        message: String?
    ) {
        when (severity) {
            GL_DEBUG_SEVERITY_HIGH -> {
                throw RuntimeException(message) // Terminate immediately on high severity
            }

            GL_DEBUG_SEVERITY_MEDIUM -> {
                logger.warn("OpenGL MEDIUM: $message")
            }

            GL_DEBUG_SEVERITY_LOW -> {
                logger.info("OpenGL LOW: $message")
            }

            GL_DEBUG_SEVERITY_NOTIFICATION -> {
                logger.debug("OpenGL NOTIFICATION: {}", message)
            }

            else -> {
                logger.debug("OpenGL MESSAGE: {}", message)
            }
        }

        val sourceString = getSourceString(source)
        val typeString = getDebugMessageType(type)

        logger.debug("OpenGL Debug Message - Source: $sourceString, Type: $typeString, ID: $id")
    }

    private fun getSourceString(source: Int): String = when (source) {
        GL_DEBUG_SOURCE_API -> "API"
        GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "WINDOW_SYSTEM"
        GL_DEBUG_SOURCE_SHADER_COMPILER -> "SHADER_COMPILER"
        GL_DEBUG_SOURCE_THIRD_PARTY -> "THIRD_PARTY"
        GL_DEBUG_SOURCE_APPLICATION -> "APPLICATION"
        GL_DEBUG_SOURCE_OTHER -> "OTHER"
        else -> "UNKNOWN"
    }

    private fun getDebugMessageType(type: Int): String = when (type) {
        GL_DEBUG_TYPE_ERROR -> "ERROR"
        GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "DEPRECATED_BEHAVIOR"
        GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "UNDEFINED_BEHAVIOR"
        GL_DEBUG_TYPE_PORTABILITY -> "PORTABILITY"
        GL_DEBUG_TYPE_PERFORMANCE -> "PERFORMANCE"
        GL_DEBUG_TYPE_MARKER -> "MARKER"
        GL_DEBUG_TYPE_PUSH_GROUP -> "PUSH_GROUP"
        GL_DEBUG_TYPE_POP_GROUP -> "POP_GROUP"
        GL_DEBUG_TYPE_OTHER -> "OTHER"
        else -> "UNKNOWN"
    }
}