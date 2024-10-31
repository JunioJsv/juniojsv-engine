#version 330

in vec3 out_vertex_position;

uniform samplerCube in_skybox;
uniform vec3 light_color;
uniform float time;

void main() {
    vec4 skybox = texture(in_skybox, out_vertex_position);
    //    float x = out_vertex_position.x;
    //    float y = out_vertex_position.y;
    //    float z = out_vertex_position.z;
    //    float color = max(step(skybox.z, out_vertex_position.y - (cos(time + abs(x - z) * 16) / 64 + .2)), (skybox.x + skybox.y + skybox.z) / 3);
    //
    //    //    gl_FragColor = vec4(vec3(cos(time + abs(x - z) * 16)), 1);
    //    //    return;
    //    if (color >= 1){
    //        gl_FragColor = vec4(light_color, 1);
    //        return;
    //    }
    gl_FragColor = vec4(vec3(skybox) * light_color, 1);
}