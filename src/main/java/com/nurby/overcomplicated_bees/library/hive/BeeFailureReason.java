package com.nurby.overcomplicated_bees.library.hive;

import com.nurby.overcomplicated_bees.library.genetics.gene_values.Humidity;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Temperature;
import com.nurby.overcomplicated_bees.library.genetics.GeneticHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public final class BeeFailureReason {
    private BeeFailureReason() {
    }

    public static MutableComponent flowerComponent(ItemStack stack) {
        return Component.translatable(
                "gui.complicated_bees.error.no_flower",
                GeneticHelper.getFlower(stack).getComponent()
        );
    }

    public static MutableComponent humidComponent(
            ItemStack stack,
            float currentHumidity
    ) {
        return Component.translatable(
                "gui.complicated_bees.error.wrong_humidity",
                GeneticHelper.getHumidity(stack).getComponent(),
                Humidity.getComponent(currentHumidity)
        );
    }

    public static MutableComponent tempComponent(
            ItemStack stack,
            float currentTemperature
    ) {
        return Component.translatable(
                "gui.complicated_bees.error.wrong_temp",
                GeneticHelper.getTemperature(stack).getComponent(),
                Temperature.getComponent(currentTemperature)
        );
    }

    public static MutableComponent timeComponent(ItemStack stack) {
        return Component.translatable(
                "gui.complicated_bees.error.wrong_time",
                GeneticHelper.getActiveTime(stack).getComponent()
        );
    }

    public static MutableComponent outputFullComponent(ItemStack stack) {
        return defaultGetter("output_full");
    }

    public static MutableComponent undergroundComponent(ItemStack stack) {
        return defaultGetter("underground");
    }

    public static MutableComponent weatherComponent(ItemStack stack) {
        return defaultGetter("weather");
    }

    public static MutableComponent ecstaticComponent(ItemStack stack) {
        return defaultGetter("ecstatic");
    }

    public static MutableComponent notUndergroundComponent(ItemStack stack) {
        return defaultGetter("not_underground");
    }

    public static MutableComponent defaultGetter(String name) {
        return Component.translatable(
                "gui.complicated_bees.error." + name
        );
    }
}