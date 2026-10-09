package com.nurby.overcomplicated_bees.registry;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BeeBlocks {

    public final static DeferredRegister<Block> BLOCKS = DeferredRegister.create(
            BuiltInRegistries.BLOCK,
            OvercomplicatedBees.MOD_ID
    );

    public static final DeferredHolder<Block, Block> APIARY = BLOCKS.register(
            "apiary",
            () -> new com.nurby.overcomplicated_bees.block.ApiaryBlock(
                    Block.Properties.of()
                            .strength(2.5F)
                            .sound(SoundType.WOOD)
                            .requiresCorrectToolForDrops()
            )
    );

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
