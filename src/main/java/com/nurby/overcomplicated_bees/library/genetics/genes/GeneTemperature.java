package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.gene_values.Temperature;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Tolerance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class GeneTemperature extends GeneWithTolerance<Float> {
    public static final String DATA = "temperature";

    private float temperature;

    // Registry constructor
    public GeneTemperature(ResourceLocation id) {
        super(id, false);
        this.temperature = defaultValue();
    }

    protected GeneTemperature(ResourceLocation id, float temperature, Tolerance tolerance, boolean dominant) {
        super(id, temperature, tolerance, dominant);
        this.temperature = temperature;
    }

    public float getTemperature() {
        return temperature;
    }

    public GeneTemperature setTemperature(float temperature) {
        this.temperature = temperature;
        return this;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public int sortingOrder() {
        return 3;
    }

    @Override
    public boolean advanced() {
        return false;
    }

    @Override
    public Float value() {
        return temperature;
    }

    @Override
    public Float defaultValue() {
        return Temperature.NORMAL;
    }

    @Override
    protected void serializePrimaryValue(CompoundTag tag) {
        tag.putFloat(DATA, temperature);
    }

    @Override
    protected Float deserializePrimaryValue(CompoundTag tag) {
        // Handle string-based temperature names from old_species format
        if (tag.contains(DATA) && tag.get(DATA) instanceof net.minecraft.nbt.StringTag) {
            return Temperature.getFromString(tag.getString(DATA));
        }
        return tag.getFloat(DATA);
    }

    @Override
    protected GeneTemperature createInstance(Float value, Tolerance tolerance, boolean dominant) {
        return new GeneTemperature(id, value, tolerance, dominant);
    }

    @Override
    public MutableComponent getValueComponent() {
        return Temperature.getComponent(temperature).append(" (").append(String.valueOf(Temperature.getLevel(temperature))).append(") | ").append(Tolerance.getComponent(tolerance));
    }

    @Override
    public GeneTemperature copy() {
        return new GeneTemperature(id, temperature, tolerance, dominant);
    }
}