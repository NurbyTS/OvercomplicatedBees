package com.nurby.overcomplicated_bees.library.genetics.genes;

import com.nurby.overcomplicated_bees.library.genetics.Gene;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.ActiveTime;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.TimeRange;
import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class GeneActiveTime extends Gene<List<TimeRange>> {
    public static final String DATA = "active_time";
    public static final String RANDOM = "random";
    public static final String START = "start";
    public static final String END = "end";

    private List<TimeRange> activeTimes;
    private boolean random;

    // Registry constructor
    public GeneActiveTime(ResourceLocation id) {
        super(id, false);
        this.activeTimes = defaultValue();
        this.random = false;
    }

    protected GeneActiveTime(ResourceLocation id, List<TimeRange> activeTimes, boolean random, boolean dominant) {
        super(id, dominant);
        this.activeTimes = new ArrayList<>(activeTimes);
        this.random = random;
    }

    public List<TimeRange> getActiveTimes() {
        return activeTimes;
    }

    public GeneActiveTime setActiveTimes(List<TimeRange> activeTimes) {
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

        for (TimeRange range : activeTimes) {
            if (dayTime >= range.start() && dayTime <= range.end()) {
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
    public int sortingOrder() {
        return 5;
    }

    @Override
    public boolean advanced() {
        return true;
    }

    @Override
    public List<TimeRange> value() {
        return activeTimes;
    }

    @Override
    public List<TimeRange> defaultValue() {
        return ActiveTime.DIURNAL;
    }

    @Override
    protected void serializeValue(CompoundTag tag) {
        tag.putBoolean(RANDOM, random);

        ListTag ranges = new ListTag();

        for (TimeRange range : activeTimes) {
            CompoundTag rangeTag = new CompoundTag();
            rangeTag.putInt(START, range.start());
            rangeTag.putInt(END, range.end());
            ranges.add(rangeTag);
        }

        tag.put(DATA, ranges);
    }

    @Override
    protected GeneActiveTime deserializeValue(CompoundTag tag, boolean dominant) {
        List<TimeRange> activeTimes = new ArrayList<>();

        // Handle string-based active time names from old_species format
        if (tag.contains(DATA) && tag.get(DATA) instanceof net.minecraft.nbt.StringTag) {
            String activeTimeName = tag.getString(DATA);
            if (activeTimeName.equalsIgnoreCase("cathemeral")) {
                return new GeneActiveTime(id, defaultValue(), true, dominant);
            }
            activeTimes = ActiveTime.getFromName(activeTimeName);
        } else {
            ListTag ranges = tag.getList(DATA, 10);

            for (int i = 0; i < ranges.size(); i++) {
                CompoundTag range = ranges.getCompound(i);

                activeTimes.add(new TimeRange(range.getInt(START), range.getInt(END)));
            }
        }

        return new GeneActiveTime(id, activeTimes, tag.getBoolean(RANDOM), dominant);
    }

    @Override
    public MutableComponent getValueComponent() {
        if (random) {
            return Component.translatable(TranslationKeys.GENE_ACTIVE_TIME_RANDOM);
        }

        return Component.translatable(TranslationKeys.geneActiveTime(ActiveTime.toName(activeTimes)));
    }

    @Override
    public GeneActiveTime copy() {
        return new GeneActiveTime(id, activeTimes, random, dominant);
    }
}