package com.nurby.overcomplicated_bees.library.flower;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class FlowerRegistry {
    private static final Map<ResourceLocation, FlowerDefinition> FLOWERS = new HashMap<>();

    public static void register(ResourceLocation id, FlowerDefinition definition) {
        FLOWERS.put(id, definition);
    }

    public static FlowerDefinition get(ResourceLocation id) {
        return FLOWERS.get(id);
    }

    public static void clear() {
        FLOWERS.clear();
    }
}