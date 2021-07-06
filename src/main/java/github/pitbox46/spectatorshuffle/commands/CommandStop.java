package github.pitbox46.spectatorshuffle.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.spectatorshuffle.ServerEvents;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandStop implements Command<CommandSource> {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final CommandStop CMD = new CommandStop();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("stop")
                .requires(cs -> {
                    try {
                        return cs.asPlayer().isSpectator();
                    } catch (CommandSyntaxException ignore) {
                        return cs.hasPermissionLevel(2);
                    }
                })
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerEvents.spectatorMap.remove(context.getSource().asPlayer());
        return 0;
    }
}
