#version 330

layout(location = 0) in vec3 vertice_position;
layout(location = 1) in vec2 uv_coordinates;
layout(location = 2) in vec3 vertice_normal;

out vec3 out_vertice_position;
out vec2 out_uv_coordinates;
out vec3 out_vertice_normal;

uniform mat4 transformation;
uniform mat4 projection;
uniform mat4 camera_view;

void main() {
    vec4 world_position = transformation * vec4(vertice_position, 1.);
    out_vertice_position = world_position.xyz;
    out_uv_coordinates = uv_coordinates;
    out_vertice_normal = normalize((transformation * vec4(vertice_normal, 0.0)).xyz);

    gl_Position = projection * camera_view * world_position;
}