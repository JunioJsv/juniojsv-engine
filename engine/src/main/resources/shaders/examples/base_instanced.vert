#version 330

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aUV;
layout (location = 2) in vec3 aNormal;
layout (location = 3) in mat4 aModel;
layout (location = 7) in mat4 aPreviousModel;
layout (location = 11) in int aTextureIndex;
layout (location = 12) in float aTextureScale;

out vec3 vWorldPosition;
out vec2 vUV;
out vec3 vNormal;
flat out int vTextureIndex;
flat out float vTextureScale;

uniform mat4 uView;
uniform mat4 uProjection;

uniform mat4 uPreviousView;
uniform mat4 uPreviousProjection;

void main() {
    // YOUR SHADER CODE
}