package com.you.nbody.renderer;

import org.joml.Vector3f;
import org.joml.Matrix4f;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL46.*;
import java.util.ArrayList;
import com.you.nbody.core.Camera;

public class BoundsRenderer{
    private int vao, vbo;
    private Shader shader;
    private ArrayList<Float> lineVertices;
    
    public BoundsRenderer(){
        shader = new Shader("lineVertex.glsl", "lineFrag.glsl");
        lineVertices = new ArrayList<>();
        
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void Clear(){
        lineVertices.clear();
    }

    public void AddBox(Vector3f min, Vector3f max){
        float[][] c ={
            {min.x, min.y, min.z}, {max.x, min.y, min.z},
            {max.x, max.y, min.z}, {min.x, max.y, min.z},

            {min.x, min.y, max.z}, {max.x, min.y, max.z},
            {max.x, max.y, max.z}, {min.x, max.y, max.z},
        };

        int[][] edges = {
            {0, 1}, {1, 2}, {2, 3}, {3, 0},
            {4, 5}, {5, 6}, {6, 7}, {7, 4},
            {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        for (int[] edge : edges) {
            lineVertices.add(c[edge[0]][0]); 
            lineVertices.add(c[edge[0]][1]); 
            lineVertices.add(c[edge[0]][2]);
            lineVertices.add(c[edge[1]][0]); 
            lineVertices.add(c[edge[1]][1]);
            lineVertices.add(c[edge[1]][2]);
        }
    }
    
    public void Draw(){
        if(lineVertices.isEmpty()) return;
        float[] rawData = new float[lineVertices.size()];

        for(int i = 0; i < lineVertices.size(); i++){
            rawData[i] = lineVertices.get(i);
        }
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = MemoryUtil.memAllocFloat(rawData.length);
        buffer.put(rawData).flip();
        glBufferData(GL_ARRAY_BUFFER, rawData, GL_DYNAMIC_DRAW);
        MemoryUtil.memFree(buffer);

        shader.Use();
        shader.SetMatrix("projection", Camera.GetProjectionMatrix());
        shader.SetMatrix("view", Camera.GetViewMatrix());

        glBindVertexArray(vao);
        glDrawArrays(GL_LINES, 0, rawData.length/3);
        glBindVertexArray(0);
        shader.UnUse();
    }

}
