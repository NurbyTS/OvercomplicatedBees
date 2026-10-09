package com.nurby.overcomplicated_bees.library.genetics.gene_values;

import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record Tolerance(int up, int down) {
    public static final Tolerance NONE = new Tolerance(0, 0);

    public static final Tolerance BOTH_5 = new Tolerance(5, 5);
    public static final Tolerance BOTH_4 = new Tolerance(4, 4);
    public static final Tolerance BOTH_3 = new Tolerance(3, 3);
    public static final Tolerance BOTH_2 = new Tolerance(2, 2);
    public static final Tolerance BOTH_1 = new Tolerance(1, 1);

    public static final Tolerance UP_5 = new Tolerance(5, 0);
    public static final Tolerance UP_4 = new Tolerance(4, 0);
    public static final Tolerance UP_3 = new Tolerance(3, 0);
    public static final Tolerance UP_2 = new Tolerance(2, 0);
    public static final Tolerance UP_1 = new Tolerance(1, 0);

    public static final Tolerance DOWN_5 = new Tolerance(0, 5);
    public static final Tolerance DOWN_4 = new Tolerance(0, 4);
    public static final Tolerance DOWN_3 = new Tolerance(0, 3);
    public static final Tolerance DOWN_2 = new Tolerance(0, 2);
    public static final Tolerance DOWN_1 = new Tolerance(0, 1);

    public static Tolerance getFromString(String name) {
        return switch (name.toUpperCase()) {
            case "NONE" -> NONE;
            case "BOTH_5" -> BOTH_5;
            case "BOTH_4" -> BOTH_4;
            case "BOTH_3" -> BOTH_3;
            case "BOTH_2" -> BOTH_2;
            case "BOTH_1" -> BOTH_1;
            case "UP_5" -> UP_5;
            case "UP_4" -> UP_4;
            case "UP_3" -> UP_3;
            case "UP_2" -> UP_2;
            case "UP_1" -> UP_1;
            case "DOWN_5" -> DOWN_5;
            case "DOWN_4" -> DOWN_4;
            case "DOWN_3" -> DOWN_3;
            case "DOWN_2" -> DOWN_2;
            case "DOWN_1" -> DOWN_1;
            default -> throw new IllegalArgumentException("Unknown tolerance: " + name);
        };
    }

    public static MutableComponent getComponent(Tolerance tolerance) {
        return Component.translatable(TranslationKeys.geneValue("tolerance", tolerance.getName()));
    }

    public String getName() {
        if (up == 0 && down == 0) {
            return "none";
        }

        if (up == down) {
            return "both_" + up;
        }

        if (down == 0) {
            return "up_" + up;
        }

        return "down_" + down;
    }
}