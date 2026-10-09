package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Productivity;
import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class GeneProductivity extends Gene<Float> {
    public static final String DATA = "productivity";

    private float productivity;

    // Registry constructor
    public GeneProductivity(ResourceLocation id) {
        super(id, false);
        this.productivity = defaultValue();
    }

    protected GeneProductivity(ResourceLocation id, float productivity, boolean dominant) {
        super(id, dominant);
        this.productivity = productivity;
    }

    public float getProductivity() {
        return productivity;
    }

    public GeneProductivity setProductivity(float productivity) {
        this.productivity = productivity;
        return this;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public int sortingOrder() {
        return 2;
    }

    @Override
    public boolean advanced() {
        return false;
    }

    @Override
    public Float value() {
        return productivity;
    }

    @Override
    public Float defaultValue() {
        return Productivity.AVERAGE;
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putFloat(DATA, productivity);
    }

    @Override
    protected GeneProductivity deserializeValue(CompoundTag tag, boolean dominant) {
        float productivity;

        // Handle string-based productivity names from old_species format
        if (tag.contains(DATA) && tag.get(DATA) instanceof net.minecraft.nbt.StringTag) {
            productivity = Productivity.getFromName(tag.getString(DATA));
        } else {
            productivity = tag.getFloat(DATA);
        }

        return new GeneProductivity(id, productivity, dominant);
    }

    @Override
    public MutableComponent getComponent() {
        return getValueComponent().append(" ").append(Component.translatable(TranslationKeys.GENE_PRODUCTIVITY_APPEND)); // So it's not Productivity:
    }

    @Override
    public MutableComponent getValueComponent() {
        return Productivity.getComponent(productivity);
    }

    @Override
    public GeneProductivity copy() {
        return new GeneProductivity(id, productivity, dominant);
    }
}