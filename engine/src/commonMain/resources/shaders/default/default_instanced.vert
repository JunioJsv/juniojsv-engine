#version 330

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aUV;
layout (location = 2) in vec3 aNormal;
layout (location = 3) in mat4 aModel;
layout (location = 11) in int aTextureIndex;
layout (location = 12) in vec2 aUVScale;
layout (location = 13) in vec2 aUVOffest;

out vec3 vWorldPosition;
out vec2 vUV;
out vec3 vNormal;
flat out int vTextureIndex;
flat out vec2 vUVScale;
flat out vec2 vUVOffest;
out vec4 vFragLightPosition;

uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uLightSpaceMatrix;

void main() {
    vWorldPosition = (aModel * vec4(aPosition, 1.)).xyz;
    vUV = aUV;
    vNormal = normalize((aModel * vec4(aNormal, 0.)).xyz);
    vTextureIndex = aTextureIndex;
    vUVScale = aUVScale;
    vUVOffest = aUVOffest;
    vFragLightPosition = uLightSpaceMatrix * vec4(vWorldPosition, 1.);

    gl_Position = uProjection * uView * vec4(vWorldPosition, 1.);
}