package com.nurby.overcomplicated_bees.library.bee.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nurby.overcomplicated_bees.library.bee.BeeProduct;
import com.nurby.overcomplicated_bees.library.genetics.Chromosome;
import com.nurby.overcomplicated_bees.library.misc.Color;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SpeciesDefinition(Color nestColor, Color primaryColor, Color outlineColor, ResourceLocation texture,
                                ResourceLocation outlineTexture, Chromosome defaultChromosome, boolean foil,
                                List<BeeProduct> products, List<BeeProduct> specialtyProducts) {

    public static final Codec<SpeciesDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(Color.CODEC.fieldOf("nest_color").forGetter(SpeciesDefinition::nestColor),

            Color.CODEC.optionalFieldOf("primary_color", Color.DEFAULT).forGetter(SpeciesDefinition::primaryColor),

            Color.CODEC.optionalFieldOf("outline_color", Color.DEFAULT).forGetter(SpeciesDefinition::outlineColor),

            ResourceLocation.CODEC.optionalFieldOf("texture", ResourceLocation.fromNamespaceAndPath("complicated_bees", "item/bee/default_bee")).forGetter(SpeciesDefinition::texture),

            ResourceLocation.CODEC.optionalFieldOf("outline_texture", ResourceLocation.fromNamespaceAndPath("complicated_bees", "item/bee/default_outline")).forGetter(SpeciesDefinition::outlineTexture),

            Chromosome.CODEC.fieldOf("default_chromosome").forGetter(SpeciesDefinition::defaultChromosome),

            Codec.BOOL.optionalFieldOf("foil", false).forGetter(SpeciesDefinition::foil),

            BeeProduct.CODEC.listOf().optionalFieldOf("products", List.of()).forGetter(SpeciesDefinition::products),

            BeeProduct.CODEC.listOf().optionalFieldOf("specialty_products", List.of()).forGetter(SpeciesDefinition::specialtyProducts)

    ).apply(instance, SpeciesDefinition::new));

    public SpeciesDefinition withDefaultChromosome(Chromosome chromosome) {
        return new SpeciesDefinition(nestColor, primaryColor, outlineColor, texture, outlineTexture, chromosome, foil, products, specialtyProducts);
    }
}