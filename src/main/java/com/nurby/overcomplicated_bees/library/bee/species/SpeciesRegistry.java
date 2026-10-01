package com.nurby.overcomplicated_bees.library.bee.species;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class SpeciesRegistry {
    private static final Map<ResourceLocation, SpeciesDefinition> SPECIES = new HashMap<>();

    public static void register(ResourceLocation id, SpeciesDefinition definition) {
        SPECIES.put(id, definition);
    }

    public static SpeciesDefinition get(ResourceLocation id) {
        return SPECIES.get(id);
    }

    public static void clear() {
        SPECIES.clear();
    }
}