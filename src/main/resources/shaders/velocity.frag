#version 330

smooth in vec4 vClipPosition;
smooth in vec4 vPreviousClipPosition;

out vec2 oVelocity;

void main() {
    vec2 currentNdc = vClipPosition.xy / vClipPosition.w;
    vec2 previousNdc = vPreviousClipPosition.xy / vPreviousClipPosition.w;

    oVelocity = currentNdc - previousNdc;
}