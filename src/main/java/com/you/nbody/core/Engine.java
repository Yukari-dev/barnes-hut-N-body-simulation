package com.you.nbody.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


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
        glfwSwapInterval(1);

        glViewport(0, 0, width, height);
    }

    public static void Run(){
        while(!glfwWindowShouldClose(window)){
            Update();
            Render();
            
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

}
