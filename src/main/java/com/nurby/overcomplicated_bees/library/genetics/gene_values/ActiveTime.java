package com.nurby.overcomplicated_bees.library.genetics.gene_values;

import java.util.List;

public final class ActiveTime {
    public static final List<TimeRange> DIURNAL = List.of(new TimeRange(0, 12000));

    public static final List<TimeRange> NOCTURNAL = List.of(new TimeRange(13000, 24000));

    public static final List<TimeRange> MATUTINAL = List.of(new TimeRange(22300, 24000));

    public static final List<TimeRange> VESPERTINE = List.of(new TimeRange(12000, 13702));

    public static final List<TimeRange> CREPUSCULAR = List.of(new TimeRange(12000, 13702), new TimeRange(22300, 24000));

    public static final List<TimeRange> NEVER_SLEEPS = List.of(new TimeRange(-1, -1));

    public static final boolean CATHEMERAL = true;

    private ActiveTime() {
    }

    public static List<TimeRange> getFromName(String name) {
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

    public static String toName(List<TimeRange> activeTime) {
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

    public static boolean isWithin(List<TimeRange> activeTimes, long time) {
        long dayTime = time % 24000L;

        for (TimeRange range : activeTimes) {
            boolean startMatch = range.start() == -1 || dayTime >= range.start();
            boolean endMatch = range.end() == -1 || dayTime <= range.end();

            if (startMatch && endMatch) {
                return true;
            }
        }

        return false;
    }
}