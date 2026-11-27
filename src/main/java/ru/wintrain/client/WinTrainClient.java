package ru.wintrain.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public final class WinTrainClient implements ClientModInitializer {
    public enum Mode { COMBAT_BOT, STATIONARY_TARGET }

    private static Mode mode = Mode.STATIONARY_TARGET;
    private static KeyMapping openMenu;

    @Override
    public void onInitializeClient() {
        Category category = Category.register(ResourceLocation.fromNamespaceAndPath("wintrain", "main"));
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
        Minecraft.getInstance().player.displayClientMessage(
                Component.literal("WinTrain: " + (mode == Mode.COMBAT_BOT ? "Боевой NPC" : "Тренировочная мишень")),
                true
        );
    }
}
