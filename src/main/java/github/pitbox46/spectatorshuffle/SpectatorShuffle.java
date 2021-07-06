package github.pitbox46.spectatorshuffle;


import github.pitbox46.spectatorshuffle.commands.ModCommands;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod("spectatorshuffle")
public class SpectatorShuffle {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final Map<Entity, List<ServerPlayerEntity>> SPECTATING_PLAYERS = new HashMap<>();
    static KeyBinding shuffleKey;
    static KeyBinding stopSpectateKey;

    public SpectatorShuffle() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        shuffleKey = new KeyBinding("key.spectatorshuffle.toggle", 89, "key.spectatorshuffle.category");
        stopSpectateKey = new KeyBinding("key.spectatorshuffle.stopspectating", 86, "key.spectatorshuffle.category");
        ClientRegistry.registerKeyBinding(shuffleKey);
        ClientRegistry.registerKeyBinding(stopSpectateKey);
    }

    @SubscribeEvent
    public void onRegisterCommand(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}
