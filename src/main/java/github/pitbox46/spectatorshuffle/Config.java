package github.pitbox46.spectatorshuffle;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static ForgeConfigSpec SERVER_CONFIG;

    public static final String GENERAL = "general";

    public static ForgeConfigSpec.DoubleValue FORCE_SWAP;

    public static final String SCORE_PER_SEC = "per_second";

    public static ForgeConfigSpec.DoubleValue STANDING;
    public static ForgeConfigSpec.DoubleValue SPRINTING;
    public static ForgeConfigSpec.DoubleValue SNEAKING;
    public static ForgeConfigSpec.DoubleValue SWIMMING;
    public static ForgeConfigSpec.DoubleValue SWINGING;
    public static ForgeConfigSpec.DoubleValue PASSENGER;
    public static ForgeConfigSpec.DoubleValue ELYTRA_FLY;
    public static ForgeConfigSpec.DoubleValue BURNING;
    public static ForgeConfigSpec.DoubleValue EFFECT;

    public static final String SCORE_PER_EVENT = "per_event";

    public static ForgeConfigSpec.DoubleValue CHANGEDIM;
    public static ForgeConfigSpec.DoubleValue XPPICKUP;
    public static ForgeConfigSpec.DoubleValue ITEM_PICKUP;
    public static ForgeConfigSpec.DoubleValue HIT_ENTITY;
    public static ForgeConfigSpec.DoubleValue HIT_PLAYER;
    public static ForgeConfigSpec.DoubleValue HEAL;
    public static ForgeConfigSpec.DoubleValue ATTACKED;
    public static ForgeConfigSpec.DoubleValue ATTACKED_BY_PLAYER;
    public static ForgeConfigSpec.DoubleValue DAMAGED;
    public static ForgeConfigSpec.DoubleValue CRAFT;
    public static ForgeConfigSpec.DoubleValue SMELT;
    public static ForgeConfigSpec.DoubleValue OPEN_CONTAINER;
    public static ForgeConfigSpec.DoubleValue FALL;
    public static ForgeConfigSpec.DoubleValue TELEPORT;
    public static ForgeConfigSpec.DoubleValue BREAK_BLOCK;
    public static ForgeConfigSpec.DoubleValue PLACE_BLOCK;
    public static ForgeConfigSpec.DoubleValue USE_ITEM;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("General Settings").push(GENERAL);

        FORCE_SWAP = SERVER_BUILDER.comment("Threshold for autospectate to forcefully change the spectated player in %")
                .defineInRange("force_swap", 1, 0, Double.MAX_VALUE);

        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Entropy Scores Given Per Second").push(SCORE_PER_SEC);

        STANDING = SERVER_BUILDER.comment("Player standing still")
                .defineInRange("standing", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        SPRINTING = SERVER_BUILDER.comment("Player sprinting")
                .defineInRange("sprinting", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        SNEAKING = SERVER_BUILDER.comment("Player sneaking")
                .defineInRange("sneaking", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        SWIMMING = SERVER_BUILDER.comment("Player swimming")
                .defineInRange("swimming", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        SWINGING = SERVER_BUILDER.comment("Player swinging arm (ie: breaking block)")
                .defineInRange("swinging", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        PASSENGER = SERVER_BUILDER.comment("Player is a passenger (ie: riding minecart)")
                .defineInRange("passenger", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        ELYTRA_FLY = SERVER_BUILDER.comment("Player is elytra flying")
                .defineInRange("elytra_fly", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        BURNING = SERVER_BUILDER.comment("Player is burning")
                .defineInRange("burning", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        EFFECT = SERVER_BUILDER.comment("Player is experiencing an effect (ie: regeneration)")
                .defineInRange("effect", 1, -Double.MAX_VALUE, Double.MAX_VALUE);

        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Entropy Scores Given Per Second").push(SCORE_PER_EVENT);

        CHANGEDIM = SERVER_BUILDER.comment("Player goes to another dimension")
                .defineInRange("change_dim", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        XPPICKUP = SERVER_BUILDER.comment("Player picksup experience orb. Per orb picked up")
                .defineInRange("xp_pickup", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        DAMAGED = SERVER_BUILDER.comment("Player is damaged (any kind and stacks with attacked)")
                .defineInRange("damaged", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        HIT_ENTITY = SERVER_BUILDER.comment("Player hits an entity (non-player)")
                .defineInRange("hit_entity", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        HIT_PLAYER = SERVER_BUILDER.comment("Player hits a player")
                .defineInRange("hit_player", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        ATTACKED = SERVER_BUILDER.comment("Player is attacked by a non-player")
                .defineInRange("attack", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        ATTACKED_BY_PLAYER = SERVER_BUILDER.comment("Player is attacked by a player")
                .defineInRange("attack_player", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        CRAFT = SERVER_BUILDER.comment("Player crafts an item")
                .defineInRange("craft", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        ITEM_PICKUP = SERVER_BUILDER.comment("Player picks up an item")
                .defineInRange("item_pickup", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        SMELT = SERVER_BUILDER.comment("Player smelts an item")
                .defineInRange("smelt", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        OPEN_CONTAINER = SERVER_BUILDER.comment("Player opens a container")
                .defineInRange("open_container", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        FALL = SERVER_BUILDER.comment("Player starts fall")
                .defineInRange("fall", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        HEAL = SERVER_BUILDER.comment("Player is healed")
                .defineInRange("heal", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        TELEPORT = SERVER_BUILDER.comment("Player is teleported")
                .defineInRange("teleport", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        BREAK_BLOCK = SERVER_BUILDER.comment("Player breaks a block")
                .defineInRange("break_block", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        PLACE_BLOCK = SERVER_BUILDER.comment("Player places a block")
                .defineInRange("place_block", 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        USE_ITEM = SERVER_BUILDER.comment("Player uses an item")
                .defineInRange("use_item", 1, -Double.MAX_VALUE, Double.MAX_VALUE);

        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
