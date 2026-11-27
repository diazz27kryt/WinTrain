package ru.wintrain.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ModeScreen extends Screen {
    public ModeScreen() {
        super(Component.literal("WinTrain"));
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;
        int left = centerX - 112;
        int buttonWidth = 224;

        addRenderableWidget(Button.builder(Component.literal("Боевой NPC"), button ->
                WinTrainClient.setMode(WinTrainClient.Mode.COMBAT_BOT)
        ).bounds(left, centerY - 43, buttonWidth, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Тренировочная мишень"), button ->
                WinTrainClient.setMode(WinTrainClient.Mode.STATIONARY_TARGET)
        ).bounds(left, centerY - 17, buttonWidth, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Призвать NPC"), button -> {
            String type = WinTrainClient.getMode() == WinTrainClient.Mode.COMBAT_BOT ? "combat" : "target";
            WinTrainClient.sendCommand("wintrain spawn " + type);
            onClose();
        }).bounds(left, centerY + 13, buttonWidth, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Удалить NPC рядом"), button -> {
            WinTrainClient.sendCommand("wintrain clear");
            onClose();
        }).bounds(left, centerY + 39, buttonWidth, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, Component.literal("WINTRAIN"), width / 2, height / 2 - 83, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal("Выбери режим и призови NPC"), width / 2, height / 2 - 66, 0xB8B8B8);

        String selected = WinTrainClient.getMode() == WinTrainClient.Mode.COMBAT_BOT
                ? "Сейчас выбран: Боевой NPC"
                : "Сейчас выбрана: Тренировочная мишень";
        graphics.drawCenteredString(font, Component.literal(selected), width / 2, height / 2 + 68, 0xD8D8D8);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }
}
