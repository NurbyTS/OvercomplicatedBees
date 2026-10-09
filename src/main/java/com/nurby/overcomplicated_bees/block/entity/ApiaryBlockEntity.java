package com.nurby.overcomplicated_bees.block.entity;

import com.nurby.overcomplicated_bees.library.apiary.BeeProductionState;
import com.nurby.overcomplicated_bees.menu.ApiaryMenu;
import com.nurby.overcomplicated_bees.registry.BeeBlockEntities;
import com.nurby.overcomplicated_bees.registry.BeeItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ApiaryBlockEntity extends BaseBeeHousing implements MenuProvider {
    public ApiaryBlockEntity(BlockPos pos, BlockState state) {
        super(BeeBlockEntities.APIARY.get(), pos, state);
    }

    /**
     * Direct inventory access for the menu.
     * Do not expose these handlers to automation.
     */
    public ItemStackHandler getBeeItems() {
        return beeInventory;
    }

    public ItemStackHandler getOutputItems() {
        return outputInventory;
    }

    public ItemStackHandler getFrameItems() {
        return frameInventory;
    }

    /**
     * Restricted handlers intended for automation.
     */
    private final IItemHandler automationHandler = new ApiaryAutomationHandler();

    public IItemHandler getAutomationHandler() {
        return automationHandler;
    }

    private class ApiaryAutomationHandler implements IItemHandler {

        @Override
        public int getSlots() {
            return beeInventory.getSlots()
                    + outputInventory.getSlots()
                    + frameInventory.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            SlotReference ref = getSlotReference(slot);

            if (ref == null) {
                return ItemStack.EMPTY;
            }

            // Automation can only extract from the output inventory.
            if (ref.inventory != outputInventory) {
                return ItemStack.EMPTY;
            }

            return outputInventory.getStackInSlot(ref.slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            // Route by item type, not by the requested slot.
            if (stack.getItem() instanceof com.nurby.overcomplicated_bees.library.apiary.IBeeModifier) {
                return insertInto(frameInventory, stack, simulate);
            }

            if (stack.is(BeeItems.QUEEN.get()) || stack.is(BeeItems.PRINCESS.get())) {
                return insertIntoBeeSlot(0, stack, simulate);
            }

            if (stack.is(BeeItems.DRONE.get())) {
                return insertIntoBeeSlot(1, stack, simulate);
            }

            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            SlotReference ref = getSlotReference(slot);

            if (ref == null || ref.inventory != outputInventory) {
                return ItemStack.EMPTY;
            }

            return outputInventory.extractItem(ref.slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            SlotReference ref = getSlotReference(slot);

            return ref == null ? 0 : ref.inventory.getSlotLimit(ref.slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }

            if (stack.getItem() instanceof com.nurby.overcomplicated_bees.library.apiary.IBeeModifier) {
                return canInsertInto(frameInventory, stack);
            }

            if (stack.is(BeeItems.QUEEN.get()) || stack.is(BeeItems.PRINCESS.get())) {
                return beeInventory.isItemValid(0, stack);
            }

            if (stack.is(BeeItems.DRONE.get())) {
                return beeInventory.isItemValid(1, stack);
            }

            return false;
        }

        private ItemStack insertInto(
                ItemStackHandler inventory,
                ItemStack stack,
                boolean simulate
        ) {
            ItemStack remainder = stack;

            for (int i = 0; i < inventory.getSlots(); i++) {
                remainder = inventory.insertItem(i, remainder, simulate);

                if (remainder.isEmpty()) {
                    break;
                }
            }

            return remainder;
        }

        private ItemStack insertIntoBeeSlot(
                int slot,
                ItemStack stack,
                boolean simulate
        ) {
            return beeInventory.insertItem(slot, stack, simulate);
        }

        private boolean canInsertInto(ItemStackHandler inventory, ItemStack stack) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                if (inventory.isItemValid(i, stack)) {
                    ItemStack existing = inventory.getStackInSlot(i);

                    if (existing.isEmpty()
                            || (ItemStack.isSameItemSameComponents(existing, stack)
                            && existing.getCount() < Math.min(
                            inventory.getSlotLimit(i),
                            existing.getMaxStackSize()
                    ))) {
                        return true;
                    }
                }
            }

            return false;
        }

        private SlotReference getSlotReference(int slot) {
            if (slot < 0 || slot >= getSlots()) {
                return null;
            }

            int beeSlots = beeInventory.getSlots();
            int outputSlots = outputInventory.getSlots();

            if (slot < beeSlots) {
                return new SlotReference(beeInventory, slot);
            }

            slot -= beeSlots;

            if (slot < outputSlots) {
                return new SlotReference(outputInventory, slot);
            }

            slot -= outputSlots;

            return new SlotReference(frameInventory, slot);
        }

        private record SlotReference(ItemStackHandler inventory, int slot) {}
    }

    private static class RestrictedItemHandler implements IItemHandler {
        private final ItemStackHandler delegate;
        private final boolean allowInsert;
        private final boolean allowExtract;

        private RestrictedItemHandler(
                ItemStackHandler delegate,
                boolean allowInsert,
                boolean allowExtract
        ) {
            this.delegate = delegate;
            this.allowInsert = allowInsert;
            this.allowExtract = allowExtract;
        }

        @Override
        public int getSlots() {
            return delegate.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return delegate.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(
                int slot,
                ItemStack stack,
                boolean simulate
        ) {
            if (!allowInsert) {
                return stack;
            }

            return delegate.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(
                int slot,
                int amount,
                boolean simulate
        ) {
            if (!allowExtract) {
                return ItemStack.EMPTY;
            }

            return delegate.extractItem(slot, amount, simulate);
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.complicated_bees.apiary");
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory playerInventory,
            net.minecraft.world.entity.player.Player player
    ) {
        return new ApiaryMenu(containerId, playerInventory, this);
    }
}