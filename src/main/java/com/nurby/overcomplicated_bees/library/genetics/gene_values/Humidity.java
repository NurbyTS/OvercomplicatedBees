package com.nurby.overcomplicated_bees.library.genetics.gene_values;

import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class Humidity {
    public static final float DRY = 0.0f;
    public static final float NORMAL = 0.3f;
    public static final float WET = 0.85f;

    private Humidity() {
    }

    public static String getName(float humidity) {
        if (humidity >= WET) {
            return "wet";
        }

        if (humidity >= NORMAL) {
            return "normal";
        }

        return "dry";
    }

    public static float getFromString(String name) {
        return switch (name.toLowerCase()) {
            case "dry" -> DRY;
            case "normal" -> NORMAL;
            case "wet" -> WET;
            default -> throw new IllegalArgumentException("Unknown humidity: " + name);
        };
    }

    public static MutableComponent getComponent(float humidity) {
        return Component.translatable(TranslationKeys.geneValue("humidity", Humidity.getName(humidity)));
    }

    public static float getFromBiome(Holder<Biome> biome) {
        return biome.value().getModifiedClimateSettings().downfall();
    }

    public static float getFromPosition(Level level, BlockPos pos) {
        return getFromLocation(level, pos);
    }

    public static float getFromLocation(Level level, BlockPos pos) {
        return getFromBiome(level.getBiome(pos));
    }

    public static int getLevel(float humidity) {
        if (humidity >= WET) {
            return 2;
        } else if (humidity >= NORMAL) {
            return 1;
        }
        return 0;
    }

    public static float fromLevel(int level) {
        return switch (level) {
            case 0 -> DRY;
            case 1 -> NORMAL;
            case 2 -> WET;
            default -> throw new IllegalArgumentException("Invalid humidity level: " + level);
        };
    }
}