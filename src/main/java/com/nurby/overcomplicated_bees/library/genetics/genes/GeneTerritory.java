package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Territory;
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

    protected GeneTerritory(
            ResourceLocation id,
            Territory territory,
            boolean dominant
    ) {
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
    public Territory value() {
        return territory;
    }

    @Override
    public Territory defaultValue() {
        return new Territory(4, 2);
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putIntArray(DATA, new int[]{
                territory.horizontalRadius(),
                territory.verticalRadius()
        });
    }

    @Override
    public GeneTerritory deserialize(CompoundTag tag) {
        int[] dimensions = tag.getIntArray(DATA);

        if (dimensions.length < 2) {
            return new GeneTerritory(
                    id,
                    new Territory(4, 2),
                    tag.getBoolean(DOMINANT)
            );
        }

        return new GeneTerritory(
                id,
                new Territory(dimensions[0], dimensions[1]),
                tag.getBoolean(DOMINANT)
        );
    }

    @Override
    public MutableComponent getComponent() {
        return Component.translatable(
                "gene.complicated_bees.territory_value",
                territory.horizontalSize(),
                territory.verticalSize(),
                territory.horizontalSize()
        );
    }

    @Override
    public GeneTerritory copy() {
        return new GeneTerritory(
                id,
                territory,
                dominant
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof GeneTerritory other)) {
            return false;
        }

        return super.equals(obj)
                && territory.equals(other.territory);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + territory.hashCode();
    }
}