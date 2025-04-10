#version 330

layout (location = 0) in vec3 aPosition;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

uniform mat4 uPreviousModel;
uniform mat4 uPreviousView;
uniform mat4 uPreviousProjection;

smooth out vec4 vClipPosition;
smooth out vec4 vPreviousClipPosition;

void main() {
    vec4 worldPosition = uModel * vec4(aPosition, 1.);
    vec4 previousWorldPosition = uPreviousModel * vec4(aPosition, 1.);

    vClipPosition = uProjection * uView * worldPosition;

    vPreviousClipPosition = uPreviousProjection * uPreviousView * previousWorldPosition;

    gl_Position = vClipPosition;
}