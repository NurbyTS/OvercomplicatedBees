package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.gene_values.Humidity;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Tolerance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class GeneHumidity extends GeneWithTolerance<Float> {
    public static final String DATA = "humidity";

    private float humidity;

    // Registry constructor
    public GeneHumidity(ResourceLocation id) {
        super(id, false);
        this.humidity = defaultValue();
    }

    protected GeneHumidity(ResourceLocation id, float humidity, Tolerance tolerance, boolean dominant) {
        super(id, humidity, tolerance, dominant);
        this.humidity = humidity;
    }

    public float getHumidity() {
        return humidity;
    }

    public GeneHumidity setHumidity(float humidity) {
        this.humidity = humidity;
        return this;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public int sortingOrder() {
        return 4;
    }

    @Override
    public boolean advanced() {
        return false;
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
    protected void serializePrimaryValue(CompoundTag tag) {
        tag.putFloat(DATA, humidity);
    }

    @Override
    protected Float deserializePrimaryValue(CompoundTag tag) {
        // Handle string-based humidity names from old_species format
        if (tag.contains(DATA) && tag.get(DATA) instanceof net.minecraft.nbt.StringTag) {
            return Humidity.getFromString(tag.getString(DATA));
        }
        return tag.getFloat(DATA);
    }

    @Override
    protected GeneHumidity createInstance(Float value, Tolerance tolerance, boolean dominant) {
        return new GeneHumidity(id, value, tolerance, dominant);
    }

    @Override
    public MutableComponent getValueComponent() {
        return Humidity.getComponent(humidity).append(" (").append(String.valueOf(Humidity.getLevel(humidity))).append(") | ").append(Tolerance.getComponent(tolerance));
    }

    @Override
    public GeneHumidity copy() {
        return new GeneHumidity(id, humidity, tolerance, dominant);
    }
}