#version 330

in vec2 vUV;

uniform sampler2D uSceneTexture;
uniform sampler2D uVelocityTexture;
uniform sampler2D uOverlayTexture;
uniform sampler2D uPreviousFrameTexture;
uniform float uMotionBlur;

out vec4 oColor;

void main() {
    float velocityScale = uMotionBlur;

    vec2 texelSize = 1.0 / vec2(textureSize(uSceneTexture, 0));

    vec2 velocity = texture(uVelocityTexture, vUV).rg;
    velocity *= velocityScale;

    float speed = length(velocity / texelSize);
    int samples = clamp(int(speed), 1, 32);

    oColor = texture(uSceneTexture, vUV);
    for (int i = 1; i < samples; ++i) {
        vec2 offset = velocity * (float(i) / float(samples - 1) - 0.5);
        oColor += texture(uSceneTexture, vUV + offset);
    }
    oColor /= float(samples);
    oColor += texture(uOverlayTexture, vUV);
}
