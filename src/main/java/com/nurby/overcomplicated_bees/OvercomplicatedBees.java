package com.nurby.overcomplicated_bees;

import com.mojang.logging.LogUtils;
import com.nurby.overcomplicated_bees.item.bee.BeeItem;
import com.nurby.overcomplicated_bees.registry.BeeDataComponents;
import com.nurby.overcomplicated_bees.registry.BeeGenes;
import com.nurby.overcomplicated_bees.registry.BeeItems;
import org.slf4j.Logger;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(OvercomplicatedBees.MOD_ID)
public class OvercomplicatedBees {
    public static final String MOD_ID = "complicated_bees";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OvercomplicatedBees(IEventBus modEventBus, ModContainer modContainer) {
        BeeGenes.register(modEventBus);

        BeeDataComponents.register(modEventBus);

        BeeItems.register(modEventBus);
    }
}