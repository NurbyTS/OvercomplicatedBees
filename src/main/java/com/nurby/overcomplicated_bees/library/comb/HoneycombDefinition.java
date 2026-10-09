package com.nurby.overcomplicated_bees.library.comb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nurby.overcomplicated_bees.library.misc.Color;
import net.minecraft.resources.ResourceLocation;

public final class HoneycombDefinition {
    public static final Codec<HoneycombDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(Color.CODEC.fieldOf("primary_color").forGetter(HoneycombDefinition::primaryColor),

            Color.CODEC.fieldOf("secondary_color").forGetter(HoneycombDefinition::secondaryColor)).apply(instance, HoneycombDefinition::new));
    private final Color primaryColor;
    private final Color secondaryColor;

    private ResourceLocation id;

    public HoneycombDefinition(Color primaryColor, Color secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    public Color primaryColor() {
        return primaryColor;
    }

    public Color secondaryColor() {
        return secondaryColor;
    }

    public ResourceLocation id() {
        return id;
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }
}
