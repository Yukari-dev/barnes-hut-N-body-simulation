package com.you.nbody.physics;

import com.you.nbody.core.Time;
import com.you.nbody.renderer.ParticleSystem;
import java.util.concurrent.*;

public class PhysicsWorld {
    private float[] positions;
    private float[] velocities;
    private float[] masses;
    private float[] colors; 
    private int maxParticles;

    private float softening = 8.0f;
    public FlatOctree octree;
    private ForkJoinPool threadPool;
    private float[] accelerations;

    public PhysicsWorld(int maxParticles, float[] positions, float[] velocities, float[] masses, float[] colors) {
        this.maxParticles = maxParticles;
        this.positions    = positions;
        this.velocities   = velocities;
        this.masses       = masses;
        this.colors       = colors;
        this.octree       = new FlatOctree(maxParticles);
        this.accelerations= new float[maxParticles * 3];
        
        this.threadPool   = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    }

    public void Update(ParticleSystem particleSystem, float timeScale) {
        float dt = (float) Math.min(Time.GetDeltaTime(), 0.033) * timeScale;

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
                    float[] localAcc = new float[3];
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

                    colors[idx]     = velocities[idx];
                    colors[idx + 1] = velocities[idx + 1];
                    colors[idx + 2] = velocities[idx + 2];
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

    public void ResetData(int maxParticles, float[] positions, float[] velocities, float[] masses, float[] colors) {
        this.maxParticles = maxParticles;

        if (this.positions == null || this.positions.length != positions.length) {
            this.positions = new float[positions.length];
        }

        if (this.velocities == null || this.velocities.length != velocities.length) {
            this.velocities = new float[velocities.length];
        }

        if (this.masses == null || this.masses.length != masses.length) {
            this.masses = new float[masses.length];
        }

        if (this.colors == null || this.colors.length != colors.length) {
            this.colors = new float[colors.length];
        }

        System.arraycopy(positions, 0, this.positions, 0, positions.length);
        System.arraycopy(velocities, 0, this.velocities, 0, velocities.length);
        System.arraycopy(masses, 0, this.masses, 0, masses.length);
        System.arraycopy(colors, 0, this.colors, 0, colors.length);
    }
}
