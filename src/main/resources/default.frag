#version 330

in vec3 out_position;
in vec3 out_normal;

uniform float sys_time;

void main() {
    vec3 light_position = vec3(0, 1.45, 0);
    vec3 cel_shader;
    vec3 color = vec3(
        cos(sys_time),
        sin(sys_time),
        -cos(sys_time)
    );
    color = max(color, 0.35);

    float intensity = dot(
        light_position,
        out_normal
    );

    cel_shader = intensity > .95 ? color :
                    intensity > .5 ? color * .65 :
                        intensity > .25 ? color * .45 :
                            color * .20;

    gl_FragColor = vec4(cel_shader, 1.0);
}