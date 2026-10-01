package com.nurby.overcomplicated_bees.event;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.library.BeeRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = OvercomplicatedBees.MOD_ID)
public class RegistryEvents {
    @SubscribeEvent
    public static void RegisterRegistries(NewRegistryEvent event) {
        event.register(BeeRegistries.GENE_REGISTRY);
    }
}
