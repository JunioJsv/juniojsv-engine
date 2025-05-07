#version 330

layout (location = 0) in vec3 aPosition;
layout (location = 3) in vec3 aNormal;

uniform mat4 uModel;
uniform mat4 uLightSpaceMatrix;

void main() {
    gl_Position = uLightSpaceMatrix * uModel * vec4(aPosition + aNormal * 0.002, 1.0);
}