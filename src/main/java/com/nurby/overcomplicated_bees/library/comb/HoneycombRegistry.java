package com.nurby.overcomplicated_bees.library.comb;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class HoneycombRegistry {
    private static final Map<ResourceLocation, HoneycombDefinition> HONEYCOMBS = new HashMap<>();

    public static void register(ResourceLocation id, HoneycombDefinition definition) {
        definition.setId(id);
        HONEYCOMBS.put(id, definition);
    }

    public static HoneycombDefinition get(ResourceLocation id) {
        return HONEYCOMBS.get(id);
    }

    public static void clear() {
        HONEYCOMBS.clear();
    }

    public static Map<ResourceLocation, HoneycombDefinition> entries() {
        return HONEYCOMBS;
    }
}
