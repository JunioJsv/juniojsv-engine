#version 330

in vec3 vWorldPosition;
in vec2 vUV;
in vec3 vNormal;
in vec4 vFragLightPosition;

uniform vec3 uLightPosition;
uniform vec3 uLightColor;
uniform sampler2D uTexture;
uniform sampler2D uShadowMapTexture;
uniform float uTextureScale;
uniform vec3 uCameraPosition;

#include <lighting.glsl>
#include <shadows.glsl>

void main() {
    float shadow = calculateShadow(vFragLightPosition);
    vec3 lighting = (1.0 - shadow) * calculateLighting();

    vec3 textureColor = texture(uTexture, vUV * uTextureScale).rgb;
    vec3 finalColor = textureColor * lighting;

    gl_FragColor = vec4(finalColor, 1.0);
}