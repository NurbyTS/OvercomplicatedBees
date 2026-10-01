package com.nurby.overcomplicated_bees.item.bee;

import com.nurby.overcomplicated_bees.library.bee.species.SpeciesDefinition;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesRegistry;
import com.nurby.overcomplicated_bees.library.genetics.GeneticHelper;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneSpecies;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BeeItem extends Item {
    private final String suffix;
    public BeeItem(String suffix) {
        super(new Item.Properties().stacksTo(1));
        this.suffix = suffix;
    }

    private ResourceLocation getSpecies(ItemStack stack) {
        GeneSpecies species = GeneticHelper.getSpecies(stack);
        if (species != null) {
            return species.getSpecies();
        }

        return null;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        ResourceLocation species = getSpecies(stack);

        return Component.translatable(
                "item.complicated_bees.dynamic_" + suffix,
                Component.translatable("species.complicated_bees." + (species == null ? "unknown" : species.getPath()))
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        GeneticHelper.addGeneTooltip(stack, tooltipComponents);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        // get its species definition from the genetics
        ResourceLocation species = getSpecies(stack);
        if (species != null) {
            SpeciesDefinition def = SpeciesRegistry.get(species);
            if (def != null) {
                return def.foil();
            }
        }
        return super.isFoil(stack);
    }
}
