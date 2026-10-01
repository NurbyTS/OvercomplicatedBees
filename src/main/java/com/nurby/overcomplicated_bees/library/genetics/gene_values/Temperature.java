package com.nurby.overcomplicated_bees.library.genetics.gene_values;

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
        MutableComponent component = Component.translatable(
                "gene.complicated_bees.temperature." + Temperature.getName(temperature)
        );

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

        return level.getBiome(pos)
                .value()
                .getModifiedClimateSettings()
                .temperature();
    }

    public static float increase(float temperature, int stages) {
        if (stages == 0) {
            return temperature;
        }

        float result = temperature;

        if (stages > 0) {
            for (int i = 0; i < stages; i++) {
                result = increase(result);
            }
        } else {
            for (int i = 0; i < -stages; i++) {
                result = decrease(result);
            }
        }

        return result;
    }

    public static float increase(float temperature) {
        if (temperature <= ICY) {
            return COLD;
        }
        if (temperature <= COLD) {
            return NORMAL;
        }
        if (temperature <= NORMAL) {
            return WARM;
        }
        if (temperature <= WARM) {
            return HOT;
        }
        if (temperature <= HOT) {
            return HELLISH;
        }

        return temperature + 0.2f;
    }

    public static float decrease(float temperature) {
        if (temperature > HOT) {
            return HOT;
        }
        if (temperature > WARM) {
            return WARM;
        }
        if (temperature > NORMAL) {
            return NORMAL;
        }
        if (temperature > COLD) {
            return COLD;
        }
        if (temperature > ICY) {
            return ICY;
        }

        return temperature - 0.2f;
    }
}