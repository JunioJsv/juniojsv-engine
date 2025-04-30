#version 330

in vec3 vWorldPosition;

uniform samplerCube uSkyBox;

void main() {
    vec4 skybox = texture(uSkyBox, vWorldPosition);
    gl_FragColor = vec4(vec3(skybox), 1);
}