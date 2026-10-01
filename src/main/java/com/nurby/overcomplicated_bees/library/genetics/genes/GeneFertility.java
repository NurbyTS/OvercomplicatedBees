package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class GeneFertility extends Gene<Integer> {
    public static final String DATA = "fertility";

    private int fertility;

    // Registry constructor
    public GeneFertility(ResourceLocation id) {
        super(id, false);
        this.fertility = defaultValue();
    }

    protected GeneFertility(
            ResourceLocation id,
            int fertility,
            boolean dominant
    ) {
        super(id, dominant);
        this.fertility = fertility;
    }

    public int getFertility() {
        return fertility;
    }

    public GeneFertility setFertility(int fertility) {
        this.fertility = fertility;
        return this;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public Integer value() {
        return fertility;
    }

    @Override
    public Integer defaultValue() {
        return 2;
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putInt(DATA, fertility);
    }

    @Override
    public GeneFertility deserialize(CompoundTag tag) {
        return new GeneFertility(
                id,
                tag.getInt(DATA),
                tag.getBoolean(DOMINANT)
        );
    }

    @Override
    public MutableComponent getComponent() {
        return Component.literal(Integer.toString(fertility));
    }

    @Override
    public GeneFertility copy() {
        return new GeneFertility(id, fertility, dominant);
    }
}