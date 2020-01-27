#version 330

layout (location = 0) in vec3 position;
layout (location = 2) in vec3 normal;

out vec3 out_position;
out vec3 out_normal;

uniform mat4 transformation;
uniform mat4 projection;
uniform mat4 camera;

void main() {
    out_position = position;
    out_normal = normal;
    gl_Position = projection * camera * transformation * vec4(position, 1.);
}