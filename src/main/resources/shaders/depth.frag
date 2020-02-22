#version 330

float near = .1;
float far = 1000.;

void main() {
    float z = gl_FragCoord.z * 2. - 1.;
    z = (2. * near * far) / (far + near - z * (far - near));
    gl_FragColor = vec4(vec3(z / far), 1.);
}