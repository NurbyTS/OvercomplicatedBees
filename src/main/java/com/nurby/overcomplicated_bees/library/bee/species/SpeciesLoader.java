package com.nurby.overcomplicated_bees.library.bee.species;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.library.BeeRegistries;
import com.nurby.overcomplicated_bees.library.genetics.Chromosome;
import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneSpecies;
import com.nurby.overcomplicated_bees.registry.BeeGenes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SpeciesLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public SpeciesLoader() {
        super(GSON, "bee_species");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsons,
            @NotNull ResourceManager resourceManager,
            @NotNull ProfilerFiller profiler
    ) {
        SpeciesRegistry.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
            ResourceLocation id = entry.getKey();

            SpeciesDefinition.CODEC.parse(
                    JsonOps.INSTANCE,
                    entry.getValue()
            ).resultOrPartial(error ->
                    OvercomplicatedBees.LOGGER.error(
                            "Failed to load species {}: {}",
                            id,
                            error
                    )
            ).ifPresent(definition -> {
                OvercomplicatedBees.LOGGER.info("Loaded species {} successfully", id);

                GeneSpecies speciesGene = BeeGenes.SPECIES.get();
                Chromosome chromosome = definition.defaultChromosome().copy();

                chromosome.addGene(
                        speciesGene.copy().setSpecies(id)
                );

                SpeciesRegistry.register(id, definition.withDefaultChromosome(chromosome));
            });
        }
    }
}