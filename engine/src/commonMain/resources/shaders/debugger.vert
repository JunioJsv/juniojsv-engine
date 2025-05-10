#version 330

layout (location = 0) in vec3 aPosition;
layout (location = 3) in mat4 aModel;

out vec3 vWorldPosition;

uniform mat4 uView;
uniform mat4 uProjection;

void main() {
    vWorldPosition = (aModel * vec4(aPosition, 1.)).xyz;

    gl_Position = uProjection * uView * vec4(vWorldPosition, 1.);
}