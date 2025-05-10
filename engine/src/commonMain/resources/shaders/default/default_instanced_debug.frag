#version 330

in vec2 vUV;
flat in int vTextureIndex;
flat in vec2 vUVScale;
flat in vec2 vUVOffest;

uniform sampler2D uTextures[32];

out vec4 oFragColor;

#include <textures.glsl>

void main() {
    vec3 textureColor = vec3(0);
    if (vTextureIndex != -1) {
        textureColor = getTextureColor();
    }
    vec3 finalColor = textureColor;

    oFragColor = vec4(finalColor, 1.0);
}
