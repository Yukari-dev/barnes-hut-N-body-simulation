package com.you.nbody.renderer;

import java.util.Random;

// THIS SECTION HAVE BEEN MADE WITH AI YES, I'M NOT THAT UNEMPLOYED TO TINKER WITH THESE VALUE, I ALSO HAVE A NATIONAL EXAM IN
// 17 DAYS AND I DIDN'T STUDY ANYTHING, SO EXECUSE THE USE AI FOR NOW.

public class StarColors {

    public enum Palette {
        BLUE_STEEL,
        FROSTBITE,
        MUTED_COSMOS,      
        NEBULA_SHADOW,
        VOID_OBSIDIAN,
        SOLAR_ECLIPSE,
        CHROME_QUARTZ,
        DEEP_ALGAE
    }

    public static float[] Generate(int count, Random random) {
        Palette[] palettes = Palette.values();
        Palette chosenPalette = palettes[random.nextInt(palettes.length)];
        return Generate(count, random, chosenPalette);
    }

    public static float[] Generate(int count, Random random, Palette palette) {
        float[] colors = new float[count * 3];

        for (int i = 0; i < count; i++) {
            float roll = random.nextFloat();
            float r = 0, g = 0, b = 0;

            switch (palette) {
                case BLUE_STEEL:
                    if (roll < 0.55f) {
                        r = 0.20f + random.nextFloat() * 0.05f;
                        g = 0.28f + random.nextFloat() * 0.05f;
                        b = 0.40f + random.nextFloat() * 0.08f;
                    } else if (roll < 0.85f) {
                        r = 0.30f + random.nextFloat() * 0.08f;
                        g = 0.38f + random.nextFloat() * 0.08f;
                        b = 0.52f + random.nextFloat() * 0.08f;
                    } else if (roll < 0.98f) {
                        r = 0.45f + random.nextFloat() * 0.05f;
                        g = 0.55f + random.nextFloat() * 0.05f;
                        b = 0.70f + random.nextFloat() * 0.05f;
                    } else {
                        r = 0.80f + random.nextFloat() * 0.05f;
                        g = 0.90f + random.nextFloat() * 0.05f;
                        b = 1.00f;
                    }
                    break;

                case FROSTBITE:
                    if (roll < 0.60f) {
                        r = 0.10f + random.nextFloat() * 0.05f;
                        g = 0.15f + random.nextFloat() * 0.05f;
                        b = 0.30f + random.nextFloat() * 0.10f;
                    } else if (roll < 0.90f) {
                        r = 0.15f + random.nextFloat() * 0.05f;
                        g = 0.35f + random.nextFloat() * 0.10f;
                        b = 0.60f + random.nextFloat() * 0.10f;
                    } else {
                        r = 0.40f + random.nextFloat() * 0.10f;
                        g = 0.75f + random.nextFloat() * 0.15f;
                        b = 1.00f;
                    }
                    break;

                case MUTED_COSMOS:
                    if (roll < 0.60f) { 
                        r = 0.35f + random.nextFloat() * 0.10f; 
                        g = 0.45f + random.nextFloat() * 0.10f;
                        b = 0.55f + random.nextFloat() * 0.10f; 
                    } else if (roll < 0.85f) { 
                        float slate = 0.40f + random.nextFloat() * 0.15f;
                        r = slate;
                        g = slate * 1.05f; 
                        b = slate * 1.10f;
                    } else { 
                        r = 0.85f + random.nextFloat() * 0.15f; 
                        g = 0.05f + random.nextFloat() * 0.05f; 
                        b = 0.10f + random.nextFloat() * 0.08f; 
                    }
                    break;

                case NEBULA_SHADOW:
                    if (roll < 0.55f) { 
                        r = 0.30f + random.nextFloat() * 0.10f;
                        g = 0.35f + random.nextFloat() * 0.10f;
                        b = 0.45f + random.nextFloat() * 0.10f;
                    } else if (roll < 0.80f) { 
                        r = 0.38f + random.nextFloat() * 0.08f;
                        g = 0.35f + random.nextFloat() * 0.08f;
                        b = 0.48f + random.nextFloat() * 0.08f;
                    } else { 
                        r = 0.90f + random.nextFloat() * 0.10f;
                        g = 0.00f + random.nextFloat() * 0.04f;
                        b = 0.15f + random.nextFloat() * 0.05f; 
                    }
                    break;

                case VOID_OBSIDIAN:
                    if (roll < 0.70f) {
                        r = 0.08f + random.nextFloat() * 0.04f;
                        g = 0.09f + random.nextFloat() * 0.04f;
                        b = 0.12f + random.nextFloat() * 0.06f;
                    } else if (roll < 0.95f) {
                        r = 0.15f + random.nextFloat() * 0.05f;
                        g = 0.18f + random.nextFloat() * 0.05f;
                        b = 0.25f + random.nextFloat() * 0.05f;
                    } else {
                        r = 0.55f + random.nextFloat() * 0.10f;
                        g = 0.60f + random.nextFloat() * 0.10f;
                        b = 0.75f + random.nextFloat() * 0.10f;
                    }
                    break;

                case SOLAR_ECLIPSE:
                    if (roll < 0.65f) {
                        r = 0.22f + random.nextFloat() * 0.06f;
                        g = 0.25f + random.nextFloat() * 0.06f;
                        b = 0.28f + random.nextFloat() * 0.06f;
                    } else if (roll < 0.95f) {
                        r = 0.45f + random.nextFloat() * 0.10f;
                        g = 0.40f + random.nextFloat() * 0.08f;
                        b = 0.30f + random.nextFloat() * 0.05f;
                    } else {
                        r = 0.85f + random.nextFloat() * 0.05f;
                        g = 0.75f + random.nextFloat() * 0.05f;
                        b = 0.55f + random.nextFloat() * 0.05f;
                    }
                    break;

                case CHROME_QUARTZ:
                    if (roll < 0.50f) {
                        float v = 0.30f + random.nextFloat() * 0.15f;
                        r = g = b = v;
                    } else if (roll < 0.90f) {
                        float v = 0.50f + random.nextFloat() * 0.20f;
                        r = g = b = v;
                    } else {
                        r = 0.90f + random.nextFloat() * 0.10f;
                        g = 0.92f + random.nextFloat() * 0.08f;
                        b = 0.98f + random.nextFloat() * 0.02f;
                    }
                    break;

                case DEEP_ALGAE:
                    if (roll < 0.60f) {
                        r = 0.18f + random.nextFloat() * 0.05f;
                        g = 0.25f + random.nextFloat() * 0.05f;
                        b = 0.22f + random.nextFloat() * 0.05f;
                    } else if (roll < 0.92f) {
                        r = 0.25f + random.nextFloat() * 0.08f;
                        g = 0.40f + random.nextFloat() * 0.08f;
                        b = 0.32f + random.nextFloat() * 0.08f;
                    } else {
                        r = 0.50f + random.nextFloat() * 0.10f;
                        g = 0.75f + random.nextFloat() * 0.10f;
                        b = 0.60f + random.nextFloat() * 0.10f;
                    }
                    break;
            }

            colors[i * 3]     = clamp(r);
            colors[i * 3 + 1] = clamp(g);
            colors[i * 3 + 2] = clamp(b);
        }
        return colors;
    }

    private static float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}
