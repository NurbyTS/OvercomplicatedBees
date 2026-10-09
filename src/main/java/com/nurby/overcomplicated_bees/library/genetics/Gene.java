package com.nurby.overcomplicated_bees.library.genetics;

import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public abstract class Gene<T> {
    public static final String DOMINANT = "dominant";
    protected final ResourceLocation id;
    protected boolean dominant;

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

    public abstract int sortingOrder();

    public abstract boolean advanced();

    public boolean hidden() {
        return false;
    }

    public abstract T value();

    public abstract T defaultValue();

    public abstract MutableComponent getValueComponent();

    public MutableComponent getComponent() {
        return Component.translatable(TranslationKeys.gene(id())).append(Component.literal(": ")).append(getValueComponent());
    }

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

    public final Gene<T> deserialize(CompoundTag tag) {
        boolean dominant = !tag.contains(DOMINANT) || tag.getBoolean(DOMINANT);
        return deserializeValue(tag, dominant);
    }

    protected abstract Gene<T> deserializeValue(CompoundTag tag, boolean dominant);

    public abstract Gene<T> copy();
}