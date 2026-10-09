package com.nurby.overcomplicated_bees.library.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Locale;

public record Color(int value) {

    public static final Color DEFAULT = new Color(0xFFFFFFFF);
    public static final Codec<Color> CODEC = Codec.of(Color::encode, Color::decode);
    private static final Codec<Color> RGB_CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("r").forGetter(Color::red), Codec.INT.fieldOf("g").forGetter(Color::green), Codec.INT.fieldOf("b").forGetter(Color::blue)).apply(instance, (r, g, b) -> new Color(0xFF000000 | (r << 16) | (g << 8) | b)));

    public Color {
        value |= 0xFF000000;
    }

    private static <T> DataResult<Pair<Color, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<Pair<Color, T>> hexResult = ops.getStringValue(input).flatMap(Color::fromHex).map(color -> Pair.of(color, input));

        if (hexResult.isSuccess()) {
            return hexResult;
        }

        return RGB_CODEC.decode(ops, input);
    }

    private static DataResult<Color> fromHex(String value) {
        String hex = value;

        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() != 6) {
            return DataResult.error(() -> "Expected 6-digit RGB hex color, got: " + value);
        }

        try {
            return DataResult.success(new Color(0xFF000000 | Integer.parseInt(hex, 16)));
        } catch (NumberFormatException exception) {
            return DataResult.error(() -> "Invalid RGB hex color: " + value);
        }
    }

    public int alpha() {
        return 255;
    }

    public int red() {
        return (value >> 16) & 0xFF;
    }

    public int green() {
        return (value >> 8) & 0xFF;
    }

    public int blue() {
        return value & 0xFF;
    }

    private <T> DataResult<T> encode(DynamicOps<T> ops, T prefix) {
        return DataResult.success(ops.createString(toHex()));
    }

    public String toHex() {
        return String.format(Locale.ROOT, "#%06x", value & 0xFFFFFF);
    }
}