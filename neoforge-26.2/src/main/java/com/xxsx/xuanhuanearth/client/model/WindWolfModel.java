package com.xxsx.xuanhuanearth.client.model;

import com.xxsx.xuanhuanearth.XuanhuanEarth;
import com.xxsx.xuanhuanearth.client.renderer.SpiritBeastRenderState;
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
            XuanhuanEarth.id("wind_wolf"), "main");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart maneTop;
    private final ModelPart maneLeft;
    private final ModelPart maneRight;
    private final ModelPart pendant;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart tail;
    private final ModelPart tailMid;
    private final ModelPart tailTip;

    public WindWolfModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        ModelPart mane = root.getChild("mane");
        this.maneTop = mane.getChild("mane_top");
        this.maneLeft = mane.getChild("mane_left");
        this.maneRight = mane.getChild("mane_right");
        this.pendant = root.getChild("collar").getChild("pendant");
        this.frontRightLeg = root.getChild("front_right_leg");
        this.frontLeftLeg = root.getChild("front_left_leg");
        this.backRightLeg = root.getChild("back_right_leg");
        this.backLeftLeg = root.getChild("back_left_leg");
        this.tail = root.getChild("tail");
        this.tailMid = this.tail.getChild("tail_mid");
        this.tailTip = this.tailMid.getChild("tail_tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 24).addBox(-4.0F, -4.0F, -7.0F, 8.0F, 8.0F, 14.0F),
                PartPose.offset(0.0F, 15.0F, 0.0F));

        PartDefinition mane = root.addOrReplaceChild("mane", CubeListBuilder.create()
                        .texOffs(48, 20).addBox(-4.5F, -5.0F, -3.0F, 9.0F, 10.0F, 6.0F,
                                new CubeDeformation(0.12F)),
                PartPose.offset(0.0F, 14.0F, -5.5F));
        mane.addOrReplaceChild("mane_top", CubeListBuilder.create()
                        .texOffs(80, 24).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 9.0F),
                PartPose.offsetAndRotation(0.0F, -4.7F, -0.5F, -0.18F, 0.0F, 0.0F));
        mane.addOrReplaceChild("mane_left", CubeListBuilder.create()
                        .texOffs(104, 24).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 4.0F, 8.0F),
                PartPose.offsetAndRotation(-3.7F, -1.0F, 0.0F, -0.1F, 0.18F, -0.28F));
        mane.addOrReplaceChild("mane_right", CubeListBuilder.create()
                        .texOffs(104, 40).addBox(0.0F, -2.0F, 0.0F, 2.0F, 4.0F, 8.0F),
                PartPose.offsetAndRotation(3.7F, -1.0F, 0.0F, -0.1F, -0.18F, 0.28F));

        root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.5F, -3.5F, -4.5F, 7.0F, 7.0F, 7.0F)
                        .texOffs(30, 0).addBox(-2.0F, -0.4F, -8.5F, 4.0F, 3.0F, 5.0F)
                        .texOffs(48, 0).addBox(-3.1F, -7.8F, -1.8F, 2.0F, 5.0F, 2.0F)
                        .texOffs(58, 0).addBox(1.1F, -7.8F, -1.8F, 2.0F, 5.0F, 2.0F),
                PartPose.offset(0.0F, 11.5F, -7.0F));

        PartDefinition collar = root.addOrReplaceChild("collar", CubeListBuilder.create()
                        .texOffs(80, 0).addBox(-4.5F, -1.0F, -3.5F, 9.0F, 2.0F, 7.0F,
                                new CubeDeformation(0.08F)),
                PartPose.offset(0.0F, 14.0F, -5.5F));
        collar.addOrReplaceChild("pendant", CubeListBuilder.create()
                        .texOffs(104, 0).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 1.0F, -4.0F));

        leg(root, "front_right_leg", 0, -2.7F, -4.7F);
        leg(root, "front_left_leg", 1, 2.7F, -4.7F);
        leg(root, "back_right_leg", 2, -2.7F, 4.7F);
        leg(root, "back_left_leg", 3, 2.7F, 4.7F);

        PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create()
                        .texOffs(0, 84).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 13.5F, 6.3F, 0.45F, 0.0F, 0.0F));
        PartDefinition tailMid = tail.addOrReplaceChild("tail_mid", CubeListBuilder.create()
                        .texOffs(28, 84).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.2F, 0.08F, 0.0F, 0.0F));
        tailMid.addOrReplaceChild("tail_tip", CubeListBuilder.create()
                        .texOffs(60, 84).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.2F, 0.12F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void leg(PartDefinition root, String name, int index, float x, float z) {
        int legU = index * 12;
        int pawU = index * 16;
        PartDefinition leg = root.addOrReplaceChild(name, CubeListBuilder.create().texOffs(legU, 52)
                        .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(x, 16.5F, z));
        leg.addOrReplaceChild("paw", CubeListBuilder.create().texOffs(pawU, 68)
                        .addBox(-1.5F, -0.5F, -2.5F, 3.0F, 2.0F, 4.0F),
                PartPose.offset(0.0F, 5.5F, -0.2F));
    }

    @Override
    public void setupAnim(SpiritBeastRenderState state) {
        super.setupAnim(state);
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float age = state.ageInTicks;

        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD + (state.aggressive ? -0.08F : 0.0F);
        this.frontRightLeg.xRot = Mth.cos(walk * 0.72F) * 1.2F * speed;
        this.frontLeftLeg.xRot = Mth.cos(walk * 0.72F + Mth.PI) * 1.2F * speed;
        this.backRightLeg.xRot = this.frontLeftLeg.xRot;
        this.backLeftLeg.xRot = this.frontRightLeg.xRot;

        float affinityMotion = 0.18F + state.affinity * 0.18F;
        this.maneTop.yRot = Mth.sin(age * 0.07F) * affinityMotion;
        this.maneLeft.yRot = 0.18F + Mth.sin(age * 0.08F + 0.8F) * affinityMotion * 0.7F;
        this.maneRight.yRot = -0.18F - Mth.sin(age * 0.08F + 0.8F) * affinityMotion * 0.7F;
        this.tail.yRot = Mth.sin(age * 0.09F) * (0.24F + affinityMotion);
        this.tailMid.yRot = Mth.sin(age * 0.12F + 0.6F) * (0.3F + affinityMotion);
        this.tailTip.yRot = Mth.sin(age * 0.15F + 1.1F) * (0.36F + affinityMotion);
        float attack = Mth.sin(state.attackProgress * Mth.PI);
        this.body.xRot = state.aggressive ? -0.06F : 0.0F;
        this.head.xRot -= attack * 0.62F;
        this.frontRightLeg.xRot -= attack * 0.38F;
        this.frontLeftLeg.xRot -= attack * 0.38F;
        if (state.sitting) {
            this.body.xRot = 0.16F;
            this.frontRightLeg.xRot = -0.12F;
            this.frontLeftLeg.xRot = -0.12F;
            this.backRightLeg.xRot = -1.05F;
            this.backLeftLeg.xRot = -1.05F;
            this.tail.xRot = 0.95F;
        }

        float pulse = 1.0F + Mth.sin(age * 0.2F) * (0.04F + state.affinity * 0.04F);
        this.pendant.xScale = pulse;
        this.pendant.yScale = pulse;
        this.pendant.zScale = pulse;
    }
}
