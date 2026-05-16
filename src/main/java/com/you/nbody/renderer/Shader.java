package com.you.nbody.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.lwjgl.opengl.GL46.*;
import org.lwjgl.system.MemoryStack;
import org.joml.Matrix4f;

public class Shader{

    private int programID;

    public Shader(String vertexPath, String fragPath){
        String shadersPath = "src/main/resources/shaders/";
        String vertexSource = GetShaderSource(shadersPath+vertexPath);
        String fragSource = GetShaderSource(shadersPath+fragPath);
        int vertexId = CompileShader(vertexSource, GL_VERTEX_SHADER);
        int fragId = CompileShader(fragSource, GL_FRAGMENT_SHADER);
        programID = CreateProgram(vertexId, fragId);
    }

    private int CompileShader(String source, int type){
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        int success = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " + glGetShaderInfoLog(shader, 512));
        }
        return shader;
    }

    private int CreateProgram(int vertexShader, int fragShader){
        int id = glCreateProgram();
        glAttachShader(id, vertexShader);
        glAttachShader(id, fragShader);
        glLinkProgram(id);
        int success = glGetProgrami(id, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            throw new RuntimeException("Program linking failed: " + glGetProgramInfoLog(id, 512));
        }
        glDeleteShader(vertexShader);
        glDeleteShader(fragShader); 
        return id;
    }

    public void Use(){
        glUseProgram(programID);
    }

    public void UnUse(){
        glUseProgram(0);
    }

    public int GetProgramID(){
        return programID;
    }

    public void SetMatrix(String name, Matrix4f matrix){
        int loc = GetLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(
                    loc,
                    false,
                    matrix.get(stack.mallocFloat(16))
            );
        }
    }

    public int GetLocation(String name){
        return glGetUniformLocation(programID, name);
    }

    String GetShaderSource(String path){
        try{
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            String src = new String(bytes, StandardCharsets.UTF_8);
            if (src.startsWith("\uFEFF")) src = src.substring(1);
            return src.trim();
        } catch (IOException e) {
            throw new RuntimeException("Could not read shader file: " + path, e);
        }
    }
}


