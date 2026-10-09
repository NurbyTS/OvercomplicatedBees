package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Lifespan;
import com.nurby.overcomplicated_bees.util.TranslationKeys;
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

    protected GeneLifespan(ResourceLocation id, int lifespan, boolean dominant) {
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
    public int sortingOrder() {
        return 1;
    }

    @Override
    public boolean advanced() {
        return false;
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
    protected GeneLifespan deserializeValue(CompoundTag tag, boolean dominant) {
        int lifespan;

        // Handle string-based lifespan names from old_species format
        if (tag.contains(DATA) && tag.get(DATA) instanceof net.minecraft.nbt.StringTag) {
            lifespan = Lifespan.getFromName(tag.getString(DATA));
        } else {
            lifespan = tag.getInt(DATA);
        }

        return new GeneLifespan(id, lifespan, dominant);
    }

    @Override
    public MutableComponent getComponent() {
        return getValueComponent().append(" ").append(Component.translatable(TranslationKeys.GENE_LIFESPAN_APPEND));
    }

    @Override
    public MutableComponent getValueComponent() {
        return Lifespan.getComponent(lifespan);
    }

    @Override
    public GeneLifespan copy() {
        return new GeneLifespan(id, lifespan, dominant);
    }
}