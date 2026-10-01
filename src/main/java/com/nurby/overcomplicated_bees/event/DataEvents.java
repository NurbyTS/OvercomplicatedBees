package com.nurby.overcomplicated_bees.event;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesLoader;
import com.nurby.overcomplicated_bees.library.flower.FlowerLoader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = OvercomplicatedBees.MOD_ID)
public class DataEvents {
    @SubscribeEvent
    public static void register(AddReloadListenerEvent event) {
        event.addListener(new FlowerLoader());
        event.addListener(new SpeciesLoader());
    }
}
