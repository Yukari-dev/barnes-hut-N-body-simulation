#version 430 core

layout(local_size_x = 64, local_size_y = 1, local_size_z = 1) in;

layout(std430, binding = 0) buffer PosBuffer { vec4 positions[]; };
layout(std430, binding = 1) buffer VelBuffer { vec4 velocities[]; };
layout(std430, binding = 2) buffer ColBuffer { vec4 colors[]; };

uniform float dt;
uniform int numParticles;
uniform float softeningSq;

void main() {
    uint idx = gl_GlobalInvocationID.x;
    if (idx >= numParticles) return;

    vec3 pos = positions[idx].xyz;
    vec3 vel = velocities[idx].xyz;
    vec3 acc = vec3(0.0);

    int i = 0;
    for (; i < numParticles - 3; i += 4) {
        vec4 p0 = positions[i];
        vec3 diff0 = p0.xyz - pos;
        float distSq0 = dot(diff0, diff0) + softeningSq;
        acc += diff0 * (p0.w / (distSq0 * sqrt(distSq0)));

        vec4 p1 = positions[i+1];
        vec3 diff1 = p1.xyz - pos;
        float distSq1 = dot(diff1, diff1) + softeningSq;
        acc += diff1 * (p1.w / (distSq1 * sqrt(distSq1)));

        vec4 p2 = positions[i+2];
        vec3 diff2 = p2.xyz - pos;
        float distSq2 = dot(diff2, diff2) + softeningSq;
        acc += diff2 * (p2.w / (distSq2 * sqrt(distSq2)));

        vec4 p3 = positions[i+3];
        vec3 diff3 = p3.xyz - pos;
        float distSq3 = dot(diff3, diff3) + softeningSq;
        acc += diff3 * (p3.w / (distSq3 * sqrt(distSq3)));
    }

    for (; i < numParticles; i++) {
        vec4 p = positions[i];
        vec3 diff = p.xyz - pos;
        float distSq = dot(diff, diff) + softeningSq;
        acc += diff * (p.w / (distSq * sqrt(distSq)));
    }

    vel += acc * dt;
    pos += vel * dt;

    positions[idx].xyz = pos;
    velocities[idx].xyz = vel;

    float speed = length(vel);
    float ratio = min(1.0, speed / 120.0);

    vec4 finalColor;
    if (ratio < 0.25) {
        finalColor = vec4(mix(0.4, 0.8, ratio / 0.25), 0.1, 0.1, 0.25);
    } else if (ratio < 0.6) {
        finalColor = vec4(1.0, mix(0.1, 0.8, (ratio - 0.25) / 0.35), 0.1, 0.5);
    } else {
        finalColor = vec4(mix(1.0, 0.1, (ratio - 0.6) / 0.4), mix(0.8, 0.9, (ratio - 0.6) / 0.4), 1.0, 0.8);
    }

    colors[idx] = finalColor;
}
