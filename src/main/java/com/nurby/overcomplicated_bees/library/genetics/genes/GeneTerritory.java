package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Territory;
import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class GeneTerritory extends Gene<Territory> {
    public static final String DATA = "territory";

    private Territory territory;

    // Registry constructor
    public GeneTerritory(ResourceLocation id) {
        super(id, false);
        this.territory = defaultValue();
    }

    protected GeneTerritory(ResourceLocation id, Territory territory, boolean dominant) {
        super(id, dominant);
        this.territory = territory;
    }

    public Territory getTerritory() {
        return territory;
    }

    public GeneTerritory setTerritory(Territory territory) {
        this.territory = territory;
        return this;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public int sortingOrder() {
        return 8;
    }

    @Override
    public boolean advanced() {
        return true;
    }

    @Override
    public Territory value() {
        return territory;
    }

    @Override
    public Territory defaultValue() {
        return new Territory(4, 2);
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putIntArray(DATA, new int[]{territory.horizontalRadius(), territory.verticalRadius()});
    }

    @Override
    protected GeneTerritory deserializeValue(CompoundTag tag, boolean dominant) {
        int[] dimensions = tag.getIntArray(DATA);

        if (dimensions.length < 2) {
            return new GeneTerritory(id, new Territory(4, 2), dominant);
        }

        return new GeneTerritory(id, new Territory(dimensions[0], dimensions[1]), dominant);
    }

    @Override
    public MutableComponent getValueComponent() {
        return Component.translatable(TranslationKeys.GENE_TERRITORY_VALUE, territory.horizontalSize(), territory.verticalSize(), territory.horizontalSize());
    }

    @Override
    public GeneTerritory copy() {
        return new GeneTerritory(id, territory, dominant);
    }
}