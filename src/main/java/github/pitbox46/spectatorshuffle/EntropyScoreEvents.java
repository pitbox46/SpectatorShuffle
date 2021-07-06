package github.pitbox46.spectatorshuffle;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = "spectatorshuffle")
public class EntropyScoreEvents {
    public static long tick = 0;
    public static final Map<ServerPlayerEntity, EntropyScore> ENTROPY_SCORES = new HashMap<>();
    public static ServerPlayerEntity highestES;

    @SubscribeEvent
    public static void onServerTick(TickEvent event) {
        if (event.type == TickEvent.Type.SERVER && event.phase == TickEvent.Phase.START && tick++ % 20 == 3) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            //Update players
            List<ServerPlayerEntity> spectatablePlayers = server.getPlayerList().getPlayers().stream().filter(p -> !p.isSpectator()).collect(Collectors.toList());
            for (ServerPlayerEntity serverPlayerEntity : spectatablePlayers) {
                ENTROPY_SCORES.putIfAbsent(serverPlayerEntity, new EntropyScore(60));
            }

            Iterator<Entry<ServerPlayerEntity, EntropyScore>> iterator = ENTROPY_SCORES.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<ServerPlayerEntity, EntropyScore> entry = iterator.next();

                if (!spectatablePlayers.contains(entry.getKey())) {
                    iterator.remove();
                    continue;
                }

                if (entry.getValue().size() == 60)
                    entry.getValue().remove(59);

                ServerPlayerEntity playerEntity = entry.getKey();
                Double score = 0D;
                if (playerEntity.isSprinting()) {
                    score += Config.SPRINTING.get();
                }
                if (playerEntity.isSteppingCarefully()) {
                    score += Config.SNEAKING.get();
                }
                if (playerEntity.isSwimming()) {
                    score += Config.SWIMMING.get();
                }
                if (playerEntity.isSwingInProgress) {
                    score += Config.SWINGING.get();
                }
                if (playerEntity.isPassenger()) {
                    score += Config.PASSENGER.get();
                }
                if (playerEntity.isElytraFlying()) {
                    score += Config.ELYTRA_FLY.get();
                }
                if (playerEntity.isBurning()) {
                    score += Config.BURNING.get();
                }
                if (playerEntity.getPositionVec().equals(entry.getValue().position)) {
                    score += Config.STANDING.get();
                }
                entry.getValue().position = playerEntity.getPositionVec();
                score += (long) playerEntity.getActivePotionEffects().size() * Config.EFFECT.get();

                entry.getValue().add(0, score);
            }
            Entry<ServerPlayerEntity, EntropyScore> entry = ENTROPY_SCORES.entrySet().stream().max(Comparator.comparingDouble(o -> o.getValue().getTotal())).orElse(null);
            highestES = entry == null ? null : entry.getKey();
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getPlayer());
        if (scores != null)
            scores.set(0, scores.get(0) + Config.CHANGEDIM.get());
    }

    @SubscribeEvent
    public static void onPlayerXpPickup(PlayerXpEvent.PickupXp event) {
        List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getPlayer());
        if (scores != null)
            scores.set(0, scores.get(0) + Config.CHANGEDIM.get());
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getPlayer());
        if (event.getTarget() instanceof PlayerEntity) {
            if (scores != null)
                scores.set(0, scores.get(0) + Config.HIT_PLAYER.get());
        } else {
            if (scores != null)
                scores.set(0, scores.get(0) + Config.HIT_ENTITY.get());
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getPlayer());
        if (scores != null)
            scores.set(0, scores.get(0) + Config.CRAFT.get());
    }

    @SubscribeEvent
    public static void onItemPickup(PlayerEvent.ItemPickupEvent event) {
        List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getPlayer());
        if (scores != null)
            scores.set(0, scores.get(0) + Config.ITEM_PICKUP.get());
    }

    @SubscribeEvent
    public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
        List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getPlayer());
        if (scores != null)
            scores.set(0, scores.get(0) + Config.SMELT.get());
    }

    @SubscribeEvent
    public static void onPlayerContainerOpen(PlayerContainerEvent.Open event) {
        List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getPlayer());
        if (scores != null)
            scores.set(0, scores.get(0) + Config.OPEN_CONTAINER.get());
    }

    @SubscribeEvent
    public static void onPlayerDamaged(LivingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getEntity());
            if (scores != null)
                scores.set(0, scores.get(0) + Config.DAMAGED.get());
        }
    }

    @SubscribeEvent
    public static void onPlayerFall(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getEntity());
            if (scores != null)
                scores.set(0, scores.get(0) + Config.FALL.get());
        }
    }

    @SubscribeEvent
    public static void onPlayerHeal(LivingHealEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getEntity());
            if (scores != null)
                scores.set(0, scores.get(0) + Config.HEAL.get());
        }
    }

    @SubscribeEvent
    public static void onPlayerAttacked(LivingAttackEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getEntity());
            if (event.getSource().getTrueSource() instanceof PlayerEntity) {
                if (scores != null)
                    scores.set(0, scores.get(0) + Config.ATTACKED_BY_PLAYER.get());
            } else {
                if (scores != null)
                    scores.set(0, scores.get(0) + Config.ATTACKED.get());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTeleport(EntityTeleportEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getEntity());
            if (scores != null)
                scores.set(0, scores.get(0) + Config.TELEPORT.get());
        }
    }

    @SubscribeEvent
    public static void onPlayerUseItem(LivingEntityUseItemEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getEntity());
            if (scores != null)
                scores.set(0, scores.get(0) + Config.USE_ITEM.get());
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getPlayer());
        if (scores != null)
            scores.set(0, scores.get(0) + Config.BREAK_BLOCK.get());
    }

    @SubscribeEvent
    public static void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            List<Double> scores = ENTROPY_SCORES.get((ServerPlayerEntity) event.getEntity());
            if (scores != null)
                scores.set(0, scores.get(0) + Config.PLACE_BLOCK.get());
        }
    }
}
