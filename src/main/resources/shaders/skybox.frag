#version 330

in vec3 out_vertice_position;

uniform samplerCube in_skybox;

void main() {
    gl_FragColor = texture(in_skybox, out_vertice_position);
}