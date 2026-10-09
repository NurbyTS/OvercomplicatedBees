package com.nurby.overcomplicated_bees.service;

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
import java.util.Objects;

public final class BeeTooltipService {
    private BeeTooltipService() {
    }

    public static MutableComponent getBeeName(ItemStack stack, String suffix) {
        return Component.translatable(TranslationKeys.itemDynamic(suffix), getSpeciesName(stack));
    }

    /**
     * Resolves the species argument for the bee's display name.
     * Typed as Object because it is passed straight through as a translation argument,
     * exactly as the individual branches were before.
     */
    private static Object getSpeciesName(ItemStack stack) {
        GeneSpecies gene = GeneticHelper.getSpeciesGene(stack);
        ResourceLocation species = gene == null ? null : gene.getSpecies();

        if (species == null) {
            return TranslationKeys.speciesUnknown();
        }

        // Check if the bee ACTUALLY EXISTS.
        if (SpeciesRegistry.get(species) == null) {
            return TranslationKeys.speciesUnloaded();
        }

        return Component.translatable(TranslationKeys.species(species));
    }

    /**
     * Adds gene information to the item tooltip.
     * Shows species purity/hybrid status and detailed gene info when shift is held.
     */
    public static void appendGeneTooltip(ItemStack stack, List<Component> tooltipComponents) {
        // TODO: Check if analyzed.

        GeneSpecies gene1 = GeneticHelper.getSpeciesGene(stack);
        GeneSpecies gene2 = GeneticHelper.getSecondarySpeciesGene(stack);

        ResourceLocation species1 = gene1 == null ? null : gene1.getSpecies();
        ResourceLocation species2 = gene2 == null ? null : gene2.getSpecies();

        if (species1 == null || species2 == null) {
            tooltipComponents.add(Component.translatable(TranslationKeys.TOOLTIP_MISSING_GENETICS));
            return;
        }

        Component name1 = Component.translatable(TranslationKeys.species(species1));

        if (!Objects.equals(species1, species2)) {
            Component name2 = Component.translatable(TranslationKeys.species(species2));

            tooltipComponents.add(
                    Component.translatable(TranslationKeys.TOOLTIP_BEE_HYBRID, name1, name2)
                            .withStyle(ChatFormatting.ITALIC, ChatFormatting.BLUE)
            );
        } else {
            tooltipComponents.add(
                    Component.translatable(TranslationKeys.TOOLTIP_BEE_PUREBRED, name1)
                            .withStyle(ChatFormatting.ITALIC, ChatFormatting.BLUE)
            );
        }

        if (FMLLoader.getDist().isClient()) {
            boolean advanced = ClientOnly.alt();

            if (!ClientOnly.shift() && !advanced) {
                tooltipComponents.add(Component.translatable(TranslationKeys.TOOLTIP_SHIFT_GENES));
                return;
            }

            GeneticHelper.getAllGenes(stack).forEach(gene -> {
                if (!gene.hidden() && (!gene.advanced() || advanced)) {
                    tooltipComponents.add(gene.getComponent().withStyle(ChatFormatting.GRAY));
                }
            });

            if (!advanced) {
                tooltipComponents.add(Component.translatable(TranslationKeys.TOOLTIP_ALT_GENES));
            }
        }
    }
}