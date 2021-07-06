package github.pitbox46.spectatorshuffle;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static github.pitbox46.spectatorshuffle.SpectatorShuffle.shuffleKey;
import static github.pitbox46.spectatorshuffle.SpectatorShuffle.stopSpectateKey;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "spectatorshuffle")
public class ClientEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onClientTickEvent(TickEvent event) {
        if(event.type == TickEvent.Type.CLIENT) {
            if(shuffleKey.isPressed()) {
                Minecraft.getInstance().player.sendChatMessage("/spectatorshuffle shuffle");
            }
            if(stopSpectateKey.isPressed()) {
                Minecraft.getInstance().player.sendChatMessage("/spectatorshuffle stop");
            }
        }
    }
}
