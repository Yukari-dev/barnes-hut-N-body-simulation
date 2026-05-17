package com.you.nbody.physics;

import com.you.nbody.renderer.BoundsRenderer;
import org.joml.Vector3f;

public class Octree {
    float cx, cy, cz, halfSize;

    float comX, comY, comZ;
    float totalMass;

    int particleIdx = -1;
    final Octree[] children = new Octree[8];

    private static final float THETA = 0.9f;
    private static final int   MAX_DEPTH = 64;

    private BoundsRenderer boundsRenderer;

    public Octree() {}

    public Octree(float cx, float cy, float cz, float halfSize) {
        reset(cx, cy, cz, halfSize);
    }

    public void reset(float cx, float cy, float cz, float halfSize) {
        this.cx       = cx;
        this.cy       = cy;
        this.cz       = cz;
        this.halfSize = halfSize;
        this.totalMass   = 0;
        this.comX = comY = comZ = 0;
        this.particleIdx = -1;
        java.util.Arrays.fill(children, null);
    }

    public void insert(int idx, float[] pos, float[] masses, int depth, OctreePool pool) {
        if (depth > MAX_DEPTH) return;

        float px = pos[idx*3];
        float py = pos[idx*3+1];
        float pz = pos[idx*3+2];
        float m  = masses[idx];

        if (totalMass == 0) {
            particleIdx = idx;
            comX = px; comY = py; comZ = pz;
            totalMass = m;
            return;
        }

        if (particleIdx != -1) {
            pushDown(pos, masses, depth, pool);
        }

        float newMass = totalMass + m;
        comX = (comX * totalMass + px * m) / newMass;
        comY = (comY * totalMass + py * m) / newMass;
        comZ = (comZ * totalMass + pz * m) / newMass;
        totalMass = newMass;

        int oct = octantOf(px, py, pz);
        if (children[oct] == null) children[oct] = makeChild(oct, pool);
        children[oct].insert(idx, pos, masses, depth + 1, pool);
    }

    private void pushDown(float[] pos, float[] masses, int depth, OctreePool pool) {
        int existing = particleIdx;
        particleIdx = -1;
        float px = pos[existing*3];
        float py = pos[existing*3+1];
        float pz = pos[existing*3+2];
        int oct = octantOf(px, py, pz);
        if (children[oct] == null) children[oct] = makeChild(oct, pool);
        children[oct].insert(existing, pos, masses, depth + 1, pool);
    }

    private int octantOf(float px, float py, float pz) {
        int idx = 0;
        if (px >= cx) idx |= 1;
        if (py >= cy) idx |= 2;
        if (pz >= cz) idx |= 4;
        return idx;
    }

    private Octree makeChild(int idx, OctreePool pool) {
        float q = halfSize * 0.5f;
        return pool.get(
            cx + ((idx & 1) != 0 ? q : -q),
            cy + ((idx & 2) != 0 ? q : -q),
            cz + ((idx & 4) != 0 ? q : -q),
            q
        );
    }

    public void computeAcceleration(int idx, float[] pos, float[] masses, float softening, float[] acc) {
        acc[0] = 0; acc[1] = 0; acc[2] = 0;
        accumulate(idx, pos, masses, softening, acc);
    }

    private void accumulate(int idx, float[] pos, float[] masses, float softening, float[] acc) {
        if (totalMass == 0) return;

        float dx = comX - pos[idx*3];
        float dy = comY - pos[idx*3+1];
        float dz = comZ - pos[idx*3+2];
        float distSq = dx*dx + dy*dy + dz*dz;
        float dist   = (float) Math.sqrt(distSq);

        if (particleIdx != -1) {
            if (particleIdx == idx) return; 
            float soft = distSq + softening * softening;
            float inv  = 1.0f / (soft * (float) Math.sqrt(soft));
            float f    = totalMass * inv;
            acc[0] += dx * f;
            acc[1] += dy * f;
            acc[2] += dz * f;
            return;
        }

        if ((halfSize * 2.0f) / dist < THETA) {
            float soft = distSq + softening * softening;
            float inv  = 1.0f / (soft * (float) Math.sqrt(soft));
            float f    = totalMass * inv;
            acc[0] += dx * f;
            acc[1] += dy * f;
            acc[2] += dz * f;
            return;
        }

        for (Octree child : children)
            if (child != null) child.accumulate(idx, pos, masses, softening, acc);
    }

    public void initRenderer() {
        boundsRenderer = new BoundsRenderer();
    }

    public void draw() {
        if (boundsRenderer == null) return;
        boundsRenderer.Clear();
        collectBounds(boundsRenderer);
        boundsRenderer.Draw();
    }

    private void collectBounds(BoundsRenderer renderer) {
        Vector3f min = new Vector3f(cx - halfSize, cy - halfSize, cz - halfSize);
        Vector3f max = new Vector3f(cx + halfSize, cy + halfSize, cz + halfSize);
        renderer.AddBox(min, max);
        for (Octree child : children)
            if (child != null) child.collectBounds(renderer);
    }
}
