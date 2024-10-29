#version 330

in vec3 out_vertex_position;
in vec2 out_uv_coordinates;
in vec3 out_vertex_normal;
in vec3 out_unnormal_vertex_position;

uniform vec3 light_position;
uniform vec3 light_color;

uniform sampler2D in_texture;

void main() {
    vec3 n_light_position = normalize(light_position - out_vertex_position);
    float brightness = max(dot(out_vertex_normal, n_light_position), out_unnormal_vertex_position.y * 0.001);
    vec3 diffuse = brightness * light_color;

    gl_FragColor = texture(in_texture, out_uv_coordinates) * vec4(diffuse, 1.);
}
