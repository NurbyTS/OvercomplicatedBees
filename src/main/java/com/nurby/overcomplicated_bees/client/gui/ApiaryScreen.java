package com.nurby.overcomplicated_bees.client.gui;

import com.nurby.overcomplicated_bees.menu.ApiaryMenu;
import com.nurby.overcomplicated_bees.registry.BeeDataComponents;
import com.nurby.overcomplicated_bees.util.GeneticHelper;
import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class ApiaryScreen extends AbstractContainerScreen<ApiaryMenu> {
    private static final String MOD_ID = "complicated_bees";

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    MOD_ID,
                    "textures/gui/apiary.png"
            );

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private static final int BAR_X = 18;
    private static final int BAR_Y = 36;
    private static final int BAR_WIDTH = 3;
    private static final int BAR_HEIGHT = 45;

    private static final int BAR_NORMAL_U = 179;
    private static final int BAR_ECSTATIC_U = 176;
    private static final int BAR_ERROR_U = 182;

    public ApiaryScreen(
            ApiaryMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);

        imageWidth = 176;
        imageHeight = 187;
    }

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;

        graphics.blit(
                TEXTURE,
                left,
                top,
                0,
                0,
                imageWidth,
                imageHeight,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );

        renderStatusBar(graphics, left, top);
    }

    private void renderStatusBar(
            GuiGraphics graphics,
            int left,
            int top
    ) {
        if (menu.hasQueen()) {
            renderQueenBar(graphics, left, top);
        } else if (menu.hasPrincessAndDrone()) {
            renderBreedingBar(graphics, left, top);
        } else if (menu.hasQueuedOutput()) {
            renderErrorBar(graphics, left, top);
        }
    }

    private void renderQueenBar(
            GuiGraphics graphics,
            int left,
            int top
    ) {
        // Failures and a blocked output both halt production, so show the
        // red bar regardless of the queen's age.
        if (menu.isBeeNotWorking() || menu.hasQueuedOutput()) {
            renderErrorBar(graphics, left, top);
            return;
        }

        ItemStack queen = menu.getQueen();

        var lifespanGene = GeneticHelper.getLifespanGene(queen);

        if (lifespanGene == null || lifespanGene.getLifespan() <= 0) {
            return;
        }

        // A queen without a STATE component is treated as age 0,
        // matching BeeLogicHandler.
        var beeState = queen.get(BeeDataComponents.STATE);
        float age = beeState == null ? 0.0f : beeState.age();

        int progress = (int) Mth.clamp(
                menu.getScaledProgress(age, lifespanGene.getLifespan()),
                0,
                BAR_HEIGHT
        );

        int textureU = menu.isEcstatic()
                ? BAR_ECSTATIC_U
                : BAR_NORMAL_U;

        graphics.blit(
                TEXTURE,
                left + BAR_X,
                top + BAR_Y + progress,
                textureU,
                progress,
                BAR_WIDTH,
                BAR_HEIGHT - progress
        );
    }

    private void renderBreedingBar(
            GuiGraphics graphics,
            int left,
            int top
    ) {
        int maxProgress = menu.getMaxMatingProgress();

        if (maxProgress <= 0) {
            return;
        }

        int progress = (int) Mth.clamp(
                menu.getScaledProgress(
                        menu.getMatingProgress(),
                        maxProgress
                ),
                0,
                BAR_HEIGHT
        );

        if (progress <= 0) {
            return;
        }

        // Fill from the bottom, sampling the matching bottom part of the strip.
        graphics.blit(
                TEXTURE,
                left + BAR_X,
                top + BAR_Y + BAR_HEIGHT - progress,
                BAR_NORMAL_U,
                BAR_HEIGHT - progress,
                BAR_WIDTH,
                progress
        );
    }

    private void renderErrorBar(
            GuiGraphics graphics,
            int left,
            int top
    ) {
        graphics.blit(
                TEXTURE,
                left + BAR_X,
                top + BAR_Y,
                BAR_ERROR_U,
                0,
                BAR_WIDTH,
                BAR_HEIGHT
        );
    }

    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        graphics.drawString(
                font,
                title,
                8,
                6,
                0x404040,
                false
        );

        // Deliberately omit the inventory label, as in the original GUI.
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        // super.render already draws the background.
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        super.renderTooltip(graphics, mouseX, mouseY);

        int relativeX = mouseX - (width - imageWidth) / 2;
        int relativeY = mouseY - (height - imageHeight) / 2;

        boolean hoveringBar =
                relativeX >= BAR_X - 1
                        && relativeX <= BAR_X + BAR_WIDTH
                        && relativeY >= BAR_Y - 1
                        && relativeY <= BAR_Y + BAR_HEIGHT;

        if (hoveringBar) {
            renderBarTooltip(graphics, mouseX, mouseY);
        }
    }

    private void renderBarTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        if (menu.hasQueen() && menu.isBeeNotWorking()) {
            List<Component> errors = menu.getFailureReasonComponents();

            if (!errors.isEmpty()) {
                graphics.renderTooltip(
                        font,
                        errors,
                        Optional.empty(),
                        mouseX,
                        mouseY
                );
                return;
            }
        }

        if (menu.hasQueen()) {
            graphics.renderTooltip(
                    font,
                    Component.translatable(menu.isEcstatic() ? TranslationKeys.ERROR_ECSTATIC : TranslationKeys.ERROR_NONE),
                    mouseX,
                    mouseY
            );
        }
    }
}