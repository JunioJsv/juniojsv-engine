#version 330

layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec2 uv_coordinates;
layout(location = 2) in vec3 vertex_normal;
layout(location = 3) in mat4 transformation;
layout(location = 7) in int texture_index;
layout(location = 8) in float texture_scale;

out vec3 out_vertex_position;
out vec3 out_unnormal_vertex_position;
out vec2 out_uv_coordinates;
out vec3 out_vertex_normal;
flat out int out_texture_index;
flat out float out_texture_scale;

uniform mat4 camera_projection;
uniform mat4 camera_view;

void main() {
    vec4 world_position = transformation * vec4(vertex_position, 1.);
    out_vertex_position = world_position.xyz;
    out_uv_coordinates = uv_coordinates;
    out_vertex_normal = normalize((transformation * vec4(vertex_normal, 0.0)).xyz);
    out_unnormal_vertex_position = vertex_position;
    out_texture_index = texture_index;
    out_texture_scale = texture_scale;

    gl_Position = camera_projection * camera_view * world_position;
}