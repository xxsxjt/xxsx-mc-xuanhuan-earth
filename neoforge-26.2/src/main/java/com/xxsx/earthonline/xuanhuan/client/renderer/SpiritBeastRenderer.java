package com.xxsx.earthonline.xuanhuan.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xxsx.earthonline.xuanhuan.entity.ContractableSpiritBeastEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public final class SpiritBeastRenderer<T extends ContractableSpiritBeastEntity,
        M extends EntityModel<SpiritBeastRenderState>> extends MobRenderer<T, SpiritBeastRenderState, M> {
    private final Identifier texture;
    private final float scale;

    public SpiritBeastRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer,
                               Function<ModelPart, M> modelFactory, Identifier texture,
                               float shadowRadius, float scale) {
        super(context, modelFactory.apply(context.bakeLayer(layer)), shadowRadius);
        this.texture = texture;
        this.scale = scale;
    }

    @Override
    public SpiritBeastRenderState createRenderState() {
        return new SpiritBeastRenderState();
    }

    @Override
    public void extractRenderState(T entity, SpiritBeastRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.affinity = entity.getAffinity() / (float) ContractableSpiritBeastEntity.MAX_AFFINITY;
        state.companionMode = entity.getCompanionMode().id();
        state.aggressive = entity.getTarget() != null;
        state.attackProgress = entity.getAttackAnim(partialTick);
        state.sitting = entity.isInSittingPose();
    }

    @Override
    public Identifier getTextureLocation(SpiritBeastRenderState state) {
        return texture;
    }

    @Override
    protected void scale(SpiritBeastRenderState state, PoseStack poseStack) {
        float resolved = state.isBaby ? scale * 0.62F : scale;
        poseStack.scale(resolved, resolved, resolved);
    }
}
