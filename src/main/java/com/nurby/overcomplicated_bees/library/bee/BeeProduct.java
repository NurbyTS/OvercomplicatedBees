package com.nurby.overcomplicated_bees.library.bee;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record BeeProduct(
        Ingredient ingredient,
        int count,
        DataComponentPatch components,
        float chance
) {

    public static final Codec<BeeProduct> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Ingredient.CODEC
                                    .fieldOf("ingredient")
                                    .forGetter(BeeProduct::ingredient),

                            Codec.INT
                                    .optionalFieldOf("count", 1)
                                    .forGetter(BeeProduct::count),

                            DataComponentPatch.CODEC
                                    .optionalFieldOf(
                                            "components",
                                            DataComponentPatch.EMPTY
                                    )
                                    .forGetter(BeeProduct::components),

                            Codec.FLOAT
                                    .optionalFieldOf("chance", 1.0f)
                                    .forGetter(BeeProduct::chance)

                    ).apply(instance, BeeProduct::new)
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeProduct> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public BeeProduct(Ingredient ingredient, float chance) {
        this(
                ingredient,
                1,
                DataComponentPatch.EMPTY,
                chance
        );
    }

    public BeeProduct(Ingredient ingredient, int count, float chance) {
        this(
                ingredient,
                count,
                DataComponentPatch.EMPTY,
                chance
        );
    }

    public ItemStack getStack() {
        ItemStack[] items = ingredient.getItems();

        if (items.length == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = items[0].copy();

        stack.setCount(count);
        stack.applyComponents(components);

        return stack;
    }

    public ItemStack getStackResult(float... modifiers) {
        float stackChance = chance;

        for (float modifier : modifiers) {
            stackChance *= modifier;
        }

        if (stackChance <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = getStack();

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int guaranteed = (int) stackChance;

        if (guaranteed > 0) {
            int originalCount = stack.getCount();

            stack.setCount(originalCount * guaranteed);

            if (Math.random() < stackChance - guaranteed) {
                stack.grow(originalCount);
            }

            return stack;
        }

        return Math.random() < stackChance
                ? stack
                : ItemStack.EMPTY;
    }
}