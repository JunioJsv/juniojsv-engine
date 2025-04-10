#version 330

layout (location = 0) in vec3 aPosition;

out vec3 vPosition;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

void main() {
    vec4 worldPosition = uModel * vec4(aPosition, 1.);
    vPosition = aPosition;
    gl_Position = (uProjection * uView * worldPosition).xyww;
}