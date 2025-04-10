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

void main() {
    vWorldPosition = (uModel * vec4(aPosition, 1.)).xyz;
    vUV = aUV;
    vNormal = normalize((uModel * vec4(aNormal, 0.)).xyz);

    gl_Position = uProjection * uView * vec4(vWorldPosition, 1.);
}