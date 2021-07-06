package github.pitbox46.spectatorshuffle.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.spectatorshuffle.ServerEvents;
import github.pitbox46.spectatorshuffle.Spectate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandShuffle implements Command<CommandSource> {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final CommandShuffle CMD = new CommandShuffle();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("shuffle")
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
        Spectate spectate = new Spectate(Spectate.Mode.SHUFFLE);
        ServerEvents.spectatorMap.put(context.getSource().asPlayer(), spectate);
        return 0;
    }
}
