package ru.wintrain.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.phys.AABB;

import java.util.List;

public final class WinTrainCommands {
    public static final String NPC_TAG = "wintrain_npc";
    public static final String TARGET_TAG = "wintrain_target";
    public static final String COMBAT_TAG = "wintrain_combat";

    private WinTrainCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                registerCommands(dispatcher));
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wintrain")
                .then(Commands.literal("spawn")
                        .then(Commands.literal("combat")
                                .executes(context -> spawn(context.getSource(), false)))
                        .then(Commands.literal("target")
                                .executes(context -> spawn(context.getSource(), true))))
                .then(Commands.literal("clear")
                        .executes(context -> clearNearby(context.getSource()))));
    }

    private static int spawn(CommandSourceStack source, boolean target) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.serverLevel();

        double yaw = Math.toRadians(player.getYRot());
        double spawnX = player.getX() - Math.sin(yaw) * 2.5;
        double spawnZ = player.getZ() + Math.cos(yaw) * 2.5;

        Husk npc = EntityType.HUSK.create(level, EntitySpawnReason.COMMAND);
        if (npc == null) {
            source.sendFailure(Component.literal("WinTrain: не удалось создать NPC."));
            return 0;
        }

        npc.moveTo(spawnX, player.getY(), spawnZ, player.getYRot(), 0.0F);
        npc.addTag(NPC_TAG);
        npc.addTag(target ? TARGET_TAG : COMBAT_TAG);
        npc.setCustomName(Component.literal(target ? "WinTrain • Мишень" : "WinTrain • Боец"));
        npc.setCustomNameVisible(true);
        npc.setPersistenceRequired();
        npc.setCanPickUpLoot(false);
        npc.setNoAi(target);
        npc.setSilent(target);

        AttributeInstance maxHealth = npc.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(1024.0);
            npc.setHealth(1024.0F);
        }

        AttributeInstance knockback = npc.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockback != null) {
            knockback.setBaseValue(target ? 1.0 : 0.2);
        }

        if (!level.noCollision(npc)) {
            npc.discard();
            source.sendFailure(Component.literal("WinTrain: перед тобой нет свободного места для NPC."));
            return 0;
        }

        if (!level.addFreshEntity(npc)) {
            source.sendFailure(Component.literal("WinTrain: сервер не смог добавить NPC в мир."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal(target
                ? "WinTrain: тренировочная мишень создана."
                : "WinTrain: боевой NPC создан."), false);
        return 1;
    }

    private static int clearNearby(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.serverLevel();
        AABB area = player.getBoundingBox().inflate(64.0);
        List<Husk> npcs = level.getEntitiesOfClass(Husk.class, area,
                entity -> entity.getTags().contains(NPC_TAG));

        for (Husk npc : npcs) {
            npc.discard();
        }

        int count = npcs.size();
        source.sendSuccess(() -> Component.literal("WinTrain: удалено NPC — " + count + "."), false);
        return count;
    }
}
