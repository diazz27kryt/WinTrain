package ru.wintrain.training;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import ru.wintrain.command.WinTrainCommands;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ComboTracker {
    private static final long COMBO_TIMEOUT_TICKS = 30L;
    private static final long CPS_WINDOW_TICKS = 20L;
    private static final Map<UUID, PlayerStats> STATS = new HashMap<>();

    private ComboTracker() {
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(ComboTracker::onNpcDamaged);
    }

    private static void onNpcDamaged(LivingEntity entity, DamageSource source,
                                     float baseDamage, float damageTaken, boolean blocked) {
        if (blocked || damageTaken <= 0.0F || !entity.getTags().contains(WinTrainCommands.NPC_TAG)) {
            return;
        }

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        long tick = player.serverLevel().getGameTime();
        PlayerStats stats = STATS.computeIfAbsent(player.getUUID(), ignored -> new PlayerStats());
        stats.recordHit(tick);

        Component message = Component.literal(
                "КОМБО: " + stats.combo
                        + "  |  ЛУЧШЕЕ: " + stats.bestCombo
                        + "  |  CPS: " + stats.getCps()
        );
        player.displayClientMessage(message, true);
    }

    private static final class PlayerStats {
        private final ArrayDeque<Long> recentHits = new ArrayDeque<>();
        private int combo;
        private int bestCombo;
        private long lastHitTick = Long.MIN_VALUE;

        private void recordHit(long tick) {
            if (lastHitTick == Long.MIN_VALUE || tick - lastHitTick > COMBO_TIMEOUT_TICKS) {
                combo = 0;
            }

            combo++;
            bestCombo = Math.max(bestCombo, combo);
            lastHitTick = tick;
            recentHits.addLast(tick);

            while (!recentHits.isEmpty() && tick - recentHits.peekFirst() >= CPS_WINDOW_TICKS) {
                recentHits.removeFirst();
            }
        }

        private int getCps() {
            return recentHits.size();
        }
    }
}
