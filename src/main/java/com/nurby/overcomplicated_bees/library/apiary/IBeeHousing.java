package com.nurby.overcomplicated_bees.library.apiary;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;

public interface IBeeHousing {
    IItemHandler getBeeInventory();

    IItemHandler getOutputInventory();

    IItemHandler getFrameInventory();

    Level getLevel();

    BlockPos getBlockPos();

    void setHousingChanged();

    void syncBeeState();

    void setBeeStackInSlot(int slot, ItemStack stack);

    void setFrameStackInSlot(int slot, ItemStack stack);
}