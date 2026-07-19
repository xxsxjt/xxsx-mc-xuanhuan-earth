package com.xxsx.xuanhuanearth.client.model;

import com.xxsx.xuanhuanearth.XuanhuanEarth;
import com.xxsx.xuanhuanearth.client.renderer.SpiritBeastRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

public final class EmberCraneModel extends EntityModel<SpiritBeastRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            XuanhuanEarth.id("ember_crane"), "main");
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart tail;

    public EmberCraneModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        body = root.getChild("body");
        neck = root.getChild("neck");
        head = neck.getChild("head");
        leftWing = root.getChild("left_wing");
        rightWing = root.getChild("right_wing");
        leftLeg = root.getChild("left_leg");
        rightLeg = root.getChild("right_leg");
        tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 24)
                        .addBox(-4.0F, -5.0F, -5.0F, 8.0F, 10.0F, 11.0F),
                PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, -0.12F, 0.0F, 0.0F));
        PartDefinition neck = root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.0F, -13.0F, -2.0F, 4.0F, 14.0F, 4.0F),
                PartPose.offset(0.0F, 12.0F, -4.0F));
        neck.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(18, 0).addBox(-2.5F, -4.0F, -3.0F, 5.0F, 5.0F, 5.0F)
                        .texOffs(38, 0).addBox(-1.0F, -1.0F, -8.0F, 2.0F, 2.0F, 6.0F)
                        .texOffs(50, 0).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, -12.0F, 0.0F));
        PartDefinition leftWing = root.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 48)
                        .addBox(0.0F, -2.0F, -4.0F, 2.0F, 7.0F, 10.0F),
                PartPose.offsetAndRotation(3.5F, 10.0F, 0.0F, 0.0F, 0.0F, -0.18F));
        addWingFeathers(leftWing, true);
        PartDefinition rightWing = root.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(26, 48)
                        .addBox(-2.0F, -2.0F, -4.0F, 2.0F, 7.0F, 10.0F),
                PartPose.offsetAndRotation(-3.5F, 10.0F, 0.0F, 0.0F, 0.0F, 0.18F));
        addWingFeathers(rightWing, false);

        PartDefinition leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(80, 92)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 10.0F, 1.0F),
                PartPose.offset(1.7F, 13.0F, 0.0F));
        leftLeg.addOrReplaceChild("foot", CubeListBuilder.create().texOffs(96, 100)
                        .addBox(-1.0F, 0.0F, -3.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 10.0F, 0.0F));
        PartDefinition rightLeg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(88, 92)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 10.0F, 1.0F),
                PartPose.offset(-1.7F, 13.0F, 0.0F));
        rightLeg.addOrReplaceChild("foot", CubeListBuilder.create().texOffs(110, 100)
                        .addBox(-1.0F, 0.0F, -3.0F, 2.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 10.0F, 0.0F));

        PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 92)
                        .addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, 11.0F, 5.0F, 0.42F, 0.0F, 0.0F));
        tail.addOrReplaceChild("left_tail_feather", CubeListBuilder.create().texOffs(28, 92)
                        .addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 9.0F),
                PartPose.offsetAndRotation(2.0F, 0.2F, 0.5F, 0.08F, 0.18F, 0.0F));
        tail.addOrReplaceChild("right_tail_feather", CubeListBuilder.create().texOffs(52, 92)
                        .addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 9.0F),
                PartPose.offsetAndRotation(-2.0F, 0.2F, 0.5F, 0.08F, -0.18F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void addWingFeathers(PartDefinition wing, boolean left) {
        int[] u = left ? new int[]{0, 22, 44} : new int[]{66, 88, 108};
        float side = left ? 1.0F : -1.0F;
        for (int i = 0; i < 3; i++) {
            wing.addOrReplaceChild("feather_" + i, CubeListBuilder.create().texOffs(u[i], 72)
                            .addBox(left ? 0.0F : -1.0F, -1.0F, -1.0F, 1.0F, 3.0F, 9.0F),
                    PartPose.offsetAndRotation(side * 1.7F, 2.2F + i * 1.35F, -2.2F + i * 1.8F,
                            0.04F + i * 0.12F, 0.0F, side * (-0.08F - i * 0.05F)));
        }
    }

    @Override
    public void setupAnim(SpiritBeastRenderState state) {
        super.setupAnim(state);
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        neck.yRot = state.yRot * Mth.DEG_TO_RAD * 0.75F;
        neck.xRot = state.xRot * Mth.DEG_TO_RAD * 0.45F;
        head.xRot = Mth.sin(state.ageInTicks * 0.06F) * 0.025F;
        leftLeg.xRot = Mth.cos(walk * 0.68F) * 0.82F * speed;
        rightLeg.xRot = Mth.cos(walk * 0.68F + Mth.PI) * 0.82F * speed;
        float attack = Mth.sin(state.attackProgress * Mth.PI);
        float wingBeat = Mth.sin(state.ageInTicks * (state.aggressive ? 0.32F : 0.09F));
        leftWing.zRot = -0.12F - wingBeat * (state.aggressive ? 0.5F : 0.06F) - attack * 0.72F;
        rightWing.zRot = -leftWing.zRot;
        neck.xRot -= attack * 0.48F;
        tail.yRot = Mth.sin(state.ageInTicks * 0.07F) * 0.08F;
        body.y = 13.0F + Mth.sin(state.ageInTicks * 0.08F) * 0.06F;
        if (state.sitting) {
            leftLeg.xRot = -1.25F;
            rightLeg.xRot = -1.25F;
            leftWing.zRot = -0.08F;
            rightWing.zRot = 0.08F;
            body.y += 1.2F;
        }
    }
}
