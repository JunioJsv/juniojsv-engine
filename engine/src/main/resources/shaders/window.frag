#version 330

in vec2 vUV;

uniform sampler2D uSceneTexture;
uniform sampler2D uSceneDepthTexture;
uniform sampler2D uVelocityTexture;
uniform sampler2D uOverlayTexture;
uniform sampler2D uPreviousFrameTexture;
uniform float uVelocityScale;

out vec4 oColor;

void main() {
    vec2 texelSize = 1.0 / vec2(textureSize(uSceneTexture, 0));

    vec2 velocity = texture(uVelocityTexture, vUV).rg;
    velocity *= uVelocityScale;

    float speed = length(velocity / texelSize);
    int samples = clamp(int(speed), 1, 32);

    float centerDepth = texture(uSceneDepthTexture, vUV).r;
    float depthThreshold = 0.01;

    vec4 color = texture(uSceneTexture, vUV);
    float weight = 1.0;

    for (int i = 1; i < samples; ++i) {
        vec2 offset = velocity * (float(i) / float(samples - 1) - 0.5);
        vec2 sampleUV = vUV + offset;

        float sampleDepth = texture(uSceneDepthTexture, sampleUV).r;

        if (abs(sampleDepth - centerDepth) < depthThreshold) {
            color += texture(uSceneTexture, sampleUV);
            weight += 1.0;
        }
    }

    color /= weight;
    color += texture(uOverlayTexture, vUV);

    oColor = color;
}