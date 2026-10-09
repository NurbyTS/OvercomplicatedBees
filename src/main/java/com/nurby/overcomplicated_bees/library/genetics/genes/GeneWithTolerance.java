package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Tolerance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public abstract class GeneWithTolerance<T> extends Gene<T> {
    public static final String TOLERANCE = "tolerance";

    protected Tolerance tolerance;

    protected GeneWithTolerance(ResourceLocation id, boolean dominant) {
        super(id, dominant);
        this.tolerance = Tolerance.NONE;
    }

    protected GeneWithTolerance(ResourceLocation id, T value, Tolerance tolerance, boolean dominant) {
        super(id, dominant);
        this.tolerance = tolerance;
    }

    public Tolerance getTolerance() {
        return tolerance;
    }

    public GeneWithTolerance<T> setTolerance(Tolerance tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        serializePrimaryValue(tag);
        tag.putString(TOLERANCE, tolerance.getName());
    }

    protected abstract void serializePrimaryValue(CompoundTag tag);

    @Override
    protected GeneWithTolerance<T> deserializeValue(CompoundTag tag, boolean dominant) {
        T value = deserializePrimaryValue(tag);
        Tolerance tolerance = tag.contains(TOLERANCE) ? Tolerance.getFromString(tag.getString(TOLERANCE)) : Tolerance.NONE;

        return createInstance(value, tolerance, dominant);
    }

    protected abstract T deserializePrimaryValue(CompoundTag tag);

    protected abstract GeneWithTolerance<T> createInstance(T value, Tolerance tolerance, boolean dominant);
}
