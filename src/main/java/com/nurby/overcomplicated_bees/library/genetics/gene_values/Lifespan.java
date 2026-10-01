package com.nurby.overcomplicated_bees.library.genetics.gene_values;

public final class Lifespan {
    public static final int IMMORTAL = -1;
    public static final int DEAD = 0;
    public static final int SHORTEST = 20;
    public static final int SHORTER = 30;
    public static final int SHORT = 42;
    public static final int AVERAGE = 58;
    public static final int LONG = 70;
    public static final int LONGER = 83;
    public static final int LONGEST = 90;

    private Lifespan() {
    }

    public static String getName(int lifespan) {
        if (lifespan == IMMORTAL) {
            return "immortal";
        }

        if (lifespan == DEAD) {
            return "dead";
        }

        if (lifespan < SHORTEST) {
            return "shortest";
        }

        if (lifespan < SHORTER) {
            return "shorter";
        }

        if (lifespan < SHORT) {
            return "short";
        }

        if (lifespan < AVERAGE) {
            return "average";
        }

        if (lifespan < LONG) {
            return "long";
        }

        if (lifespan < LONGER) {
            return "longer";
        }

        return "longest";
    }

    public static int getFromName(String name) {
        return switch (name.toLowerCase()) {
            case "immortal" -> IMMORTAL;
            case "dead" -> DEAD;
            case "shortest" -> SHORTEST;
            case "shorter" -> SHORTER;
            case "short" -> SHORT;
            case "average" -> AVERAGE;
            case "long" -> LONG;
            case "longer" -> LONGER;
            case "longest" -> LONGEST;
            default -> throw new IllegalArgumentException("Unknown lifespan: " + name);
        };
    }
}