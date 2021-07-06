package github.pitbox46.spectatorshuffle;

import net.minecraft.entity.player.ServerPlayerEntity;

public class Spectate {
    public Mode mode;
    public long time;
    public ServerPlayerEntity spectated;
    public Spectate(Mode mode) {
        this.mode = mode;
        time = 0;
        spectated = null;
    }

    public enum Mode {
        SHUFFLE,
        FOCUS,
        AUTO
    }
}
