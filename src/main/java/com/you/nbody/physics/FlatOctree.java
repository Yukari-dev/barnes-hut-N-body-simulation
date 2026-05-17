package com.you.nbody.physics;

import java.util.Arrays;

public class FlatOctree {
    private static final int MAX_DEPTH = 64;

    public float[] cx;
    public float[] cy;
    public float[] cz;
    public float[] halfSize;

    public float[] comX;
    public float[] comY;
    public float[] comZ;
    public float[] totalMass;

    public int[] particleIdx;
    public int[] children; 

    private int nodeCount = 0;
    private final int capacity;
    public float theta = 1.5f;

    public FlatOctree(int maxParticles) {
        this.capacity = maxParticles * 4;
        
        this.cx = new float[capacity];
        this.cy = new float[capacity];
        this.cz = new float[capacity];
        this.halfSize = new float[capacity];

        this.comX = new float[capacity];
        this.comY = new float[capacity];
        this.comZ = new float[capacity];
        this.totalMass = new float[capacity];

        this.particleIdx = new int[capacity];
        this.children = new int[capacity * 8];
    }

    public void reset(float rootCx, float rootCy, float rootCz, float rootHalfSize) {
        nodeCount = 1;
        
        cx[0] = rootCx;
        cy[0] = rootCy;
        cz[0] = rootCz;
        halfSize[0] = rootHalfSize;
        
        totalMass[0] = 0.0f;
        comX[0] = comY[0] = comZ[0] = 0.0f;
        particleIdx[0] = -1;

        Arrays.fill(children, 0, 8, -1);
    }

    public void insert(int particleId, float[] pos, float[] masses) {
        insertNode(0, particleId, pos, masses, 0);
    }

    private void insertNode(int node, int idx, float[] pos, float[] masses, int depth) {
        if (depth > MAX_DEPTH) return;

        float px = pos[idx * 3];
        float py = pos[idx * 3 + 1];
        float pz = pos[idx * 3 + 2];
        float m  = masses[idx];

        if (totalMass[node] == 0.0f) {
            particleIdx[node] = idx;
            comX[node] = px; comY[node] = py; comZ[node] = pz;
            totalMass[node] = m;
            return;
        }

        if (particleIdx[node] != -1) {
            pushDown(node, pos, masses, depth);
        }

        float newMass = totalMass[node] + m;
        comX[node] = (comX[node] * totalMass[node] + px * m) / newMass;
        comY[node] = (comY[node] * totalMass[node] + py * m) / newMass;
        comZ[node] = (comZ[node] * totalMass[node] + pz * m) / newMass;
        totalMass[node] = newMass;

        int oct = octantOf(node, px, py, pz);
        int childBaseIdx = node * 8 + oct;
        
        if (children[childBaseIdx] == -1) {
            children[childBaseIdx] = makeChild(node, oct);
        }
        
        insertNode(children[childBaseIdx], idx, pos, masses, depth + 1);
    }

    private void pushDown(int node, float[] pos, float[] masses, int depth) {
        int existing = particleIdx[node];
        particleIdx[node] = -1;
        
        float px = pos[existing * 3];
        float py = pos[existing * 3 + 1];
        float pz = pos[existing * 3 + 2];
        
        int oct = octantOf(node, px, py, pz);
        int childBaseIdx = node * 8 + oct;
        
        if (children[childBaseIdx] == -1) {
            children[childBaseIdx] = makeChild(node, oct);
        }
        
        insertNode(children[childBaseIdx], existing, pos, masses, depth + 1);
    }

    private int octantOf(int node, float px, float py, float pz) {
        int idx = 0;
        if (px >= cx[node]) idx |= 1;
        if (py >= cy[node]) idx |= 2;
        if (pz >= cz[node]) idx |= 4;
        return idx;
    }

    private int makeChild(int parentNode, int octant) {
        if (nodeCount >= capacity) return parentNode; 

        int childNode = nodeCount++;
        float q = halfSize[parentNode] * 0.5f;

        cx[childNode] = cx[parentNode] + ((octant & 1) != 0 ? q : -q);
        cy[childNode] = cy[parentNode] + ((octant & 2) != 0 ? q : -q);
        cz[childNode] = cz[parentNode] + ((octant & 4) != 0 ? q : -q);
        halfSize[childNode] = q;

        totalMass[childNode] = 0.0f;
        comX[childNode] = comY[childNode] = comZ[childNode] = 0.0f;
        particleIdx[childNode] = -1;

        int childBase = childNode * 8;
        Arrays.fill(children, childBase, childBase + 8, -1);

        return childNode;
    }

    public void computeAcceleration(int idx, float[] pos, float softening, float[] acc) {
        float ax = 0.0f, ay = 0.0f, az = 0.0f;
        float px = pos[idx * 3];
        float py = pos[idx * 3 + 1];
        float pz = pos[idx * 3 + 2];

        int[] stack = new int[128]; 
        int stackPtr = 0;
        stack[stackPtr++] = 0; 

        while (stackPtr > 0) {
            int node = stack[--stackPtr];

            if (totalMass[node] == 0.0f) continue;

            float dx = comX[node] - px;
            float dy = comY[node] - py;
            float dz = comZ[node] - pz;
            float distSq = dx * dx + dy * dy + dz * dz;
            float dist = (float) Math.sqrt(distSq);

            if (particleIdx[node] != -1) {
                if (particleIdx[node] == idx) continue;
                float soft = distSq + softening * softening;
                float inv = 1.0f / (soft * (float) Math.sqrt(soft));
                float f = totalMass[node] * inv;
                ax += dx * f;
                ay += dy * f;
                az += dz * f;
                continue;
            }

            if ((halfSize[node] * 2.0f) / dist < theta) {
                float soft = distSq + softening * softening;
                float inv = 1.0f / (soft * (float) Math.sqrt(soft));
                float f = totalMass[node] * inv;
                ax += dx * f;
                ay += dy * f;
                az += dz * f;
                continue;
            }

            int baseIdx = node * 8;
            for (int i = 0; i < 8; i++) {
                int childNode = children[baseIdx + i];
                if (childNode != -1) {
                    stack[stackPtr++] = childNode;
                }
            }
        }

        acc[0] = ax;
        acc[1] = ay;
        acc[2] = az;
    }
}
