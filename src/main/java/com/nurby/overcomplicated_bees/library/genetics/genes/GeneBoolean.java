package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class GeneBoolean extends Gene<Boolean> {
    public static final String DATA = "value";

    private boolean value;

    // Registry constructor
    public GeneBoolean(ResourceLocation id) {
        super(id, true);
        this.value = defaultValue();
    }

    protected GeneBoolean(ResourceLocation id, boolean value, boolean dominant) {
        super(id, dominant);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public GeneBoolean setValue(boolean value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public int sortingOrder() {
        return 10;
    }

    @Override
    public boolean advanced() {
        return true;
    }

    @Override
    public Boolean value() {
        return value;
    }

    @Override
    public Boolean defaultValue() {
        return false;
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putBoolean(DATA, value);
    }

    @Override
    public boolean hidden() {
        return !value; // So it doesn't display if the value is false
    }

    @Override
    protected GeneBoolean deserializeValue(CompoundTag tag, boolean dominant) {
        return new GeneBoolean(id, tag.getBoolean(DATA), dominant);
    }

    @Override
    public MutableComponent getValueComponent() {
        return Component.translatable(TranslationKeys.geneBooleanValue(id, value));
    }

    @Override
    public MutableComponent getComponent() {
        return Component.translatable(TranslationKeys.gene(id()));
    }

    @Override
    public GeneBoolean copy() {
        return new GeneBoolean(id, value, dominant);
    }
}