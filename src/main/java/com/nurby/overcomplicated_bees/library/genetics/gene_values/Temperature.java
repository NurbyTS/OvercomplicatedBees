package com.nurby.overcomplicated_bees.library.genetics.gene_values;

import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

public final class Temperature {
    public static final float FROZEN = -Float.MAX_VALUE;
    public static final float ICY = -0.35f;
    public static final float COLD = 0.0f;
    public static final float NORMAL = 0.35f;
    public static final float WARM = 0.85f;
    public static final float HOT = 1.0f;
    public static final float HELLISH = 2.0f;

    private Temperature() {
    }

    public static String getName(float temperature) {
        if (temperature > HELLISH) {
            return "hellish";
        } else if (temperature > HOT) {
            return "hot";
        } else if (temperature > WARM) {
            return "warm";
        } else if (temperature > NORMAL) {
            return "normal";
        } else if (temperature > COLD) {
            return "cold";
        } else if (temperature > ICY) {
            return "icy";
        }

        return "frozen";
    }

    public static MutableComponent getComponent(float temperature) {
        MutableComponent component = Component.translatable(TranslationKeys.geneValue("temperature", Temperature.getName(temperature)));

        if (temperature < -0.35f) {
            int minuses = (int) ((-0.35f - temperature) / 0.2f);

            for (int i = 0; i < minuses; i++) {
                component.append("-");
            }
        } else if (temperature > 2.0f) {
            int pluses = (int) ((temperature - 2.0f) / 0.2f);

            for (int i = 0; i < pluses; i++) {
                component.append("+");
            }
        }

        return component;
    }

    public static float getFromString(String value) {
        return switch (value.toLowerCase()) {
            case "frozen" -> FROZEN;
            case "icy" -> ICY;
            case "cold" -> COLD;
            case "normal" -> NORMAL;
            case "warm" -> WARM;
            case "hot" -> HOT;
            case "hellish" -> HELLISH;
            default -> NORMAL;
        };
    }

    public static float getFromLocation(Level level, BlockPos pos) {
        if (level.dimension() == Level.NETHER) {
            return HELLISH;
        }

        return level.getBiome(pos).value().getModifiedClimateSettings().temperature();
    }

    public static int getLevel(float temperature) {
        if (temperature > HELLISH) {
            return 6;
        } else if (temperature > HOT) {
            return 5;
        } else if (temperature > WARM) {
            return 4;
        } else if (temperature > NORMAL) {
            return 3;
        } else if (temperature > COLD) {
            return 2;
        } else if (temperature > ICY) {
            return 1;
        }
        return 0;
    }

    public static float fromLevel(int level) {
        return switch (level) {
            case 0 -> FROZEN;
            case 1 -> ICY;
            case 2 -> COLD;
            case 3 -> NORMAL;
            case 4 -> WARM;
            case 5 -> HOT;
            case 6 -> HELLISH;
            default -> throw new IllegalArgumentException("Invalid temperature level: " + level);
        };
    }

    public static float modifier(float temperature, int stages) {
        if (stages > 0) {
            return increaseBy(temperature, stages);
        } else if (stages < 0) {
            return decreaseBy(temperature, -stages);
        }

        return temperature;
    }

    public static float increaseBy(float temperature, int stages) {
        int currentLevel = getLevel(temperature);
        return fromLevel(Math.min(currentLevel + stages, 6));
    }

    public static float increase(float temperature) {
        int currentLevel = getLevel(temperature);
        return fromLevel(Math.min(currentLevel + 1, 6));
    }

    public static float decreaseBy(float temperature, int stages) {
        int currentLevel = getLevel(temperature);
        return fromLevel(Math.max(currentLevel - stages, 0));
    }

    public static float decrease(float temperature) {
        int currentLevel = getLevel(temperature);
        return fromLevel(Math.max(currentLevel - 1, 0));
    }
}