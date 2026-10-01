package com.nurby.overcomplicated_bees.library.bee.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BeeState(
        float age,
        boolean analyzed,
        int generation
) {

    public static final Codec<BeeState> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.FLOAT
                                    .fieldOf("age")
                                    .forGetter(BeeState::age),

                            Codec.BOOL
                                    .fieldOf("analyzed")
                                    .forGetter(BeeState::analyzed),

                            Codec.INT
                                    .fieldOf("generation")
                                    .forGetter(BeeState::generation)

                    ).apply(instance, BeeState::new)
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeState> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT,
                    BeeState::age,

                    ByteBufCodecs.BOOL,
                    BeeState::analyzed,

                    ByteBufCodecs.INT,
                    BeeState::generation,

                    BeeState::new
            );
}