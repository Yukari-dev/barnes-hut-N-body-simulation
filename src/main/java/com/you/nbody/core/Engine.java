package com.you.nbody.core;

import com.you.nbody.renderer.*;
import com.you.nbody.physics.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine{
    private static long window;
    private static int width;
    private static int height;
    private static double accumulator = 0;
    private static double fixedStep   = 0.016;

    public static void Init(int Iwidth, int Iheight){
        width = Iwidth;
        height = Iheight;
        glfwInit();

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
        window = glfwCreateWindow(width, height, "opengl-app", 0, 0);

        glfwMakeContextCurrent(window);

        glfwSetFramebufferSizeCallback(window, (window, newWidth, newHeight) -> {
            glViewport(0, 0, newWidth, newHeight);
            width = newWidth;
            height = newHeight;
        });

        glfwSetScrollCallback(window, (win, xoffset, yoffset) -> {
            float zoomSpeed = 100.0f;
            Camera.Zoom((float) -yoffset * zoomSpeed);
        });

        GL.createCapabilities();
        glEnable(GL_PROGRAM_POINT_SIZE);
        glEnable(GL_DEPTH_TEST);
        glfwSwapInterval(0);

        glViewport(0, 0, width, height);
    }

    public static void Run(){
        Shader shader = new Shader("vertexShader.glsl", "fragmentShader.glsl");

        int maxParticles = 20000;
        Random random = new Random();
        ParticleSystem particleSystem = new ParticleSystem(maxParticles, width, height);
        Camera.SetPosition(new Vector3f(0, 600, 0));
        float[] positions = new float[maxParticles * 3];
        float[] velocities = new float[maxParticles * 3];
        float[] masses = new float[maxParticles];
        float[] colors = new float[maxParticles * 3];
        float[] starColors = StarColors.Generate(maxParticles, random);
        particleSystem.UploadStarColors(starColors);

        // Formations.TwoGalaxies(positions, velocities, masses, maxParticles, random);
        // Formations.OneGalaxy(positions, velocities, masses, maxParticles, random);
        // Formations.TwoSpheresHeadOn(positions, velocities, masses, maxParticles, random);
        // Formations.SphereIntoGalaxy(positions, velocities, masses, maxParticles, random);
        // Formations.RingAndSphere(positions, velocities, masses, maxParticles, random);
        // Formations.ThreeGalaxies(positions, velocities, masses, maxParticles, random);
        // Formations.ColdCollapse(positions, velocities, masses, maxParticles, random);
        Formations.CosmicNebula(positions, velocities, masses, maxParticles, random);
        // Formations.AccretionJet(positions, velocities, masses, maxParticles, random);
        // Formations.BinaryStream(positions, velocities, masses, maxParticles, random);
        // Formations.GlobularCore(positions, velocities, masses, maxParticles, random);
        // Formations.Slingshot(positions, velocities, masses, maxParticles, random);

        PhysicsWorld physicsWorld = new PhysicsWorld(maxParticles, positions, velocities, masses, colors);

        while(!glfwWindowShouldClose(window)){
            Time.Update();
            Input.Update();
            Camera.HandleMouseInput();

            glClearColor(0f, 0f, 0f, 1f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            physicsWorld.Update(particleSystem);

            particleSystem.Draw(shader);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        physicsWorld.Cleanup();
    }


    public static void Exit(){
        glfwDestroyWindow(window);
        glfwTerminate();
        System.exit(0);
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
