package com.nurby.overcomplicated_bees.util;

import net.minecraft.resources.ResourceLocation;

public final class TranslationKeys {
    public static final String TOOLTIP_MISSING_GENETICS = "gui.complicated_bees.tooltip.missing_genetics";
    public static final String TOOLTIP_SHIFT_GENES = "gui.complicated_bees.tooltip.shift_genes";
    public static final String TOOLTIP_ALT_GENES = "gui.complicated_bees.tooltip.alt_genes";
    public static final String TOOLTIP_UNLOADED_BEE_WARNING = "gui.complicated_bees.tooltip.unloaded_bee.warning";
    public static final String TOOLTIP_BEE_HYBRID = "gui.complicated_bees.tooltip.hybrid";
    public static final String TOOLTIP_BEE_PUREBRED = "gui.complicated_bees.tooltip.purebred";
    public static final String ERROR_NO_FLOWER = "gui.complicated_bees.error.no_flower";
    public static final String ERROR_WRONG_HUMIDITY = "gui.complicated_bees.error.wrong_humidity";
    public static final String ERROR_WRONG_TEMP = "gui.complicated_bees.error.wrong_temp";
    public static final String ERROR_WRONG_TIME = "gui.complicated_bees.error.wrong_time";
    public static final String ERROR_OUTPUT_FULL = "gui.complicated_bees.error.output_full";
    public static final String ERROR_UNDERGROUND = "gui.complicated_bees.error.underground";
    public static final String ERROR_WEATHER = "gui.complicated_bees.error.weather";
    public static final String ERROR_ECSTATIC = "gui.complicated_bees.error.ecstatic";
    public static final String ERROR_NOT_UNDERGROUND = "gui.complicated_bees.error.not_underground";
    public static final String ITEM_GROUP_BEES = "itemGroup.complicated_bees.bees";
    public static final String COMB_PREFIX = "comb.complicated_bees";
    public static final String GENE_LIFESPAN_APPEND = "gene.complicated_bees.lifespan.append";
    public static final String GENE_PRODUCTIVITY_APPEND = "gene.complicated_bees.productivity.append";
    public static final String GENE_ACTIVE_TIME_RANDOM = "gene.complicated_bees.active_time.random";
    public static final String GENE_TERRITORY_VALUE = "gene.complicated_bees.territory.value";
    private TranslationKeys() {
    }

    public static String itemDynamic(String suffix) {
        return "item.complicated_bees.dynamic_" + suffix;
    }

    public static String species(ResourceLocation species) {
        return "species." + species.getNamespace() + "." + species.getPath();
    }

    public static String speciesUnknown() {
        return "species.complicated_bees.unknown";
    }

    public static String speciesUnloaded() {
        return "species.complicated_bees.unloaded";
    }

    public static String gene(ResourceLocation geneId) {
        return "gene." + geneId.getNamespace() + "." + geneId.getPath();
    }

    public static String geneValue(String geneName, String value) {
        return "gene.complicated_bees." + geneName + "." + value;
    }

    public static String geneActiveTime(String activeTime) {
        return "gene.complicated_bees.active_time." + activeTime;
    }

    public static String geneBooleanValue(ResourceLocation geneId, boolean value) {
        return "gene.complicated_bees." + geneId.getPath() + "." + value;
    }

    public static String flower(ResourceLocation flower) {
        return "flower." + flower.getNamespace() + "." + flower.getPath();
    }

    public static String effect(ResourceLocation effect) {
        return "effect." + effect.getNamespace() + "." + effect.getPath();
    }

    public static String effectDescription(ResourceLocation effect) {
        return "effect." + effect.getNamespace() + "." + effect.getPath() + ".desc";
    }

    public static String comb(ResourceLocation combType) {
        return "comb." + combType.getNamespace() + "." + combType.getPath();
    }

    public static String error(String errorName) {
        return "gui.complicated_bees.error." + errorName;
    }
}
