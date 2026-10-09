package com.nurby.overcomplicated_bees.block;

import com.mojang.serialization.MapCodec;
import com.nurby.overcomplicated_bees.block.entity.ApiaryBlockEntity;
import com.nurby.overcomplicated_bees.registry.BeeBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.extensions.IPlayerExtension;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ApiaryBlock extends BaseEntityBlock {
    public static final MapCodec<ApiaryBlock> CODEC =
            simpleCodec(ApiaryBlock::new);

    public ApiaryBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ApiaryBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ApiaryBlockEntity apiary)) {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(
                    apiary,
                    buffer -> buffer.writeBlockPos(apiary.getBlockPos())
            );
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide()) {
            return null;
        }

        return createTickerHelper(
                type,
                BeeBlockEntities.APIARY.get(),
                (serverLevel, pos, blockState, apiary) ->
                        apiary.serverTick()
        );
    }

    @Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof ApiaryBlockEntity apiary) {
                if (!level.isClientSide()) {
                    for (int slot = 0;
                         slot < apiary.getBeeInventory().getSlots();
                         slot++) {
                        Containers.dropItemStack(
                                level,
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                apiary.getBeeInventory().getStackInSlot(slot)
                        );
                    }

                    for (int slot = 0;
                         slot < apiary.getOutputInventory().getSlots();
                         slot++) {
                        Containers.dropItemStack(
                                level,
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                apiary.getOutputInventory().getStackInSlot(slot)
                        );
                    }

                    for (int slot = 0;
                         slot < apiary.getFrameInventory().getSlots();
                         slot++) {
                        Containers.dropItemStack(
                                level,
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                apiary.getFrameInventory().getStackInSlot(slot)
                        );
                    }

                    apiary.getBeeLogic().dropOutputBuffer(level, pos);
                }
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }
}