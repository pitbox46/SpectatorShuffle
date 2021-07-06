package github.pitbox46.spectatorshuffle.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmdTut = dispatcher.register(
                Commands.literal("spectatorshuffle")
                        .then(CommandAuto.register(dispatcher))
                        .then(CommandFocus.register(dispatcher))
                        .then(CommandShuffle.register(dispatcher))
                        .then(CommandStop.register(dispatcher))
                        .then(CommandTop10.register(dispatcher))
        );

        dispatcher.register(Commands.literal("spectatorshuffle").redirect(cmdTut));
    }
}
