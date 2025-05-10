#version 330

in vec3 vNormal;

out vec4 oFragColor;

void main() {
    oFragColor = vec4(vNormal, 1);
}