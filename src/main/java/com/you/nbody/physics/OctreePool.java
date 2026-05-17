package com.you.nbody.physics;

public class OctreePool {
    private final Octree[] pool;
    private int used = 0;

    public OctreePool(int capacity) {
        pool = new Octree[capacity];
        for (int i = 0; i < capacity; i++)
            pool[i] = new Octree();
    }

    public Octree get(float cx, float cy, float cz, float halfSize) {
        if (used >= pool.length) {
            return new Octree(cx, cy, cz, halfSize);
        }
        Octree node = pool[used++];
        node.reset(cx, cy, cz, halfSize);
        return node;
    }

    public void reset() {
        used = 0; 
    }

    public int getUsed() { return used; }
}
