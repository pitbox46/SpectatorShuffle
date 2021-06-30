package github.pitbox46.spectatorshuffle.mixin;

import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FMLNetworkConstants.class, remap = false)
public interface FMLNetworkConstantsAccessor {
    @Accessor(value = "playChannel")
    static SimpleChannel getPlayChannel() {
        throw new RuntimeException("Mixin failed to load");
    }
}
