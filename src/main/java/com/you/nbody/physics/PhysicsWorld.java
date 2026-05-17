package com.you.nbody.physics;

import com.you.nbody.core.Time;
import com.you.nbody.renderer.ParticleSystem;
import java.util.concurrent.*;

public class PhysicsWorld {
    private final float[] positions;
    private final float[] velocities;
    private final float[] masses;
    private final float[] colors;
    private final int maxParticles;

    private final float softening = 9.0f;
    private final FlatOctree octree;
    private final ForkJoinPool threadPool;
    private final float[] accelerations;

    public PhysicsWorld(int maxParticles, float[] positions, float[] velocities, float[] masses, float[] colors) {
        this.maxParticles = maxParticles;
        this.positions    = positions;
        this.velocities   = velocities;
        this.masses       = masses;
        this.colors       = colors;
        this.octree       = new FlatOctree(maxParticles);
        this.accelerations = new float[maxParticles * 3];
        
        this.threadPool   = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    }

    public void Update(ParticleSystem particleSystem) {
        float dt = (float) Math.min(Time.GetDeltaTime(), 0.033);

        float maxRange = 0;
        for (int i = 0; i < maxParticles; i++) {
            maxRange = Math.max(maxRange, Math.abs(positions[i * 3]));
            maxRange = Math.max(maxRange, Math.abs(positions[i * 3 + 1]));
            maxRange = Math.max(maxRange, Math.abs(positions[i * 3 + 2]));
        }

        octree.reset(0, 0, 0, maxRange * 1.1f);
        for (int i = 0; i < maxParticles; i++) {
            octree.insert(i, positions, masses);
        }

        try {
            threadPool.submit(() ->
                java.util.stream.IntStream.range(0, maxParticles).parallel().forEach(i -> {
                    float[] localAcc = new float[3]; // Clean local array stack instantiation per particle thread
                    octree.computeAcceleration(i, positions, softening, localAcc);
                    accelerations[i * 3]     = localAcc[0];
                    accelerations[i * 3 + 1] = localAcc[1];
                    accelerations[i * 3 + 2] = localAcc[2];
                })
            ).get();

            threadPool.submit(() ->
                java.util.stream.IntStream.range(0, maxParticles).parallel().forEach(i -> {
                    int idx = i * 3;

                    velocities[idx]     += accelerations[idx]     * dt;
                    velocities[idx + 1] += accelerations[idx + 1] * dt;
                    velocities[idx + 2] += accelerations[idx + 2] * dt;

                    positions[idx]     += velocities[idx]     * dt;
                    positions[idx + 1] += velocities[idx + 1] * dt;
                    positions[idx + 2] += velocities[idx + 2] * dt;

                    float vx = velocities[idx];
                    float vy = velocities[idx + 1];
                    float vz = velocities[idx + 2];
                    float speed = (float) Math.sqrt(vx * vx + vy * vy + vz * vz);
                    float ratio = Math.min(1.0f, speed / 5.0f);

                    if (ratio < 0.5f) {
                        float t = ratio * 2.0f;
                        colors[idx]     = 0.0f;
                        colors[idx + 1] = t;
                        colors[idx + 2] = 1.0f - t;
                    } else {
                        float t = (ratio - 0.5f) * 2.0f;
                        colors[idx]     = t;
                        colors[idx + 1] = 1.0f - t;
                        colors[idx + 2] = 0.0f;
                    }
                })
            ).get();

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }

        particleSystem.UpdatePositions(positions);
        particleSystem.UpdateColors(colors);
    }

    public void Cleanup() {
        threadPool.shutdownNow();
    }
}
