vec3 getTextureColor() {
    if (vTextureIndex == 0) return texture(uTextures[0], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 1) return texture(uTextures[1], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 2) return texture(uTextures[2], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 3) return texture(uTextures[3], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 4) return texture(uTextures[4], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 5) return texture(uTextures[5], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 6) return texture(uTextures[6], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 7) return texture(uTextures[7], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 8) return texture(uTextures[8], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 9) return texture(uTextures[9], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 10) return texture(uTextures[10], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 11) return texture(uTextures[11], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 12) return texture(uTextures[12], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 13) return texture(uTextures[13], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 14) return texture(uTextures[14], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 15) return texture(uTextures[15], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 16) return texture(uTextures[16], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 17) return texture(uTextures[17], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 18) return texture(uTextures[18], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 19) return texture(uTextures[19], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 20) return texture(uTextures[20], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 21) return texture(uTextures[21], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 22) return texture(uTextures[22], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 23) return texture(uTextures[23], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 24) return texture(uTextures[24], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 25) return texture(uTextures[25], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 26) return texture(uTextures[26], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 27) return texture(uTextures[27], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 28) return texture(uTextures[28], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 29) return texture(uTextures[29], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 30) return texture(uTextures[30], vUV * vTextureScale).rgb;
    else if (vTextureIndex == 31) return texture(uTextures[31], vUV * vTextureScale).rgb;

    return vec3(0.0);
}
