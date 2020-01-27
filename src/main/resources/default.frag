#version 330

in vec3 color;

uniform float sys_time;

void main() {
    float intensity = dot(vec3(-cos(sys_time) / 2, sin(sys_time) / 2, 0.55), color);

    if (intensity > 0.95)
    gl_FragColor = vec4(0.5, 1.0, 0.5, 1.0);
    else if (intensity > 0.5)
    gl_FragColor = vec4(0.3, 0.6, 0.3, 1.0);
    else if (intensity > 0.25)
    gl_FragColor = vec4(0.2, 0.4, 0.2, 1.0);
    else
    gl_FragColor = vec4(0.1, 0.2, 0.1, 1.0);

}