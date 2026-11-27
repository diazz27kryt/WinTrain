package ru.wintrain.client;

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
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        addRenderableWidget(Button.builder(Component.literal("Боевой NPC"), button -> {
            WinTrainClient.setMode(WinTrainClient.Mode.COMBAT_BOT);
            onClose();
        }).bounds(centerX - 100, centerY - 24, 200, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Тренировочная мишень"), button -> {
            WinTrainClient.setMode(WinTrainClient.Mode.STATIONARY_TARGET);
            onClose();
        }).bounds(centerX - 100, centerY + 4, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, Component.literal("WINTRAIN"), width / 2, height / 2 - 62, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal("Выбери режим тренировки"), width / 2, height / 2 - 44, 0xB8B8B8);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
