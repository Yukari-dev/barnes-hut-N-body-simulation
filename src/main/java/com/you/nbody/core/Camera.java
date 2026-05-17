package com.you.nbody.core;

import org.joml.Vector3f;
import org.joml.Matrix4f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private static Vector3f position = new Vector3f(0);
    private static Vector3f target   = new Vector3f(0, 0, 0);
    private static Vector3f up       = new Vector3f(0, 1, 0);

    private static float yaw = 0.0f;
    private static float pitch = 0.0f;
    private static float distance = 150.f;

    private static float fov = (float)Math.toRadians(60.f);
    private static float nearPlane = 0.1f;
    private static float farPlane = 100000.f;

    private static double lastX = 0, lastY = 0;
    private static boolean firstMouse = true;

    static {
        UpdateCameraPosition();
    }

    public static void HandleMouseInput() {
        boolean leftPressed = Input.IsMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT);
        boolean middlePressed = Input.IsMouseButtonPressed(GLFW_MOUSE_BUTTON_MIDDLE);

        if (leftPressed || middlePressed) {
            double[] xpos = new double[1];
            double[] ypos = new double[1];
            glfwGetCursorPos(Engine.GetWindow(), xpos, ypos);

            if (firstMouse) {
                lastX = xpos[0];
                lastY = ypos[0];
                firstMouse = false;
            }

            float offsetX = (float) (lastX - xpos[0]);
            float offsetY = (float) (ypos[0] - lastY);

            lastX = xpos[0];
            lastY = ypos[0];

            if (middlePressed) {
                float panSensitivity = (distance / 1000.0f) * 1.5f; 
                
                Camera.Pan(-offsetX * panSensitivity, offsetY * panSensitivity);
            } 
            else {
                float rotateSensitivity = 0.2f;
                offsetX *= rotateSensitivity;
                offsetY *= rotateSensitivity;
                Camera.Rotate(offsetX, offsetY);
            }
        } else {
            firstMouse = true;
        }
    }

    private static void UpdateCameraPosition() {
        if (pitch > 89.0f) pitch = 89.0f;
        if (pitch < -89.0f) pitch = -89.0f;

        float radYaw = (float) Math.toRadians(yaw);
        float radPitch = (float) Math.toRadians(pitch);

        float x = (float) (distance * Math.cos(radPitch) * Math.sin(radYaw));
        float y = (float) (distance * Math.sin(radPitch));
        float z = (float) (distance * Math.cos(radPitch) * Math.cos(radYaw));

        position.set(x + target.x, y + target.y, z + target.z);
    }

    public static void Pan(float screenX, float screenY) {
        Vector3f lookDir = new Vector3f();
        position.sub(target, lookDir).normalize();

        Vector3f right = new Vector3f();
        lookDir.cross(up, right).normalize();

        Vector3f localUp = new Vector3f();
        right.cross(lookDir, localUp).normalize();

        right.mul(screenX);
        localUp.mul(screenY);

        target.add(right).add(localUp);
        position.add(right).add(localUp);
    }

    public static void Rotate(float deltaYaw, float deltaPitch) {
        yaw += deltaYaw;
        pitch += deltaPitch;
        UpdateCameraPosition();
    }

    public static void Zoom(float deltaDistance) {
        distance += deltaDistance;
        if (distance < 1.0f) distance = 1.0f;
        UpdateCameraPosition();
    }

    public static void SetTarget(Vector3f newTarget) {
        target.set(newTarget);
        UpdateCameraPosition();
    }
    
    public static Vector3f GetPosition() {
        return position;
    }

    public static void SetPosition(Vector3f newPosition){
        position = new Vector3f(newPosition);   
    }

    public static Matrix4f GetViewMatrix(){
        return new Matrix4f().lookAt(position, target, up);
    }

    public static Matrix4f GetProjectionMatrix(){
        float aspectRatio = (float) Engine.GetWidth() / Engine.GetHeight();
        return new Matrix4f().perspective(fov, aspectRatio, nearPlane, farPlane);
    }
}
