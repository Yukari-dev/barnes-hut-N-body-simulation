package com.you.nbody.renderer;

import static org.lwjgl.opengl.GL46.*;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

import com.you.nbody.core.Camera;
import com.you.nbody.renderer.Shader;
import org.joml.Matrix4f;

public class GPUParticleSystem {
    private final int numParticles;
    private int vao;
    private int positionSSBO, velocitySSBO, colorSSBO;

    public GPUParticleSystem(int numParticles, float[] initialPositions, float[] initialMasses, float[] initialVelocities) {
        this.numParticles = numParticles;

        FloatBuffer posBuffer = BufferUtils.createFloatBuffer(numParticles * 4);
        FloatBuffer velBuffer = BufferUtils.createFloatBuffer(numParticles * 4);
        FloatBuffer colBuffer = BufferUtils.createFloatBuffer(numParticles * 4);

        for (int i = 0; i < numParticles; i++) {
            posBuffer.put(initialPositions[i * 3]).put(initialPositions[i * 3 + 1]).put(initialPositions[i * 3 + 2]).put(initialMasses[i]);
            velBuffer.put(initialVelocities[i * 3]).put(initialVelocities[i * 3 + 1]).put(initialVelocities[i * 3 + 2]).put(0.0f);
            colBuffer.put(0.0f).put(0.0f).put(1.0f).put(1.0f);
        }
        posBuffer.flip(); velBuffer.flip(); colBuffer.flip();

        positionSSBO = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, positionSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, posBuffer, GL_DYNAMIC_DRAW);

        velocitySSBO = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, velocitySSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, velBuffer, GL_DYNAMIC_DRAW);

        colorSSBO = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, colorSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, colBuffer, GL_DYNAMIC_DRAW);

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, positionSSBO);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, colorSSBO);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void Draw(Shader renderShader) {
        renderShader.Use();
        renderShader.SetMatrix("projection", Camera.GetProjectionMatrix());
        renderShader.SetMatrix("view", Camera.GetViewMatrix());
        renderShader.SetMatrix("model", new Matrix4f());
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glDepthMask(false);

        glBindVertexArray(vao);
        glDrawArrays(GL_POINTS, 0, numParticles); 
        glBindVertexArray(0);

        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    public int getPositionSSBO() { return positionSSBO; }
    public int getVelocitySSBO() { return velocitySSBO; }
    public int getColorSSBO() { return colorSSBO; }
}
