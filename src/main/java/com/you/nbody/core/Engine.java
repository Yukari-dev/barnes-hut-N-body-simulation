package com.you.nbody.core;

import com.you.nbody.renderer.*;
import com.you.nbody.physics.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;
import org.joml.Vector3f;

import java.util.Random;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImInt;
import imgui.type.ImFloat;

public class Engine {
    private static long window;
    private static int width;
    private static int height;
    private static ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static ImGuiImplGl3  imGuiGl3  = new ImGuiImplGl3();

    private static int maxParticles = 20000; 
    private static float[] positions;
    private static float[] velocities;
    private static float[] masses;
    private static float[] colors;
    private static float[] starColors;
    
    private static ParticleSystem particleSystem;
    private static PhysicsWorld physicsWorld;
    private static Shader shader;
    private static final Random random = new Random();

    public static final String[] FORMATION_NAMES = {
        "Core", "Galaxy", "Pair", "Triple", "HeadOn", "Infall", "Ring", "Nebula", 
        "Jet", "Binary", "Cold", "Sling", "Cubic", "Vortex", 
        "Shell", "Burst", "NearPair", "Chaos", "Disk", "Hollow", "Well"
    };

    private static final String[] PALETTE_NAMES = {
        "Blue Steel", "Frostbite", "Muted Cosmos", "Nebula Shadow", "Void Obsidian",
        "Solar Eclipse", "Chrome Quartz", "Deep Algae", "Magma Core", "Emerald Isles",
        "Amethyst Void", "Sunset Drift", "Toxic Waste", "Candy Crush", "Royal Purple",
        "Golden Hour", "Fire and Ice", "Ocean Abyss", "Neon Cyber", "Blood Moon",
        "Spring Meadow", "Autumn Leaves", "Winter Chill", "Galaxy Edge",
        "Vaporwave", "Acid Rain", "Pure Gold", "The Matrix", "Deep Space X",
        "Lava Lamp", "Electric Blue", "Psychedelic", "Radioactive", "Overload"
    };

    private static final ImInt currentFormationIdx = new ImInt(0);
    private static final ImInt currentPaletteIdx = new ImInt(0);
    private static final ImInt uiParticleCount = new ImInt(maxParticles);
    private static final ImFloat uiTimeScale = new ImFloat(1.0f);
    private static final ImFloat uiSoftening = new ImFloat(8.0f);
    private static final ImFloat uiTheta = new ImFloat(1.5f);

    private static boolean isPaused = false;
    private static int framesCount = 0;
    private static float fpsUpdateTimer = 0;
    private static int smoothedFPS = 60;

