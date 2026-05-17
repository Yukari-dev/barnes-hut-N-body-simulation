package com.you.nbody.physics;

import java.util.Random;

public class Formations {

    private static final float G            = 1.0f;
    private static final float CENTRAL_MASS = 1e5f;


    private static void spawnSphere(
            float[] positions, float[] velocities, float[] masses,
            int start, int end,
            float cx, float cy, float cz,
            float radius,
            float bvx, float bvy, float bvz,
            float minMass, float maxMass,
            Random random) {

        for (int i = start; i < end; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi   = Math.acos(2 * random.nextDouble() - 1);
            double r     = radius * Math.pow(random.nextDouble(), 1.0 / 3.0);

            positions[i*3]   = (float)(cx + r * Math.sin(phi) * Math.cos(theta));
            positions[i*3+1] = (float)(cy + r * Math.sin(phi) * Math.sin(theta));
            positions[i*3+2] = (float)(cz + r * Math.cos(phi));

            velocities[i*3]   = bvx;
            velocities[i*3+1] = bvy;
            velocities[i*3+2] = bvz;

            masses[i] = minMass + random.nextFloat() * (maxMass - minMass);
        }
    }

    private static void spawnGalaxy(
            float[] positions, float[] velocities, float[] masses,
            int centralIdx, int start, int end,
            float cx, float cy, float cz,
            float innerRadius, float outerRadius, float thickness,
            float bvx, float bvy, float bvz,
            Random random) {

        positions[centralIdx*3]   = cx;
        positions[centralIdx*3+1] = cy;
        positions[centralIdx*3+2] = cz;
        velocities[centralIdx*3]   = bvx;
        velocities[centralIdx*3+1] = bvy;
        velocities[centralIdx*3+2] = bvz;
        masses[centralIdx] = CENTRAL_MASS;

        for (int i = start; i < end; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double r     = innerRadius + random.nextDouble() * (outerRadius - innerRadius);

            positions[i*3]   = (float)(cx + r * Math.cos(angle));
            positions[i*3+1] = (float)(cy + (random.nextDouble() - 0.5) * thickness);
            positions[i*3+2] = (float)(cz + r * Math.sin(angle));

            double speed = Math.sqrt(G * CENTRAL_MASS / r);
            velocities[i*3]   = bvx + (float)(-Math.sin(angle) * speed);
            velocities[i*3+1] = bvy + (float)((random.nextDouble() - 0.5) * speed * 0.05);
            velocities[i*3+2] = bvz + (float)( Math.cos(angle) * speed);

            masses[i] = 1.0f + random.nextFloat() * 2f;
        }
    }

    public static void TwoSpheresHeadOn(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        int   half       = maxParticles / 2;
        float radius     = 80f;
        float separation = 350f;
        float speed      = 0.6f;

        spawnSphere(positions, velocities, masses,
                0, half,
                -separation/2, 0, 0,
                radius, speed, 0, 0,
                1f, 3f, random);

        spawnSphere(positions, velocities, masses,
                half, maxParticles,
                separation/2, 0, 0,
                radius, -speed, 0, 0,
                1f, 3f, random);
    }


    public static void OneGalaxy(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        spawnGalaxy(positions, velocities, masses,
                0, 1, maxParticles,
                0, 0, 0,
                10f, 100f, 8f,
                0, 0, 0,
                random);

    }

    public static void TwoGalaxies(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        int   half       = maxParticles / 2;
        float separation = 600f;
        float speed      = 0.4f;

        spawnGalaxy(positions, velocities, masses,
                0, 1, half,
                -separation/2, 0, 0,
                10f, 100f, 8f,
                speed, 0, 0,
                random);

        spawnGalaxy(positions, velocities, masses,
                half, half+1, maxParticles,
                separation/2, 0, 0,
                10f, 100f, 8f,
                -speed, 0, 0,
                random);
    }

    public static void SphereIntoGalaxy(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        int galaxyCount = (int)(maxParticles * 0.7f);
        int sphereCount = maxParticles - galaxyCount;
        float speed     = 40f;

        spawnGalaxy(positions, velocities, masses,
                0, 1, galaxyCount,
                0, 0, 0,
                10f, 100f, 8f,
                0, 0, 0,
                random);

        spawnSphere(positions, velocities, masses,
                galaxyCount, maxParticles,
                300, 0, 0,
                60f,
                0, 0, speed,
                1f, 3f, random);
    }

    public static void RingAndSphere(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        int ringCount = (int)(maxParticles * 0.7f);
        float ringInner = 50f, ringOuter = 120f;

        positions[0] = 0; positions[1] = 0; positions[2] = 0;
        velocities[0] = 0; velocities[1] = 0; velocities[2] = 0;
        masses[0] = CENTRAL_MASS;

        for (int i = 1; i < ringCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double r     = ringInner + random.nextDouble() * (ringOuter - ringInner);
            positions[i*3]   = (float)(r * Math.cos(angle));
            positions[i*3+1] = (float)((random.nextDouble() - 0.5) * 2f);
            positions[i*3+2] = (float)(r * Math.sin(angle));
            double speed = Math.sqrt(G * CENTRAL_MASS / r);
            velocities[i*3]   = (float)(-Math.sin(angle) * speed);
            velocities[i*3+2] = (float)( Math.cos(angle) * speed);
            masses[i] = 1.0f;
        }

        spawnSphere(positions, velocities, masses,
                ringCount, maxParticles,
                400, 0, 0,
                50f,
                -0.8f, 0, 0,
                1f, 4f, random);
    }

    public static void ThreeGalaxies(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        int third = maxParticles / 3;

        float[] cx  = {   0f,  300f, 150f };
        float[] cz  = { -300f, -300f, 200f };
        float[] bvx = {  0.3f, -0.3f,  0f  };
        float[] bvz = {  0.3f,  0.3f, -0.5f};

        for (int g = 0; g < 3; g++) {
            int start = g * third;
            int end   = (g == 2) ? maxParticles : start + third;

            spawnGalaxy(positions, velocities, masses,
                    start, start+1, end,
                    cx[g], 0, cz[g],
                    10f, 60f, 8f,
                    bvx[g], 0, bvz[g],
                    random);
        }
    }

    public static void ColdCollapse(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        spawnSphere(positions, velocities, masses,
                0, maxParticles,
                0, 0, 0,
                200f,
                0, 0, 0,
                1f, 6f, random);
    }

    public static void Slingshot(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        int galaxyCount  = (int)(maxParticles * 0.8f);
        float bigMass    = 1e10f;

        positions[0] = 0; positions[1] = 0; positions[2] = 0;
        velocities[0] = 0; masses[0] = bigMass;

        float galaxyRadius = 120f;
        for (int i = 1; i < galaxyCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double r     = 10 + random.nextDouble() * galaxyRadius;
            positions[i*3]   = (float)(r * Math.cos(angle));
            positions[i*3+1] = (float)((random.nextDouble() - 0.5) * 6);
            positions[i*3+2] = (float)(r * Math.sin(angle));
            double speed = Math.sqrt(G * bigMass / r);
            velocities[i*3]   = (float)(-Math.sin(angle) * speed);
            velocities[i*3+2] = (float)( Math.cos(angle) * speed);
            masses[i] = 1.0f + random.nextFloat() * 2f;
        }

        spawnSphere(positions, velocities, masses,
                galaxyCount, maxParticles,
                500, 0, 150,
                30f,
                -1.2f, 0, 0,
                1f, 3f, random);
    }
}
