#version 330

attribute vec3 vertices;

out vec3 uv;

uniform mat4 transformation;
uniform mat4 projection;

void main() {
    uv = vertices;
    gl_Position = projection * transformation * vec4(vertices, 1.);
}