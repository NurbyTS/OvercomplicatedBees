package com.nurby.overcomplicated_bees.service;

import com.nurby.overcomplicated_bees.library.bee.species.SpeciesDefinition;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesRegistry;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneSpecies;
import com.nurby.overcomplicated_bees.library.misc.ClientOnly;
import com.nurby.overcomplicated_bees.util.GeneticHelper;
import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLLoader;

import java.util.List;

public class BeeTooltipService {
    public static MutableComponent getBeeName(ItemStack stack, String suffix) {
        GeneSpecies gene1 = GeneticHelper.getSpeciesGene(stack);
        if (gene1 == null) {
            return Component.translatable(TranslationKeys.itemDynamic(suffix), TranslationKeys.speciesUnknown());
        }
        ResourceLocation species = gene1.getSpecies();
        // Species should literally never be null in any usual case but fuck it why not
        if (species == null) {
            return Component.translatable(TranslationKeys.itemDynamic(suffix), TranslationKeys.speciesUnknown());
        }
        // Check if the bee ACTUALLY EXISTS.
        SpeciesDefinition def = SpeciesRegistry.get(species);
        if (def == null) {
            return Component.translatable(TranslationKeys.itemDynamic(suffix), TranslationKeys.speciesUnloaded());
        }

        return Component.translatable(TranslationKeys.itemDynamic(suffix), Component.translatable(TranslationKeys.species(species)));
    }

    /**
     * Adds gene information to the item tooltip.
     * Shows species purity/hybrid status and detailed gene info when shift is held.
     */
    public static void appendGeneTooltip(ItemStack stack, List<Component> tooltipComponents) {
        // TODO: Check if analyzed.

        GeneSpecies gene1 = GeneticHelper.getSpeciesGene(stack);
        GeneSpecies gene2 = GeneticHelper.getSecondarySpeciesGene(stack);
        if (gene1 == null || gene2 == null) {
            tooltipComponents.add(Component.translatable(TranslationKeys.TOOLTIP_MISSING_GENETICS));
            return;
        }

        ResourceLocation species1 = gene1.getSpecies();
        ResourceLocation species2 = gene2.getSpecies();

        if (!species1.equals(species2)) {
            tooltipComponents.add(Component.translatable(TranslationKeys.TOOLTIP_BEE_HYBRID, Component.translatable(TranslationKeys.species(species1)), Component.translatable(TranslationKeys.species(species2))).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.BLUE));
        } else {
            tooltipComponents.add(Component.translatable(TranslationKeys.TOOLTIP_BEE_PUREBRED, Component.translatable(TranslationKeys.species(species1))).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.BLUE));
        }

        if (FMLLoader.getDist().isClient()) {
            boolean advanced = ClientOnly.alt();
            if (!ClientOnly.shift() && !advanced) {
                tooltipComponents.add(Component.translatable(TranslationKeys.TOOLTIP_SHIFT_GENES));
            } else {
                GeneticHelper.getAllGenes(stack).forEach(gene -> {
                    if (!gene.hidden() && (!gene.advanced() || (gene.advanced() && advanced))) {
                        tooltipComponents.add(gene.getComponent().withStyle(ChatFormatting.GRAY));
                    }
                });

                if (!advanced) {
                    tooltipComponents.add(Component.translatable(TranslationKeys.TOOLTIP_ALT_GENES));
                }
            }
        }
    }
}
