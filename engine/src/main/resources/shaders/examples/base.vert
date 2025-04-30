#version 330

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aUV;
layout (location = 2) in vec3 aNormal;

out vec3 vWorldPosition;
out vec2 vUV;
out vec3 vNormal;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

uniform mat4 uPreviousModel;
uniform mat4 uPreviousView;
uniform mat4 uPreviousProjection;

void main() {
    // YOUR SHADER CODE
}