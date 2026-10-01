package com.nurby.overcomplicated_bees.library.genetics.gene_values;

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
        return Component.translatable(
                "gene.complicated_bees.humidity." + Humidity.getName(humidity)
        );
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

    public static float increase(float humidity) {
        if (humidity < NORMAL) {
            return NORMAL;
        }

        if (humidity < WET) {
            return WET;
        }

        return humidity;
    }

    public static float decrease(float humidity) {
        if (humidity > WET) {
            return WET;
        }

        if (humidity > NORMAL) {
            return NORMAL;
        }

        return DRY;
    }

    public static float increaseBy(float humidity, int stages) {
        if (stages < 0) {
            return decreaseBy(humidity, -stages);
        }

        for (int i = 0; i < stages; i++) {
            humidity = increase(humidity);
        }

        return humidity;
    }

    public static float decreaseBy(float humidity, int stages) {
        if (stages < 0) {
            return increaseBy(humidity, -stages);
        }

        for (int i = 0; i < stages; i++) {
            humidity = decrease(humidity);
        }

        return humidity;
    }
}