package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Temperature;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Tolerance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class GeneTemperature extends Gene<Float> {
    public static final String DATA = "temperature";
    public static final String TOLERANCE = "tolerance";

    private float temperature;
    private Tolerance tolerance;

    // Registry constructor
    public GeneTemperature(ResourceLocation id) {
        super(id, false);
        this.temperature = defaultValue();
        this.tolerance = Tolerance.NONE;
    }

    protected GeneTemperature(
            ResourceLocation id,
            float temperature,
            Tolerance tolerance,
            boolean dominant
    ) {
        super(id, dominant);
        this.temperature = temperature;
        this.tolerance = tolerance;
    }

    public float getTemperature() {
        return temperature;
    }

    public GeneTemperature setTemperature(float temperature) {
        this.temperature = temperature;
        return this;
    }

    public Tolerance getTolerance() {
        return tolerance;
    }

    public GeneTemperature setTolerance(Tolerance tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    @Override
    public boolean isRequired() {
        return true;
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
    protected void serializeValue(CompoundTag tag) {
        tag.putFloat(DATA, temperature);
        tag.putString(TOLERANCE, tolerance.getName());
    }

    @Override
    public GeneTemperature deserialize(CompoundTag tag) {
        Tolerance tolerance = tag.contains(TOLERANCE)
                ? Tolerance.getFromString(tag.getString(TOLERANCE))
                : Tolerance.NONE;

        return new GeneTemperature(
                id,
                tag.getFloat(DATA),
                tolerance,
                tag.getBoolean(DOMINANT)
        );
    }

    @Override
    public MutableComponent getComponent() {
        return Temperature.getComponent(temperature);
    }

    @Override
    public GeneTemperature copy() {
        return new GeneTemperature(
                id,
                temperature,
                tolerance,
                dominant
        );
    }
}