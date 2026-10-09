package com.nurby.overcomplicated_bees.block.entity;

import com.nurby.overcomplicated_bees.menu.ApiaryMenu;
import com.nurby.overcomplicated_bees.registry.BeeBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

public class ApiaryBlockEntity extends BaseBeeHousing implements MenuProvider {

    /**
     * Restricted view of the inventories intended for automation (hoppers, pipes, etc.).
     * Slot order: bees, output, frames.
     */
    private final IItemHandlerModifiable automationHandler = new CombinedInvWrapper(
            new RestrictedItemHandler(beeInventory, true, false),     // insert only
            new RestrictedItemHandler(outputInventory, false, true),  // extract only
            new RestrictedItemHandler(frameInventory, true, false)    // insert only
    );

    public ApiaryBlockEntity(BlockPos pos, BlockState state) {
        super(BeeBlockEntities.APIARY.get(), pos, state);
    }

    /**
     * Restricted handler for automation. Register this (not the raw inventories)
     * for the item handler block capability.
     */
    public IItemHandler getAutomationHandler() {
        return automationHandler;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.complicated_bees.apiary");
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory playerInventory,
            Player player
    ) {
        return new ApiaryMenu(containerId, playerInventory, this);
    }

    /**
         * Wraps an inventory and optionally blocks insertion and/or extraction.
         * Slot validity is delegated to the wrapped inventory.
         */
        private record RestrictedItemHandler(ItemStackHandler delegate, boolean allowInsert, boolean allowExtract) implements IItemHandlerModifiable {

        @Override
            public int getSlots() {
                return delegate.getSlots();
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return delegate.getStackInSlot(slot);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (!allowInsert) {
                    return stack;
                }

                return delegate.insertItem(slot, stack, simulate);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!allowExtract) {
                    return ItemStack.EMPTY;
                }

                return delegate.extractItem(slot, amount, simulate);
            }

            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                // intentionally ignored: automation must go through insert/extract
            }

            @Override
            public int getSlotLimit(int slot) {
                return delegate.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return allowInsert && delegate.isItemValid(slot, stack);
            }
        }
}