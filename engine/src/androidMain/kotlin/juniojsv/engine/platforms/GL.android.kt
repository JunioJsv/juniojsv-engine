package juniojsv.engine.platforms

import android.opengl.GLES32
import android.opengl.GLES32.DebugProc
import juniojsv.engine.features.utils.ShaderConverter
import juniojsv.engine.platforms.constants.GL_SHADER_TYPE
import juniojsv.engine.platforms.constants.GL_VERTEX_SHADER
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

typealias imp = GLES32

actual typealias GLDebugMessageCallback = DebugProc

actual object GL : AndroidOpenGLES()

open class AndroidOpenGLES : IGL {
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
        imp.glDebugMessageCallback(callback)
    }

    override fun glGenVertexArrays(): Int {
        val array = IntArray(1)
        imp.glGenVertexArrays(1, array, 0)
        return array[0]
    }

    override fun glBindVertexArray(array: Int) {
        imp.glBindVertexArray(array)
    }

    override fun glDeleteVertexArrays(array: Int) {
        imp.glDeleteVertexArrays(1, IntArray(array), 0)
    }

    override fun glGenBuffers(): Int {
        val buffer = IntArray(1)
        imp.glGenBuffers(1, buffer, 0)
        return buffer[0]
    }

    override fun glBindBuffer(target: Int, buffer: Int) {
        imp.glBindBuffer(target, buffer)
    }

    override fun glBufferData(target: Int, data: FloatBuffer, usage: Int) {
        imp.glBufferData(target, data.capacity() * 4, data, usage)
    }

    override fun glBufferData(target: Int, data: IntBuffer, usage: Int) {
        imp.glBufferData(target, data.capacity() * 4, data, usage)
    }

    override fun glBufferData(target: Int, size: Long, usage: Int) {
        imp.glBufferData(target, size.toInt(), null, usage)
    }

    override fun glDeleteBuffers(buffer: Int) {
        imp.glDeleteBuffers(1, IntArray(buffer), 0)
    }

    override fun glDeleteBuffers(buffers: IntArray) {
        imp.glDeleteBuffers(buffers.size, buffers, 0)
    }

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        offset: Int
    ) {
        imp.glVertexAttribPointer(index, size, type, normalized, stride, offset)
    }

    override fun glVertexAttribIPointer(
        index: Int,
        size: Int,
        type: Int,
        stride: Int,
        offset: Int
    ) {
        imp.glVertexAttribIPointer(index, size, type, stride, offset)
    }

    override fun glEnableVertexAttribArray(index: Int) {
        imp.glEnableVertexAttribArray(index)
    }

    override fun glGetInteger(pname: Int): Int {
        val integer = IntArray(1)
        imp.glGetIntegerv(pname, integer, 0)
        return integer[0]
    }

    override fun glLineWidth(width: Float) {
        imp.glLineWidth(width)
    }

    override fun glCreateShader(type: Int): Int {
        return imp.glCreateShader(type)
    }

    override fun glShaderSource(shader: Int, source: String) {
        val type = IntArray(1)
        imp.glGetShaderiv(shader, GL_SHADER_TYPE, type, 0)
        val isVertexShader = type[0] == GL_VERTEX_SHADER
        val converted = ShaderConverter.fromGLtoGLES(source, isVertexShader)
        imp.glShaderSource(shader, converted)
    }

    override fun glCompileShader(shader: Int) {
        imp.glCompileShader(shader)
    }

    override fun glGetShaderi(shader: Int, pname: Int): Int {
        val integer = IntArray(1)
        imp.glGetShaderiv(shader, pname, integer, 0)
        return integer[0]
    }

    override fun glGetShaderInfoLog(shader: Int): String {
        val log = imp.glGetShaderInfoLog(shader)
        return log
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
        imp.glUniformMatrix4fv(location, 1, transpose, value)
    }

    override fun glUniform1i(location: Int, value: Int) {
        imp.glUniform1i(location, value)
    }

    override fun glUniform1f(location: Int, value: Float) {
        imp.glUniform1f(location, value)
    }

    override fun glUniform3fv(location: Int, value: FloatBuffer) {
        imp.glUniform3fv(location, 1, value)
    }

    override fun glUniform2fv(location: Int, value: FloatBuffer) {
        imp.glUniform2fv(location, 1, value)
    }

    override fun glUniform1iv(location: Int, value: IntBuffer) {
        imp.glUniform1iv(location, 1, value)
    }

    override fun glValidateProgram(program: Int) {
        imp.glValidateProgram(program)
    }

    override fun glUseProgram(program: Int) {
        imp.glUseProgram(program)
    }

    override fun glGetProgrami(program: Int, pname: Int): Int {
        val integer = IntArray(1)
        imp.glGetProgramiv(program, pname, integer, 0)
        return integer[0]
    }

    override fun glGetProgramInfoLog(program: Int): String {
        val log = imp.glGetProgramInfoLog(program)
        return log
    }

    override fun glDeleteProgram(program: Int) {
        imp.glDeleteProgram(program)
    }

    override fun glDrawElements(mode: Int, count: Int, type: Int, indices: Int) {
        imp.glDrawElements(mode, count, type, indices)
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
        val texture = IntArray(1)
        imp.glGenTextures(1, texture, 0)
        return texture[0]
    }

    override fun glGenerateMipmap(target: Int) {
        imp.glGenerateMipmap(target)
    }

    override fun glDeleteTextures(texture: Int) {
        imp.glDeleteTextures(1, IntArray(texture), 0)
    }

    override fun glTexParameteri(target: Int, pname: Int, param: Int) {
        imp.glTexParameteri(target, pname, param)
    }

    override fun glTexParameterfv(target: Int, pname: Int, params: FloatArray) {
        imp.glTexParameterfv(target, pname, params, 0)
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
        imp.glDeleteFramebuffers(1, IntArray(framebuffer), 0)
    }

    override fun glCheckFramebufferStatus(target: Int): Int {
        return imp.glCheckFramebufferStatus(target)
    }

    override fun glDrawBuffers(buf: Int) {
        imp.glDrawBuffers(1, IntArray(buf), 0)
    }

    override fun glDrawBuffers(bufs: IntArray) {
        imp.glDrawBuffers(bufs.size, bufs, 0)
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
        val framebuffer = IntArray(1)
        imp.glGenFramebuffers(1, framebuffer, 0)
        return framebuffer[0]
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
        imp.glDrawElementsInstanced(mode, count, type, indices, primcount)
    }

    override fun glMapBufferRange(
        target: Int,
        offset: Long,
        length: Long,
        access: Int
    ): ByteBuffer? {
        val buffer = imp.glMapBufferRange(target, offset.toInt(), length.toInt(), access)
        return buffer as ByteBuffer
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