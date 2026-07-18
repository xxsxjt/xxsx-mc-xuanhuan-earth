package com.xxsx.earthonline.xuanhuan.client.model;

import com.xxsx.earthonline.xuanhuan.EarthOnlineXuanhuan;
import com.xxsx.earthonline.xuanhuan.client.renderer.SpiritBeastRenderState;
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

public final class WindWolfModel extends EntityModel<SpiritBeastRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            EarthOnlineXuanhuan.id("wind_wolf"), "main");
    private final ModelPart head;
    private final ModelPart mane;
    private final ModelPart body;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart tail;
    private final ModelPart tailTip;

    public WindWolfModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        head = root.getChild("head");
        mane = root.getChild("mane");
        body = root.getChild("body");
        frontRightLeg = root.getChild("front_right_leg");
        frontLeftLeg = root.getChild("front_left_leg");
        backRightLeg = root.getChild("back_right_leg");
        backLeftLeg = root.getChild("back_left_leg");
        tail = root.getChild("tail");
        tailTip = tail.getChild("tail_tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 24).addBox(-4.5F, -5.0F, -7.0F, 9.0F, 9.0F, 14.0F)
                        .texOffs(48, 28).addBox(-4.0F, -5.6F, -6.0F, 8.0F, 1.0F, 11.0F, new CubeDeformation(0.12F)),
                PartPose.offset(0.0F, 15.0F, 0.0F));
        root.addOrReplaceChild("mane", CubeListBuilder.create()
                        .texOffs(48, 0).addBox(-5.5F, -6.0F, -3.5F, 11.0F, 12.0F, 7.0F, new CubeDeformation(0.2F)),
                PartPose.offset(0.0F, 14.0F, -5.5F));
        root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -4.0F, -5.0F, 8.0F, 7.0F, 7.0F)
                        .texOffs(30, 0).addBox(-2.2F, -0.5F, -8.5F, 4.4F, 3.0F, 4.0F)
                        .texOffs(0, 15).addBox(-3.8F, -8.0F, -2.5F, 3.0F, 5.0F, 2.0F)
                        .texOffs(11, 15).addBox(0.8F, -8.0F, -2.5F, 3.0F, 5.0F, 2.0F),
                PartPose.offset(0.0F, 11.5F, -7.0F));
        leg(root, "front_right_leg", 0, 50, -2.8F, -4.5F);
        leg(root, "front_left_leg", 14, 50, 2.8F, -4.5F);
        leg(root, "back_right_leg", 28, 50, -2.8F, 4.5F);
        leg(root, "back_left_leg", 42, 50, 2.8F, 4.5F);
        PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create()
                        .texOffs(0, 64).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 13.5F, 6.5F, 0.32F, 0.0F, 0.0F));
        tail.addOrReplaceChild("tail_tip", CubeListBuilder.create()
                        .texOffs(32, 64).addBox(-2.2F, -2.2F, 0.0F, 4.4F, 4.4F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 9.0F, 0.18F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 96, 96);
    }

    private static void leg(PartDefinition root, String name, int u, int v, float x, float z) {
        root.addOrReplaceChild(name, CubeListBuilder.create().texOffs(u, v)
                        .addBox(-1.6F, 0.0F, -1.6F, 3.2F, 7.0F, 3.2F),
                PartPose.offset(x, 17.0F, z));
    }

    @Override
    public void setupAnim(SpiritBeastRenderState state) {
        super.setupAnim(state);
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        head.xRot = state.xRot * Mth.DEG_TO_RAD;
        frontRightLeg.xRot = Mth.cos(walk * 0.72F) * 1.2F * speed;
        frontLeftLeg.xRot = Mth.cos(walk * 0.72F + Mth.PI) * 1.2F * speed;
        backRightLeg.xRot = frontLeftLeg.xRot;
        backLeftLeg.xRot = frontRightLeg.xRot;
        float pulse = 0.12F + state.affinity * 0.12F;
        mane.yRot = Mth.sin(state.ageInTicks * 0.08F) * pulse * 0.25F;
        tail.yRot = Mth.sin(state.ageInTicks * 0.11F) * (0.28F + pulse);
        tailTip.yRot = Mth.sin(state.ageInTicks * 0.14F + 0.7F) * (0.32F + pulse);
        body.xRot = state.aggressive ? -0.08F : 0.0F;
    }
}
