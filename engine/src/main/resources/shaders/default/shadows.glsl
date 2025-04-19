uniform bool uIsShadowsEnabled;

float rand(vec2 co) {
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

bool isInsideLightFrustum(vec4 fragLightPos) {
    vec3 projCoords = fragLightPos.xyz / fragLightPos.w;

    return all(greaterThanEqual(projCoords, vec3(-1.0))) && all(lessThanEqual(projCoords, vec3(1.0)));
}

float calculateShadow(vec4 fragLightPos) {
    if (!uIsShadowsEnabled) return 0.0;

    vec3 projCoords = fragLightPos.xyz / fragLightPos.w;
    projCoords = projCoords * 0.5 + 0.5;
    if (projCoords.z > 1.0) return 0.0;

    float currentDepth = projCoords.z;
    float bias = 0.000;
    float shadow = 0.0;

    vec2 texelSize = 1.0 / vec2(textureSize(uShadowMapTexture, 0));
    float jitter = rand(gl_FragCoord.xy) * 0.5;

    vec2 sampleOffsets[8] = vec2[](
    vec2(-1.0, -1.0),
    vec2(1.0, -1.0),
    vec2(-1.0, 1.0),
    vec2(1.0, 1.0),

    vec2(-0.6, 0.2),
    vec2(0.6, -0.3),
    vec2(0.1, 0.7),
    vec2(-0.4, -0.6)
    );

    float sampleRadius = 1.5;

    for (int i = 0; i < 8; ++i) {
        vec2 offset = (sampleOffsets[i] + jitter) * sampleRadius * texelSize;
        float closestDepth = texture(uShadowMapTexture, projCoords.xy + offset).r;
        if (currentDepth > closestDepth + bias) {
            shadow += 1.0;
        }
    }

    shadow /= 8.0;

    return shadow * 0.65;
}