vec3 getTextureColor() {
    vec2 uv = vUVOffest + vUV * vUVScale;
    if (vTextureIndex == 0) return texture(uTextures[0], uv).rgb;
    else if (vTextureIndex == 1) return texture(uTextures[1], uv).rgb;
    else if (vTextureIndex == 2) return texture(uTextures[2], uv).rgb;
    else if (vTextureIndex == 3) return texture(uTextures[3], uv).rgb;
    else if (vTextureIndex == 4) return texture(uTextures[4], uv).rgb;
    else if (vTextureIndex == 5) return texture(uTextures[5], uv).rgb;
    else if (vTextureIndex == 6) return texture(uTextures[6], uv).rgb;
    else if (vTextureIndex == 7) return texture(uTextures[7], uv).rgb;
    else if (vTextureIndex == 8) return texture(uTextures[8], uv).rgb;
    else if (vTextureIndex == 9) return texture(uTextures[9], uv).rgb;
    else if (vTextureIndex == 10) return texture(uTextures[10], uv).rgb;
    else if (vTextureIndex == 11) return texture(uTextures[11], uv).rgb;
    else if (vTextureIndex == 12) return texture(uTextures[12], uv).rgb;
    else if (vTextureIndex == 13) return texture(uTextures[13], uv).rgb;
    else if (vTextureIndex == 14) return texture(uTextures[14], uv).rgb;
    else if (vTextureIndex == 15) return texture(uTextures[15], uv).rgb;
    else if (vTextureIndex == 16) return texture(uTextures[16], uv).rgb;
    else if (vTextureIndex == 17) return texture(uTextures[17], uv).rgb;
    else if (vTextureIndex == 18) return texture(uTextures[18], uv).rgb;
    else if (vTextureIndex == 19) return texture(uTextures[19], uv).rgb;
    else if (vTextureIndex == 20) return texture(uTextures[20], uv).rgb;
    else if (vTextureIndex == 21) return texture(uTextures[21], uv).rgb;
    else if (vTextureIndex == 22) return texture(uTextures[22], uv).rgb;
    else if (vTextureIndex == 23) return texture(uTextures[23], uv).rgb;
    else if (vTextureIndex == 24) return texture(uTextures[24], uv).rgb;
    else if (vTextureIndex == 25) return texture(uTextures[25], uv).rgb;
    else if (vTextureIndex == 26) return texture(uTextures[26], uv).rgb;
    else if (vTextureIndex == 27) return texture(uTextures[27], uv).rgb;
    else if (vTextureIndex == 28) return texture(uTextures[28], uv).rgb;
    else if (vTextureIndex == 29) return texture(uTextures[29], uv).rgb;
    else if (vTextureIndex == 30) return texture(uTextures[30], uv).rgb;
    else if (vTextureIndex == 31) return texture(uTextures[31], uv).rgb;

    return vec3(0.0);
}
