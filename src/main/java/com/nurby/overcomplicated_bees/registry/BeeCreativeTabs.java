package com.nurby.overcomplicated_bees.registry;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.item.BeeItem;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesDefinition;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesRegistry;
import com.nurby.overcomplicated_bees.library.comb.HoneycombDefinition;
import com.nurby.overcomplicated_bees.library.comb.HoneycombRegistry;
import com.nurby.overcomplicated_bees.library.genetics.Genome;
import com.nurby.overcomplicated_bees.util.GeneticHelper;
import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BeeCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, OvercomplicatedBees.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LOOT_BAGS = TABS.register("loot_bags", () -> CreativeModeTab.builder().title(Component.translatable(TranslationKeys.ITEM_GROUP_BEES)).icon(() -> new ItemStack(BeeItems.DRONE.get())).displayItems((parameters, output) -> {
        for (SpeciesDefinition species : SpeciesRegistry.entries().values()) {
            output.accept(quickCreateBee(BeeItems.QUEEN.get(), species));
            output.accept(quickCreateBee(BeeItems.PRINCESS.get(), species));
            output.accept(quickCreateBee(BeeItems.DRONE.get(), species));
        }

        for (HoneycombDefinition definition : HoneycombRegistry.entries().values()) {
            ItemStack stack = new ItemStack(BeeItems.COMB.get());
            stack.set(BeeDataComponents.COMB_TYPE, definition.id());
            output.accept(stack);
        }

        output.accept(new ItemStack(BeeItems.APIARY.get()));
    }).build());

    private static ItemStack quickCreateBee(BeeItem bee, SpeciesDefinition def) {
        ItemStack stack = new ItemStack(bee);
        GeneticHelper.setGenome(stack, new Genome(def.defaultChromosome()));
        return stack;
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
