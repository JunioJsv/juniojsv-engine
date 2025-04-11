#version 330

in vec2 vUV;
flat in int vTextureIndex;
flat in float vTextureScale;

uniform sampler2D uTextures[16];

void main() {
    vec3 textureColor = vec3(0);
    if (vTextureIndex != -1) {
        textureColor = texture(uTextures[vTextureIndex], vUV * vTextureScale).rgb;
    }
    vec3 finalColor = textureColor;

    gl_FragColor = vec4(finalColor, 1.0);
}
