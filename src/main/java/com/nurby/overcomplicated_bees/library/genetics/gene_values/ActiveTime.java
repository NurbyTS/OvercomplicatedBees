package com.nurby.overcomplicated_bees.library.genetics.gene_values;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public final class ActiveTime {
    public static final List<Pair<Integer, Integer>> DIURNAL =
            List.of(new ImmutablePair<>(0, 12000));

    public static final List<Pair<Integer, Integer>> NOCTURNAL =
            List.of(new ImmutablePair<>(13000, 24000));

    public static final List<Pair<Integer, Integer>> MATUTINAL =
            List.of(new ImmutablePair<>(22300, 24000));

    public static final List<Pair<Integer, Integer>> VESPERTINE =
            List.of(new ImmutablePair<>(12000, 13702));

    public static final List<Pair<Integer, Integer>> CREPUSCULAR =
            List.of(
                    new ImmutablePair<>(12000, 13702),
                    new ImmutablePair<>(22300, 24000)
            );

    public static final List<Pair<Integer, Integer>> NEVER_SLEEPS =
            List.of(new ImmutablePair<>(-1, -1));

    public static final boolean CATHEMERAL = true;

    private ActiveTime() {
    }

    public static List<Pair<Integer, Integer>> getFromName(String name) {
        return switch (name.toLowerCase()) {
            case "diurnal" -> DIURNAL;
            case "nocturnal" -> NOCTURNAL;
            case "matutinal" -> MATUTINAL;
            case "vespertine" -> VESPERTINE;
            case "crepuscular" -> CREPUSCULAR;
            case "never_sleeps" -> NEVER_SLEEPS;
            default -> throw new IllegalArgumentException("Unknown active time: " + name);
        };
    }

    public static String toName(List<Pair<Integer, Integer>> activeTime) {
        if (DIURNAL.equals(activeTime)) {
            return "diurnal";
        }

        if (NOCTURNAL.equals(activeTime)) {
            return "nocturnal";
        }

        if (MATUTINAL.equals(activeTime)) {
            return "matutinal";
        }

        if (VESPERTINE.equals(activeTime)) {
            return "vespertine";
        }

        if (CREPUSCULAR.equals(activeTime)) {
            return "crepuscular";
        }

        if (NEVER_SLEEPS.equals(activeTime)) {
            return "never_sleeps";
        }

        if (activeTime == null) {
            return "unknown";
        }

        return "unknown";
    }

    public static boolean isWithin(
            List<Pair<Integer, Integer>> activeTimes,
            long time
    ) {
        long dayTime = time % 24000L;

        for (Pair<Integer, Integer> range : activeTimes) {
            if (range.getLeft() == -1 && range.getRight() == -1) {
                return true;
            }

            if (dayTime >= range.getLeft() && dayTime <= range.getRight()) {
                return true;
            }
        }

        return false;
    }
}