    public static void Init(int Iwidth, int Iheight) {
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
            if (!ImGui.getIO().getWantCaptureMouse()) {
                float zoomSpeed = 120.0f;
                Camera.Zoom((float) -yoffset * zoomSpeed);
            }
        });

        GL.createCapabilities();
        ImGui.createContext();
        imGuiGlfw.init(window, true);
        imGuiGl3.init("#version 330 core");
        glEnable(GL_PROGRAM_POINT_SIZE);
        glEnable(GL_DEPTH_TEST);
        
        glfwSwapInterval(0); 
        glViewport(0, 0, width, height);
    }

    private static void ChangeFormation(int index, int particleCount) {
        isPaused = true;
        maxParticles = particleCount;

        positions = new float[maxParticles * 3];
        velocities = new float[maxParticles * 3];
        masses = new float[maxParticles];
        colors = new float[maxParticles * 3];

        StarColors.Palette selectedPalette = StarColors.Palette.values()[currentPaletteIdx.get()];
        starColors = StarColors.Generate(maxParticles, random, selectedPalette);

        switch (index) {
            case 0 -> Formations.GlobularCore(positions, velocities, masses, maxParticles, random);
            case 1 -> Formations.OneGalaxy(positions, velocities, masses, maxParticles, random);
            case 2 -> Formations.TwoGalaxies(positions, velocities, masses, maxParticles, random);
            case 3 -> Formations.ThreeGalaxies(positions, velocities, masses, maxParticles, random);
            case 4 -> Formations.TwoSpheresHeadOn(positions, velocities, masses, maxParticles, random);
            case 5 -> Formations.SphereIntoGalaxy(positions, velocities, masses, maxParticles, random);
            case 6 -> Formations.RingAndSphere(positions, velocities, masses, maxParticles, random);
            case 7 -> Formations.CosmicNebula(positions, velocities, masses, maxParticles, random);
            case 8 -> Formations.AccretionJet(positions, velocities, masses, maxParticles, random);
            case 9 -> Formations.BinaryStream(positions, velocities, masses, maxParticles, random);
            case 10 -> Formations.ColdCollapse(positions, velocities, masses, maxParticles, random);
            case 11 -> Formations.Slingshot(positions, velocities, masses, maxParticles, random);
            case 12 -> Formations.GridCubic(positions, velocities, masses, maxParticles, random);
            case 13 -> Formations.VortexRing(positions, velocities, masses, maxParticles, random);
            case 14 -> Formations.ExpandingShell(positions, velocities, masses, maxParticles, random);
            case 15 -> Formations.StarBurst(positions, velocities, masses, maxParticles, random);
            case 16 -> Formations.GalaxyPairClose(positions, velocities, masses, maxParticles, random);
            case 17 -> Formations.ChaoticCluster(positions, velocities, masses, maxParticles, random);
            case 18 -> Formations.FlatDisk(positions, velocities, masses, maxParticles, random);
            case 19 -> Formations.HollowSphere(positions, velocities, masses, maxParticles, random);
            case 20 -> Formations.GravityWell(positions, velocities, masses, maxParticles, random);
        }

        if (particleSystem == null) {
            particleSystem = new ParticleSystem(maxParticles);
        } else {
            particleSystem.Resize(maxParticles);
        }

        particleSystem.UploadStarColors(starColors);

        if (physicsWorld == null) {
            physicsWorld = new PhysicsWorld(maxParticles, positions, velocities, masses, colors);
        } else {
            physicsWorld.ResetData(maxParticles, positions, velocities, masses, colors);
        }

        isPaused = false;
    }

    private static void RefreshStellarColors() {
        StarColors.Palette selectedPalette = StarColors.Palette.values()[currentPaletteIdx.get()];
        starColors = StarColors.Generate(maxParticles, random, selectedPalette);
        particleSystem.UploadStarColors(starColors);
    }

    public static void Run() {
        shader = new Shader("vertexShader.glsl", "fragmentShader.glsl");
        Camera.SetPosition(new Vector3f(0, 750, 0));

        ChangeFormation(currentFormationIdx.get(), uiParticleCount.get());

        while (!glfwWindowShouldClose(window)) {
            Time.Update();
            Input.Update();
            
            framesCount++;
            fpsUpdateTimer += Time.GetDeltaTime();
            if (fpsUpdateTimer >= 0.5f) { 
                smoothedFPS = (int) (framesCount / fpsUpdateTimer);
                framesCount = 0;
                fpsUpdateTimer = 0.0f;
            }

            if (!ImGui.getIO().getWantCaptureMouse()) {
                Camera.HandleMouseInput();
            }

            glClearColor(0.01f, 0.01f, 0.018f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            try {
                java.lang.reflect.Field softeningField = physicsWorld.getClass().getDeclaredField("softening");
                softeningField.setAccessible(true);
                softeningField.setFloat(physicsWorld, uiSoftening.get());
            } catch (Exception ignored) {}
            
            physicsWorld.octree.theta = uiTheta.get();

            if (!isPaused) {
                physicsWorld.Update(particleSystem, uiTimeScale.get());
            }

            particleSystem.Draw(shader);

            imGuiGlfw.newFrame();
            ImGui.newFrame();

            ImGui.begin("Universal Command Dashboard");
            
            ImGui.textColored(0.2f, 0.8f, 1.0f, 1.0f, "SYSTEM PERFORMANCE");
            ImGui.text(String.format("Frame Speed: %d FPS", smoothedFPS));
            ImGui.text(String.format("Calculated Step: %.3f ms", 1000.0f / Math.max(smoothedFPS, 1)));
            ImGui.text("Active Mass Nodes: " + maxParticles);
            ImGui.separator();

            ImGui.textColored(1.0f, 0.8f, 0.2f, 1.0f, "STATE CONTROLS");
            if (isPaused) {
                if (ImGui.button("Resume Simulation")) isPaused = false;
            } else {
                if (ImGui.button("Pause Engine")) isPaused = true;
            }
            ImGui.sameLine();
            if (ImGui.button("Reset Particles")) {
                ChangeFormation(currentFormationIdx.get(), uiParticleCount.get());
            }
            ImGui.separator();

            ImGui.textColored(0.4f, 1.0f, 0.4f, 1.0f, "PHYSICS TUNING");
            ImGui.sliderFloat("Time Warp Factor", uiTimeScale.getData(), 0.0f, 4.0f, "%.2fx");
            ImGui.sliderFloat("Gravity Softening", uiSoftening.getData(), 0.1f, 50.0f, "e = %.1f");
            
            if (ImGui.isItemHovered()) {
                ImGui.setTooltip("Higher values prevent high-speed slingshot ejections inside dense cores.");
            }
            ImGui.sliderFloat("Barnes-hut Theta", uiTheta.getData(), 0.1f, 10.0f, "THETA = %.1f");
            ImGui.separator();

            ImGui.textColored(0.9f, 0.4f, 0.9f, 1.0f, "VISUAL THEME ENGINE");
            if (ImGui.combo("Spectra Palette", currentPaletteIdx, PALETTE_NAMES)) {
                RefreshStellarColors();
            }
            ImGui.separator();

            ImGui.textColored(1.0f, 0.4f, 0.4f, 1.0f, "COSMIC INITIALIZATION ARCHETYPE");
            ImGui.sliderInt("Stellar Densities", uiParticleCount.getData(), 1000, 150000);
            
            if (ImGui.combo("Preset Target", currentFormationIdx, FORMATION_NAMES)) {
                ChangeFormation(currentFormationIdx.get(), uiParticleCount.get());
            }

            if (uiParticleCount.get() > 80000) {
                ImGui.textColored(1.0f, 0.3f, 0.3f, 1.0f, "High Node Count: Tree processing may experience lag.");
            }

            ImGui.separator();
            Vector3f camPos = Camera.GetPosition();
            ImGui.text(String.format("Camera Position -> X:%.0f Y:%.0f Z:%.0f", camPos.x, camPos.y, camPos.z));
            if (ImGui.button("Snap View Home")) {
                Camera.SetPosition(new Vector3f(0, 750, 0));
            }

            ImGui.end();

            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        
        physicsWorld.Cleanup();
        particleSystem.Cleanup();
    }

    public static void Exit() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        glfwDestroyWindow(window);
        glfwTerminate();
        System.exit(0);
    }

    public static long GetWindow()  { return window; }
    public static int GetWidth()    { return width; }
    public static int GetHeight()   { return height; }
}
