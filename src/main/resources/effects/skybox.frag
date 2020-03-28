#version 330

in vec3 out_vertex_position;

uniform samplerCube in_skybox;
uniform vec3 offset;

void main() {
    gl_FragColor = texture(in_skybox, out_vertex_position) * vec4(offset, 1);
}