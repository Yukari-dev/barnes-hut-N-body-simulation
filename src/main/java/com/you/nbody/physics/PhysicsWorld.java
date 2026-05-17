package com.you.nbody.physics;

import com.you.nbody.core.Time;
import com.you.nbody.renderer.ParticleSystem;
import org.joml.Vector3f;

import java.util.concurrent.*;

public class PhysicsWorld {
    private final float[] positions;
    private final float[] velocities;
    private final float[] masses;
    private final float[] colors;
    private final int maxParticles;

    private final float softening = 8.0f;

    private Octree root;
    private final OctreePool pool;
    private final ForkJoinPool threadPool = new ForkJoinPool();

    private final ThreadLocal<float[]> accBuffer = ThreadLocal.withInitial(() -> new float[3]);

    public PhysicsWorld(int maxParticles, float[] positions, float[] velocities, float[] masses, float[] colors) {
        this.maxParticles = maxParticles;
        this.positions    = positions;
        this.velocities   = velocities;
        this.masses       = masses;
        this.colors       = colors;
        this.pool         = new OctreePool(maxParticles * 8);
    }

    public void Update(ParticleSystem particleSystem) {
        double dt = Time.GetFixedDeltaTime();

        float maxRange = 0;
        for (int i = 0; i < maxParticles; i++) {
            maxRange = Math.max(maxRange, Math.abs(positions[i*3]));
            maxRange = Math.max(maxRange, Math.abs(positions[i*3+1]));
            maxRange = Math.max(maxRange, Math.abs(positions[i*3+2]));
        }

        pool.reset();
        root = pool.get(0, 0, 0, maxRange * 1.1f);
        root.initRenderer();
        for (int i = 0; i < maxParticles; i++)
            root.insert(i, positions, masses, 0, pool);

        final Octree   finalRoot      = root;
        final float    finalSoftening = softening;
        final double   finalDt        = dt;

        try {
            threadPool.submit(() ->
                java.util.stream.IntStream.range(0, maxParticles).parallel().forEach(i -> {
                    float[] acc = accBuffer.get();
                    finalRoot.computeAcceleration(i, positions, masses, finalSoftening, acc);

                    velocities[i*3]   += acc[0] * finalDt;
                    velocities[i*3+1] += acc[1] * finalDt;
                    velocities[i*3+2] += acc[2] * finalDt;
                    positions[i*3]    += velocities[i*3]   * finalDt;
                    positions[i*3+1]  += velocities[i*3+1] * finalDt;
                    positions[i*3+2]  += velocities[i*3+2] * finalDt;
                })
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }

        for (int i = 0; i < maxParticles; i++) {
            float vx = velocities[i*3];
            float vy = velocities[i*3+1];
            float vz = velocities[i*3+2];
            float speed = (float) Math.sqrt(vx*vx + vy*vy + vz*vz);
            float ratio = Math.min(1.0f, speed / 5.0f);
            // colors[i*3]   = 1.0f;
            // colors[i*3+1] = 1;
            // colors[i*3+2] = 1.0f;

            if (ratio < 0.5f) {
                float t = ratio * 2.0f;
                colors[i*3]   = 0.0f;
                colors[i*3+1] = t;
                colors[i*3+2] = 1.0f - t;
            } else {
                float t = (ratio - 0.5f) * 2.0f;
                colors[i*3]   = t;
                colors[i*3+1] = 1.0f - t;
                colors[i*3+2] = 0.0f;
            }
        }

        particleSystem.UpdatePositions(positions);
        particleSystem.UpdateColors(colors);
    }

    public void Draw() {
        if (root != null) root.draw();
    }

    public void Cleanup() {
        threadPool.shutdownNow();
    }
}
