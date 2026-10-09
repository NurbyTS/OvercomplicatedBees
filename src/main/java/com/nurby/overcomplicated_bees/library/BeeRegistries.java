package com.nurby.overcomplicated_bees.library;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.library.genetics.Gene;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class BeeRegistries {
    public static final ResourceKey<Registry<Gene<?>>> GENE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "gene"));

    public static final Registry<Gene<?>> GENE_REGISTRY = new RegistryBuilder<>(GENE_KEY).sync(true).defaultKey(ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "unknown")).create();

    public static Object getGeneDefault(ResourceLocation id) {
        Gene<?> gene = GENE_REGISTRY.get(id);
        if (gene == null) {
            OvercomplicatedBees.LOGGER.warn("Gene with id {} not found in registry, returning default gene.", id);
            return GENE_REGISTRY.get(ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "unknown"));
        }
        return gene.defaultValue();
    }
}
