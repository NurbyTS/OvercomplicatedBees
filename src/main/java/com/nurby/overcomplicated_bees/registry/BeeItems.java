package com.nurby.overcomplicated_bees.registry;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.item.BeeItem;
import com.nurby.overcomplicated_bees.item.CombItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BeeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, OvercomplicatedBees.MOD_ID);

    public static final DeferredHolder<Item, BeeItem> QUEEN = ITEMS.register("dynamic_queen", () -> new BeeItem("queen"));
    public static final DeferredHolder<Item, BeeItem> DRONE = ITEMS.register("dynamic_drone", () -> new BeeItem("drone"));
    public static final DeferredHolder<Item, BeeItem> PRINCESS = ITEMS.register("dynamic_princess", () -> new BeeItem("princess"));

    public static final DeferredHolder<Item, CombItem> COMB = ITEMS.register("comb", CombItem::new);

    public static final DeferredHolder<Item, BlockItem> APIARY =
            ITEMS.register("apiary",
                    () -> new BlockItem(
                            BeeBlocks.APIARY.get(),
                            new Item.Properties()
                    ));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
