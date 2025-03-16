#version 330

in vec3 out_vertex_position;
in vec2 out_uv_coordinates;
in vec3 out_vertex_normal;
flat in int out_texture_index;
flat in float out_texture_scale;

uniform vec3 light_position;
uniform vec3 light_color;
uniform sampler2D textures[16];
uniform vec3 camera_position;

void main() {
    vec3 texture_color = vec3(0);
    if (out_texture_index != - 1) {
        texture_color = texture(textures[out_texture_index], out_uv_coordinates * out_texture_scale).rgb;
    }
    vec3 final_color = texture_color;

    gl_FragColor = vec4(final_color, 1.0);
}
