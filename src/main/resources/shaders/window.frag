#version 330

in vec3 out_vertex_position;
in vec2 out_uv_coordinates;

uniform sampler2D scene_texture;
uniform sampler2D velocity_texture;
uniform sampler2D overlay_texture;
uniform sampler2D previous_frame_texture;
uniform float motion_blur;

out vec4 color;

void main() {
    float velocity_scale = motion_blur;

    vec2 texel_size = 1.0 / vec2(textureSize(scene_texture, 0));

    vec2 velocity = texture(velocity_texture, out_uv_coordinates).rg;
    velocity *= velocity_scale;

    float speed = length(velocity / texel_size);
    int samples = clamp(int(speed), 1, 32);

    color = texture(scene_texture, out_uv_coordinates);
    for (int i = 1; i < samples; ++i) {
        vec2 offset = velocity * (float(i) / float(samples - 1) - 0.5);
        color += texture(scene_texture, out_uv_coordinates + offset);
    }
    color /= float(samples);
    color += texture(overlay_texture, out_uv_coordinates);
}
