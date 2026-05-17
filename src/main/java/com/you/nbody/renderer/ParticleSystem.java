package com.you.nbody.renderer;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL46.*;
import com.you.nbody.core.Camera;

public class ParticleSystem {
    private int maxParticles;
    private int vao, vbo, colorVbo, starColorVbo;
    
    private FloatBuffer positionScratchBuffer;
    private FloatBuffer colorScratchBuffer;

    public ParticleSystem(int maxParticles, int width, int height) {
        this.maxParticles = maxParticles;
        
        this.positionScratchBuffer = MemoryUtil.memAllocFloat(maxParticles * 3);
        this.colorScratchBuffer = MemoryUtil.memAllocFloat(maxParticles * 3);

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        long totalBufferSizeInBytes = (long) maxParticles * 3 * Float.BYTES;
        glBufferData(GL_ARRAY_BUFFER, totalBufferSizeInBytes, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        colorVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, colorVbo);
        glBufferData(GL_ARRAY_BUFFER, totalBufferSizeInBytes, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);

        starColorVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, starColorVbo);
        glBufferData(GL_ARRAY_BUFFER, totalBufferSizeInBytes, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void UpdatePositions(float[] positions) {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        
        positionScratchBuffer.clear();
        positionScratchBuffer.put(positions).flip();

        glBufferSubData(GL_ARRAY_BUFFER, 0, positionScratchBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void UpdateColors(float[] colors) {
        glBindBuffer(GL_ARRAY_BUFFER, colorVbo);
        
        colorScratchBuffer.clear();
        colorScratchBuffer.put(colors).flip();

        glBufferSubData(GL_ARRAY_BUFFER, 0, colorScratchBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void UploadStarColors(float[] starColors) {
        glBindBuffer(GL_ARRAY_BUFFER, starColorVbo);
        FloatBuffer buffer = MemoryUtil.memAllocFloat(starColors.length);
        buffer.put(starColors).flip();
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
        MemoryUtil.memFree(buffer);
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
        glDrawArrays(GL_POINTS, 0, maxParticles);
        glBindVertexArray(0);

        glDepthMask(true);
        glDisable(GL_BLEND);
        shader.UnUse();
    }

    public void Cleanup() {
        MemoryUtil.memFree(positionScratchBuffer);
        MemoryUtil.memFree(colorScratchBuffer);
    }
}
