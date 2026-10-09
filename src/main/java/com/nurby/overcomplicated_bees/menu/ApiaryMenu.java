package com.nurby.overcomplicated_bees.menu;

import com.nurby.overcomplicated_bees.block.entity.ApiaryBlockEntity;
import com.nurby.overcomplicated_bees.registry.BeeItems;
import com.nurby.overcomplicated_bees.registry.BeeMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.List;

public class ApiaryMenu extends AbstractContainerMenu {
    private static final int DATA_COUNT = 4;

    private final ApiaryBlockEntity apiary;
    private final ContainerData data;

    public ApiaryMenu(
            int containerId,
            Inventory playerInventory,
            ApiaryBlockEntity apiary
    ) {
        super(BeeMenus.APIARY.get(), containerId);

        this.apiary = apiary;
        this.data = createContainerData(apiary);

        addApiarySlots(apiary);
        addPlayerInventory(playerInventory);

        addDataSlots(data);
    }

    private static ContainerData createContainerData(ApiaryBlockEntity apiary) {
        int[] syncedValues = new int[DATA_COUNT];

        return new ContainerData() {
            @Override
            public int get(int index) {
                if (apiary.getLevel() != null
                        && apiary.getLevel().isClientSide()) {
                    return syncedValues[index];
                }

                return switch (index) {
                    case 0 -> apiary.getMatingProgress();
                    case 1 -> apiary.getMaxMatingProgress();
                    case 2 -> apiary.getMenuStatus();
                    case 3 -> apiary.isOutputBlocked() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index >= 0 && index < DATA_COUNT) {
                    syncedValues[index] = value;
                }
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }


    public ApiaryMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(
                containerId,
                playerInventory,
                getApiaryFromBuffer(playerInventory, buffer)
        );
    }

    private static ApiaryBlockEntity getApiaryFromBuffer(
            Inventory inventory,
            FriendlyByteBuf buffer
    ) {
        var pos = buffer.readBlockPos();
        var blockEntity = inventory.player.level().getBlockEntity(pos);

        if (blockEntity instanceof ApiaryBlockEntity apiary) {
            return apiary;
        }

        throw new IllegalStateException(
                "No apiary block entity at " + pos
        );
    }

    private void addApiarySlots(ApiaryBlockEntity apiary) {
        // Bee slots
        addSlot(new SlotItemHandler(
                apiary.getBeeItems(), 0, 29, 38
        ));
        addSlot(new SlotItemHandler(
                apiary.getBeeItems(), 1, 29, 63
        ));

        // Frame slots
        addSlot(new SlotItemHandler(
                apiary.getFrameItems(), 0, 65, 23
        ));
        addSlot(new SlotItemHandler(
                apiary.getFrameItems(), 1, 65, 51
        ));
        addSlot(new SlotItemHandler(
                apiary.getFrameItems(), 2, 65, 79
        ));

        // Output slots, arranged around the production area
        addOutputSlot(0, 115, 51);
        addOutputSlot(1, 115, 26);
        addOutputSlot(2, 137, 39);
        addOutputSlot(3, 137, 64);
        addOutputSlot(4, 115, 76);
        addOutputSlot(5, 93, 64);
        addOutputSlot(6, 93, 39);
    }

    private void addOutputSlot(int index, int x, int y) {
        addSlot(new SlotItemHandler(apiary.getOutputItems(), index, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(
                        inventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        105 + row * 18
                ));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(
                    inventory,
                    column,
                    8 + column * 18,
                    163
            ));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result;
        Slot slot = slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        result = stack.copy();

        int apiarySlotCount = 2 + 3 + 7;

        if (index < apiarySlotCount) {
            if (!moveItemStackTo(
                    stack,
                    apiarySlotCount,
                    slots.size(),
                    true
            )) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean moved = false;

            if (apiary.getBeeItems().isItemValid(0, stack)) {
                moved = moveItemStackTo(stack, 0, 1, false);
            }

            if (!moved && apiary.getBeeItems().isItemValid(1, stack)) {
                moved = moveItemStackTo(stack, 1, 2, false);
            }

            if (!moved && apiary.getFrameItems().isItemValid(0, stack)) {
                moved = moveItemStackTo(stack, 2, 5, false);
            }

            if (!moved) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return apiary.getBlockPos().distToCenterSqr(
                player.getX(), player.getY(), player.getZ()
        ) <= 64.0
                && !apiary.isRemoved();
    }

    public ApiaryBlockEntity getApiary() {
        return apiary;
    }

    public int getProductionProgress() {
        return apiary.getProductionProgress();
    }

    public int getMatingProgress() {
        return data.get(0);
    }

    public int getMaxMatingProgress() {
        return data.get(1);
    }

    public int getWorkingState() {
        return data.get(2);
    }

    public boolean hasQueen() {
        return getSlot(0).getItem().is(BeeItems.QUEEN.get());
    }

    public ItemStack getQueen() {
        return getSlot(0).getItem();
    }

    public boolean isBreeding() {
        return getMatingProgress() > 0;
    }

    public boolean isEcstatic() {
        return getWorkingState() == 1;
    }

    public boolean hasFailureReasons() {
        return getWorkingState() == 2;
    }

    public boolean hasQueuedOutput() {
        return data.get(3) != 0;
    }

    public float getScaledProgress(float progress, float maxProgress) {
        if (maxProgress <= 0 || progress <= 0) {
            return 0;
        }

        return Mth.clamp(
                progress * 45 / maxProgress,
                0,
                45
        );
    }

    public List<Component> getFailureReasonComponents() {
        return apiary.getBeeState().getFailureReasons();
    }

    public boolean hasPrincessAndDrone() {
        return getSlot(0).getItem().is(BeeItems.PRINCESS.get())
                && getSlot(1).getItem().is(BeeItems.DRONE.get());
    }
}