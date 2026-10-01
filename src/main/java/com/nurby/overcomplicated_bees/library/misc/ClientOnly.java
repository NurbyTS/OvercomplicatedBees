package com.nurby.overcomplicated_bees.library.misc;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientOnly {
    public static boolean shift() {
        return Screen.hasShiftDown();
    }

    public static boolean ctrl() {
        return Screen.hasControlDown();
    }

    public static boolean alt() {
        return Screen.hasAltDown();
    }
}
