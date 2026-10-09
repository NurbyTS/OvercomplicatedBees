package com.nurby.overcomplicated_bees.util;

import com.nurby.overcomplicated_bees.library.genetics.gene_values.Humidity;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Temperature;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public final class BeeFailureReason {
    private BeeFailureReason() {
    }

    public static MutableComponent flowerComponent(ItemStack stack) {
        return Component.translatable(TranslationKeys.ERROR_NO_FLOWER, GeneticHelper.getFlowerGene(stack).getValueComponent());
    }

    public static MutableComponent humidComponent(ItemStack stack, float currentHumidity) {
        return Component.translatable(TranslationKeys.ERROR_WRONG_HUMIDITY, Humidity.getComponent(currentHumidity), Humidity.getComponent(GeneticHelper.getHumidityGene(stack).getHumidity()));
    }

    public static MutableComponent tempComponent(ItemStack stack, float currentTemperature) {
        return Component.translatable(TranslationKeys.ERROR_WRONG_TEMP, Temperature.getComponent(currentTemperature), Temperature.getComponent(GeneticHelper.getTemperatureGene(stack).getTemperature()));
    }

    public static MutableComponent timeComponent(ItemStack stack) {
        return Component.translatable(TranslationKeys.ERROR_WRONG_TIME, GeneticHelper.getActiveTimeGene(stack).getValueComponent());
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
        return Component.translatable(TranslationKeys.error(name));
    }
}