package github.pitbox46.spectatorshuffle;


import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod("spectatorshuffle")
public class SpectatorShuffle {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final Map<Entity, List<ServerPlayerEntity>> SPECTATING_PLAYERS = new HashMap<>();
    private static KeyBinding shuffleKey;
    private static boolean shuffle;
    //Previous time in ns
    private static long shuffleTime = 0;

    public SpectatorShuffle() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        shuffleKey = new KeyBinding("key.spectatorshuffle.toggle", 89, "key.spectatorshuffle.category");
        ClientRegistry.registerKeyBinding(shuffleKey);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent event) {
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
