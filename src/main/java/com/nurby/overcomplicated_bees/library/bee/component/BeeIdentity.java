package com.nurby.overcomplicated_bees.library.bee.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record BeeIdentity(
        ResourceLocation species
) {

    public static final Codec<BeeIdentity> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            ResourceLocation.CODEC
                                    .fieldOf("species")
                                    .forGetter(BeeIdentity::species)
                    ).apply(instance, BeeIdentity::new)
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeIdentity> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC,
                    BeeIdentity::species,
                    BeeIdentity::new
            );
}