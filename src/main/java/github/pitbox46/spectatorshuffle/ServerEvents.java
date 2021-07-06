package github.pitbox46.spectatorshuffle;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = "spectatorshuffle")
public class ServerEvents {
    public static final Map<ServerPlayerEntity, Spectate> spectatorMap = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent event) {
        if(event.type == TickEvent.Type.SERVER && event.phase == TickEvent.Phase.END) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            Iterator<Map.Entry<ServerPlayerEntity, Spectate>> iterator = spectatorMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ServerPlayerEntity, Spectate> entry = iterator.next();
                ServerPlayerEntity player = entry.getKey();
                Spectate spectate = entry.getValue();

                if (!entry.getKey().isSpectator()) {
                    iterator.remove();
                    continue;
                }
                switch (spectate.mode) {
                    case SHUFFLE: {
                        if (spectate.time + 1200 < player.getServerWorld().getGameTime()) {
                            spectate.time = player.getServerWorld().getGameTime();
                            spectateRandomPlayer(server, player);
                        }
                        if (player.getServerWorld().getGameTime() % 20 == 0) {
                            if (spectate.spectated != null) {
                                updateSpectate(player, spectate.spectated);
                            } else {
                                iterator.remove();
                                continue;
                            }
                        }
                        break;
                    }
                    case FOCUS: {
                        if (player.getServerWorld().getGameTime() % 20 == 1) {
                            if (spectate.spectated != null) {
                                updateSpectate(player, spectate.spectated);
                            } else {
                                iterator.remove();
                                continue;
                            }
                        }
                        break;
                    }
                    case AUTO: {
                        if (spectate.time + 1200 < player.getServerWorld().getGameTime()) {
                            spectate.time = player.getServerWorld().getGameTime();
                            autoSpectate(player);
                        } else if(player.getServerWorld().getGameTime() % 20 == 2 && !EntropyScoreEvents.ENTROPY_SCORES.containsKey(spectate.spectated)) {
                            autoSpectate(player);
                        } else if(player.getServerWorld().getGameTime() % 20 == 2 && EntropyScoreEvents.highestES != spectate.spectated && EntropyScoreEvents.ENTROPY_SCORES.get(EntropyScoreEvents.highestES).getTotal() / EntropyScoreEvents.ENTROPY_SCORES.get(spectate.spectated).getTotal() > Config.FORCE_SWAP.get()) {
                            autoSpectate(player);
                        }
                        if (player.getServerWorld().getGameTime() % 20 == 2) {
                            if (spectate.spectated != null) {
                                updateSpectate(player, spectate.spectated);
                            } else {
                                iterator.remove();
                                continue;
                            }
                        }
                        break;
                    }
                    default: {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private static void spectateRandomPlayer(MinecraftServer server, ServerPlayerEntity player) {
        if(spectatorMap.get(player) == null || spectatorMap.get(player).mode != Spectate.Mode.SHUFFLE)
            return;
        List<ServerPlayerEntity> spectatabePlayers = server.getPlayerList().getPlayers().stream().filter(p -> !p.isSpectator() && p != player).collect(Collectors.toList());

        if(spectatabePlayers.size() > 0) {
            ServerPlayerEntity spectatedEntity = spectatabePlayers.get(player.getRNG().nextInt(spectatabePlayers.size()));
            spectatorMap.get(player).spectated = spectatedEntity;

            player.setSpectatingEntity(null);
            player.teleport(spectatedEntity.getServerWorld(), spectatedEntity.getPosX(), spectatedEntity.getPosY(), spectatedEntity.getPosZ(), spectatedEntity.cameraYaw, spectatedEntity.rotationPitch);
            player.setSpectatingEntity(spectatedEntity);
            player.sendStatusMessage(new StringTextComponent("Now spectating: " + spectatedEntity.getGameProfile().getName()).mergeStyle(TextFormatting.GREEN), false);
            player.sendStatusMessage(new StringTextComponent("Now spectating: " + spectatedEntity.getGameProfile().getName()).mergeStyle(TextFormatting.GREEN), true);
        }
        else {
            spectatorMap.get(player).spectated = null;
            player.sendStatusMessage(new StringTextComponent("Could not find anyone to spectate. Trying again in 60 seconds.").mergeStyle(TextFormatting.GREEN), false);
            player.sendStatusMessage(new StringTextComponent("Could not find anyone to spectate. Trying again in 60 seconds.").mergeStyle(TextFormatting.GREEN), true);
        }
    }

    private static void autoSpectate(ServerPlayerEntity player) {
        if (EntropyScoreEvents.ENTROPY_SCORES.size() > 0 && EntropyScoreEvents.highestES != null) {
            ServerPlayerEntity spectated = spectatorMap.get(player).spectated = EntropyScoreEvents.highestES;

            player.setSpectatingEntity(null);
            player.teleport(spectated.getServerWorld(), spectated.getPosX(), spectated.getPosY(), spectated.getPosZ(), spectated.cameraYaw, spectated.rotationPitch);
            player.setSpectatingEntity(spectated);
            player.sendStatusMessage(new StringTextComponent("Now spectating: " + spectated.getGameProfile().getName()).mergeStyle(TextFormatting.GREEN), false);
            player.sendStatusMessage(new StringTextComponent("Now spectating: " + spectated.getGameProfile().getName()).mergeStyle(TextFormatting.GREEN), true);
        } else {
            player.sendStatusMessage(new StringTextComponent("Could not find anyone to spectate. Trying again in 60 seconds.").mergeStyle(TextFormatting.GREEN), false);
            player.sendStatusMessage(new StringTextComponent("Could not find anyone to spectate. Trying again in 60 seconds.").mergeStyle(TextFormatting.GREEN), true);
        }
    }

    private static void updateSpectate(ServerPlayerEntity player, ServerPlayerEntity spectated) {
        player.setSpectatingEntity(null);
        player.teleport(spectated.getServerWorld(), spectated.getPosX(), spectated.getPosY(), spectated.getPosZ(), spectated.cameraYaw, spectated.rotationPitch);
        player.setSpectatingEntity(spectated);
    }
}
