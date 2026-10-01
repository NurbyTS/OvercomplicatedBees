package com.nurby.overcomplicated_bees.registry;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.item.bee.BeeItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BeeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, OvercomplicatedBees.MOD_ID);

    public static final DeferredHolder<Item, Item> QUEEN = ITEMS.register("dynamic_queen", () -> new BeeItem("queen"));
    public static final DeferredHolder<Item, Item> DRONE = ITEMS.register("dynamic_drone", () -> new BeeItem("drone"));
    public static final DeferredHolder<Item, Item> PRINCESS = ITEMS.register("dynamic_princess", () -> new BeeItem("princess"));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
