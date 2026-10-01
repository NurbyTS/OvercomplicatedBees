package com.nurby.overcomplicated_bees.library.genetics;

import com.nurby.overcomplicated_bees.library.BeeRegistries;
import com.nurby.overcomplicated_bees.library.bee.component.BeeGenetics;
import com.nurby.overcomplicated_bees.library.genetics.genes.*;
import com.nurby.overcomplicated_bees.library.misc.ClientOnly;
import com.nurby.overcomplicated_bees.registry.BeeDataComponents;
import com.nurby.overcomplicated_bees.registry.BeeGenes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLLoader;

import java.util.List;
import java.util.Random;

public class GeneticHelper {
    private static final Random rand = new Random();

    private static Gene<?> getGeneOrDefault(ItemStack stack, ResourceLocation id) {
        BeeGenetics genetics = stack.get(BeeDataComponents.GENETICS);
        if (genetics != null) {
            Gene<?> gene = genetics.genome().primary().getGene(id);
            if (gene != null) {
                return gene;
            }
        }
        return (Gene<?>) BeeRegistries.getGeneDefault(id);
    }

    private static Gene<?> getGene(ItemStack stack, ResourceLocation id) {
        BeeGenetics genetics = stack.get(BeeDataComponents.GENETICS);
        if (genetics != null) {
            return genetics.genome().primary().getGene(id);
        }
        return null;
    }

    public static GeneFlower getFlower(ItemStack stack) {
        return (GeneFlower) getGeneOrDefault(stack, BeeGenes.FLOWER.getId());
    }

    public static GeneTemperature getTemperature(ItemStack stack) {
        return (GeneTemperature) getGeneOrDefault(stack, BeeGenes.TEMPERATURE.getId());
    }

    public static GeneHumidity getHumidity(ItemStack stack) {
        return (GeneHumidity) getGeneOrDefault(stack, BeeGenes.HUMIDITY.getId());
    }

    public static GeneActiveTime getActiveTime(ItemStack stack) {
        return (GeneActiveTime) getGeneOrDefault(stack, BeeGenes.ACTIVE_TIME.getId());
    }

    public static GeneSpecies getSpecies(ItemStack stack) {
        return (GeneSpecies) getGene(stack, BeeGenes.SPECIES.getId());
    }

    public static void addGeneTooltip(ItemStack stack, List<Component> tooltipComponents) {
        if (FMLLoader.getDist().isClient()) {
            if (ClientOnly.shift()) {
                tooltipComponents.add(Component.translatable("gui.complicated_bees.tooltip.shift_genes"));
            }
            else {
                BeeGenetics genetics = stack.get(BeeDataComponents.GENETICS);
                if (genetics == null) {
                    tooltipComponents.add(Component.translatable("gui.complicated_bees.tooltip.missing_genetics"));
                }
                else {
                    genetics.genome().primary().genes().values().forEach(gene -> {
                        if (gene.shouldBeSerialized()) {


                            tooltipComponents.add(
                                    Component.translatable("gene." + gene.id().getNamespace() + "." + gene.id().getPath()).append(
                                            Component.literal(":").append(
                                                    gene.getComponent()
                                            )
                                    )
                            );
                        }
                    });
                }
            }
        }
    }
}