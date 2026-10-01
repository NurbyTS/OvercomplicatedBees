package com.nurby.overcomplicated_bees.library.genetics.gene_values;

import net.minecraft.world.phys.Vec3;

public record Territory(int horizontalRadius, int verticalRadius) {
    public int horizontalSize() {
        return horizontalRadius * 2 + 1;
    }

    public int verticalSize() {
        return verticalRadius * 2 + 1;
    }

    public boolean isWithin(Vec3 position, Vec3 center) {
        double dx = Math.abs(position.x - center.x);
        double dy = Math.abs(position.y - center.y);
        double dz = Math.abs(position.z - center.z);

        return dx <= horizontalRadius
                && dy <= verticalRadius
                && dz <= horizontalRadius;
    }
}