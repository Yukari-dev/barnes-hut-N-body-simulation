package com.you.nbody;
import com.you.nbody.core.Engine;

public class Main {
    public static void main(String[] args) {
        Engine.Init(1368, 723);
        Engine.Run();
        Engine.Exit();
    }
}
