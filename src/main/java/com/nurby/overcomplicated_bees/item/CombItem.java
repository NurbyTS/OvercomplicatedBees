package com.nurby.overcomplicated_bees.item;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.registry.BeeDataComponents;
import com.nurby.overcomplicated_bees.util.TranslationKeys;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CombItem extends Item {
    public CombItem() {
        super(new Item.Properties().stacksTo(64));
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        ResourceLocation combType = stack.get(BeeDataComponents.COMB_TYPE);

        if (combType == null) {
            combType = ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "invalid");
        }

        return Component.translatable(TranslationKeys.COMB_PREFIX, Component.translatable(TranslationKeys.comb(combType)));
    }
}
