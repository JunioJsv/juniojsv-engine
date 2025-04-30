vec3 calculateLighting() {
    vec3 lightDir = normalize(uLightPosition - vWorldPosition);
    vec3 normal = normalize(vNormal);
    vec3 viewDir = normalize(uCameraPosition - vWorldPosition);
    vec3 reflectDir = reflect(-lightDir, normal);

    float diff = max(dot(normal, lightDir), 0.1);
    vec3 diffuse = diff * uLightColor;

    float shininess = 8.0;
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    float specularStrength = 1.0;
    vec3 specular = spec * specularStrength * vec3(1.0) * uLightColor;

    return diffuse + specular;
}