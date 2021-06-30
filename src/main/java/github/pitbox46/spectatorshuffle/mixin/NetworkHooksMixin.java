package github.pitbox46.spectatorshuffle.mixin;

import github.pitbox46.spectatorshuffle.SpectatorShuffle;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

@Mixin(value = NetworkHooks.class)
public class NetworkHooksMixin {
    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/inventory/container/INamedContainerProvider.getDisplayName()Lnet/minecraft/util/text/ITextComponent;"), method = "openGui(Lnet/minecraft/entity/player/ServerPlayerEntity;Lnet/minecraft/inventory/container/INamedContainerProvider;Ljava/util/function/Consumer;)V", locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void onOpenGui(ServerPlayerEntity player, INamedContainerProvider containerSupplier, Consumer extraDataWriter, CallbackInfo ci, int openContainerId, PacketBuffer extraData, PacketBuffer output, Container c, ContainerType type) {
        if(SpectatorShuffle.SPECTATING_PLAYERS.containsKey(player)) {
            for (ServerPlayerEntity spectator : SpectatorShuffle.SPECTATING_PLAYERS.get(player)) {
                spectator.closeContainer();
                spectator.currentWindowId = player.currentWindowId;
                try {
                    Class<FMLPlayMessages.OpenContainer> clazz = FMLPlayMessages.OpenContainer.class;
                    Constructor<FMLPlayMessages.OpenContainer> constructor = clazz.getDeclaredConstructor(ContainerType.class, int.class, ITextComponent.class, PacketBuffer.class);
                    FMLPlayMessages.OpenContainer message = constructor.newInstance(type, openContainerId, containerSupplier.getDisplayName(), new PacketBuffer(output.copy()));
                    FMLNetworkConstantsAccessor.getPlayChannel().sendTo(message, spectator.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
                spectator.openContainer = c;
                c.addListener(spectator);
                MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(spectator, c));
            }
        }
    }
}
