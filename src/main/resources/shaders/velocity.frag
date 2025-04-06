#version 330

smooth in vec4 out_current_clip_position;
smooth in vec4 out_previous_clip_position;

out vec2 velocity;

void main() {
    vec2 current_ndc = (out_current_clip_position.xy / out_current_clip_position.w) * .5 + .5;
    vec2 previous_ndc = (out_previous_clip_position.xy / out_previous_clip_position.w) * .5 + .5;

    velocity = current_ndc - previous_ndc;
}