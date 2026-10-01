package com.nurby.overcomplicated_bees.library.genetics;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public abstract class Gene<T> {
    protected final ResourceLocation id;
    protected boolean dominant;

    public static final String DOMINANT = "dominant";

    protected Gene(ResourceLocation id, boolean dominant) {
        this.id = id;
        this.dominant = dominant;
    }

    public ResourceLocation id() {
        return id;
    }

    public boolean isDominant() {
        return dominant;
    }

    public Gene<T> setDominant(boolean dominant) {
        this.dominant = dominant;
        return this;
    }

    public abstract boolean isRequired();

    public abstract T value();

    public abstract T defaultValue();

    public boolean isPresent() {
        if (value() == null) {
            return false;
        }

        return !isRequired() || !value().equals(defaultValue());
    }

    public boolean shouldBeSerialized() {
        return isPresent();
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        if (!shouldBeSerialized()) {
            return tag;
        }

        tag.putBoolean(DOMINANT, dominant);
        serializeValue(tag);

        return tag;
    }

    protected abstract void serializeValue(CompoundTag tag);

    public abstract Gene<T> deserialize(CompoundTag tag);

    public abstract MutableComponent getComponent();

    public abstract Gene<T> copy();
}