package com.nurby.overcomplicated_bees.client.gui;

import com.nurby.overcomplicated_bees.menu.ApiaryMenu;
import com.nurby.overcomplicated_bees.registry.BeeDataComponents;
import com.nurby.overcomplicated_bees.util.GeneticHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ApiaryScreen extends AbstractContainerScreen<ApiaryMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "complicated_bees",
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

        // The original screen did not display the inventory label.
        inventoryLabelY = imageHeight - 10000;
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
        ItemStack queen = menu.getQueen();

        var lifespanGene = GeneticHelper.getLifespanGene(queen);
        var beeState = queen.get(BeeDataComponents.STATE);

        if (lifespanGene == null || beeState == null) {
            return;
        }

        float lifespan = lifespanGene.getLifespan();

        if (lifespan <= 0) {
            return;
        }

        int progress = (int)Mth.clamp(
                menu.getScaledProgress(beeState.age(), lifespan),
                0,
                BAR_HEIGHT
        );

        if (menu.hasFailureReasons()) {
            renderErrorBar(graphics, left, top);
            return;
        }

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

        int progress = (int)Mth.clamp(
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

        graphics.blit(
                TEXTURE,
                left + BAR_X,
                top + BAR_Y + BAR_HEIGHT - progress,
                BAR_NORMAL_U,
                0,
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
        renderBackground(graphics, mouseX, mouseY, partialTick);
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

        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;

        int relativeX = mouseX - left;
        int relativeY = mouseY - top;

        boolean hoveringBar =
                relativeX > 16
                        && relativeX < 22
                        && relativeY > 34
                        && relativeY < 82;

        if (!hoveringBar) {
            return;
        }

        if (menu.hasQueen()) {
            renderQueenTooltip(graphics, mouseX, mouseY);
        } else if (menu.hasQueuedOutput()) {
            graphics.renderTooltip(
                    font,
                    Component.translatable(
                            "gui.complicated_bees.error.output_full"
                    ),
                    mouseX,
                    mouseY
            );
        }
    }

    private void renderQueenTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        if (menu.hasFailureReasons()) {
            List<Component> errors = menu.getFailureReasonComponents();

            if (!errors.isEmpty()) {
                graphics.renderTooltip(
                        font,
                        errors,
                        java.util.Optional.empty(),
                        mouseX,
                        mouseY
                );
                return;
            }
        }

        graphics.renderTooltip(
                font,
                Component.translatable(
                        "gui.complicated_bees.error.none"
                ),
                mouseX,
                mouseY
        );
    }
}