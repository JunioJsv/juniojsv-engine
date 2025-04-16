#version 330

layout (location = 0) in vec3 aPosition;
layout (location = 3) in mat4 aModel;

uniform mat4 uLightSpaceMatrix;

void main() {
    gl_Position = uLightSpaceMatrix * aModel * vec4(aPosition, 1.0);
}