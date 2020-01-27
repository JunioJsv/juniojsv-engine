#version 330

in vec3 position;

out vec3 color;

uniform mat4 transformation;
uniform mat4 projection;
uniform mat4 camera;

void main() {
    color = position;
    gl_Position = projection * camera * transformation * vec4(position, 1.);
}