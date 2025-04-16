#version 330

in vec3 vWorldPosition;
in vec2 vUV;
in vec3 vNormal;
flat in int vTextureIndex;
flat in float vTextureScale;
in vec4 vFragLightPosition;

uniform vec3 uLightPosition;
uniform vec3 uLightColor;
uniform sampler2D uTextures[16];
uniform vec3 uCameraPosition;
uniform sampler2D uShadowMapTexture;

#include <shadows.glsl>
#include <lighting.glsl>

void main() {
    float shadow = calculateShadow(vFragLightPosition);
    vec3 lighting = (1.0 - shadow) * calculateLighting();

    vec3 textureColor = vec3(0);
    if (vTextureIndex != -1) {
        textureColor = texture(uTextures[vTextureIndex], vUV * vTextureScale).rgb;
    }
    vec3 finalColor = textureColor * lighting;

    gl_FragColor = vec4(finalColor, 1.0);
}