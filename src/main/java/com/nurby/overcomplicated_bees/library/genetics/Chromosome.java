package com.nurby.overcomplicated_bees.library.genetics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.library.BeeRegistries;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Chromosome {
    private final Map<ResourceLocation, Gene<?>> genes;

    public static final Codec<Chromosome> CODEC = Codec.of(
            Chromosome::encode,
            Chromosome::decode
    );

    public static final StreamCodec<ByteBuf, Chromosome> STREAM_CODEC =
            ByteBufCodecs.COMPOUND_TAG.map(
                    Chromosome::fromTag,
                    Chromosome::toTag
            );

    public Chromosome() {
        this.genes = new HashMap<>();
    }

    public Chromosome(Map<ResourceLocation, Gene<?>> genes) {
        this.genes = new HashMap<>(genes);
    }

    public Map<ResourceLocation, Gene<?>> genes() {
        return Collections.unmodifiableMap(genes);
    }

    public Gene<?> getGene(ResourceLocation id) {
        return genes.get(id);
    }

    public Object getGeneOrDefault(ResourceLocation id) {
        Gene<?> gene = genes.get(id);

        if (gene != null) {
            return gene.value();
        }

        Gene<?> registered = BeeRegistries.GENE_REGISTRY
                .getOptional(id)
                .orElse(null);

        if (registered == null) {
            return null;
        }

        return registered.defaultValue();
    }

    public Chromosome addGene(Gene<?> gene) {
        genes.put(gene.id(), gene);
        return this;
    }

    public Chromosome removeGene(ResourceLocation id) {
        genes.remove(id);
        return this;
    }

    public Chromosome copy() {
        Map<ResourceLocation, Gene<?>> copiedGenes = new HashMap<>(genes.size());

        for (Gene<?> gene : genes.values()) {
            copiedGenes.put(gene.id(), gene.copy());
        }

        return new Chromosome(copiedGenes);
    }

    private static <T> DataResult<T> encode(
            Chromosome chromosome,
            DynamicOps<T> ops,
            T prefix
    ) {
        CompoundTag tag = chromosome.toTag();

        return CompoundTag.CODEC.encode(tag, ops, prefix);
    }

    private static <T> DataResult<com.mojang.datafixers.util.Pair<Chromosome, T>> decode(
            DynamicOps<T> ops,
            T input
    ) {
        return CompoundTag.CODEC.decode(ops, input)
                .map(pair ->
                        com.mojang.datafixers.util.Pair.of(
                                fromTag(pair.getFirst()),
                                pair.getSecond()
                        )
                );
    }

    private CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        for (Gene<?> gene : genes.values()) {
            if (!gene.shouldBeSerialized()) {
                continue;
            }

            tag.put(gene.id().toString(), gene.serialize());
        }

        return tag;
    }

    private static Chromosome fromTag(CompoundTag tag) {
        Chromosome chromosome = new Chromosome();

        for (String key : tag.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(key);

            if (id == null) {
                continue;
            }

            // in case of a blank namespace it defaults to minecraft, we'll make it default to our mod instead
            if (id.getNamespace().equals("minecraft")) {
                id = ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, id.getPath());
            }

            Gene<?> prototype = BeeRegistries.GENE_REGISTRY
                    .getOptional(id)
                    .orElse(null);

            if (prototype == null) {
                continue;
            }

            chromosome.addGene(
                    prototype.deserialize(tag.getCompound(key))
            );
        }

        return chromosome;
    }
}