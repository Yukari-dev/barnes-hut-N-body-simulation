package com.you.nbody.renderer;

import org.joml.Vector3f;
import org.joml.Matrix4f;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL46.*;
import com.you.nbody.core.Camera;

public class Particle{
    private int vao;
    public Vector3f position;
    public float mass;

    public Particle(Vector3f position){
        this.position = position;
        float[] localPos = {0f, 0f, 0f};
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(localPos.length);
        buffer.put(localPos).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        MemoryUtil.memFree(buffer);
    }

    public void Draw(Shader shader){
        shader.Use();

        shader.SetMatrix("projection", Camera.GetProjectionMatrix());
        shader.SetMatrix("view", Camera.GetViewMatrix());
        Matrix4f model = new Matrix4f().translate(position);
        shader.SetMatrix("model", model);

        glBindVertexArray(vao);
        glDrawArrays(GL_POINTS, 0, 1);
        glBindVertexArray(0);

        shader.UnUse();
    }

}

