package github.pitbox46.spectatorshuffle.mixin;

import github.pitbox46.spectatorshuffle.ServerEvents;
import net.minecraft.command.CommandSource;
import net.minecraft.command.impl.SpectateCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpectateCommand.class)
public class SpectateCommandMixin {
    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/entity/player/ServerPlayerEntity.setSpectatingEntity(Lnet/minecraft/entity/Entity;)V"), method = "func_229829_a_")
    private static void onSpectateCommand(CommandSource p_229829_0_, Entity p_229829_1_, ServerPlayerEntity p_229829_2_, CallbackInfoReturnable<Integer> cir) {
        ServerEvents.spectatorMap.remove(p_229829_2_);
    }
}
