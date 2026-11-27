package ru.wintrain;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.wintrain.command.WinTrainCommands;
import ru.wintrain.training.ComboTracker;

public final class WinTrain implements ModInitializer {
    public static final String MOD_ID = "wintrain";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        WinTrainCommands.register();
        ComboTracker.register();
        LOGGER.info("WinTrain initialized for Minecraft 1.21.11");
    }
}
