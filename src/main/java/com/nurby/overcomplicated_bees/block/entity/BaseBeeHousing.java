
package com.nurby.overcomplicated_bees.block.entity;

import com.nurby.overcomplicated_bees.library.apiary.BeeLogicHandler;
import com.nurby.overcomplicated_bees.library.apiary.BeeProductionState;
import com.nurby.overcomplicated_bees.library.apiary.IBeeHousing;
import com.nurby.overcomplicated_bees.library.apiary.IBeeModifier;
import com.nurby.overcomplicated_bees.registry.BeeItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BaseBeeHousing extends BlockEntity implements IBeeHousing {
    public static final int BEE_SLOT_COUNT = 2;
    public static final int OUTPUT_SLOT_COUNT = 7;
    public static final int FRAME_SLOT_COUNT = 3;

    public static final int MENU_STATUS_NORMAL = 0;
    public static final int MENU_STATUS_ECSTATIC = 1;
    public static final int MENU_STATUS_ERROR = 2;
    public static final int MENU_STATUS_OUTPUT_FULL = 3;

    private final BeeLogicHandler beeLogic;

    protected final ItemStackHandler beeInventory =
            new ItemStackHandler(BEE_SLOT_COUNT) {
                @Override
                public boolean isItemValid(int slot, ItemStack stack) {
                    return switch (slot) {
                        case 0 -> stack.is(BeeItems.QUEEN.get())
                                || stack.is(BeeItems.PRINCESS.get());
                        case 1 -> stack.is(BeeItems.DRONE.get());
                        default -> false;
                    };
                }

                @Override
                protected void onContentsChanged(int slot) {
                    setChanged();

                    if (beeLogic != null) {
                        beeLogic.invalidateFlowerCache();

                        if (slot == 0) {
                            beeLogic.resetProgressForQueenChange();
                        }
                    }
                }
            };

    protected final ItemStackHandler outputInventory =
            new ItemStackHandler(OUTPUT_SLOT_COUNT) {
                @Override
                protected void onContentsChanged(int slot) {
                    setChanged();
                }
            };

    protected final ItemStackHandler frameInventory =
            new ItemStackHandler(FRAME_SLOT_COUNT) {
                @Override
                public boolean isItemValid(int slot, ItemStack stack) {
                    return stack.getItem() instanceof IBeeModifier;
                }

                @Override
                protected void onContentsChanged(int slot) {
                    setChanged();

                    if (beeLogic != null) {
                        beeLogic.invalidateFlowerCache();
                    }
                }
            };

    protected BaseBeeHousing(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state
    ) {
        super(type, pos, state);
        this.beeLogic = new BeeLogicHandler(this);
    }

    @Override
    public IItemHandler getBeeInventory() {
        return beeInventory;
    }

    @Override
    public IItemHandler getOutputInventory() {
        return outputInventory;
    }

    @Override
    public IItemHandler getFrameInventory() {
        return frameInventory;
    }

    public BeeLogicHandler getBeeLogic() {
        return beeLogic;
    }

    public int getProductionProgress() {
        return beeLogic.getProductionProgress();
    }

    public int getMatingProgress() {
        return beeLogic.getMatingProgress();
    }

    public int getMaxMatingProgress() {
        return beeLogic.getMaxMatingProgress();
    }

    public BeeProductionState getBeeState() {
        return beeLogic.getBeeState();
    }

    public boolean isOutputBlocked() {
        return beeLogic.isOutputBlocked();
    }

    public int getMenuStatus() {
        BeeProductionState state = beeLogic.getBeeState();

        if (!state.getFailureReasons().isEmpty()) {
            return MENU_STATUS_ERROR;
        }

        if (state.isEcstatic()) {
            return MENU_STATUS_ECSTATIC;
        }

        if (beeLogic.isOutputBlocked()) {
            return MENU_STATUS_OUTPUT_FULL;
        }

        return MENU_STATUS_NORMAL;
    }

    // ==================== Server Lifecycle ====================

    /**
     * Call from the server-side block entity ticker.
     */
    public void serverTick() {
        beeLogic.tick();
    }

    // ==================== IBeeHousing Callbacks ====================

    @Override
    public void setHousingChanged() {
        setChanged();
    }

    @Override
    public void syncBeeState() {
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    Block.UPDATE_CLIENTS
            );
        }
    }

    @Override
    public void setBeeStackInSlot(int slot, ItemStack stack) {
        beeInventory.setStackInSlot(slot, stack);
    }

    @Override
    public void setFrameStackInSlot(int slot, ItemStack stack) {
        frameInventory.setStackInSlot(slot, stack);
    }

    // ==================== Inventory Persistence ====================

    @Override
    protected void saveAdditional(
            @NotNull CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(tag, registries);

        tag.put("bee_inventory", beeInventory.serializeNBT(registries));
        tag.put("output_inventory", outputInventory.serializeNBT(registries));
        tag.put("frame_inventory", frameInventory.serializeNBT(registries));

        beeLogic.saveState(tag, registries);
    }

    @Override
    protected void loadAdditional(
            @NotNull CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.loadAdditional(tag, registries);

        if (tag.contains("bee_inventory")) {
            beeInventory.deserializeNBT(
                    registries,
                    tag.getCompound("bee_inventory")
            );
        }

        if (tag.contains("output_inventory")) {
            outputInventory.deserializeNBT(
                    registries,
                    tag.getCompound("output_inventory")
            );
        }

        if (tag.contains("frame_inventory")) {
            frameInventory.deserializeNBT(
                    registries,
                    tag.getCompound("frame_inventory")
            );
        }

        beeLogic.loadState(tag, registries);
    }

    // ==================== Client Synchronization ====================

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        beeLogic.saveClientState(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.handleUpdateTag(tag, registries);
        beeLogic.loadClientState(tag, registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}