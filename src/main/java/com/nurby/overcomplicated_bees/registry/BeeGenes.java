package com.nurby.overcomplicated_bees.registry;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.library.BeeRegistries;
import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneActiveTime;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneBoolean;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneEffect;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneFertility;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneFlower;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneHumidity;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneLifespan;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneProductivity;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneSpecies;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneTemperature;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneTerritory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BeeGenes {
    public static final DeferredRegister<Gene<?>> GENE_REGISTER =
            DeferredRegister.create(
                    BeeRegistries.GENE_REGISTRY,
                    OvercomplicatedBees.MOD_ID
            );

    public static final DeferredHolder<Gene<?>, GeneSpecies> SPECIES =
            GENE_REGISTER.register(
                    "species",
                    GeneSpecies::new
            );

    public static final DeferredHolder<Gene<?>, GeneLifespan> LIFESPAN =
            GENE_REGISTER.register(
                    "lifespan",
                    GeneLifespan::new
            );

    public static final DeferredHolder<Gene<?>, GeneTemperature> TEMPERATURE =
            GENE_REGISTER.register(
                    "temperature",
                    GeneTemperature::new
            );

    public static final DeferredHolder<Gene<?>, GeneHumidity> HUMIDITY =
            GENE_REGISTER.register(
                    "humidity",
                    GeneHumidity::new
            );

    public static final DeferredHolder<Gene<?>, GeneFlower> FLOWER =
            GENE_REGISTER.register(
                    "flower",
                    GeneFlower::new
            );

    public static final DeferredHolder<Gene<?>, GeneFertility> FERTILITY =
            GENE_REGISTER.register(
                    "fertility",
                    GeneFertility::new
            );

    public static final DeferredHolder<Gene<?>, GeneProductivity> PRODUCTIVITY =
            GENE_REGISTER.register(
                    "productivity",
                    GeneProductivity::new
            );

    public static final DeferredHolder<Gene<?>, GeneTerritory> TERRITORY =
            GENE_REGISTER.register(
                    "territory",
                    GeneTerritory::new
            );

    public static final DeferredHolder<Gene<?>, GeneEffect> EFFECT =
            GENE_REGISTER.register(
                    "effect",
                    GeneEffect::new
            );

    public static final DeferredHolder<Gene<?>, GeneActiveTime> ACTIVE_TIME =
            GENE_REGISTER.register(
                    "active_time",
                    GeneActiveTime::new
            );

    public static final DeferredHolder<Gene<?>, GeneBoolean> CAVE_DWELLING =
            GENE_REGISTER.register(
                    "cave_dwelling",
                    GeneBoolean::new
            );

    public static final DeferredHolder<Gene<?>, GeneBoolean> WEATHERPROOF =
            GENE_REGISTER.register(
                    "weatherproof",
                    GeneBoolean::new
            );

    public static void register(IEventBus bus) {
        GENE_REGISTER.register(bus);
    }
}