package github.pitbox46.spectatorshuffle.mixin;

import github.pitbox46.spectatorshuffle.SpectatorShuffle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = FMLPlayMessages.OpenContainer.class, remap = false)
public class OpenContainerMixin {
    @Inject(at = @At(value = "HEAD", remap = false), method = "handle", cancellable = true)
    private static void onHandle(FMLPlayMessages.OpenContainer msg, Supplier<NetworkEvent.Context> ctx, CallbackInfo ci) {
        try {
            ctx.get().enqueueWork(() -> {
                ScreenManager.getScreenFactory(msg.getType(), Minecraft.getInstance(), msg.getWindowId(), msg.getName())
                        .ifPresent(f -> {
                            Container c = msg.getType().create(msg.getWindowId(), Minecraft.getInstance().player.inventory, msg.getAdditionalData());
                            @SuppressWarnings("unchecked")
                            Screen s = ((ScreenManager.IScreenFactory<Container, ?>) f).create(c, Minecraft.getInstance().player.inventory, msg.getName());
                            Minecraft.getInstance().player.openContainer = ((IHasContainer<?>) s).getContainer();
                            Minecraft.getInstance().displayGuiScreen(s);
                        });
            });
        } catch (Exception e) {
            SpectatorShuffle.LOGGER.warn("Container could not be opened\n{} {}", msg.getType().getRegistryName(), msg.getName());
            Minecraft.getInstance().player.openContainer = Minecraft.getInstance().player.container;
        }
        ctx.get().setPacketHandled(true);
        ci.cancel();
    }
}
