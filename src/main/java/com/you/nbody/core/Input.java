package com.you.nbody.core;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.glfw.GLFW.*;

public class Input{


    public static void Update(){
        if(IsKeyPressed(GLFW_KEY_ESCAPE))
            glfwSetWindowShouldClose(Engine.GetWindow(), true);
    }

    public static boolean IsKeyPressed(int key){
        return glfwGetKey(Engine.GetWindow(), key) == GLFW_PRESS;
    }

    public static boolean IsMouseButtonPressed(int key){
        return glfwGetMouseButton(Engine.GetWindow(), key) == GLFW_PRESS;
    }

}

