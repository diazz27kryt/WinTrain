package ru.wintrain.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class WinTrainClient implements ClientModInitializer {
    public enum Mode {
        COMBAT_BOT,
        STATIONARY_TARGET
    }

    private static Mode mode = Mode.STATIONARY_TARGET;
    private static KeyMapping openMenu;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("wintrain", "main")
        );
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.wintrain.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.consumeClick()) {
                if (client.player != null && client.options.keyShift.isDown()) {
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
        if (client.player != null) {
            client.player.displayClientMessage(
                    Component.literal("WinTrain: выбран режим — "
                            + (mode == Mode.COMBAT_BOT ? "Боевой NPC" : "Тренировочная мишень")),
                    true
            );
        }
    }

    public static void sendCommand(String command) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.player.connection != null) {
            client.player.connection.sendCommand(command);
        }
    }
}
