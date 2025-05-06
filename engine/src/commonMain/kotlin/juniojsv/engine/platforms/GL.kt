package juniojsv.engine.platforms

import juniojsv.engine.features.textures.RawTexture
import juniojsv.engine.features.textures.Texture
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

expect interface GLDebugMessageCallback

interface IGLUtils {
    fun getTextureData(texture: Texture): RawTexture
}

interface IGL {
    fun glEnable(target: Int)
    fun glDisable(target: Int)
    fun glCullFace(mode: Int)
    fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float)
    fun glClear(mask: Int)
    fun glViewport(x: Int, y: Int, width: Int, height: Int)
    fun glDebugMessageCallback(callback: GLDebugMessageCallback)
    fun glGenVertexArrays(): Int
    fun glBindVertexArray(array: Int)
    fun glDeleteVertexArrays(array: Int)
    fun glGenBuffers(): Int
    fun glBindBuffer(target: Int, buffer: Int)
    fun glBufferData(target: Int, data: FloatBuffer, usage: Int)
    fun glBufferData(target: Int, data: IntBuffer, usage: Int)
    fun glBufferData(target: Int, size: Long, usage: Int)
    fun glDeleteBuffers(buffer: Int)
    fun glDeleteBuffers(buffers: IntArray)
    fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        offset: Int
    )

    fun glVertexAttribIPointer(index: Int, size: Int, type: Int, stride: Int, offset: Int)
    fun glEnableVertexAttribArray(index: Int)
    fun glGetInteger(pname: Int): Int
    fun glLineWidth(width: Float)
    fun glCreateShader(type: Int): Int
    fun glShaderSource(shader: Int, source: String)
    fun glCompileShader(shader: Int)
    fun glGetShaderi(shader: Int, pname: Int): Int
    fun glGetShaderInfoLog(shader: Int): String
    fun glDeleteShader(shader: Int)
    fun glCreateProgram(): Int
    fun glAttachShader(program: Int, shader: Int)
    fun glLinkProgram(program: Int)
    fun glGetUniformLocation(program: Int, name: String): Int
    fun glUniformMatrix4fv(location: Int, transpose: Boolean, value: FloatBuffer)
    fun glUniform1i(location: Int, value: Int)
    fun glUniform1f(location: Int, value: Float)
    fun glUniform3fv(location: Int, value: FloatBuffer)
    fun glUniform2fv(location: Int, value: FloatBuffer)
    fun glUniform1iv(location: Int, value: IntBuffer)
    fun glValidateProgram(program: Int)
    fun glUseProgram(program: Int)
    fun glGetProgrami(program: Int, pname: Int): Int
    fun glGetProgramInfoLog(program: Int): String
    fun glDeleteProgram(program: Int)
    fun glDrawElements(mode: Int, count: Int, type: Int, indices: Int)
    fun glDrawArrays(mode: Int, first: Int, count: Int)
    fun glActiveTexture(texture: Int)
    fun glBindTexture(target: Int, texture: Int)
    fun glGenTextures(): Int
    fun glGenerateMipmap(target: Int)
    fun glDeleteTextures(texture: Int)
    fun glTexParameteri(target: Int, pname: Int, param: Int)
    fun glTexParameterfv(target: Int, pname: Int, params: FloatArray)
    fun glTexImage2D(
        target: Int,
        level: Int,
        internalformat: Int,
        width: Int,
        height: Int,
        border: Int,
        format: Int,
        type: Int,
        pixels: ByteBuffer?
    )

    fun glTexImage2D(
        target: Int,
        level: Int,
        internalformat: Int,
        width: Int,
        height: Int,
        border: Int,
        format: Int,
        type: Int,
        pixels: IntBuffer?
    )

    fun glCopyTexImage2D(
        target: Int,
        level: Int,
        internalformat: Int,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        border: Int
    )

    fun glDeleteFramebuffers(framebuffer: Int)
    fun glCheckFramebufferStatus(target: Int): Int
    fun glDrawBuffers(buf: Int)
    fun glDrawBuffers(bufs: IntArray)
    fun glReadBuffer(src: Int)
    fun glFramebufferTexture2D(
        target: Int,
        attachment: Int,
        textarget: Int,
        texture: Int,
        level: Int
    )

    fun glGenFramebuffers(): Int
    fun glBlitFramebuffer(
        srcX0: Int,
        srcY0: Int,
        srcX1: Int,
        srcY1: Int,
        dstX0: Int,
        dstY0: Int,
        dstX1: Int,
        dstY1: Int,
        mask: Int,
        filter: Int
    )

    fun glBindFramebuffer(target: Int, framebuffer: Int)
    fun glVertexAttribDivisor(index: Int, divisor: Int)
    fun glDrawElementsInstanced(mode: Int, count: Int, type: Int, indices: Int, primcount: Int)
    fun glMapBufferRange(target: Int, offset: Long, length: Long, access: Int): ByteBuffer?
    fun glUnmapBuffer(target: Int): Boolean
    fun glGetShaderSource(shader: Int): String
    fun glGetString(name: Int): String?
    fun glBindAttribLocation(program: Int, index: Int, name: String)
    fun glGetTexImage(target: Int, level: Int, format: Int, type: Int, pixels: IntBuffer)
    fun glReadPixels(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        format: Int,
        type: Int,
        pixels: IntBuffer
    )
}

expect object GL : IGL
expect object GLUtils : IGLUtils