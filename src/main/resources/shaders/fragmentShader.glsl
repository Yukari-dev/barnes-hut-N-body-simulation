#version 330 core
in vec3 fragColor;
in float particleSpeed;
out vec4 outColor;

void main() {
    float speedFactor = clamp(particleSpeed * 0.1, 0.0, 0.8);
    
    vec2 coord = gl_PointCoord - vec2(0.5);
    coord.x /= (1.0 + speedFactor); 
    coord.y *= (1.0 - speedFactor * 0.5);

    float dist = dot(coord, coord);
    if (dist > 0.25) discard;
    
    float glow = exp(-10.0 * dist);
    
    vec3 hotColor = mix(fragColor, vec3(0.7, 0.9, 1.0), clamp(particleSpeed * 0.3, 0.0, 1.0));
    vec3 finalColor = hotColor * glow * 2.0;

    outColor = vec4(finalColor, glow);
}
