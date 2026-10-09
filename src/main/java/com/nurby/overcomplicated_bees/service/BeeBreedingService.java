package com.nurby.overcomplicated_bees.service;

import com.nurby.overcomplicated_bees.item.BeeItem;
import com.nurby.overcomplicated_bees.library.BeeRegistries;
import com.nurby.overcomplicated_bees.library.bee.component.BeeState;
import com.nurby.overcomplicated_bees.library.genetics.Chromosome;
import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.Genome;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneBoolean;
import com.nurby.overcomplicated_bees.registry.BeeDataComponents;
import com.nurby.overcomplicated_bees.registry.BeeItems;
import com.nurby.overcomplicated_bees.util.GeneticHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class BeeBreedingService {
    // Private util methods.
    private static Gene<?> selectRandomGene(RandomSource rand, Genome genome, ResourceLocation id) {
        Chromosome chromosome = rand.nextBoolean() ? genome.primary() : genome.secondary();

        Gene<?> gene = chromosome.getGene(id);

        if (gene != null) {
            return gene;
        }

        Gene<?> registeredGene = BeeRegistries.GENE_REGISTRY.get(id);

        if (registeredGene instanceof GeneBoolean booleanGene) {
            return booleanGene.copy().setValue(booleanGene.defaultValue());
        }

        throw new IllegalStateException("Missing registered gene of type: " + id);
    }

    private static void sortByDominance(RandomSource rand, Chromosome primary, Chromosome secondary) {
        for (ResourceLocation id : BeeRegistries.GENE_REGISTRY.keySet()) {
            Gene<?> primaryGene = primary.getGene(id);
            Gene<?> secondaryGene = secondary.getGene(id);

            if (primaryGene == null || secondaryGene == null) {
                continue;
            }

            if (!primaryGene.isDominant()) {
                primary.setGene(id, secondaryGene);
                secondary.setGene(id, primaryGene);
            } else if (secondaryGene.isDominant() && rand.nextBoolean()) {
                primary.setGene(id, secondaryGene);
                secondary.setGene(id, primaryGene);
            }
        }
    }

    private static Genome breed(RandomSource rand, Genome left, Genome right) {
        Objects.requireNonNull(rand, "Random source cannot be null");
        Objects.requireNonNull(left, "Left genome cannot be null");
        Objects.requireNonNull(right, "Right genome cannot be null");

        Chromosome primary = new Chromosome();
        Chromosome secondary = new Chromosome();

        // TODO: Impl mutation logic.

        for (ResourceLocation id : BeeRegistries.GENE_REGISTRY.keySet()) {
            Gene<?> leftGene = selectRandomGene(rand, left, id);
            Gene<?> rightGene = selectRandomGene(rand, right, id);

            primary.setGene(id, leftGene.copy());
            secondary.setGene(id, rightGene.copy());
        }

        sortByDominance(rand, primary, secondary);

        return new Genome(primary, secondary);
    }

    // Actual useful methods.

    /**
     * Creates a queen item stack from a princess and drone.
     * The queen inherits the princess's genome and state, with the drone as the mate.
     */
    public static ItemStack createQueen(ItemStack princess, ItemStack drone) {
        ItemStack queen = new ItemStack(BeeItems.QUEEN.get());

        Genome princessGenome = GeneticHelper.getGenome(princess);
        Genome droneGenome = GeneticHelper.getGenome(drone);

        if (princessGenome == null || droneGenome == null) {
            throw new IllegalArgumentException("Cannot create a queen from bees without genetics.");
        }

        GeneticHelper.setGenome(queen, princessGenome);
        GeneticHelper.setMate(queen, droneGenome);

        BeeState state = princess.get(BeeDataComponents.STATE);

        if (state == null) {
            state = new BeeState(0, false, 0);
        }

        queen.set(BeeDataComponents.STATE, state);

        // TODO: Add analyzed state.
        return queen;
    }

    /**
     * Creates an offspring item from a queen.
     * If the queen has a mate, breeds the genomes. Otherwise, clones the queen's genome.
     * If the result is a princess, increments the generation count.
     */
    public static ItemStack createOffspring(RandomSource random, ItemStack queen, BeeItem resultType) {
        ItemStack result = new ItemStack(resultType);

        Genome genome = GeneticHelper.getGenome(queen);
        Genome mate = GeneticHelper.getMate(queen);

        if (genome == null) {
            return result;
        }

        if (mate == null) {
            GeneticHelper.setGenome(result, genome);
        } else {
            GeneticHelper.setGenome(result, breed(random, genome, mate));
        }

        if (resultType == BeeItems.PRINCESS.get()) {
            // TODO: Add analyzed state.
            BeeState queenState = queen.get(BeeDataComponents.STATE);
            if (queenState == null) {
                queenState = new BeeState(0, false, 0);
            }
            result.set(BeeDataComponents.STATE, new BeeState(0, false, queenState.generation() + 1));
        }

        return result;
    }
}
