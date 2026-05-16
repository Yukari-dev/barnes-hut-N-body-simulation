package com.you.nbody.core;

import org.joml.Vector3f;
import org.joml.Matrix4f;


public class Camera{
    private static Vector3f position = new Vector3f(0);
    private static Vector3f rotation = new Vector3f(0);
    private static Vector3f forward  = new Vector3f(0, 0, -1);
    private static Vector3f target   = new Vector3f(0, 0, 0);
    private static Vector3f up       = new Vector3f(0, 1, 0);

    private static float fov = (float)Math.toRadians(60.f);
    private static float nearPlane = 0.1f;
    private static float farPlane = 1000.f;

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

