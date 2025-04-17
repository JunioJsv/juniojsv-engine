package juniojsv.engine.features.utils

import org.lwjgl.opengl.GL43
import org.lwjgl.opengl.GLDebugMessageCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OpenGLDebugCallback private constructor() : GLDebugMessageCallback() {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(OpenGLDebugCallback::class.java)
        private val INSTANCE by lazy { OpenGLDebugCallback() }
        fun create(): OpenGLDebugCallback {
            return INSTANCE
        }
    }

    override fun invoke(
        source: Int,
        type: Int,
        id: Int,
        severity: Int,
        length: Int,
        message: Long,
        userParam: Long
    ) {
        val messageStr = getMessage(length, message)

        when (severity) {
            GL43.GL_DEBUG_SEVERITY_HIGH -> {
                throw RuntimeException(messageStr) // Terminate immediately on high severity
            }

            GL43.GL_DEBUG_SEVERITY_MEDIUM -> {
                logger.warn("OpenGL MEDIUM: $messageStr")
            }

            GL43.GL_DEBUG_SEVERITY_LOW -> {
                logger.info("OpenGL LOW: $messageStr")
            }

            GL43.GL_DEBUG_SEVERITY_NOTIFICATION -> {
                logger.debug("OpenGL NOTIFICATION: $messageStr")
            }

            else -> {
                logger.debug("OpenGL MESSAGE: $messageStr")
            }
        }

        val sourceString = getSourceString(source)
        val typeString = getDebugMessageType(type)

        logger.debug("OpenGL Debug Message - Source: $sourceString, Type: $typeString, ID: $id")
    }

    private fun getSourceString(source: Int): String = when (source) {
        GL43.GL_DEBUG_SOURCE_API -> "API"
        GL43.GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "WINDOW_SYSTEM"
        GL43.GL_DEBUG_SOURCE_SHADER_COMPILER -> "SHADER_COMPILER"
        GL43.GL_DEBUG_SOURCE_THIRD_PARTY -> "THIRD_PARTY"
        GL43.GL_DEBUG_SOURCE_APPLICATION -> "APPLICATION"
        GL43.GL_DEBUG_SOURCE_OTHER -> "OTHER"
        else -> "UNKNOWN"
    }

    private fun getDebugMessageType(type: Int): String = when (type) {
        GL43.GL_DEBUG_TYPE_ERROR -> "ERROR"
        GL43.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "DEPRECATED_BEHAVIOR"
        GL43.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "UNDEFINED_BEHAVIOR"
        GL43.GL_DEBUG_TYPE_PORTABILITY -> "PORTABILITY"
        GL43.GL_DEBUG_TYPE_PERFORMANCE -> "PERFORMANCE"
        GL43.GL_DEBUG_TYPE_MARKER -> "MARKER"
        GL43.GL_DEBUG_TYPE_PUSH_GROUP -> "PUSH_GROUP"
        GL43.GL_DEBUG_TYPE_POP_GROUP -> "POP_GROUP"
        GL43.GL_DEBUG_TYPE_OTHER -> "OTHER"
        else -> "UNKNOWN"
    }

}