package com.nurby.overcomplicated_bees.item;

import com.nurby.overcomplicated_bees.library.bee.species.SpeciesDefinition;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesRegistry;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneSpecies;
import com.nurby.overcomplicated_bees.service.BeeTooltipService;
import com.nurby.overcomplicated_bees.util.GeneticHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BeeItem extends Item {
    private final String suffix;

    public BeeItem(String suffix) {
        super(new Item.Properties().stacksTo(64));
        this.suffix = suffix;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return BeeTooltipService.getBeeName(stack, this.suffix);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        BeeTooltipService.appendGeneTooltip(stack, tooltipComponents);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        GeneSpecies gene = GeneticHelper.getSpeciesGene(stack);

        if (gene == null) {
            return super.isFoil(stack);
        }

        SpeciesDefinition definition = SpeciesRegistry.get(gene.getSpecies());

        return definition != null ? definition.foil() : super.isFoil(stack);
    }
}
