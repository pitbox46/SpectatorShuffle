package github.pitbox46.spectatorshuffle.mixin;

import github.pitbox46.spectatorshuffle.SpectatorShuffle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.MerchantOffers;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow public int currentWindowId;

    @Shadow private Entity spectatingEntity;

    @Inject(at = @At(value = "FIELD", target = "net/minecraft/entity/player/ServerPlayerEntity.connection:Lnet/minecraft/network/play/ServerPlayNetHandler;"), method = "setSpectatingEntity")
    public void onSetSpectatingEntity(Entity entityToSpectate, CallbackInfo ci) {
        try {
            SpectatorShuffle.SPECTATING_PLAYERS.entrySet().removeIf(entry -> entry.getValue().contains((ServerPlayerEntity) (Object) this));

            if(this.spectatingEntity != (ServerPlayerEntity) (Object) this) {
                SpectatorShuffle.SPECTATING_PLAYERS.putIfAbsent(this.spectatingEntity, new ArrayList<>());
                SpectatorShuffle.SPECTATING_PLAYERS.get(this.spectatingEntity).add((ServerPlayerEntity) (Object) this);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "net/minecraftforge/eventbus/api/IEventBus.post(Lnet/minecraftforge/eventbus/api/Event;)Z", remap = false), method = "openContainer")
    public void onOpenContainer(INamedContainerProvider p_213829_1_, CallbackInfoReturnable<OptionalInt> cir) {
        if(SpectatorShuffle.SPECTATING_PLAYERS.containsKey((ServerPlayerEntity)(Object) this)) {
            for (ServerPlayerEntity spectator : SpectatorShuffle.SPECTATING_PLAYERS.get((ServerPlayerEntity) (Object) this)) {
                if (spectator.openContainer != spectator.container) {
                    spectator.closeScreen();
                }

                spectator.currentWindowId = this.currentWindowId;
                Container container = p_213829_1_.createMenu(spectator.currentWindowId, ((ServerPlayerEntity) (Object) this).inventory, spectator);
                if (container == null) {
                    if (spectator.isSpectator()) {
                        spectator.sendStatusMessage((new TranslationTextComponent("container.spectatorCantOpen")).mergeStyle(TextFormatting.RED), true);
                    }
                } else {
                    spectator.connection.sendPacket(new SOpenWindowPacket(container.windowId, container.getType(), p_213829_1_.getDisplayName()));
                    container.addListener(spectator);
                    spectator.openContainer = container;
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(spectator, spectator.openContainer));
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "openMerchantContainer")
    public void onOpenMerchantContainer(int containerId, MerchantOffers offers, int level, int xp, boolean p_213818_5_, boolean p_213818_6_, CallbackInfo ci) {
        if(SpectatorShuffle.SPECTATING_PLAYERS.containsKey((ServerPlayerEntity)(Object) this)) {
            for (ServerPlayerEntity spectator : SpectatorShuffle.SPECTATING_PLAYERS.get((ServerPlayerEntity) (Object) this)) {
                spectator.currentWindowId = this.currentWindowId;
                spectator.connection.sendPacket(new SMerchantOffersPacket(spectator.currentWindowId, offers, level, xp, p_213818_5_, p_213818_6_));
            }
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/entity/player/ServerPlayerEntity.getNextWindowId()V"), method = "openHorseInventory")
    public void onOpenHorseInventory(AbstractHorseEntity horse, IInventory inventoryIn, CallbackInfo ci) {
        if(SpectatorShuffle.SPECTATING_PLAYERS.containsKey((ServerPlayerEntity)(Object) this)) {
            for (ServerPlayerEntity spectator : SpectatorShuffle.SPECTATING_PLAYERS.get((ServerPlayerEntity) (Object) this)) {
                spectator.currentWindowId = this.currentWindowId;
                spectator.connection.sendPacket(new SOpenHorseWindowPacket(spectator.currentWindowId, inventoryIn.getSizeInventory(), horse.getEntityId()));
                spectator.openContainer = new HorseInventoryContainer(spectator.currentWindowId, ((ServerPlayerEntity) (Object) this).inventory, inventoryIn, horse);
                spectator.openContainer.addListener(spectator);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(spectator, spectator.openContainer));
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "closeContainer")
    public void onCloseContainer(CallbackInfo ci) {
        if(SpectatorShuffle.SPECTATING_PLAYERS.containsKey((ServerPlayerEntity)(Object) this)) {
            for (ServerPlayerEntity spectator : SpectatorShuffle.SPECTATING_PLAYERS.get((ServerPlayerEntity) (Object) this)) {
                if(spectator != (ServerPlayerEntity) (Object) this)
                    spectator.closeScreen();
            }
        }
    }
}
