#version 330

layout (location = 0) in vec3 vertex_position;

uniform mat4 transformation;
uniform mat4 camera_projection;
uniform mat4 camera_view;

uniform mat4 previous_transformation;
uniform mat4 previous_camera_projection;
uniform mat4 previous_camera_view;

smooth out vec4 out_current_clip_position;
smooth out vec4 out_previous_clip_position;

out vec3 out_current_world_position;

void main() {
    vec4 world_position = transformation * vec4(vertex_position, 1.);

    out_current_clip_position = camera_projection * camera_view * world_position;

    out_previous_clip_position = previous_camera_projection * previous_camera_view * world_position;

    out_current_world_position = world_position.xyz;

    gl_Position = out_current_clip_position;
}