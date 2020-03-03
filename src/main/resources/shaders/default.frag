#version 330

in vec3 out_vertex_position;
in vec2 out_uv_coordinates;
in vec3 out_vertex_normal;

uniform float sys_time;
uniform vec3 light_position;
uniform vec3 light_color;
uniform vec3 fog_color;

uniform float camera_near;
uniform float camera_far;

uniform int has_texture;
uniform sampler2D in_texture;

float near = .1;
float far = 1000.;

void main() {
    vec3 n_light_position = normalize(light_position - out_vertex_position);
    float brightness = max(dot(out_vertex_normal, n_light_position), 0.);
    vec3 diffuse = brightness * light_color;

    float z = gl_FragCoord.z * 2. - 1.;
    z = (2. * camera_near * camera_far) / (camera_far + camera_near - z * (camera_far - camera_near));
    vec4 fog = vec4(vec3(z / camera_far) * fog_color, 1.);

    gl_FragColor = (has_texture == 1 ?
                        texture(in_texture, out_uv_coordinates) * vec4(diffuse, 1.) :
                            vec4(diffuse, 1.)) + fog;
}