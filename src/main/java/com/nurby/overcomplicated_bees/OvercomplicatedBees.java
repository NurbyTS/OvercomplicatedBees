package com.nurby.overcomplicated_bees;

import com.mojang.logging.LogUtils;
import com.nurby.overcomplicated_bees.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

@Mod(OvercomplicatedBees.MOD_ID)
public class OvercomplicatedBees {
    public static final String MOD_ID = "complicated_bees";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OvercomplicatedBees(IEventBus modEventBus, ModContainer modContainer) {
        BeeGenes.register(modEventBus);
        BeeDataComponents.register(modEventBus);
        BeeItems.register(modEventBus);
        BeeBlocks.register(modEventBus);
        BeeBlockEntities.register(modEventBus);
        BeeMenus.register(modEventBus);
        BeeCreativeTabs.register(modEventBus);

        modEventBus.addListener(OvercomplicatedBees::registerCapabilities);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BeeBlockEntities.APIARY.get(),
                (blockEntity, side) -> blockEntity.getAutomationHandler()
        );
    }
}