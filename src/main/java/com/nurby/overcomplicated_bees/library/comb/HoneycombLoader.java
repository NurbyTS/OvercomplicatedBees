package com.nurby.overcomplicated_bees.library.comb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HoneycombLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public HoneycombLoader() {
        super(GSON, "honeycombs");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        HoneycombRegistry.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
            HoneycombDefinition.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).resultOrPartial(error -> OvercomplicatedBees.LOGGER.error("Failed to load honeycomb {}: {}", entry.getKey(), error)).ifPresent(definition -> {
                HoneycombRegistry.register(entry.getKey(), definition);
            });
        }
    }
}
