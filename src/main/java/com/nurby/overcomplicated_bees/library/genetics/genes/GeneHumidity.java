package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Humidity;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Tolerance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class GeneHumidity extends Gene<Float> {
    public static final String DATA = "humidity";
    public static final String TOLERANCE = "tolerance";

    private float humidity;
    private Tolerance tolerance;

    // Registry constructor
    public GeneHumidity(ResourceLocation id) {
        super(id, false);
        this.humidity = defaultValue();
        this.tolerance = Tolerance.NONE;
    }

    protected GeneHumidity(
            ResourceLocation id,
            float humidity,
            Tolerance tolerance,
            boolean dominant
    ) {
        super(id, dominant);
        this.humidity = humidity;
        this.tolerance = tolerance;
    }

    public float getHumidity() {
        return humidity;
    }

    public GeneHumidity setHumidity(float humidity) {
        this.humidity = humidity;
        return this;
    }

    public Tolerance getTolerance() {
        return tolerance;
    }

    public GeneHumidity setTolerance(Tolerance tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public Float value() {
        return humidity;
    }

    @Override
    public Float defaultValue() {
        return Humidity.NORMAL;
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putFloat(DATA, humidity);
        tag.putString(TOLERANCE, tolerance.getName());
    }

    @Override
    public GeneHumidity deserialize(CompoundTag tag) {
        Tolerance tolerance = tag.contains(TOLERANCE)
                ? Tolerance.getFromString(tag.getString(TOLERANCE))
                : Tolerance.NONE;

        return new GeneHumidity(
                id,
                tag.getFloat(DATA),
                tolerance,
                tag.getBoolean(DOMINANT)
        );
    }

    @Override
    public MutableComponent getComponent() {
        return Component.translatable(
                "gene.complicated_bees.humidity." + Humidity.getName(humidity)
        );
    }

    @Override
    public GeneHumidity copy() {
        return new GeneHumidity(
                id,
                humidity,
                tolerance,
                dominant
        );
    }
}