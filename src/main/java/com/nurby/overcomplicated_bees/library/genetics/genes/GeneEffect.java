package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.library.genetics.Gene;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class GeneEffect extends Gene<ResourceLocation> {
    public static final String DATA = "effect";

    private ResourceLocation effect;

    // Registry constructor
    public GeneEffect(ResourceLocation id) {
        super(id, false);

        // TODO: Make this actually get this from the EffectRegistry instead of just being a placeholder
        this.effect = defaultValue();
    }

    protected GeneEffect(
            ResourceLocation id,
            ResourceLocation effect,
            boolean dominant
    ) {
        super(id, dominant);
        this.effect = effect;
    }

    public ResourceLocation getEffect() {
        return effect;
    }

    public GeneEffect setEffect(ResourceLocation effect) {
        this.effect = Objects.requireNonNull(effect);
        return this;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public ResourceLocation value() {
        return effect;
    }

    @Override
    public ResourceLocation defaultValue() {
        return ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "invalid");
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putString(DATA, effect.toString());
    }

    @Override
    public GeneEffect deserialize(CompoundTag tag) {
        ResourceLocation effectId = ResourceLocation.tryParse(tag.getString(DATA));

        return new GeneEffect(
                id,
                effectId,
                tag.getBoolean(DOMINANT)
        );
    }

    @Override
    public MutableComponent getComponent() {
        return Component.translatable(
                "effect." + effect.getNamespace() + "." + effect.getPath()
        );
    }

    public MutableComponent getDescriptionKey() {
        return Component.translatable(
                "effect." + effect.getNamespace() + "." + effect.getPath() + ".desc"
        );
    }

    @Override
    public GeneEffect copy() {
        return new GeneEffect(id, effect, dominant);
    }
}