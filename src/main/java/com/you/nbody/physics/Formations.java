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

            positions[i*3]     = (float)(cx + r * Math.cos(angle)); 
            positions[i*3+2]   = (float)(cz + r * Math.sin(angle)); 
            positions[i*3+1]   = (float)(cy + (random.nextDouble() - 0.5) * thickness); 


            double speed = Math.sqrt(G * CENTRAL_MASS / r)*1.1;
            
            velocities[i*3]     = bvx + (float)(-Math.sin(angle) * speed); 
            velocities[i*3+2]   = bvz + (float)( Math.cos(angle) * speed); 
            velocities[i*3+1]   = bvy + (float)((random.nextDouble() - 0.5) * speed * 0.05);

            float coreProximity = 1.0f - (float)((r - innerRadius) / (outerRadius - innerRadius));
            masses[i] = 0.5f + (coreProximity * 2.5f) + random.nextFloat() * 1.0f;
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
            positions[i*3+1] = (float)(finalR * Math.sin(t));
            positions[i*3+2] = (float)((random.nextDouble() - 0.5) * (40.0 + (finalR * 0.1)));

            masses[i] = 0.1f + random.nextFloat() * 0.4f;

            double orbitalSpeed = Math.sqrt(G * 40000.0f / finalR) * 0.65;
            velocities[i*3]   = (float)(-Math.sin(t) * orbitalSpeed + (random.nextDouble() - 0.5) * 1.5);
            velocities[i*3+1] = (float)( Math.cos(t) * orbitalSpeed + (random.nextDouble() - 0.5) * 1.5);
            velocities[i*3+2] = (float)((random.nextDouble() - 0.5) * 1.0);
        }

        float[] clusterX = { -120f,  130f,  40f, -60f };
        float[] clusterY = {  -90f,   80f, 140f, -140f }; 
        float[] clusterZ = {   10f,  -15f,  30f,  -20f }; 
        
        float[] driftX   = {  2.2f, -1.8f, -0.5f,  1.2f };
        float[] driftY   = {  1.1f,  1.5f, -2.0f, -0.8f }; 
        float[] driftZ   = {  0.2f, -0.3f,  0.8f, -0.4f }; 

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

            // Stable XY disk rotation plane
            positions[i*3]   = (float)(r * Math.cos(theta));
            positions[i*3+1] = (float)(r * Math.sin(theta));
            positions[i*3+2] = (float)((random.nextDouble() - 0.5) * 2.5f);

            double speed = Math.sqrt(G * masses[0] / r);
            velocities[i*3]   = (float)(-Math.sin(theta) * speed);
            velocities[i*3+1] = (float)( Math.cos(theta) * speed);
            velocities[i*3+2] = 0.0f;
            masses[i] = 0.5f + random.nextFloat() * 1.5f;
        }

        for (int i = diskCount; i < maxParticles; i++) {
            float jetDirection = (random.nextBoolean()) ? 1.0f : -1.0f;
            float lengthZ = 15.0f + random.nextFloat() * 250.0f;
            double angle = random.nextDouble() * 2 * Math.PI;
            float radius = 3.0f + (lengthZ * 0.08f); 

            positions[i*3]   = (float)(radius * Math.cos(angle));
            positions[i*3+1] = (float)(radius * Math.sin(angle));
            positions[i*3+2] = lengthZ * jetDirection; 

            velocities[i*3]   = (float)(-Math.sin(angle) * 8.0f);
            velocities[i*3+1] = (float)( Math.cos(angle) * 8.0f);
            velocities[i*3+2] = 45.0f * jetDirection; 
            masses[i] = 0.01f;
        }
    }

    public static void BinaryStream(
            float[] positions, float[] velocities, float[] masses,
            int maxParticles, Random random) {

        int half = maxParticles / 2;

        spawnGalaxy(positions, velocities, masses,
                0, 1, half,
                -180f, 0f, -40f,
                10f, 90f, 6f,
                1.5f, 0.5f, 0f,
                random);

        int centralB = half;
        positions[centralB*3]   = 180f;
        positions[centralB*3+1] = 0f;
        positions[centralB*3+2] = 40f;
        velocities[centralB*3]   = -1.5f;
        velocities[centralB*3+1] = -0.5f;
        velocities[centralB*3+2] = 0f;
        masses[centralB] = CENTRAL_MASS;

        for (int i = half + 1; i < maxParticles; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double r     = 10f + random.nextDouble() * 80f;

            float localX = (float)(r * Math.cos(angle));
            float localY = (float)(r * Math.sin(angle));
            float localZ = (float)((random.nextDouble() - 0.5) * 6f);

            float rotatedX = localX;
            float rotatedY = (float)(localY * Math.cos(0.78) - localZ * Math.sin(0.78));
            float rotatedZ = (float)(localY * Math.sin(0.78) + localZ * Math.cos(0.78));

            positions[i*3]   = 180f + rotatedX;
            positions[i*3+1] = 0f + rotatedY;
            positions[i*3+2] = 40f + rotatedZ;

            double speed = Math.sqrt(G * CENTRAL_MASS / r);
            float vX = (float)(-Math.sin(angle) * speed);
            float vY = (float)( Math.cos(angle) * speed);
            float vZ = 0f;

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

        float clusterRadius = 120.0f; 
        
        for (int i = 0; i < maxParticles; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi   = Math.acos(2 * random.nextDouble() - 1);
            
            double r = clusterRadius * Math.pow(random.nextDouble(), 1.5);

            positions[i*3]   = (float)(r * Math.sin(phi) * Math.cos(theta));
            positions[i*3+1] = (float)(r * Math.sin(phi) * Math.sin(theta));
            positions[i*3+2] = (float)(r * Math.cos(phi));

            float virialSpeed = (float) Math.sqrt((G * 50000.0f) / (r + 10.0f));

            float rx = (float)((random.nextDouble() - 0.5) * virialSpeed * 0.8f);
            float ry = (float)((random.nextDouble() - 0.5) * virialSpeed * 0.8f);
            float rz = (float)((random.nextDouble() - 0.5) * virialSpeed * 0.8f);

            float spinSpeed = virialSpeed * 0.4f;
            float sx = (float)(-Math.sin(theta) * spinSpeed);
            float sy = (float)( Math.cos(theta) * spinSpeed);

            velocities[i*3]   = rx + sx;
            velocities[i*3+1] = ry + sy;
            velocities[i*3+2] = rz;

            masses[i] = 1.0f + random.nextFloat() * 5.0f;
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
        spawnGalaxy(positions, velocities, masses, 0, 1, half, -separation/2, 0, 0, 10f, 100f, 8f, 0, speed, 0, random);
        spawnGalaxy(positions, velocities, masses, half, half+1, maxParticles, separation/2, 0, 0, 10f, 100f, 8f, 0, -speed, 0, random);
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
            positions[i*3] = (float)(r * Math.cos(angle)); positions[i*3+1] = (float)(r * Math.sin(angle)); positions[i*3+2] = (float)((random.nextDouble() - 0.5) * 2f);
            double speed = Math.sqrt(G * CENTRAL_MASS / r); velocities[i*3] = (float)(-Math.sin(angle) * speed); velocities[i*3+1] = (float)(Math.cos(angle) * speed); masses[i] = 1.0f;
        }
        spawnSphere(positions, velocities, masses, ringCount, maxParticles, 400, 0, 0, 50f, -0.8f, 0, 0, 1f, 4f, random);
    }

    public static void ThreeGalaxies(float[] positions, float[] velocities, float[] masses, int maxParticles, Random random) {
        int third = maxParticles / 3; float[] cx = {0f, 300f, 150f}; float[] cy = {-300f, -300f, 200f}; float[] bvx = {0.3f, -0.3f, 0f}; float[] bvy = {0.3f, 0.3f, -0.5f};
        for (int g = 0; g < 3; g++) {
            int start = g * third; int end = (g == 2) ? maxParticles : start + third;
            spawnGalaxy(positions, velocities, masses, start, start+1, end, cx[g], cy[g], 0f, 10f, 60f, 8f, bvx[g], bvy[g], 0f, random);
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
            positions[i*3] = (float)(r * Math.cos(angle)); positions[i*3+1] = (float)(r * Math.sin(angle)); positions[i*3+2] = (float)((random.nextDouble() - 0.5) * 6);
            double speed = Math.sqrt(G * bigMass / r); velocities[i*3] = (float)(-Math.sin(angle) * speed); velocities[i*3+1] = (float)(Math.cos(angle) * speed); masses[i] = 1.0f + random.nextFloat() * 2f;
        }
        spawnSphere(positions, velocities, masses, galaxyCount, maxParticles, 500, 150, 0, 30f, -1.2f, 0, 0, 1f, 3f, random);
    }

    public static void GridCubic(float[] p, float[] v, float[] m, int n, Random r) {
        int dim = (int) Math.cbrt(n);
        for(int i=0; i<n; i++) {
            p[i*3] = (i%dim - dim/2f) * 20f; p[i*3+1] = ((i/dim)%dim - dim/2f) * 20f; p[i*3+2] = (i/(dim*dim) - dim/2f) * 20f;
            m[i] = 2.0f;
        }
    }

    public static void VortexRing(float[] p, float[] v, float[] m, int n, Random r) {
        for(int i=0; i<n; i++) {
            float t = (float)i * 0.1f; float rad = 100f;
            p[i*3] = (float)Math.cos(t) * rad; p[i*3+1] = (float)Math.sin(t) * rad; p[i*3+2] = (float)Math.sin(t*2) * 50f;
            v[i*3] = (float)-Math.sin(t) * 5f; v[i*3+1] = (float)Math.cos(t) * 5f; v[i*3+2] = (float)Math.cos(t*2) * 2f;
            m[i] = 1.0f;
        }
    }

    public static void ExpandingShell(float[] p, float[] v, float[] m, int n, Random r) {
        for(int i=0; i<n; i++) {
            float theta = r.nextFloat()*6f; float phi = r.nextFloat()*3f;
            p[i*3] = (float)Math.cos(theta)*10f; p[i*3+1] = (float)Math.sin(theta)*10f; p[i*3+2] = (float)Math.cos(phi)*10f;
            v[i*3] = p[i*3]*0.5f; v[i*3+1] = p[i*3+1]*0.5f; v[i*3+2] = p[i*3+2]*0.5f; m[i]=1f;
        }
    }

    public static void StarBurst(float[] p, float[] v, float[] m, int n, Random r) {
        for(int i=0; i<n; i++) {
            float vel = 10f + r.nextFloat()*20f;
            v[i*3] = (r.nextFloat()-0.5f)*vel; v[i*3+1] = (r.nextFloat()-0.5f)*vel; v[i*3+2] = (r.nextFloat()-0.5f)*vel;
            m[i] = 0.1f;
        }
    }

    public static void GalaxyPairClose(float[] p, float[] v, float[] m, int n, Random r) {
        spawnSphere(p, v, m, 0, n/2, -100, 0, 0, 80, 0, 1, 0, 1, 5, r);
        spawnSphere(p, v, m, n/2, n, 100, 0, 0, 80, 0, -1, 0, 1, 5, r);
    }

    public static void ChaoticCluster(float[] p, float[] v, float[] m, int n, Random r) {
        for(int i=0; i<n; i++) {
            p[i*3] = (r.nextFloat()-0.5f)*500; p[i*3+1] = (r.nextFloat()-0.5f)*500; p[i*3+2] = (r.nextFloat()-0.5f)*500;
            v[i*3] = (r.nextFloat()-0.5f)*2; v[i*3+1] = (r.nextFloat()-0.5f)*2; v[i*3+2] = (r.nextFloat()-0.5f)*2;
            m[i] = r.nextFloat()*10f;
        }
    }

    public static void FlatDisk(float[] p, float[] v, float[] m, int n, Random r) {
        for(int i=0; i<n; i++) {
            float rad = r.nextFloat()*200f; float ang = r.nextFloat()*6f;
            p[i*3] = (float)Math.cos(ang)*rad; p[i*3+1] = (float)Math.sin(ang)*rad; p[i*3+2] = 0;
            v[i*3] = -(float)Math.sin(ang)*5f; v[i*3+1] = (float)Math.cos(ang)*5f; m[i]=1f;
        }
    }

    public static void TripleSunSystem(float[] p, float[] v, float[] m, int n, Random r) {
        p[0]=-200; p[1]=0; p[2]=0; m[0]=1e6f; p[3]=200; p[4]=0; p[5]=0; m[1]=1e6f; p[6]=0; p[7]=200; p[8]=0; m[2]=1e6f;
        spawnSphere(p, v, m, 9, n, 0, 0, 0, 100, 0, 0, 0, 0.1f, 0.5f, r);
    }

    public static void HollowSphere(float[] p, float[] v, float[] m, int n, Random r) {
        for(int i=0; i<n; i++) {
            double t = r.nextDouble()*6f; double p1 = r.nextDouble()*3f;
            p[i*3] = (float)(Math.sin(p1)*Math.cos(t)*150); p[i*3+1] = (float)(Math.sin(p1)*Math.sin(t)*150); p[i*3+2] = (float)(Math.cos(p1)*150);
            m[i] = 1f;
        }
    }

    public static void GravityWell(float[] p, float[] v, float[] m, int n, Random r) {
        p[0]=0; p[1]=0; p[2]=0; m[0]=1e8f;
        for(int i=1; i<n; i++) {
            float r_ = 50f + r.nextFloat()*300f; float ang = r.nextFloat()*6f;
            p[i*3] = (float)Math.cos(ang)*r_; p[i*3+1] = (float)Math.sin(ang)*r_; v[i*3+2] = (r.nextFloat()-0.5f)*5;
            m[i]=1f;
        }
    }

}
