package com.xxsx.xuanhuanearth.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xxsx.xuanhuanearth.XuanhuanEarth;
import com.xxsx.xuanhuanearth.client.model.SpiritFoxModel;
import com.xxsx.xuanhuanearth.entity.SpiritFoxEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public final class SpiritFoxRenderer extends MobRenderer<SpiritFoxEntity, SpiritFoxRenderState, SpiritFoxModel> {
    private static final Identifier TEXTURE = XuanhuanEarth.id("textures/entity/spirit_fox.png");

    public SpiritFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiritFoxModel(context.bakeLayer(SpiritFoxModel.LAYER_LOCATION)), 0.46F);
    }

    @Override
    public SpiritFoxRenderState createRenderState() {
        return new SpiritFoxRenderState();
    }

    @Override
    public void extractRenderState(SpiritFoxEntity entity, SpiritFoxRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.spiritAffinity = entity.getAffinity() / (float) com.xxsx.xuanhuanearth.entity.ContractableSpiritBeastEntity.MAX_AFFINITY;
        state.attackProgress = entity.getAttackAnim(partialTick);
        state.sitting = entity.isInSittingPose();
    }

    @Override
    public Identifier getTextureLocation(SpiritFoxRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void scale(SpiritFoxRenderState state, PoseStack poseStack) {
        if (state.isBaby) {
            poseStack.scale(0.68F, 0.68F, 0.68F);
        }
    }
}
