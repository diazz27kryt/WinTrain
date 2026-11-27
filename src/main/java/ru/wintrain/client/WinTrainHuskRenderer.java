package ru.wintrain.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class WinTrainHuskRenderer extends HuskRenderer {
    private static final Identifier WINTRAIN_SKIN =
            Identifier.fromNamespaceAndPath("wintrain", "textures/entity/wintrain_skin.png");

    public WinTrainHuskRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(ZombieRenderState state) {
        Component nameTag = state.nameTag;
        if (nameTag != null && nameTag.getString().startsWith("WinTrain •")) {
            return WINTRAIN_SKIN;
        }

        return super.getTextureLocation(state);
    }
}
