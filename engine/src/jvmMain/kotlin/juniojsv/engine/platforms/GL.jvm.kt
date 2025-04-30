package juniojsv.engine.platforms

import org.lwjgl.opengl.GL43
import org.lwjgl.opengl.GLCapabilities
import org.lwjgl.opengl.GLDebugMessageCallbackI
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import org.lwjgl.opengl.GL as Utils

private typealias imp = GL43

actual typealias GLDebugMessageCallback = GLDebugMessageCallbackI

actual object GL : JvmOpenGL()

open class JvmOpenGL : IGL {
    fun createCapabilities(): GLCapabilities {
        return Utils.createCapabilities()
    }

    fun getCapabilities(): GLCapabilities {
        return Utils.getCapabilities()
    }

    override fun glEnable(target: Int) {
        imp.glEnable(target)
    }

    override fun glDisable(target: Int) {
        imp.glDisable(target)
    }

    override fun glCullFace(mode: Int) {
        imp.glCullFace(mode)
    }

    override fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float) {
        imp.glClearColor(red, green, blue, alpha)
    }

    override fun glClear(mask: Int) {
        imp.glClear(mask)
    }

    override fun glViewport(x: Int, y: Int, width: Int, height: Int) {
        imp.glViewport(x, y, width, height)
    }

    override fun glDebugMessageCallback(callback: GLDebugMessageCallback) {
        imp.glDebugMessageCallback(callback, 0L)
    }

    override fun glGenVertexArrays(): Int {
        return imp.glGenVertexArrays()
    }

    override fun glBindVertexArray(array: Int) {
        imp.glBindVertexArray(array)
    }

    override fun glDeleteVertexArrays(array: Int) {
        imp.glDeleteVertexArrays(array)
    }

    override fun glGenBuffers(): Int {
        return imp.glGenBuffers()
    }

    override fun glBindBuffer(target: Int, buffer: Int) {
        imp.glBindBuffer(target, buffer)
    }

    override fun glBufferData(target: Int, data: FloatBuffer, usage: Int) {
        imp.glBufferData(target, data, usage)
    }

    override fun glBufferData(target: Int, data: IntBuffer, usage: Int) {
        imp.glBufferData(target, data, usage)
    }

    override fun glBufferData(target: Int, size: Long, usage: Int) {
        imp.glBufferData(target, size, usage)
    }

    override fun glDeleteBuffers(buffer: Int) {
        imp.glDeleteBuffers(buffer)
    }

    override fun glDeleteBuffers(buffers: IntArray) {
        imp.glDeleteBuffers(buffers)
    }

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        offset: Int
    ) {
        imp.glVertexAttribPointer(index, size, type, normalized, stride, offset.toLong())
    }

    override fun glVertexAttribIPointer(
        index: Int,
        size: Int,
        type: Int,
        stride: Int,
        offset: Int
    ) {
        imp.glVertexAttribIPointer(index, size, type, stride, offset.toLong())
    }

    override fun glEnableVertexAttribArray(index: Int) {
        imp.glEnableVertexAttribArray(index)
    }

    override fun glGetInteger(pname: Int): Int {
        return imp.glGetInteger(pname)
    }

    override fun glLineWidth(width: Float) {
        imp.glLineWidth(width)
    }

    override fun glCreateShader(type: Int): Int {
        return imp.glCreateShader(type)
    }

    override fun glShaderSource(shader: Int, source: String) {
        imp.glShaderSource(shader, source)
    }

    override fun glCompileShader(shader: Int) {
        imp.glCompileShader(shader)
    }

    override fun glGetShaderi(shader: Int, pname: Int): Int {
        return imp.glGetShaderi(shader, pname)
    }

    override fun glGetShaderInfoLog(shader: Int): String {
        return imp.glGetShaderInfoLog(shader)
    }

    override fun glDeleteShader(shader: Int) {
        imp.glDeleteShader(shader)
    }

    override fun glCreateProgram(): Int {
        return imp.glCreateProgram()
    }

    override fun glAttachShader(program: Int, shader: Int) {
        imp.glAttachShader(program, shader)
    }

    override fun glLinkProgram(program: Int) {
        imp.glLinkProgram(program)
    }

    override fun glGetUniformLocation(program: Int, name: String): Int {
        return imp.glGetUniformLocation(program, name)
    }

    override fun glUniformMatrix4fv(location: Int, transpose: Boolean, value: FloatBuffer) {
        imp.glUniformMatrix4fv(location, transpose, value)
    }

    override fun glUniform1i(location: Int, value: Int) {
        imp.glUniform1i(location, value)
    }

    override fun glUniform1f(location: Int, value: Float) {
        imp.glUniform1f(location, value)
    }

    override fun glUniform3fv(location: Int, value: FloatBuffer) {
        imp.glUniform3fv(location, value)
    }

    override fun glUniform2fv(location: Int, value: FloatBuffer) {
        imp.glUniform2fv(location, value)
    }

    override fun glUniform1iv(location: Int, value: IntBuffer) {
        imp.glUniform1iv(location, value)
    }

    override fun glValidateProgram(program: Int) {
        imp.glValidateProgram(program)
    }

    override fun glUseProgram(program: Int) {
        imp.glUseProgram(program)
    }

    override fun glGetProgrami(program: Int, pname: Int): Int {
        return imp.glGetProgrami(program, pname)
    }

    override fun glGetProgramInfoLog(program: Int): String {
        return imp.glGetProgramInfoLog(program)
    }

    override fun glDeleteProgram(program: Int) {
        imp.glDeleteProgram(program)
    }

    override fun glDrawElements(mode: Int, count: Int, type: Int, indices: Int) {
        imp.glDrawElements(mode, count, type, indices.toLong())
    }

    override fun glDrawArrays(mode: Int, first: Int, count: Int) {
        imp.glDrawArrays(mode, first, count)
    }

    override fun glActiveTexture(texture: Int) {
        imp.glActiveTexture(texture)
    }

    override fun glBindTexture(target: Int, texture: Int) {
        imp.glBindTexture(target, texture)
    }

    override fun glGenTextures(): Int {
        return imp.glGenTextures()
    }

    override fun glGenerateMipmap(target: Int) {
        imp.glGenerateMipmap(target)
    }

    override fun glDeleteTextures(texture: Int) {
        imp.glDeleteTextures(texture)
    }

    override fun glTexParameteri(target: Int, pname: Int, param: Int) {
        imp.glTexParameteri(target, pname, param)
    }

    override fun glTexParameterfv(target: Int, pname: Int, params: FloatArray) {
        imp.glTexParameterfv(target, pname, params)
    }

    override fun glTexImage2D(
        target: Int,
        level: Int,
        internalformat: Int,
        width: Int,
        height: Int,
        border: Int,
        format: Int,
        type: Int,
        pixels: ByteBuffer?
    ) {
        imp.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels)
    }

    override fun glTexImage2D(
        target: Int,
        level: Int,
        internalformat: Int,
        width: Int,
        height: Int,
        border: Int,
        format: Int,
        type: Int,
        pixels: IntBuffer?
    ) {
        imp.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels)
    }

    override fun glCopyTexImage2D(
        target: Int,
        level: Int,
        internalformat: Int,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        border: Int
    ) {
        imp.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border)
    }

    override fun glDeleteFramebuffers(framebuffer: Int) {
        imp.glDeleteFramebuffers(framebuffer)
    }

    override fun glCheckFramebufferStatus(target: Int): Int {
        return imp.glCheckFramebufferStatus(target)
    }

    override fun glDrawBuffers(buf: Int) {
        imp.glDrawBuffers(buf)
    }

    override fun glDrawBuffers(bufs: IntArray) {
        imp.glDrawBuffers(bufs)
    }

    override fun glReadBuffer(src: Int) {
        imp.glReadBuffer(src)
    }

    override fun glFramebufferTexture2D(
        target: Int,
        attachment: Int,
        textarget: Int,
        texture: Int,
        level: Int
    ) {
        imp.glFramebufferTexture2D(target, attachment, textarget, texture, level)
    }

    override fun glGenFramebuffers(): Int {
        return imp.glGenFramebuffers()
    }

    override fun glBlitFramebuffer(
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
    ) {
        imp.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter)
    }

    override fun glBindFramebuffer(target: Int, framebuffer: Int) {
        imp.glBindFramebuffer(target, framebuffer)
    }

    override fun glVertexAttribDivisor(index: Int, divisor: Int) {
        imp.glVertexAttribDivisor(index, divisor)
    }

    override fun glDrawElementsInstanced(
        mode: Int,
        count: Int,
        type: Int,
        indices: Int,
        primcount: Int
    ) {
        imp.glDrawElementsInstanced(mode, count, type, indices.toLong(), primcount)
    }

    override fun glMapBufferRange(
        target: Int,
        offset: Long,
        length: Long,
        access: Int
    ): ByteBuffer? {
        return imp.glMapBufferRange(target, offset, length, access)
    }

    override fun glUnmapBuffer(target: Int): Boolean {
        return imp.glUnmapBuffer(target)
    }

    override fun glGetShaderSource(shader: Int): String {
        return imp.glGetShaderSource(shader)
    }

    override fun glGetString(name: Int): String? {
        return imp.glGetString(name)
    }

    override fun glBindAttribLocation(program: Int, index: Int, name: String) {
        imp.glBindAttribLocation(program, index, name)
    }
}