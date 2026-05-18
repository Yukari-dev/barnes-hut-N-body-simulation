package com.you.nbody.physics;

import java.util.Random;

public class Formations {

    private final static float G            = 1.0f;
    private final static float CENTRAL_MASS = 1e5f;

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

    public static void CosmicNebula(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        int numClusters = 4;
        int particlesPerCluster = (int)(maxParticles * 0.25f);
        int backgroundGasCount = maxParticles - (particlesPerCluster * numClusters);

        for (int i = 0; i < backgroundGasCount; i++) {
            double t = random.nextDouble() * 2.0 * Math.PI;
            double r = 40.0 + random.nextDouble() * 260.0;
            double warpFactor = Math.sin(t * 3.0) * 45.0; 
            double finalR = r + warpFactor;

            positions[i*3]   = (float)(finalR * Math.cos(t));
            positions[i*3+1] = (float)((random.nextDouble() - 0.5) * (40.0 + (finalR * 0.1)));
            positions[i*3+2] = (float)(finalR * Math.sin(t));

            masses[i] = 0.1f + random.nextFloat() * 0.4f;

            double orbitalSpeed = Math.sqrt(G * 40000.0f / finalR) * 0.65;
            velocities[i*3]   = (float)(-Math.sin(t) * orbitalSpeed + (random.nextDouble() - 0.5) * 1.5);
            velocities[i*3+1] = (float)((random.nextDouble() - 0.5) * 1.0);
            velocities[i*3+2] = (float)(Math.cos(t) * orbitalSpeed + (random.nextDouble() - 0.5) * 1.5);
        }

        float[] clusterX = { -120f,  130f,  40f, -60f };
        float[] clusterY = {   10f,  -15f,  30f, -20f };
        float[] clusterZ = {  -90f,   80f, 140f, -140f };
        
        float[] driftX   = {  2.2f, -1.8f, -0.5f,  1.2f };
        float[] driftY   = {  0.2f, -0.3f,  0.8f, -0.4f };
        float[] driftZ   = {  1.1f,  1.5f, -2.0f, -0.8f };

        int pointer = backgroundGasCount;
        for (int c = 0; c < numClusters; c++) {
            int end = pointer + particlesPerCluster;
            if (c == numClusters - 1) end = maxParticles;

            spawnSphere(positions, velocities, masses,
                    pointer, end,
                    clusterX[c], clusterY[c], clusterZ[c],
                    35.0f,
                    driftX[c], driftY[c], driftZ[c],
                    2.0f, 8.0f, random);

            pointer = end;
        }
    }

    public static void AccretionJet(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        int jetCount = (int)(maxParticles * 0.20f);
        int diskCount = maxParticles - jetCount;

        positions[0] = 0; positions[1] = 0; positions[2] = 0;
        velocities[0] = 0; velocities[1] = 0; velocities[2] = 0;
        masses[0] = 8e5f;

        for (int i = 1; i < diskCount; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double r = 8.0 + 120.0 * Math.pow(random.nextDouble(), 2.0);

            positions[i*3]   = (float)(r * Math.cos(theta));
            positions[i*3+1] = (float)((random.nextDouble() - 0.5) * 2.5f);
            positions[i*3+2] = (float)(r * Math.sin(theta));

            double speed = Math.sqrt(G * masses[0] / r);
            velocities[i*3]   = (float)(-Math.sin(theta) * speed);
            velocities[i*3+1] = 0.0f;
            velocities[i*3+2] = (float)(Math.cos(theta) * speed);
            masses[i] = 0.5f + random.nextFloat() * 1.5f;
        }

        for (int i = diskCount; i < maxParticles; i++) {
            float jetDirection = (random.nextBoolean()) ? 1.0f : -1.0f;
            float height = 15.0f + random.nextFloat() * 250.0f;
            double angle = random.nextDouble() * 2 * Math.PI;
            float radius = 3.0f + (height * 0.08f); 

            positions[i*3]   = (float)(radius * Math.cos(angle));
            positions[i*3+1] = height * jetDirection;
            positions[i*3+2] = (float)(radius * Math.sin(angle));

            velocities[i*3]   = (float)(-Math.sin(angle) * 8.0f);
            velocities[i*3+1] = 45.0f * jetDirection;
            velocities[i*3+2] = (float)(Math.cos(angle) * 8.0f);
            masses[i] = 0.01f;
        }
    }

