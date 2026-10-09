package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class GeneSpecies extends Gene<ResourceLocation> {
    public static final String DATA = "species";

    private ResourceLocation species;

    // Registry constructor
    public GeneSpecies(ResourceLocation id) {
        super(id, false);
        this.species = defaultValue();
    }

    protected GeneSpecies(ResourceLocation id, ResourceLocation species, boolean dominant) {
        super(id, dominant);
        this.species = Objects.requireNonNull(species);
    }

    public ResourceLocation getSpecies() {
        return species;
    }

    public GeneSpecies setSpecies(ResourceLocation species) {
        this.species = Objects.requireNonNull(species);
        return this;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public int sortingOrder() {
        return 0;
    }

    @Override
    public boolean advanced() {
        return false;
    }

    @Override
    public ResourceLocation value() {
        return species;
    }

    @Override
    public boolean hidden() {
        return true;
    }

    @Override
    public ResourceLocation defaultValue() {
        return ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "invalid");
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putString(DATA, species.toString());
    }

    @Override
    protected GeneSpecies deserializeValue(CompoundTag tag, boolean dominant) {
        ResourceLocation species = ResourceLocation.tryParse(tag.getString(DATA));

        if (species == null) {
            throw new IllegalArgumentException("Invalid species ID: " + tag.getString(DATA));
        }

        return new GeneSpecies(id, species, dominant);
    }

    @Override
    public MutableComponent getValueComponent() {
        return Component.translatable(TranslationKeys.species(species));
    }

    @Override
    public GeneSpecies copy() {
        return new GeneSpecies(id, species, dominant);
    }
}