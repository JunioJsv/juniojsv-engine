#version 330

in vec3 out_vertex_position;

uniform samplerCube in_skybox;

void main() {
    gl_FragColor = texture(in_skybox, out_vertex_position);
}