    public static void BinaryStream(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        int half = maxParticles / 2;

        spawnGalaxy(positions, velocities, masses,
                0, 1, half,
                -180f, -40f, 0f,
                10f, 90f, 6f,
                1.5f, 0f, 0.5f,
                random);

        int centralB = half;
        positions[centralB*3]   = 180f;
        positions[centralB*3+1] = 40f;
        positions[centralB*3+2] = 0f;
        velocities[centralB*3]   = -1.5f;
        velocities[centralB*3+1] = 0f;
        velocities[centralB*3+2] = -0.5f;
        masses[centralB] = CENTRAL_MASS;

        for (int i = half + 1; i < maxParticles; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double r     = 10f + random.nextDouble() * 80f;

            float localX = (float)(r * Math.cos(angle));
            float localY = (float)((random.nextDouble() - 0.5) * 6f);
            float localZ = (float)(r * Math.sin(angle));

            float rotatedX = localX;
            float rotatedY = (float)(localY * Math.cos(0.78) - localZ * Math.sin(0.78));
            float rotatedZ = (float)(localY * Math.sin(0.78) + localZ * Math.cos(0.78));

            positions[i*3]   = 180f + rotatedX;
            positions[i*3+1] = 40f + rotatedY;
            positions[i*3+2] = 0f + rotatedZ;

            double speed = Math.sqrt(G * CENTRAL_MASS / r);
            float vX = (float)(-Math.sin(angle) * speed);
            float vY = 0f;
            float vZ = (float)(Math.cos(angle) * speed);

            float rotVX = vX;
            float rotVY = (float)(vY * Math.cos(0.78) - vZ * Math.sin(0.78));
            float rotVZ = (float)(vY * Math.sin(0.78) + vZ * Math.cos(0.78));

            velocities[i*3]   = -1.5f + rotVX;
            velocities[i*3+1] = 0f + rotVY;
            velocities[i*3+2] = -0.5f + rotVZ;

            masses[i] = 1.0f + random.nextFloat() * 1.5f;
        }
    }

    public static void GlobularCore(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        float clusterRadius = 75.0f;
        
        for (int i = 0; i < maxParticles; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi   = Math.acos(2 * random.nextDouble() - 1);
            double r = clusterRadius * Math.pow(random.nextDouble(), 3.0);

            positions[i*3]   = (float)(r * Math.sin(phi) * Math.cos(theta));
            positions[i*3+1] = (float)(r * Math.sin(phi) * Math.sin(theta));
            positions[i*3+2] = (float)(r * Math.cos(phi));

            float dispersionScale = 3.5f * (float)(1.2 - (r / clusterRadius));
            velocities[i*3]   = (float)((random.nextDouble() - 0.5) * dispersionScale);
            velocities[i*3+1] = (float)((random.nextDouble() - 0.5) * dispersionScale);
            velocities[i*3+2] = (float)((random.nextDouble() - 0.5) * dispersionScale);

            masses[i] = 4.0f + random.nextFloat() * 12.0f;
        }
    }

    public static void TwoSpheresHeadOn(float[] positions, float[] velocities, float[] masses, int maxParticles, Random random) {
        int half = maxParticles / 2; float radius = 80f; float separation = 200f; float speed = 0.6f;
        spawnSphere(positions, velocities, masses, 0, half, -separation/2, 0, 0, radius, speed, 0, 0, 1f, 3f, random);
        spawnSphere(positions, velocities, masses, half, maxParticles, separation/2, 0, 0, radius, -speed, 0, 0, 1f, 3f, random);
    }

    public static void OneGalaxy(float[] positions, float[] velocities, float[] masses, int maxParticles, Random random) {
        spawnGalaxy(positions, velocities, masses, 0, 1, maxParticles, 0, 0, 0, 10f, 100f, 8f, 0, 0, 0, random);
    }

