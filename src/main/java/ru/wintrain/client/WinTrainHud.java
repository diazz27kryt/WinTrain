package ru.wintrain.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WinTrainHud {
    private static final Pattern STATS = Pattern.compile(
            "КОМБО:\\s*(\\d+)\\s*\\|\\s*ЛУЧШЕЕ:\\s*(\\d+)\\s*\\|\\s*CPS:\\s*(\\d+)"
    );
    private static int combo;
    private static int best;
    private static int cps;
    private static long lastUpdate;

    private WinTrainHud() {
    }

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("wintrain", "combo_hud"),
                WinTrainHud::render
        );
    }

    public static boolean acceptMessage(String message) {
        Matcher matcher = STATS.matcher(message);
        if (!matcher.find()) {
            return false;
        }

        combo = Integer.parseInt(matcher.group(1));
        best = Integer.parseInt(matcher.group(2));
        cps = Integer.parseInt(matcher.group(3));
        lastUpdate = System.currentTimeMillis();
        return true;
    }

    private static void render(GuiGraphics graphics, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui || lastUpdate == 0L
                || System.currentTimeMillis() - lastUpdate > 3500L) {
            return;
        }

        // Compact signature panel, aligned to the top-right safe edge.
        int width = 178;
        int height = 34;
        int x = graphics.guiWidth() - width - 7;
        int y = 7;

        graphics.fill(x, y, x + width, y + height, 0xD9000000);
        graphics.fill(x, y, x + width, y + 1, 0xFFFFFFFF);
        graphics.fill(x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
        graphics.fill(x, y, x + 1, y + height, 0xFFFFFFFF);
        graphics.fill(x + width - 1, y, x + width, y + height, 0xFFFFFFFF);

        // WinTrain mark and the three compact stat columns.
        graphics.drawString(client.font, "W", x + 9, y + 10, 0xFFFFFFFF, false);
        graphics.fill(x + 32, y + 6, x + 33, y + height - 6, 0xFF888888);

        drawStat(graphics, client, "COMBO", combo, x + 40, y);
        graphics.fill(x + 85, y + 7, x + 86, y + height - 7, 0xFF777777);
        drawStat(graphics, client, "CPS", cps, x + 94, y);
        graphics.fill(x + 132, y + 7, x + 133, y + height - 7, 0xFF777777);
        drawStat(graphics, client, "BEST", best, x + 140, y);

        // Three short signature slashes on the lower-right edge.
        graphics.fill(x + width - 22, y + height - 4, x + width - 17, y + height - 2, 0xFFFFFFFF);
        graphics.fill(x + width - 14, y + height - 4, x + width - 9, y + height - 2, 0xFFFFFFFF);
    }

    private static void drawStat(GuiGraphics graphics, Minecraft client, String label, int value, int x, int y) {
        graphics.drawString(client.font, label, x, y + 6, 0xFFBDBDBD, false);
        String number = Integer.toString(value);
        int textWidth = client.font.width(number);
        graphics.drawString(client.font, number, x + 17 - textWidth / 2, y + 18, 0xFFFFFFFF, false);
    }
}
