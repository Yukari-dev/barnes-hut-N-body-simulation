package com.you.nbody;
import com.you.nbody.core.Engine;

public class Main {
    public static void main(String[] args) {
        Engine.Init(800, 600);
        Engine.Run();
        Engine.Exit();
    }
}
