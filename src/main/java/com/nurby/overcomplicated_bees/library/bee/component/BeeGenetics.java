package com.nurby.overcomplicated_bees.library.bee.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nurby.overcomplicated_bees.library.genetics.Genome;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BeeGenetics(Genome genome, Genome mate) {

    public static final Codec<BeeGenetics> CODEC = RecordCodecBuilder.create(instance -> instance.group(Genome.CODEC.fieldOf("genome").forGetter(BeeGenetics::genome),

            Genome.CODEC.optionalFieldOf("mate").forGetter(genetics -> java.util.Optional.ofNullable(genetics.mate()))

    ).apply(instance, (genome, mate) -> new BeeGenetics(genome, mate.orElse(null))));

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeGenetics> STREAM_CODEC = StreamCodec.composite(Genome.STREAM_CODEC, BeeGenetics::genome,

            ByteBufCodecs.optional(Genome.STREAM_CODEC), genetics -> java.util.Optional.ofNullable(genetics.mate()),

            (genome, mate) -> new BeeGenetics(genome, mate.orElse(null)));
}