package com.nurby.overcomplicated_bees.library.genetics.gene_values;

public final class Productivity {
    public static final float SLOWEST = 0.1f;
    public static final float SLOWER = 0.3f;
    public static final float SLOW = 0.5f;
    public static final float AVERAGE = 0.8f;
    public static final float FAST = 1.0f;
    public static final float FASTER = 1.2f;
    public static final float FASTEST = 1.5f;

    private Productivity() {
    }

    public static String getName(float productivity) {
        if (productivity >= FASTEST) {
            return "fastest";
        }

        if (productivity >= FASTER) {
            return "faster";
        }

        if (productivity >= FAST) {
            return "fast";
        }

        if (productivity >= AVERAGE) {
            return "average";
        }

        if (productivity >= SLOW) {
            return "slow";
        }

        if (productivity >= SLOWER) {
            return "slower";
        }

        return "slowest";
    }

    public static float getFromName(String name) {
        return switch (name.toLowerCase()) {
            case "slowest" -> SLOWEST;
            case "slower" -> SLOWER;
            case "slow" -> SLOW;
            case "average" -> AVERAGE;
            case "fast" -> FAST;
            case "faster" -> FASTER;
            case "fastest" -> FASTEST;
            default -> throw new IllegalArgumentException("Unknown productivity: " + name);
        };
    }
}