    public static void TwoGalaxies(float[] positions, float[] velocities, float[] masses, int maxParticles, Random random) {
        int half = maxParticles / 2; float separation = 600f; float speed = 0.4f;
        spawnGalaxy(positions, velocities, masses, 0, 1, half, -separation/2, 0, 0, 10f, 100f, 8f, speed, 0, 0, random);
        spawnGalaxy(positions, velocities, masses, half, half+1, maxParticles, separation/2, 0, 0, 10f, 100f, 8f, -speed, 0, 0, random);
    }

    public static void SphereIntoGalaxy(float[] positions, float[] velocities, float[] masses, int maxParticles, Random random) {
        int galaxyCount = (int)(maxParticles * 0.7f); float speed = 20f;
        spawnGalaxy(positions, velocities, masses, 0, 1, galaxyCount, 0, 0, 0, 10f, 100f, 8f, 0, 0, 0, random);
        spawnSphere(positions, velocities, masses, galaxyCount, maxParticles, 200, 0, 0, 60f, 0, 0, speed, 1f, 3f, random);
    }

    public static void RingAndSphere(float[] positions, float[] velocities, float[] masses, int maxParticles, Random random) {
        int ringCount = (int)(maxParticles * 0.7f); float ringInner = 50f, ringOuter = 120f;
        positions[0] = 0; positions[1] = 0; positions[2] = 0; velocities[0] = 0; velocities[1] = 0; velocities[2] = 0; masses[0] = CENTRAL_MASS;
        for (int i = 1; i < ringCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI; double r = ringInner + random.nextDouble() * (ringOuter - ringInner);
            positions[i*3] = (float)(r * Math.cos(angle)); positions[i*3+1] = (float)((random.nextDouble() - 0.5) * 2f); positions[i*3+2] = (float)(r * Math.sin(angle));
            double speed = Math.sqrt(G * CENTRAL_MASS / r); velocities[i*3] = (float)(-Math.sin(angle) * speed); velocities[i*3+2] = (float)( Math.cos(angle) * speed); masses[i] = 1.0f;
        }
        spawnSphere(positions, velocities, masses, ringCount, maxParticles, 400, 0, 0, 50f, -0.8f, 0, 0, 1f, 4f, random);
    }

    public static void ThreeGalaxies(float[] positions, float[] velocities, float[] masses, int maxParticles, Random random) {
        int third = maxParticles / 3; float[] cx = {0f, 300f, 150f}; float[] cz = {-300f, -300f, 200f}; float[] bvx = {0.3f, -0.3f, 0f}; float[] bvz = {0.3f, 0.3f, -0.5f};
        for (int g = 0; g < 3; g++) {
            int start = g * third; int end = (g == 2) ? maxParticles : start + third;
            spawnGalaxy(positions, velocities, masses, start, start+1, end, cx[g], 0, cz[g], 10f, 60f, 8f, bvx[g], 0, bvz[g], random);
        }
    }

    public static void ColdCollapse(float[] positions, float[] velocities, float[] masses, int maxParticles, Random random) {
        spawnSphere(positions, velocities, masses, 0, maxParticles, 0, 0, 0, 200f, 0, 0, 0, 1f, 6f, random);
    }

    public static void Slingshot(float[] positions, float[] velocities, float[] masses, int maxParticles, Random random) {
        int galaxyCount = (int)(maxParticles * 0.8f); float bigMass = 1e10f;
        positions[0] = 0; positions[1] = 0; positions[2] = 0; velocities[0] = 0; masses[0] = bigMass;
        float galaxyRadius = 120f;
        for (int i = 1; i < galaxyCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI; double r = 10 + random.nextDouble() * galaxyRadius;
            positions[i*3] = (float)(r * Math.cos(angle)); positions[i*3+1] = (float)((random.nextDouble() - 0.5) * 6); positions[i*3+2] = (float)(r * Math.sin(angle));
            double speed = Math.sqrt(G * bigMass / r); velocities[i*3] = (float)(-Math.sin(angle) * speed); velocities[i*3+2] = (float)( Math.cos(angle) * speed); masses[i] = 1.0f + random.nextFloat() * 2f;
        }
        spawnSphere(positions, velocities, masses, galaxyCount, maxParticles, 500, 0, 150, 30f, -1.2f, 0, 0, 1f, 3f, random);
    }
}
