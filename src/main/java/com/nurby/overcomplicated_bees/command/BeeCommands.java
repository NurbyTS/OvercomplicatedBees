package com.nurby.overcomplicated_bees.command;

import com.mojang.brigadier.CommandDispatcher;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneFertility;
import com.nurby.overcomplicated_bees.registry.BeeItems;
import com.nurby.overcomplicated_bees.service.BeeBreedingService;
import com.nurby.overcomplicated_bees.util.GeneticHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class BeeCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("bee").requires(source -> source.getEntity() instanceof Player).then(Commands.literal("breed").executes(context -> breedBees(context.getSource()))).then(Commands.literal("offspring").executes(context -> produceOffspring(context.getSource()))));
    }

    private static int breedBees(CommandSourceStack source) {
        Player player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("You need to be a player to breed bees."));
            return 0;
        }

        ItemStack drone = ItemStack.EMPTY;
        ItemStack princess = ItemStack.EMPTY;

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (drone.isEmpty() && stack.is(BeeItems.DRONE)) {
                drone = stack;
            } else if (princess.isEmpty() && stack.is(BeeItems.PRINCESS)) {
                princess = stack;
            }

            if (!drone.isEmpty() && !princess.isEmpty()) {
                break;
            }
        }

        if (drone.isEmpty()) {
            source.sendFailure(Component.literal("You need a drone in your inventory."));
            return 0;
        }

        if (princess.isEmpty()) {
            source.sendFailure(Component.literal("You need a princess in your inventory."));
            return 0;
        }

        if (GeneticHelper.getGenome(drone) == null || GeneticHelper.getGenome(princess) == null) {
            source.sendFailure(Component.literal("One of the bees is missing genetics."));
            return 0;
        }

        ItemStack queen = BeeBreedingService.createQueen(princess, drone);

        drone.shrink(1);
        princess.shrink(1);

        if (!player.getInventory().add(queen)) {
            player.drop(queen, false);
        }

        source.sendSuccess(() -> Component.literal("Successfully created a queen!"), false);

        return 1;
    }

    private static int produceOffspring(CommandSourceStack source) {
        Player player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("You need to be a player to produce offspring."));
            return 0;
        }

        ItemStack queen = ItemStack.EMPTY;

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (stack.is(BeeItems.QUEEN)) {
                queen = stack;
                break;
            }
        }

        if (queen.isEmpty()) {
            source.sendFailure(Component.literal("You need a queen in your inventory."));
            return 0;
        }

        if (GeneticHelper.getGenome(queen) == null) {
            source.sendFailure(Component.literal("The queen is missing genetics."));
            return 0;
        }

        ItemStack princess = BeeBreedingService.createOffspring(source.getLevel().getRandom(), queen, BeeItems.PRINCESS.get());

        if (!player.getInventory().add(princess)) {
            player.drop(princess, false);
        }

        GeneFertility gene = GeneticHelper.getFertilityGene(queen);
        if (gene == null) {
            source.sendFailure(Component.literal("The queen is somehow missing fertility genetics."));
            return 0;
        }
        int fertility = gene.getFertility();

        for (int i = 0; i < fertility; i++) {
            ItemStack drone = BeeBreedingService.createOffspring(source.getLevel().getRandom(), queen, BeeItems.DRONE.get());

            if (!player.getInventory().add(drone)) {
                player.drop(drone, false);
            }
        }

        queen.shrink(1);

        source.sendSuccess(() -> Component.literal("Queen produced 1 princess and " + fertility + " drone" + (fertility == 1 ? "." : "s.")), false);

        return 1;
    }
}