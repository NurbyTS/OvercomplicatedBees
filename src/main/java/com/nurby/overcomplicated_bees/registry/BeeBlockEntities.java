package com.nurby.overcomplicated_bees.registry;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.block.entity.ApiaryBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BeeBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    OvercomplicatedBees.MOD_ID
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ApiaryBlockEntity>> APIARY = BLOCK_ENTITIES.register(
            "apiary",
            () -> BlockEntityType.Builder.of(
                    ApiaryBlockEntity::new,
                    BeeBlocks.APIARY.get()
            ).build(null)
    );

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
