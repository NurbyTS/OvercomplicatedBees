package com.nurby.overcomplicated_bees.client.event;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.client.model.LayeredBeeGeometry;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesDefinition;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesRegistry;
import com.nurby.overcomplicated_bees.library.comb.HoneycombDefinition;
import com.nurby.overcomplicated_bees.library.comb.HoneycombRegistry;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneSpecies;
import com.nurby.overcomplicated_bees.registry.BeeDataComponents;
import com.nurby.overcomplicated_bees.registry.BeeItems;
import com.nurby.overcomplicated_bees.util.GeneticHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = OvercomplicatedBees.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerColors(RegisterColorHandlersEvent.Item event) {
        event.register(ClientEvents::getCombColor, BeeItems.COMB.get());

        event.register(ClientEvents::getBeeColor, BeeItems.QUEEN.get(), BeeItems.PRINCESS.get(), BeeItems.DRONE.get());
    }

    private static int getBeeColor(ItemStack stack, int tintIndex) {
        GeneSpecies gene = GeneticHelper.getSpeciesGene(stack);

        if (gene == null)
            return 0xffffffff;

        SpeciesDefinition def = SpeciesRegistry.get(gene.getSpecies());

        if (def == null)
            return 0xffffffff;

        if (tintIndex == 0) {
            return def.primaryColor().value();
        } else if (tintIndex == 1) {
            return def.outlineColor().value();
        }

        return 0xffffffff;
    }

    private static int getCombColor(ItemStack stack, int tintIndex) {
        ResourceLocation comb = stack.get(BeeDataComponents.COMB_TYPE);
        if (comb == null)
            return 0xffffffff;
        HoneycombDefinition definition = HoneycombRegistry.get(comb);
        if (definition == null)
            return 0xffffffff;

        if (tintIndex == 0) {
            return definition.primaryColor().value();
        }

        return definition.secondaryColor().value();
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath("complicated_bees", "bee"), LayeredBeeGeometry.Loader.INSTANCE);
    }
}
