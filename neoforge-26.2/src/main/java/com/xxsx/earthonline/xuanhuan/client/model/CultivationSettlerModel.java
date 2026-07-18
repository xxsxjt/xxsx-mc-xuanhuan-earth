package com.xxsx.earthonline.xuanhuan.client.model;

import com.xxsx.earthonline.xuanhuan.EarthOnlineXuanhuan;
import com.xxsx.earthonline.xuanhuan.client.renderer.CultivationSettlerRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

public final class CultivationSettlerModel extends EntityModel<CultivationSettlerRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            EarthOnlineXuanhuan.id("cultivation_settler"), "main");
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart backpack;

    public CultivationSettlerModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        head = root.getChild("head");
        body = root.getChild("body");
        rightArm = root.getChild("right_arm");
        leftArm = root.getChild("left_arm");
        rightLeg = root.getChild("right_leg");
        leftLeg = root.getChild("left_leg");
        backpack = root.getChild("backpack");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                        .texOffs(32, 0).addBox(-4.5F, -8.5F, -4.5F, 9.0F, 2.0F, 9.0F, new CubeDeformation(0.12F)),
                PartPose.offset(0.0F, 6.0F, 0.0F));
        root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(16, 18).addBox(-4.0F, 0.0F, -2.5F, 8.0F, 12.0F, 5.0F)
                        .texOffs(16, 36).addBox(-5.0F, 7.0F, -3.0F, 10.0F, 7.0F, 6.0F, new CubeDeformation(0.08F)),
                PartPose.offset(0.0F, 6.0F, 0.0F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 18)
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 13.0F, 4.0F),
                PartPose.offset(-5.0F, 8.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(44, 18)
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 13.0F, 4.0F),
                PartPose.offset(5.0F, 8.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 36)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(-2.0F, 18.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(48, 36)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(2.0F, 18.0F, 0.0F));
        root.addOrReplaceChild("backpack", CubeListBuilder.create().texOffs(0, 50)
                        .addBox(-4.5F, -5.0F, 0.0F, 9.0F, 10.0F, 4.0F),
                PartPose.offset(0.0F, 12.0F, 2.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CultivationSettlerRenderState state) {
        super.setupAnim(state);
        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        head.xRot = state.xRot * Mth.DEG_TO_RAD;
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        rightLeg.xRot = Mth.cos(walk * 0.6662F) * 1.0F * speed;
        leftLeg.xRot = Mth.cos(walk * 0.6662F + Mth.PI) * 1.0F * speed;
        if (state.trading) {
            rightArm.xRot = -0.72F;
            leftArm.xRot = -0.72F;
            rightArm.yRot = -0.28F;
            leftArm.yRot = 0.28F;
        } else {
            rightArm.xRot = leftLeg.xRot * 0.65F;
            leftArm.xRot = rightLeg.xRot * 0.65F;
            rightArm.yRot = 0.0F;
            leftArm.yRot = 0.0F;
        }
        body.yRot = Mth.sin(state.ageInTicks * 0.03F) * 0.015F;
        backpack.visible = state.role == 0;
    }
}
