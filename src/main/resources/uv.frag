#version 330

in vec3 uv;

uniform float sys_time;

void main() {
    gl_FragColor = vec4(vec3(uv), 1.);
}