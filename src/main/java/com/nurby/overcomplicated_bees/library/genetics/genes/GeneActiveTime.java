package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.ActiveTime;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class GeneActiveTime extends Gene<List<Pair<Integer, Integer>>> {
    public static final String DATA = "active_time";
    public static final String RANDOM = "random";
    public static final String START = "start";
    public static final String END = "end";

    private List<Pair<Integer, Integer>> activeTimes;
    private boolean random;

    // Registry constructor
    public GeneActiveTime(ResourceLocation id) {
        super(id, false);
        this.activeTimes = defaultValue();
        this.random = false;
    }

    protected GeneActiveTime(
            ResourceLocation id,
            List<Pair<Integer, Integer>> activeTimes,
            boolean random,
            boolean dominant
    ) {
        super(id, dominant);
        this.activeTimes = new ArrayList<>(activeTimes);
        this.random = random;
    }

    public List<Pair<Integer, Integer>> getActiveTimes() {
        return activeTimes;
    }

    public GeneActiveTime setActiveTimes(List<Pair<Integer, Integer>> activeTimes) {
        this.activeTimes = new ArrayList<>(activeTimes);
        return this;
    }

    public boolean isRandom() {
        return random;
    }

    public GeneActiveTime setRandom(boolean random) {
        this.random = random;
        return this;
    }

    public boolean isSatisfied(Level level) {
        if (random) {
            return level.random.nextBoolean();
        }

        long dayTime = level.getDayTime() % 24000L;

        for (Pair<Integer, Integer> range : activeTimes) {
            if (dayTime >= range.getLeft() && dayTime <= range.getRight()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public List<Pair<Integer, Integer>> value() {
        return activeTimes;
    }

    @Override
    public List<Pair<Integer, Integer>> defaultValue() {
        return ActiveTime.DIURNAL;
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putBoolean(RANDOM, random);

        ListTag ranges = new ListTag();

        for (Pair<Integer, Integer> range : activeTimes) {
            CompoundTag rangeTag = new CompoundTag();
            rangeTag.putInt(START, range.getLeft());
            rangeTag.putInt(END, range.getRight());
            ranges.add(rangeTag);
        }

        tag.put(DATA, ranges);
    }

    @Override
    public GeneActiveTime deserialize(CompoundTag tag) {
        List<Pair<Integer, Integer>> activeTimes = new ArrayList<>();
        ListTag ranges = tag.getList(DATA, 10);

        for (int i = 0; i < ranges.size(); i++) {
            CompoundTag range = ranges.getCompound(i);

            activeTimes.add(new ImmutablePair<>(
                    range.getInt(START),
                    range.getInt(END)
            ));
        }

        return new GeneActiveTime(
                id,
                activeTimes,
                tag.getBoolean(RANDOM),
                tag.getBoolean(DOMINANT)
        );
    }

    @Override
    public MutableComponent getComponent() {
        if (random) {
            return Component.translatable(
                    "gene.complicated_bees.active_time.random"
            );
        }

        return Component.translatable(
                "gene.complicated_bees.active_time." + ActiveTime.toName(activeTimes)
        );
    }

    @Override
    public GeneActiveTime copy() {
        return new GeneActiveTime(
                id,
                activeTimes,
                random,
                dominant
        );
    }
}