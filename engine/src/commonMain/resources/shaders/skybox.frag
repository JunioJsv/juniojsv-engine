#version 330

in vec3 vWorldPosition;

uniform samplerCube uSkyBox;

out vec4 oFragColor;

void main() {
    vec4 skybox = texture(uSkyBox, vWorldPosition);
    oFragColor = vec4(vec3(skybox), 1);
}