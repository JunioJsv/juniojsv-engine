#version 330

in vec3 out_vertice_position;
in vec2 out_uv_coordinates;
in vec3 out_vertice_normal;

uniform float sys_time;
uniform vec3 light_position;
uniform vec3 light_color;

uniform int has_texture;
uniform sampler2D in_texture;

void main() {
    vec3 n_light_position = normalize(light_position - out_vertice_position);

    float brightness = dot(out_vertice_normal, n_light_position);
    brightness = max(brightness, 0.);

    vec3 light_color = brightness > .95 ? light_color * .95 :
        brightness > .5 ? light_color * .65  :
            brightness > .25 ? light_color * .30 :
                light_color * .15;
    vec3 diffuse = brightness * light_color;

    gl_FragColor = (has_texture == 1 ?
                        texture(in_texture, out_uv_coordinates) * vec4(diffuse, 1.) :
                            vec4(diffuse, 1.)) + vec4(.15, .15, .15, 1.);
}