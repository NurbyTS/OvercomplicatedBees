package com.nurby.overcomplicated_bees.library.flower;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public record FlowerDefinition(
        List<ResourceLocation> blocks,
        List<ResourceLocation> tags
) {
    public static final Codec<FlowerDefinition> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            ResourceLocation.CODEC.listOf()
                                    .optionalFieldOf("blocks", List.of())
                                    .forGetter(FlowerDefinition::blocks),

                            ResourceLocation.CODEC.listOf()
                                    .optionalFieldOf("tags", List.of())
                                    .forGetter(FlowerDefinition::tags)
                    ).apply(instance, FlowerDefinition::new)
            );

    public boolean isFlower(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        for (ResourceLocation blockId : blocks) {
            if (BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(blockId)) {
                return true;
            }
        }

        for (ResourceLocation tagId : tags) {
            if (state.is(TagKey.create(Registries.BLOCK, tagId))) {
                return true;
            }
        }

        return false;
    }
}