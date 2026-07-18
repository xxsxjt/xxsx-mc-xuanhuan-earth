package com.xxsx.earthonline.xuanhuan.client.renderer;

import com.xxsx.earthonline.xuanhuan.EarthOnlineXuanhuan;
import com.xxsx.earthonline.xuanhuan.client.model.CultivationSettlerModel;
import com.xxsx.earthonline.xuanhuan.entity.CultivationSettlerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public final class CultivationSettlerRenderer extends MobRenderer<CultivationSettlerEntity,
        CultivationSettlerRenderState, CultivationSettlerModel> {
    private static final Identifier[] TEXTURES = {
            EarthOnlineXuanhuan.id("textures/entity/cultivation_settler_merchant.png"),
            EarthOnlineXuanhuan.id("textures/entity/cultivation_settler_spring_keeper.png"),
            EarthOnlineXuanhuan.id("textures/entity/cultivation_settler_steward.png")
    };

    public CultivationSettlerRenderer(EntityRendererProvider.Context context) {
        super(context, new CultivationSettlerModel(context.bakeLayer(CultivationSettlerModel.LAYER_LOCATION)), 0.48F);
    }

    @Override
    public CultivationSettlerRenderState createRenderState() {
        return new CultivationSettlerRenderState();
    }

    @Override
    public void extractRenderState(CultivationSettlerEntity entity, CultivationSettlerRenderState state,
                                   float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.role = entity.getRole().id();
        state.trading = entity.isTrading();
    }

    @Override
    public Identifier getTextureLocation(CultivationSettlerRenderState state) {
        return TEXTURES[Math.max(0, Math.min(TEXTURES.length - 1, state.role))];
    }
}
