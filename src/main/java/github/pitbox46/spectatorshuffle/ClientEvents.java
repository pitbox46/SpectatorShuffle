package github.pitbox46.spectatorshuffle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

import static github.pitbox46.spectatorshuffle.SpectatorShuffle.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "spectatorshuffle")
public class ClientEvents {
    private static boolean shuffle;
    //Previous time in ns
    private static long shuffleTime = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent event) {
        if(event.type == TickEvent.Type.CLIENT && event.phase == TickEvent.Phase.END) {
            if(ServerLifecycleHooks.getCurrentServer() != null && Minecraft.getInstance().playerController != null && Minecraft.getInstance().playerController.isSpectatorMode()) {
                if (shuffleKey.isPressed()) {
                    if(shuffle) {
                        shuffle = false;
                        Minecraft.getInstance().player.sendStatusMessage(new TranslationTextComponent("message.spectatorshuffle.shuffleoff"), true);
                    } else {
                        shuffle = true;
                        shuffleTime = Minecraft.getInstance().world.getGameTime();
                        spectateRandomPlayer();
                    }
                }
                if (shuffle && shuffleTime + 1200 < Minecraft.getInstance().world.getGameTime()) {
                    shuffleTime = Minecraft.getInstance().world.getGameTime();
                    spectateRandomPlayer();
                }
            } else {
                shuffle = false;
            }
        }
    }
    private static void spectateRandomPlayer() {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
        assert player != null;
        assert connection != null;
        List<NetworkPlayerInfo> players = new ArrayList<>(connection.getPlayerInfoMap());
        List<NetworkPlayerInfo> spectatabePlayers = new ArrayList<>();
        players.forEach(p -> {
            if(p != connection.getPlayerInfo(player.getUniqueID()) && (p.getGameType().isCreative() || p.getGameType().isSurvivalOrAdventure()))
                spectatabePlayers.add(p);
        });

        if(spectatabePlayers.size() > 0) {
            NetworkPlayerInfo chosenPlayer = spectatabePlayers.get(player.getRNG().nextInt(spectatabePlayers.size()));
            player.sendChatMessage("/spectate " + chosenPlayer.getGameProfile().getName());
        }
        else {
            player.sendStatusMessage(new TranslationTextComponent("message.spectatorshuffle.nonespectatable"), true);
        }
    }
}
