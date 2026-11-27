package ru.wintrain.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public final class WinTrainClient implements ClientModInitializer {
    public enum Mode { COMBAT_BOT, STATIONARY_TARGET }

    private static final String NPC_TAG = "wintrain_dummy";
    private static Mode mode = Mode.STATIONARY_TARGET;
    private static KeyMapping openMenu;

    @Override
    public void onInitializeClient() {
        Category category = Category.register(Identifier.fromNamespaceAndPath("wintrain", "main"));
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wintrain.open_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.consumeClick()) {
                if (client.options.keyShift.isDown()) {
                    client.setScreen(new ModeScreen());
                }
            }
        });
    }

    public static Mode getMode() {
        return mode;
    }

    public static void setMode(Mode newMode) {
        mode = newMode;
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        if (mode == Mode.COMBAT_BOT) {
            spawnStandingNpc(client);
        } else {
            client.player.displayClientMessage(
                    Component.literal("WinTrain: Тренировочная мишень — пока в разработке"), true);
        }
    }

    private static void spawnStandingNpc(Minecraft client) {
        var integratedServer = client.getSingleplayerServer();
        if (integratedServer == null) {
            client.player.displayClientMessage(
                    Component.literal("WinTrain: NPC пока доступен только в одиночном мире"), true);
            return;
        }

        UUID playerId = client.player.getUUID();
        integratedServer.execute(() -> {
            ServerPlayer player = integratedServer.getPlayerList().getPlayer(playerId);
            if (player == null) return;

            ServerLevel level = player.level();

            level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(64),
                    mob -> mob.getTags().contains(NPC_TAG))
                    .forEach(Entity::discard);

            Mob npc = EntityType.ZOMBIE.create(level, EntitySpawnReason.TRIGGERED);
            if (npc == null) return;

            var look = player.getLookAngle();
            double x = player.getX() + look.x * 2.5;
            double y = player.getY();
            double z = player.getZ() + look.z * 2.5;

            npc.moveTo(x, y, z, player.getYRot(), 0.0F);
            npc.setNoAi(true);
            npc.setPersistenceRequired();
            npc.setCanPickUpLoot(false);
            npc.setSilent(true);
            npc.addTag(NPC_TAG);
            npc.setCustomName(Component.literal("WinTrain NPC"));
            npc.setCustomNameVisible(true);

            level.addFreshEntity(npc);
            player.displayClientMessage(
                    Component.literal("WinTrain: NPC создан — он стоит и отталкивается ударами"), true);
        });
    }
}
