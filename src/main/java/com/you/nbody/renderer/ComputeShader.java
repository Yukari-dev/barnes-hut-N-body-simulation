package com.you.nbody.renderer;

import static org.lwjgl.opengl.GL43.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.joml.Matrix4f;

public class ComputeShader {
    private final int programID;

    public ComputeShader(String filepath) {
        String source;
        try {
            source = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read compute shader file at: " + filepath, e);
        }

        int shaderID = glCreateShader(GL_COMPUTE_SHADER);
        glShaderSource(shaderID, source);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            int logLength = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH);
            String log = glGetShaderInfoLog(shaderID, logLength);
            throw new RuntimeException("Compute Shader Compilation Failed Error:\n" + log);
        }

        programID = glCreateProgram();
        glAttachShader(programID, shaderID);
        glLinkProgram(programID);

        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            int logLength = glGetProgrami(programID, GL_INFO_LOG_LENGTH);
            String log = glGetProgramInfoLog(programID, logLength);
            throw new RuntimeException("Compute Program Link Failed Error:\n" + log);
        }

        glDeleteShader(shaderID);
    }

    public void Use() {
        glUseProgram(programID);
    }

    public void UnUse() {
        glUseProgram(0);
    }


    public void SetInt(String name, int value) {
        int location = glGetUniformLocation(programID, name);
        if (location != -1) {
            glUniform1i(location, value);
        }
    }

    public void SetFloat(String name, float value) {
        int location = glGetUniformLocation(programID, name);
        if (location != -1) {
            glUniform1f(location, value);
        }
    }

    public void Cleanup() {
        glDeleteProgram(programID);
    }
}
