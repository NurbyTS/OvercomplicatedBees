package com.nurby.overcomplicated_bees.library.bee.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nurby.overcomplicated_bees.library.bee.BeeProduct;
import com.nurby.overcomplicated_bees.library.misc.Color;
import com.nurby.overcomplicated_bees.library.genetics.Chromosome;

import java.util.List;

public record SpeciesDefinition(
        Color color,
        Color stripeColor,
        Color outlineColor,
        Color nestColor,
        Chromosome defaultChromosome,
        boolean foil,
        List<BeeProduct> products,
        List<BeeProduct> specialtyProducts
) {

    public static final Codec<SpeciesDefinition> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Color.CODEC
                                    .optionalFieldOf("color", Color.DEFAULT)
                                    .forGetter(SpeciesDefinition::color),

                            Color.CODEC
                                    .optionalFieldOf("stripe_color", Color.DEFAULT)
                                    .forGetter(SpeciesDefinition::stripeColor),

                            Color.CODEC
                                    .optionalFieldOf("outline_color", Color.DEFAULT)
                                    .forGetter(SpeciesDefinition::outlineColor),

                            Color.CODEC
                                    .optionalFieldOf("nest_color", Color.DEFAULT)
                                    .forGetter(SpeciesDefinition::nestColor),

                            Chromosome.CODEC
                                    .fieldOf("default_chromosome")
                                    .forGetter(SpeciesDefinition::defaultChromosome),

                            Codec.BOOL
                                    .optionalFieldOf("foil", false)
                                    .forGetter(SpeciesDefinition::foil),

                            BeeProduct.CODEC
                                    .listOf()
                                    .optionalFieldOf("products", List.of())
                                    .forGetter(SpeciesDefinition::products),

                            BeeProduct.CODEC
                                    .listOf()
                                    .optionalFieldOf("specialty_products", List.of())
                                    .forGetter(SpeciesDefinition::specialtyProducts)

                    ).apply(instance, SpeciesDefinition::new)
            );

    public SpeciesDefinition withDefaultChromosome(Chromosome chromosome) {
        return new SpeciesDefinition(
                color,
                stripeColor,
                outlineColor,
                nestColor,
                chromosome,
                foil,
                products,
                specialtyProducts
        );
    }
}