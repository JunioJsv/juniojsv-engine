#version 330

in vec3 vWorldPosition;
in vec2 vUV;
in vec3 vNormal;

uniform vec3 uLightPosition;
uniform vec3 uLightColor;
uniform sampler2D uTexture;
uniform float uTextureScale;
uniform vec3 uCameraPosition;

void main() {
    vec3 lightPosition = normalize(uLightPosition - vWorldPosition);
    vec3 normal = normalize(vNormal);

    float brightness = max(dot(normal, lightPosition), 0.1);
    vec3 diffuse = brightness * uLightColor;

    vec3 viwDir = normalize(uCameraPosition - vWorldPosition);
    vec3 reflectDir = reflect(-lightPosition, normal);

    float shininess = 8.0;
    float spec = pow(max(dot(viwDir, reflectDir), 0.0), shininess);
    float specularStrength = 1.0;
    vec3 specular = spec * specularStrength * vec3(1) * uLightColor;

    vec3 textureColor = texture(uTexture, vUV * uTextureScale).rgb;
    vec3 finalColor = textureColor * (diffuse + specular);

    gl_FragColor = vec4(finalColor, 1.0);
}
