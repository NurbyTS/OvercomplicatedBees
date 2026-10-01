package com.nurby.overcomplicated_bees.library.flower;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FlowerLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public FlowerLoader() {
        super(GSON, "flowers");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsons,
            @NotNull ResourceManager resourceManager,
            @NotNull ProfilerFiller profiler
    ) {
        FlowerRegistry.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
            FlowerDefinition.CODEC.parse(
                    JsonOps.INSTANCE,
                    entry.getValue()
            ).resultOrPartial(error ->
                    OvercomplicatedBees.LOGGER.error(
                            "Failed to load flower {}: {}",
                            entry.getKey(),
                            error
                    )
            ).ifPresent(definition -> {
                    FlowerRegistry.register(entry.getKey(), definition);
                }
            );
        }
    }
}