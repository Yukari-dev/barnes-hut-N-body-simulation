package com.you.nbody.renderer;

import com.you.nbody.core.Camera;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.*;

public class ParticleSystem {

    private int maxParticles;
    private int activeParticles;

    private int vao;
    private int positionVbo;
    private int colorVbo;
    private int starColorVbo;

    private long totalBufferSizeBytes;

    private FloatBuffer positionScratch;
    private FloatBuffer colorScratch;
    private FloatBuffer starColorScratch;

    public ParticleSystem(int maxParticles) {
        this.maxParticles = maxParticles;
        this.activeParticles = maxParticles;

        totalBufferSizeBytes = (long) maxParticles * 3 * Float.BYTES;

        positionScratch = MemoryUtil.memAllocFloat(maxParticles * 3);
        colorScratch = MemoryUtil.memAllocFloat(maxParticles * 3);
        starColorScratch = MemoryUtil.memAllocFloat(maxParticles * 3);

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        positionVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, positionVbo);
        glBufferData(GL_ARRAY_BUFFER, totalBufferSizeBytes, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        colorVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, colorVbo);
        glBufferData(GL_ARRAY_BUFFER, totalBufferSizeBytes, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);

        starColorVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, starColorVbo);
        glBufferData(GL_ARRAY_BUFFER, totalBufferSizeBytes, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void Resize(int newMaxParticles) {
        maxParticles = newMaxParticles;
        activeParticles = newMaxParticles;

        totalBufferSizeBytes = (long) newMaxParticles * 3 * Float.BYTES;

        if (positionScratch != null)
            MemoryUtil.memFree(positionScratch);

        if (colorScratch != null)
            MemoryUtil.memFree(colorScratch);

        if (starColorScratch != null)
            MemoryUtil.memFree(starColorScratch);

        positionScratch = MemoryUtil.memAllocFloat(newMaxParticles * 3);
        colorScratch = MemoryUtil.memAllocFloat(newMaxParticles * 3);
        starColorScratch = MemoryUtil.memAllocFloat(newMaxParticles * 3);

        glBindBuffer(GL_ARRAY_BUFFER, positionVbo);
        glBufferData(GL_ARRAY_BUFFER, totalBufferSizeBytes, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, colorVbo);
        glBufferData(GL_ARRAY_BUFFER, totalBufferSizeBytes, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, starColorVbo);
        glBufferData(GL_ARRAY_BUFFER, totalBufferSizeBytes, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void UpdatePositions(float[] positions) {
        activeParticles = positions.length / 3;

        long requiredBytes = (long) positions.length * Float.BYTES;

        glBindBuffer(GL_ARRAY_BUFFER, positionVbo);

        if (requiredBytes > totalBufferSizeBytes) {
            totalBufferSizeBytes = requiredBytes;
            glBufferData(GL_ARRAY_BUFFER, totalBufferSizeBytes, GL_DYNAMIC_DRAW);
        }

        if (positionScratch.capacity() < positions.length) {
            MemoryUtil.memFree(positionScratch);
            positionScratch = MemoryUtil.memAllocFloat(positions.length);
        }

        positionScratch.clear();
        positionScratch.put(positions).flip();

        glBufferSubData(GL_ARRAY_BUFFER, 0, positionScratch);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void UpdateColors(float[] colors) {
        long requiredBytes = (long) colors.length * Float.BYTES;

        glBindBuffer(GL_ARRAY_BUFFER, colorVbo);

        if (requiredBytes > totalBufferSizeBytes) {
            totalBufferSizeBytes = requiredBytes;
            glBufferData(GL_ARRAY_BUFFER, totalBufferSizeBytes, GL_DYNAMIC_DRAW);
        }

        if (colorScratch.capacity() < colors.length) {
            MemoryUtil.memFree(colorScratch);
            colorScratch = MemoryUtil.memAllocFloat(colors.length);
        }

        colorScratch.clear();
        colorScratch.put(colors).flip();

        glBufferSubData(GL_ARRAY_BUFFER, 0, colorScratch);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void UploadStarColors(float[] starColors) {
        long requiredBytes = (long) starColors.length * Float.BYTES;

        glBindBuffer(GL_ARRAY_BUFFER, starColorVbo);

        if (requiredBytes > totalBufferSizeBytes) {
            glBufferData(GL_ARRAY_BUFFER, requiredBytes, GL_STATIC_DRAW);
        }

        if (starColorScratch.capacity() < starColors.length) {
            MemoryUtil.memFree(starColorScratch);
            starColorScratch = MemoryUtil.memAllocFloat(starColors.length);
        }

        starColorScratch.clear();
        starColorScratch.put(starColors).flip();

        glBufferSubData(GL_ARRAY_BUFFER, 0, starColorScratch);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void Draw(Shader shader) {
        shader.Use();
        shader.SetMatrix("projection", Camera.GetProjectionMatrix());
        shader.SetMatrix("view", Camera.GetViewMatrix());
        shader.SetMatrix("model", new Matrix4f());

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        glDepthMask(false);

        glBindVertexArray(vao);
        glDrawArrays(GL_POINTS, 0, activeParticles);
        glBindVertexArray(0);

        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    public void Cleanup() {
        if (positionScratch != null)
            MemoryUtil.memFree(positionScratch);

        if (colorScratch != null)
            MemoryUtil.memFree(colorScratch);

        if (starColorScratch != null)
            MemoryUtil.memFree(starColorScratch);

        glDeleteBuffers(positionVbo);
        glDeleteBuffers(colorVbo);
        glDeleteBuffers(starColorVbo);

        glDeleteVertexArrays(vao);
    }
}
