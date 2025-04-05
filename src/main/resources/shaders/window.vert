#version 330
layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec2 uv_coordinates;

out vec3 out_vertex_position;
out vec2 out_uv_coordinates;

void main() {
    gl_Position = vec4(vertex_position.x, vertex_position.y, 0.0, 1.0);
    out_uv_coordinates = uv_coordinates;
}
