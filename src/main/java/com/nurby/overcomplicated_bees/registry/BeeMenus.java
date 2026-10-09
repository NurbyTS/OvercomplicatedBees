package com.nurby.overcomplicated_bees.registry;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.menu.ApiaryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BeeMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(
                    Registries.MENU,
                    OvercomplicatedBees.MOD_ID
            );

    public static final DeferredHolder<MenuType<?>, MenuType<ApiaryMenu>> APIARY =
            MENUS.register(
                    "apiary",
                    () -> IMenuTypeExtension.create(ApiaryMenu::new)
            );

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}