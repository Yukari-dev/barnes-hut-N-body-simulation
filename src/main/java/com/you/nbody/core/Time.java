package com.you.nbody.core;

import static org.lwjgl.glfw.GLFW.*;

public class Time{
    private static double deltaTime;
    private static double currentTime;
    private static double lastFrame;
    private static double frameCount;
    private static double framesPerSecond;
    private static double updateTimer;

    public static void Update(){
        currentTime = glfwGetTime();
        deltaTime = currentTime - lastFrame;
        lastFrame = currentTime;
        updateTimer += deltaTime;
        frameCount++;
        if(updateTimer >= 1.0f){
            framesPerSecond = frameCount/updateTimer;
            updateTimer = 0f;
            frameCount = 0;
        }

        System.out.printf("deltaTime: %f, FPS: %f\n", deltaTime, framesPerSecond);
    }

    public static double GetDeltaTime(){
        return deltaTime;
    }

}

