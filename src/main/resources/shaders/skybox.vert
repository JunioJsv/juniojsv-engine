#version 330

layout(location = 0) in vec3 vertice_position;

out vec3 out_vertice_position;

uniform mat4 transformation;
uniform mat4 camera_projection;
uniform mat4 camera_view;

void main() {
    vec4 world_position = transformation * vec4(vertice_position, 1.);
    out_vertice_position = vertice_position;
    gl_Position = camera_projection * camera_view * world_position;
}