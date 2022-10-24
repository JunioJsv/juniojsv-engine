#version 330

in vec3 out_vertex_position;
in vec2 out_uv_coordinates;
in vec3 out_vertex_normal;

uniform vec3 light_position;
uniform vec3 light_color;

uniform float camera_near;
uniform float camera_far;
uniform float time;

uniform sampler2D in_texture;

void main() {
    vec3 color = vec3(1);
    float sun = step(mod(out_uv_coordinates.y * 50. + time, .6), -0.2 + 0.35 * out_uv_coordinates.y * 3.7);
    color.x *= sun;
    color.y *= sun * (0.6 + out_uv_coordinates.y - 0.7);
    color.z = 0.;

    gl_FragColor = vec4(max(color, vec3(.8, .2, .0)), 1.);
}