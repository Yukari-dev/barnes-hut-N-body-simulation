#version 330 core
in vec3 fragColor;
out vec4 outColor;

uniform mat4 view;
uniform float nearDepth = 0.01; 
uniform float farDepth = 10000; 

void main() {
    vec2 coord = gl_PointCoord - vec2(0.5);
    float dist = length(coord);
    if (dist > 0.5) discard;
    float alpha = 1.0 - smoothstep(0.0, 0.5, dist);

    float depth = gl_FragCoord.z;
    float t = smoothstep(0.0, 1.0, depth);

    vec3 depthTint = mix(vec3(1.0, 0.8, 0.5), vec3(0.3, 0.5, 1.0), t);
    vec3 finalColor = fragColor;

    outColor = vec4(finalColor, alpha);
}
