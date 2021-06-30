package github.pitbox46.spectatorshuffle.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.matrix.MatrixStack;
import github.pitbox46.spectatorshuffle.FirstPersonRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    @Shadow @Final private Minecraft mc;
    @Shadow @Final private LightTexture lightmapTexture;
    @Shadow @Final public FirstPersonRenderer itemRenderer;
    @Shadow @Final private RenderTypeBuffers renderTypeBuffers;

    @Inject(at = @At(value = "INVOKE", target = "com/mojang/blaze3d/matrix/MatrixStack.pop()V"), method = "renderHand")
    public void onRenderHand(MatrixStack matrixStackIn, ActiveRenderInfo activeRenderInfoIn, float partialTicks, CallbackInfo ci) {
        if(this.mc.player != null && this.mc.gameSettings.getPointOfView().func_243192_a() && !this.mc.gameSettings.hideGUI && this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR) {
            if(this.mc.getRenderViewEntity() instanceof AbstractClientPlayerEntity) {
                AbstractClientPlayerEntity spectated = (AbstractClientPlayerEntity) this.mc.getRenderViewEntity();

                this.lightmapTexture.enableLightmap();

                float f = spectated.getSwingProgress(partialTicks);
                Hand hand = MoreObjects.firstNonNull(spectated.swingingHand, Hand.MAIN_HAND);
                float f1 = MathHelper.lerp(partialTicks, spectated.prevRotationPitch, spectated.rotationPitch);
                boolean flag = true;
                boolean flag1 = true;
                if (spectated.isHandActive()) {
                    ItemStack itemstack = spectated.getActiveItemStack();
                    if (itemstack.getItem() instanceof net.minecraft.item.ShootableItem) {
                        flag = spectated.getActiveHand() == Hand.MAIN_HAND;
                        flag1 = !flag;
                    }

                    Hand hand1 = spectated.getActiveHand();
                    if (hand1 == Hand.MAIN_HAND) {
                        ItemStack itemstack1 = spectated.getHeldItemOffhand();
                        if (itemstack1.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack1)) {
                            flag1 = false;
                        }
                    }
                } else {
                    ItemStack itemstack2 = spectated.getHeldItemMainhand();
                    ItemStack itemstack3 = spectated.getHeldItemOffhand();
                    if (itemstack2.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack2)) {
                        flag1 = false;
                    }

                    if (itemstack3.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack3)) {
                        flag = !itemstack2.isEmpty();
                        flag1 = !flag;
                    }
                }

                //matrixStackIn.rotate(Vector3f.XP.rotationDegrees((spectated.getPitch(partialTicks)) * 0.1F));
                //matrixStackIn.rotate(Vector3f.YP.rotationDegrees((spectated.getYaw(partialTicks)) * 0.1F));

                    if(flag) {
                        float f5 = hand == Hand.MAIN_HAND ? f : 0.0F;
                        ((FirstPersonRendererAccessor) this.itemRenderer).callRenderItemInFirstPerson(spectated, partialTicks, f1, Hand.MAIN_HAND, f5, spectated.getHeldItemMainhand(), 0.0F, matrixStackIn, this.renderTypeBuffers.getBufferSource(), this.mc.getRenderManager().getPackedLight(spectated, partialTicks));
                    }
                    if(flag1) {
                        float f5 = hand == Hand.OFF_HAND ? f : 0.0F;
                        ((FirstPersonRendererAccessor) this.itemRenderer).callRenderItemInFirstPerson(spectated, partialTicks, f1, Hand.OFF_HAND, f5, spectated.getHeldItemOffhand(), 0.0F, matrixStackIn, this.renderTypeBuffers.getBufferSource(), this.mc.getRenderManager().getPackedLight(spectated, partialTicks));
                    }
                this.lightmapTexture.disableLightmap();
                this.renderTypeBuffers.getBufferSource().finish();
            }
        }
    }
}
