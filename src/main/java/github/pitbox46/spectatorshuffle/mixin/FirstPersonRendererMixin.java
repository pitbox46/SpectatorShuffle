package github.pitbox46.spectatorshuffle.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import github.pitbox46.spectatorshuffle.mixinaccessors.FirstPersonRendererAccessor;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FirstPersonRenderer.class)
public abstract class FirstPersonRendererMixin implements FirstPersonRendererAccessor {
    @Shadow protected abstract void renderItemInFirstPerson(AbstractClientPlayerEntity player, float partialTicks, float pitch, Hand handIn, float swingProgress, ItemStack stack, float equippedProgress, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn);

    @Override
    public void callRenderItemInFirstPerson(AbstractClientPlayerEntity player, float partialTicks, float pitch, Hand handIn, float swingProgress, ItemStack stack, float equippedProgress, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn) {
        renderItemInFirstPerson(player, partialTicks, pitch, handIn, swingProgress, stack, equippedProgress, matrixStackIn, bufferIn, combinedLightIn);
    }
}
