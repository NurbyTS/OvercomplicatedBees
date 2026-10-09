package com.nurby.overcomplicated_bees.library.genetics.gene_values;

import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class Productivity {
    public static final float SLOWEST = 0.3f;
    public static final float SLOWER = 0.5f;
    public static final float SLOW = 0.8f;
    public static final float AVERAGE = 1.0f;
    public static final float FAST = 1.2f;
    public static final float FASTER = 1.5f;
    public static final float FASTEST = 1.7f;

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

    public static MutableComponent getComponent(float productivity) {
        MutableComponent component = Component.translatable(TranslationKeys.geneValue("productivity", getName(productivity)));

        if (productivity < SLOWEST) {
            return component.append("-");
        } else if (productivity > FASTEST) {
            // For each 0.2 above FASTEST, add a "+"
            int pluses = (int) ((productivity - FASTEST) / 0.2f);
            for (int i = 0; i < pluses; i++) {
                component.append("+");
            }
        }

        return component;
    }
}