#version 330

in vec3 out_vertex_position;
in vec2 out_uv_coordinates;
in vec3 out_vertex_normal;

uniform vec3 light_position;
uniform vec3 light_color;
uniform vec3 fog_color;
uniform vec3 offset;

uniform float camera_near;
uniform float camera_far;

uniform int has_texture;
uniform sampler2D in_texture;

void main() {
    vec3 n_light_position = normalize(light_position - out_vertex_position);
    float brightness = max(dot(out_vertex_normal, n_light_position), 0.);
    vec3 diffuse = brightness * light_color;

    float z = gl_FragCoord.z * 2. - 1.;
    z = (2. * camera_near * camera_far) / (camera_far + camera_near - z * (camera_far - camera_near));
    vec4 fog = vec4(vec3(z / camera_far) * fog_color, 1.);

    if (z / camera_far < .01) {
        vec3 grid = vec3(cos(gl_FragCoord));
        if (grid.x > .05 || grid.y > .05)
            discard;
    }

    gl_FragColor = (has_texture == 1 ?
        texture(in_texture, out_uv_coordinates) * vec4(diffuse, 1.) :
            vec4(diffuse, 1.)) + fog;
    gl_FragColor += vec4(offset, 1) * .05;
    if(z / camera_far < .0105 && z / camera_far > 0.01)
        gl_FragColor += vec4(.75 , 0, 0, 1);
}