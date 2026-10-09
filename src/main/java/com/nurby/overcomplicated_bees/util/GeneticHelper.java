package com.nurby.overcomplicated_bees.util;

import com.nurby.overcomplicated_bees.library.BeeRegistries;
import com.nurby.overcomplicated_bees.library.bee.component.BeeGenetics;
import com.nurby.overcomplicated_bees.library.genetics.Chromosome;
import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.Genome;
import com.nurby.overcomplicated_bees.library.genetics.genes.*;
import com.nurby.overcomplicated_bees.registry.BeeDataComponents;
import com.nurby.overcomplicated_bees.registry.BeeGenes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class GeneticHelper {
    private static Gene<?> getGeneOrDefault(ItemStack stack, ResourceLocation id) {
        BeeGenetics genetics = stack.get(BeeDataComponents.GENETICS);

        if (genetics != null && genetics.genome() != null) {
            Chromosome chromosome = genetics.genome().primary();

            Gene<?> gene = chromosome.getGene(id);

            if (gene != null) {
                return gene;
            }
        }

        return BeeRegistries.GENE_REGISTRY.get(id).copy();
    }

    private static Gene<?> getGene(ItemStack stack, ResourceLocation id, boolean secondary) {
        BeeGenetics genetics = stack.get(BeeDataComponents.GENETICS);

        if (genetics == null || genetics.genome() == null) {
            return null;
        }

        Chromosome chromosome = secondary ? genetics.genome().secondary() : genetics.genome().primary();

        return chromosome.getGene(id);
    }

    public static GeneFlower getFlowerGene(ItemStack stack) {
        Gene<?> gene = getGeneOrDefault(stack, BeeGenes.FLOWER.getId());
        if (!(gene instanceof GeneFlower flower)) {
            throw new IllegalStateException("Expected GeneFlower but got: " + gene.getClass().getSimpleName());
        }
        return flower;
    }

    public static GeneTemperature getTemperatureGene(ItemStack stack) {
        Gene<?> gene = getGeneOrDefault(stack, BeeGenes.TEMPERATURE.getId());
        if (!(gene instanceof GeneTemperature temperature)) {
            throw new IllegalStateException("Expected GeneTemperature but got: " + gene.getClass().getSimpleName());
        }
        return temperature;
    }

    public static GeneHumidity getHumidityGene(ItemStack stack) {
        Gene<?> gene = getGeneOrDefault(stack, BeeGenes.HUMIDITY.getId());
        if (!(gene instanceof GeneHumidity humidity)) {
            throw new IllegalStateException("Expected GeneHumidity but got: " + gene.getClass().getSimpleName());
        }
        return humidity;
    }

    public static GeneActiveTime getActiveTimeGene(ItemStack stack) {
        Gene<?> gene = getGeneOrDefault(stack, BeeGenes.ACTIVE_TIME.getId());
        if (!(gene instanceof GeneActiveTime activeTime)) {
            throw new IllegalStateException("Expected GeneActiveTime but got: " + gene.getClass().getSimpleName());
        }
        return activeTime;
    }

    public static GeneSpecies getSpeciesGene(ItemStack stack) {
        Gene<?> gene = getGene(stack, BeeGenes.SPECIES.getId(), false);
        if (gene == null) {
            return null;
        }
        if (!(gene instanceof GeneSpecies species)) {
            throw new IllegalStateException("Expected GeneSpecies but got: " + gene.getClass().getSimpleName());
        }
        return species;
    }

    public static GeneSpecies getSecondarySpeciesGene(ItemStack stack) {
        Gene<?> gene = getGene(stack, BeeGenes.SPECIES.getId(), true);
        if (gene == null) {
            return null;
        }
        if (!(gene instanceof GeneSpecies species)) {
            throw new IllegalStateException("Expected GeneSpecies but got: " + gene.getClass().getSimpleName());
        }
        return species;
    }

    public static GeneFertility getFertilityGene(ItemStack stack) {
        Gene<?> gene = getGene(stack, BeeGenes.FERTILITY.getId(), false);
        if (gene == null) {
            return null;
        }
        if (!(gene instanceof GeneFertility fertility)) {
            throw new IllegalStateException("Expected GeneFertility but got: " + gene.getClass().getSimpleName());
        }
        return fertility;
    }

    public static GeneTerritory getTerritoryGene(ItemStack stack) {
        Gene<?> gene = getGeneOrDefault(stack, BeeGenes.TERRITORY.getId());
        if (!(gene instanceof GeneTerritory territory)) {
            throw new IllegalStateException("Expected GeneTerritory but got: " + gene.getClass().getSimpleName());
        }
        return territory;
    }

    public static GeneBoolean getBooleanGene(ItemStack stack, ResourceLocation id) {
        Gene<?> gene = getGeneOrDefault(stack, id);
        if (!(gene instanceof GeneBoolean booleanGene)) {
            throw new IllegalStateException("Expected GeneBoolean but got: " + gene.getClass().getSimpleName());
        }
        return booleanGene;
    }

    public static GeneLifespan getLifespanGene(ItemStack stack) {
        Gene<?> gene = getGeneOrDefault(stack, BeeGenes.LIFESPAN.getId());
        if (!(gene instanceof GeneLifespan lifespan)) {
            throw new IllegalStateException("Expected GeneLifespan but got: " + gene.getClass().getSimpleName());
        }
        return lifespan;
    }

    // ==================== Genome Getters/Setters ====================

    public static Collection<Gene<?>> getAllGenes(ItemStack stack) {
        Genome genome = getGenome(stack);
        if (genome == null) {
            return new ArrayList<>();
        }
        return genome.primary().genes().values();
    }

    public static Genome getGenome(ItemStack stack) {
        BeeGenetics genetics = stack.get(BeeDataComponents.GENETICS);

        if (genetics == null) {
            return null;
        }

        return genetics.genome();
    }

    public static void setGenome(ItemStack stack, Genome genome) {
        BeeGenetics genetics = stack.get(BeeDataComponents.GENETICS);

        if (genetics == null) {
            stack.set(BeeDataComponents.GENETICS, new BeeGenetics(genome, null));
            return;
        }

        stack.set(BeeDataComponents.GENETICS, new BeeGenetics(genome, genetics.mate()));
    }

    public static void setMate(ItemStack stack, Genome mate) {
        BeeGenetics genetics = stack.get(BeeDataComponents.GENETICS);

        if (genetics == null) {
            stack.set(BeeDataComponents.GENETICS, new BeeGenetics(null, mate));
            return;
        }

        stack.set(BeeDataComponents.GENETICS, new BeeGenetics(genetics.genome(), mate));
    }

    public static Genome getMate(ItemStack stack) {
        BeeGenetics genetics = stack.get(BeeDataComponents.GENETICS);

        if (genetics == null) {
            return null;
        }

        return genetics.mate();
    }
}