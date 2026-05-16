package com.you.nbody.renderer;

import org.joml.Vector3f;
import org.joml.Matrix4f;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL46.*;
import com.you.nbody.core.Camera;

public class ParticleSystem{
    private int maxParticles;
    private int vao, vbo;

    public ParticleSystem(int maxParticles){
        this.maxParticles = maxParticles;
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        long totalBufferSizeInBytes = (long) maxParticles * 3 * Float.BYTES;
        glBufferData(GL_ARRAY_BUFFER, totalBufferSizeInBytes, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void UpdatePositions(float[] positions){
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        
        FloatBuffer buffer = MemoryUtil.memAllocFloat(positions.length);
        buffer.put(positions).flip();

        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);

        MemoryUtil.memFree(buffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void Draw(Shader shader){
        shader.Use();

        shader.SetMatrix("projection", Camera.GetProjectionMatrix());
        shader.SetMatrix("view", Camera.GetViewMatrix());
        shader.SetMatrix("model", new Matrix4f());

        glBindVertexArray(vao);
        glDrawArrays(GL_POINTS, 0, maxParticles);
        glBindVertexArray(0);

        shader.UnUse();
    }
}

