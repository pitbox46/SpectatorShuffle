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
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandFocus implements Command<CommandSource> {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final CommandFocus CMD = new CommandFocus();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("focus")
                .requires(cs -> {
                    try {
                        return cs.asPlayer().isSpectator();
                    } catch (CommandSyntaxException ignore) {
                        return cs.hasPermissionLevel(2);
                    }
                })
                .then(Commands.argument("player", EntityArgument.entity())
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = EntityArgument.getPlayer(context, "player");
        Spectate spectate = new Spectate(Spectate.Mode.FOCUS);
        spectate.spectated = playerEntity;
        ServerEvents.spectatorMap.put(context.getSource().asPlayer(), spectate);
        return 0;
    }
}
