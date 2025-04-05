#version 330

in vec3 out_vertex_position;
in vec2 out_uv_coordinates;

uniform sampler2D in_texture;

void main() {
    gl_FragColor = texture(in_texture, out_uv_coordinates);
}
