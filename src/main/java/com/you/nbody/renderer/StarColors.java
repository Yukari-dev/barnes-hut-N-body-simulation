package com.you.nbody.renderer;

import java.util.Random;

// THIS SECTION HAVE BEEN MADE WITH AI YES, I'M NOT THAT UNEMPLOYED TO TINKER WITH THESE VALUE, I ALSO HAVE A NATIONAL EXAM IN
// 17 DAYS AND I DIDN'T STUDY ANYTHING, SO EXECUSE THE USE AI FOR NOW.

public class StarColors {

    public enum Palette {
        BLUE_STEEL, FROSTBITE, MUTED_COSMOS, NEBULA_SHADOW, VOID_OBSIDIAN,
        SOLAR_ECLIPSE, CHROME_QUARTZ, DEEP_ALGAE, MAGMA_CORE, EMERALD_ISLES,
        AMETHYST_VOID, SUNSET_DRIFT, TOXIC_WASTE, CANDY_CRUSH, ROYAL_PURPLE,
        GOLDEN_HOUR, FIRE_AND_ICE, OCEAN_ABYSS, NEON_CYBER, BLOOD_MOON,
        SPRING_MEADOW, AUTUMN_LEAVES, WINTER_CHILL, GALAXY_EDGE,
        VAPORWAVE, ACID_RAIN, PURE_GOLD, THE_MATRIX, DEEP_SPACE_X,
        LAVA_LAMP, ELECTRIC_BLUE, PSYCHEDELIC, RADIOACTIVE, OVERLOAD
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
                case BLUE_STEEL -> { r = 0.2f + roll * 0.1f; g = 0.3f + roll * 0.1f; b = 0.5f + roll * 0.2f; }
                case FROSTBITE -> { r = 0.1f + roll * 0.1f; g = 0.2f + roll * 0.2f; b = 0.7f + roll * 0.3f; }
                case MUTED_COSMOS -> { r = 0.3f + roll * 0.2f; g = 0.3f + roll * 0.2f; b = 0.4f + roll * 0.2f; }
                case NEBULA_SHADOW -> { r = 0.4f + roll * 0.3f; g = 0.1f + roll * 0.2f; b = 0.5f + roll * 0.3f; }
                case VOID_OBSIDIAN -> { r = 0.05f + roll * 0.05f; g = 0.05f + roll * 0.05f; b = 0.1f + roll * 0.1f; }
                case SOLAR_ECLIPSE -> { r = 0.8f + roll * 0.2f; g = 0.5f + roll * 0.2f; b = 0.2f + roll * 0.1f; }
                case CHROME_QUARTZ -> { float v = 0.7f + roll * 0.3f; r = g = b = v; }
                case DEEP_ALGAE -> { r = 0.1f + roll * 0.1f; g = 0.4f + roll * 0.2f; b = 0.2f + roll * 0.1f; }
                case MAGMA_CORE -> { r = 0.9f + roll * 0.1f; g = 0.2f + roll * 0.1f; b = 0.0f + roll * 0.05f; }
                case EMERALD_ISLES -> { r = 0.0f + roll * 0.1f; g = 0.7f + roll * 0.3f; b = 0.3f + roll * 0.2f; }
                case AMETHYST_VOID -> { r = 0.6f + roll * 0.2f; g = 0.2f + roll * 0.1f; b = 0.8f + roll * 0.2f; }
                case SUNSET_DRIFT -> { r = 0.9f + roll * 0.1f; g = 0.3f + roll * 0.2f; b = 0.5f + roll * 0.2f; }
                case TOXIC_WASTE -> { r = 0.4f + roll * 0.2f; g = 0.9f + roll * 0.1f; b = 0.1f + roll * 0.1f; }
                case CANDY_CRUSH -> { r = random.nextFloat(); g = random.nextFloat(); b = random.nextFloat(); }
                case ROYAL_PURPLE -> { r = 0.5f + roll * 0.1f; g = 0.0f + roll * 0.1f; b = 0.7f + roll * 0.3f; }
                case GOLDEN_HOUR -> { r = 0.9f + roll * 0.1f; g = 0.7f + roll * 0.2f; b = 0.1f + roll * 0.1f; }
                case FIRE_AND_ICE -> { if(roll < 0.5f) {r=1f; g=0.2f; b=0f;} else {r=0f; g=0.5f; b=1f;} }
                case OCEAN_ABYSS -> { r = 0.0f + roll * 0.1f; g = 0.2f + roll * 0.2f; b = 0.6f + roll * 0.4f; }
                case NEON_CYBER -> { r = roll > 0.5f ? 1.0f : 0.0f; g = 1.0f; b = 1.0f; }
                case BLOOD_MOON -> { r = 0.6f + roll * 0.4f; g = 0.0f; b = 0.0f; }
                case SPRING_MEADOW -> { r = 0.4f + roll * 0.3f; g = 0.9f + roll * 0.1f; b = 0.3f + roll * 0.3f; }
                case AUTUMN_LEAVES -> { r = 0.8f + roll * 0.2f; g = 0.4f + roll * 0.2f; b = 0.1f + roll * 0.1f; }
                case WINTER_CHILL -> { r = 0.8f + roll * 0.2f; g = 0.9f + roll * 0.1f; b = 1.0f; }
                case GALAXY_EDGE -> { r = 0.1f + roll * 0.2f; g = 0.1f + roll * 0.2f; b = 0.7f + roll * 0.3f; }
                case VAPORWAVE -> { r = 0.9f; g = 0.2f + roll * 0.3f; b = 0.9f; }
                case ACID_RAIN -> { r = 0.1f; g = 1.0f; b = 0.1f + roll * 0.8f; }
                case PURE_GOLD -> { r = 1.0f; g = 0.84f; b = 0.2f + roll * 0.2f; }
                case THE_MATRIX -> { r = 0.0f; g = 0.6f + roll * 0.4f; b = 0.0f; }
                case DEEP_SPACE_X -> { r = 0.0f; g = 0.0f; b = 0.2f + roll * 0.8f; }
                case LAVA_LAMP -> { r = 1.0f; g = roll * 0.5f; b = 0.0f; }
                case ELECTRIC_BLUE -> { r = 0.0f; g = 0.8f + roll * 0.2f; b = 1.0f; }
                case PSYCHEDELIC -> { r = random.nextFloat(); g = random.nextFloat(); b = random.nextFloat(); }
                case RADIOACTIVE -> { r = 0.7f + roll * 0.3f; g = 1.0f; b = 0.0f; }
                case OVERLOAD -> { r = roll > 0.66f ? 1.0f : 0.0f; g = roll > 0.33f && roll <= 0.66f ? 1.0f : 0.0f; b = roll <= 0.33f ? 1.0f : 0.0f; }
            }
            colors[i * 3] = clamp(r);
            colors[i * 3 + 1] = clamp(g);
            colors[i * 3 + 2] = clamp(b);
        }
        return colors;
    }
    private static float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }
}
