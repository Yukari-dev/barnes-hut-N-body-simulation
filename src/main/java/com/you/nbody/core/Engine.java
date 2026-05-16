package com.you.nbody.core;

import com.you.nbody.renderer.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine{
    private static long window;
    private static int width;
    private static int height;

    public static void Init(int Iwidth, int Iheight){
        width = Iwidth;
        height = Iheight;
        glfwInit();

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        window = glfwCreateWindow(width, height, "opengl-app", 0, 0);

        glfwMakeContextCurrent(window);

        glfwSetFramebufferSizeCallback(window, (window, newWidth, newHeight) -> {
            glViewport(0, 0, newWidth, newHeight);
            width = newWidth;
            height = newHeight;
        });

        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glfwSwapInterval(0);

        glViewport(0, 0, width, height);
    }

    public static void Run(){
        Shader shader = new Shader("vertexShader.glsl", "fragmentShader.glsl");
        int maxParticles = 10000000;
        Random random = new Random();
        ParticleSystem particleSystem = new ParticleSystem(maxParticles);
        Camera.SetPosition(new Vector3f(0, 0, -100));
        float[] positions = new float[maxParticles * 3];
        float posRange = 100;
            
        for (int i = 0; i < maxParticles; i++) {
            positions[i * 3 + 0] = random.nextFloat(-posRange, posRange);
            positions[i * 3 + 1] = random.nextFloat(-posRange, posRange);
            positions[i * 3 + 2] = 100;
        }
        particleSystem.UpdatePositions(positions);

        while(!glfwWindowShouldClose(window)){
            Update();
            Render();

            particleSystem.Draw(shader);
            
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private static void Render(){
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private static void Update(){
        Time.Update();
        Input.Update();
    }

    public static void Exit(){
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public static long GetWindow(){
        return window;
    }

    public static int GetWidth(){
        return width;
    }

    public static int GetHeight(){
        return height;
    }
}
