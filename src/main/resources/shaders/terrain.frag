#version 330

in vec3 out_vertex_position;
in vec2 out_uv_coordinates;
in vec3 out_vertex_normal;
in vec3 out_unnormal_vertex_position;

uniform vec3 light_position;
uniform vec3 light_color;

uniform float camera_near;
uniform float camera_far;
uniform float time;
uniform vec3 camera_position;

uniform sampler2D in_texture;

void main() {
    vec3 n_light_position = normalize(light_position - out_vertex_position);
    vec3 normal = normalize(out_vertex_normal);

    // Calcular a componente difusa
    float brightness = max(dot(normal, n_light_position), 0.0);
    vec3 diffuse = brightness * light_color;

    // Calcular a componente especular
    vec3 view_dir = normalize(camera_position - out_vertex_position);
    vec3 reflect_dir = reflect(-n_light_position, normal);

    float shininess = 6.0;// Fator de brilho ajustável
    float spec = pow(max(dot(view_dir, reflect_dir), 0.0), shininess);
    float specular_strength = .5;// Ajuste a intensidade especular
    vec3 specular = spec * specular_strength * vec3(1) * light_color;// Brilho branco

    // Calcular a cor final
    vec3 texture_color = texture(in_texture, out_uv_coordinates).rgb;
    vec3 final_color = texture_color * (diffuse + specular);

    float y = out_unnormal_vertex_position.y;

    if (y * 0.008 <= .2) discard;
    if (y * 0.008 <= .21) {
        gl_FragColor = vec4(vec3(0), 1);
        return;
    }

    gl_FragColor = vec4(final_color, 1.);
}