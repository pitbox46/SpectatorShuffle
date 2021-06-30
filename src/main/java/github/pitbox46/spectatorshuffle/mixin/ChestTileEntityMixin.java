package github.pitbox46.spectatorshuffle.mixin;

import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestTileEntity.class)
public abstract class ChestTileEntityMixin extends TileEntity {
    public ChestTileEntityMixin(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    @Shadow
    public static int calculatePlayersUsing(World world, LockableTileEntity p_213976_1_, int x, int y, int z) {
        return 0;
    }
    @Shadow protected int numPlayersUsing;

    /**
     * Fixes syncing issue with chest lids
     */
    @Inject(at = @At(value = "RETURN"), method = "onOpenOrClose")
    public void afterOpenOrClose(CallbackInfo ci) {
        this.numPlayersUsing = calculatePlayersUsing(this.world, (ChestTileEntity) (Object) this, this.pos.getX(), this.pos.getY(), this.pos.getZ());
    }
}
