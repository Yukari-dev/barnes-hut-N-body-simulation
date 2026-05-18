#version 430 core

layout(local_size_x = 256, local_size_y = 1, local_size_z = 1) in;

layout(std430, binding = 0) buffer PositionBuffer { vec4 positions[]; };
layout(std430, binding = 1) buffer VelocityBuffer { vec4 velocities[]; };
layout(std430, binding = 2) buffer ColorBuffer    { vec4 colors[]; };

uniform float dt;
uniform int numParticles;
uniform float softeningSq;

void main() {
    uint gIdx = gl_GlobalInvocationID.x;
    if (gIdx >= numParticles) return;

    float fixedDt = (dt < 0.00001) ? 0.016 : dt;

    vec3 pos = positions[gIdx].xyz;
    vec3 vel = velocities[gIdx].xyz;

    vec3 acceleration = vec3(0.0);

    for (int i = 0; i < numParticles; i++) {
        if (i == gIdx) continue;

        vec3 diff = positions[i].xyz - pos;
        float distSq = dot(diff, diff) + softeningSq;
        
        if (distSq > 0.01) {
            float invDist = 1.0 / sqrt(distSq);
            float invDistCube = invDist * invDist * invDist;
            
            acceleration += diff * (800*positions[i].w * invDistCube);
        }
    }

    vel += acceleration * fixedDt;
    pos += vel * fixedDt;

    positions[gIdx].xyz = pos;
    velocities[gIdx].xyz = vel;
}
