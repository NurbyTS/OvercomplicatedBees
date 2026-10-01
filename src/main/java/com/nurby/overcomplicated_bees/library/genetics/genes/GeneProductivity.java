package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Productivity;
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

    protected GeneProductivity(
            ResourceLocation id,
            float productivity,
            boolean dominant
    ) {
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
    public GeneProductivity deserialize(CompoundTag tag) {
        return new GeneProductivity(
                id,
                tag.getFloat(DATA),
                tag.getBoolean(DOMINANT)
        );
    }

    @Override
    public MutableComponent getComponent() {
        MutableComponent component = Component.translatable(
                "gene.complicated_bees.productivity." + getProductivityName()
        );

        if (productivity <= 0.1f) {
            return component.append("+");
        }
        else if (productivity >= 1.7f) {
            // For each 0.2 above 1.7, add a "+"
            int pluses = (int) ((productivity - 1.7f) / 0.2f);
            for (int i = 0; i < pluses; i++) {
                component.append("+");
            }
        }

        return component;
    }

    private String getProductivityName() {
       if (productivity < 0.3f) {
            return "slowest";
        } else if (productivity < 0.5f) {
            return "slower";
        } else if (productivity < 0.8f) {
            return "slow";
        } else if (productivity < 1.0f) {
            return "average";
        } else if (productivity < 1.2f) {
            return "fast";
        } else if (productivity < 1.5f) {
            return "faster";
        } else {
            return "fastest";
        }
    }

    @Override
    public GeneProductivity copy() {
        return new GeneProductivity(
                id,
                productivity,
                dominant
        );
    }
}