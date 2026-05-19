package com.you.nbody.core;

import static org.lwjgl.glfw.GLFW.*;

public class Time{
    private static double deltaTime;
    private static double currentTime;
    private static double lastFrame;
    private static double frameCount;
    private static double framesPerSecond;
    private static double updateTimer;
    private static double fixedDeltaTime = 0.0016;
    private static double framesPerSecondUpdateTime = 1.0f;

    public static void Update(){
        currentTime = glfwGetTime();
        deltaTime = currentTime - lastFrame;
        lastFrame = currentTime;
        updateTimer += deltaTime;
        frameCount++;
        if(updateTimer >= framesPerSecondUpdateTime){
            framesPerSecond = frameCount/updateTimer;
            updateTimer = 0f;
            frameCount = 0;
        }
        // System.out.printf("deltaTime: %f, FPS: %f\n", deltaTime, framesPerSecond);
    }

    public static double GetDeltaTime(){
        return deltaTime;
    }

    public static double GetFixedDeltaTime(){
        return fixedDeltaTime;
    }

    public static double GetFramesPerSecond(){
        return framesPerSecond;
    }
}

