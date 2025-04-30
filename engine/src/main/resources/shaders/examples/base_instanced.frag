#version 330

in vec3 vWorldPosition;
in vec2 vUV;
in vec3 vNormal;
flat in int vTextureIndex;
flat in float vTextureScale;

uniform vec3 uLightPosition;
uniform vec3 uLightColor;
uniform sampler2D uTextures[16];
uniform vec3 uCameraPosition;

void main() {
    // YOUR SHADER CODE
}
