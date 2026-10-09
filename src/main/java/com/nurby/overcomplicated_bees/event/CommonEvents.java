package com.nurby.overcomplicated_bees.event;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.client.gui.ApiaryScreen;
import com.nurby.overcomplicated_bees.command.BeeCommands;
import com.nurby.overcomplicated_bees.library.BeeRegistries;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesLoader;
import com.nurby.overcomplicated_bees.library.comb.HoneycombLoader;
import com.nurby.overcomplicated_bees.library.flower.FlowerLoader;
import com.nurby.overcomplicated_bees.registry.BeeMenus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = OvercomplicatedBees.MOD_ID)
public class CommonEvents {
    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(BeeRegistries.GENE_REGISTRY);
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        BeeCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new FlowerLoader());
        event.addListener(new SpeciesLoader());
        event.addListener(new HoneycombLoader());
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(BeeMenus.APIARY.get(), ApiaryScreen::new);
    }
}
