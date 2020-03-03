#version 330

layout(location = 0) in vec3 vertex_position;

out vec3 out_vertex_position;

uniform mat4 transformation;
uniform mat4 camera_projection;
uniform mat4 camera_view;

void main() {
    vec4 world_position = transformation * vec4(vertex_position, 1.);
    out_vertex_position = vertex_position;
    gl_Position = camera_projection * camera_view * world_position;
}