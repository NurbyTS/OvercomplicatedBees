package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class GeneFlower extends Gene<ResourceLocation> {
    public static final String DATA = "flower";

    private ResourceLocation flower;

    // Registry constructor
    public GeneFlower(ResourceLocation id) {
        super(id, false);
        this.flower = defaultValue();
    }

    protected GeneFlower(ResourceLocation id, ResourceLocation flower, boolean dominant) {
        super(id, dominant);
        this.flower = Objects.requireNonNull(flower);
    }

    public ResourceLocation getFlower() {
        return flower;
    }

    public GeneFlower setFlower(ResourceLocation flower) {
        this.flower = Objects.requireNonNull(flower);
        return this;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public int sortingOrder() {
        return 6;
    }

    @Override
    public boolean advanced() {
        return true;
    }

    @Override
    public ResourceLocation value() {
        return flower;
    }

    @Override
    public ResourceLocation defaultValue() {
        return ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "flowers");
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putString(DATA, flower.toString());
    }

    @Override
    protected GeneFlower deserializeValue(CompoundTag tag, boolean dominant) {
        ResourceLocation flowerId = ResourceLocation.tryParse(tag.getString(DATA));

        if (flowerId == null) {
            flowerId = ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "invalid");
        }

        return new GeneFlower(id, flowerId, dominant);
    }

    @Override
    public MutableComponent getValueComponent() {
        return Component.translatable(TranslationKeys.flower(flower));
    }

    @Override
    public GeneFlower copy() {
        return new GeneFlower(id, flower, dominant);
    }
}