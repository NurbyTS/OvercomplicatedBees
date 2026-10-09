package com.nurby.overcomplicated_bees.library.genetics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record Genome(Chromosome primary, Chromosome secondary) {

    public static final Codec<Genome> CODEC = RecordCodecBuilder.create(instance -> instance.group(Chromosome.CODEC.fieldOf("primary").forGetter(Genome::primary), Chromosome.CODEC.fieldOf("secondary").forGetter(Genome::secondary)).apply(instance, Genome::new));

    public static final StreamCodec<ByteBuf, Genome> STREAM_CODEC = StreamCodec.composite(Chromosome.STREAM_CODEC, Genome::primary, Chromosome.STREAM_CODEC, Genome::secondary, Genome::new);

    public Genome(Chromosome chromosome) {
        this(chromosome.copy(), chromosome.copy());
    }

    public Genome copy() {
        return new Genome(primary.copy(), secondary.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Genome(
                Chromosome primary1,
                Chromosome secondary1
        )))
            return false;

        return Objects.equals(primary, primary1) && Objects.equals(secondary, secondary1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primary, secondary);
    }
}