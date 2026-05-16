package com.you.nbody.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.lwjgl.opengl.GL46.*;

public class Shader{
    public Shader(String vertexPath, String fragmentPath){
        String vertexSource = GetShaderSource(vertexPath);
        CompileShader(vertexSource, GL_VERTEX_SHADER);
    }

    private void CompileShader(String source, int type){

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


