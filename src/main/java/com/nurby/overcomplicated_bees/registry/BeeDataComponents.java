package com.nurby.overcomplicated_bees.registry;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.library.bee.component.BeeGenetics;
import com.nurby.overcomplicated_bees.library.bee.component.BeeState;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BeeDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, OvercomplicatedBees.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BeeState>> STATE = DATA_COMPONENTS.register("state", () -> DataComponentType.<BeeState>builder().persistent(BeeState.CODEC).networkSynchronized(BeeState.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BeeGenetics>> GENETICS = DATA_COMPONENTS.register("genetics", () -> DataComponentType.<BeeGenetics>builder().persistent(BeeGenetics.CODEC).networkSynchronized(BeeGenetics.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> COMB_TYPE = DATA_COMPONENTS.register("comb_type", () -> DataComponentType.<ResourceLocation>builder().persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).build());

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}