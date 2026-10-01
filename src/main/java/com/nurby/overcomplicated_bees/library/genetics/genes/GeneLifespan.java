package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Lifespan;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class GeneLifespan extends Gene<Integer> {
    public static final String DATA = "lifespan";

    private int lifespan;

    // Registry constructor
    public GeneLifespan(ResourceLocation id) {
        super(id, false);
        this.lifespan = defaultValue();
    }

    protected GeneLifespan(
            ResourceLocation id,
            int lifespan,
            boolean dominant
    ) {
        super(id, dominant);
        this.lifespan = lifespan;
    }

    public int getLifespan() {
        return lifespan;
    }

    public GeneLifespan setLifespan(int lifespan) {
        this.lifespan = lifespan;
        return this;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public Integer value() {
        return lifespan;
    }

    @Override
    public Integer defaultValue() {
        return Lifespan.AVERAGE;
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putInt(DATA, lifespan);
    }

    @Override
    public GeneLifespan deserialize(CompoundTag tag) {
        return new GeneLifespan(
                id,
                tag.getInt(DATA),
                tag.getBoolean(DOMINANT)
        );
    }

    @Override
    public MutableComponent getComponent() {
        MutableComponent component = Component.translatable(
                "gene.complicated_bees.lifespan." + getLifespanName()
        );

        if (lifespan >= 90) {
            int pluses = (lifespan - 90) / 15;

            for (int i = 0; i < pluses; i++) {
                component.append("+");
            }
        } else if (lifespan < 15 && lifespan >= 0) {
            int pluses = (15 - lifespan) / 5;

            for (int i = 0; i < pluses; i++) {
                component.append("+");
            }
        }

        return component;
    }

    private String getLifespanName() {
        if (lifespan == -1) {
            return "immortal";
        }
        else if (lifespan == 0) {
            return "dead";
        }
        else if (lifespan < 20) {
            return "shortest";
        } else if (lifespan < 30) {
            return "shorter";
        } else if (lifespan < 42) {
            return "short";
        } else if (lifespan < 58) {
            return "average";
        } else if (lifespan < 70) {
            return "long";
        } else if (lifespan < 83) {
            return "longer";
        } else {
            return "longest";
        }
    }

    @Override
    public GeneLifespan copy() {
        return new GeneLifespan(
                id,
                lifespan,
                dominant
        );
    }
}