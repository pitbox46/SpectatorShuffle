package github.pitbox46.spectatorshuffle.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.spectatorshuffle.EntropyScore;
import github.pitbox46.spectatorshuffle.EntropyScoreEvents;
import github.pitbox46.spectatorshuffle.ServerEvents;
import github.pitbox46.spectatorshuffle.Spectate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class CommandTop10 implements Command<CommandSource> {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final CommandTop10 CMD = new CommandTop10();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("top10")
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
        IFormattableTextComponent msg = new StringTextComponent("");
        msg.appendString("-----TOP 10 Entropy Scores-----").appendString("\n");
        List<Map.Entry<ServerPlayerEntity,EntropyScore>> sortedList = EntropyScoreEvents.ENTROPY_SCORES.entrySet().stream().sorted(Comparator.comparingDouble(o -> o.getValue().getTotal())).collect(Collectors.toList());
        for(int i = sortedList.size() - 1; i >= 0 && i > sortedList.size() - 11; i--) {
            msg.appendString(sortedList.get(i).getKey().getGameProfile().getName() + " | " + sortedList.get(i).getValue().getTotal()).appendString("\n");
        }
        context.getSource().asPlayer().sendStatusMessage(msg.mergeStyle(TextFormatting.GREEN), false);
        return 0;
    }
}
