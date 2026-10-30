package com.xxsx.xuanhuanearth.client.model;

import com.xxsx.xuanhuanearth.XuanhuanEarth;
import com.xxsx.xuanhuanearth.client.renderer.SpiritFoxRenderState;
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

public final class SpiritFoxModel extends EntityModel<SpiritFoxRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            XuanhuanEarth.id("spirit_fox"), "main");

    private final ModelPart head;
    private final ModelPart crest;
    private final ModelPart snout;
    private final ModelPart body;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart tail;
    private final ModelPart tailMid;
    private final ModelPart tailTip;
    private final ModelPart[] ears;

    public SpiritFoxModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.head = root.getChild("head");
        this.crest = this.head.getChild("crest");
        this.snout = this.head.getChild("snout");
        this.body = root.getChild("body");
        this.frontRightLeg = root.getChild("front_right_leg");
        this.frontLeftLeg = root.getChild("front_left_leg");
        this.backRightLeg = root.getChild("back_right_leg");
        this.backLeftLeg = root.getChild("back_left_leg");
        this.tail = root.getChild("tail");
        this.tailMid = this.tail.getChild("tail_mid");
        this.tailTip = this.tailMid.getChild("tail_tip");
        this.ears = new ModelPart[]{this.head.getChild("left_ear"), this.head.getChild("right_ear")};
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 18).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 12.0F)
                        .texOffs(40, 22).addBox(-3.5F, -4.6F, -5.0F, 7.0F, 1.0F, 9.0F,
                                new CubeDeformation(0.08F)),
                PartPose.offset(0.0F, 15.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -4.0F, -5.0F, 8.0F, 7.0F, 7.0F),
                PartPose.offset(0.0F, 12.5F, -6.0F));

        head.addOrReplaceChild("snout",
                CubeListBuilder.create()
                        .texOffs(30, 0).addBox(-2.0F, -0.5F, -4.0F, 4.0F, 3.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("left_ear",
                CubeListBuilder.create().texOffs(46, 0)
                        .addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(2.2F, -3.4F, -1.4F, -0.08F, 0.0F, 0.12F));
        head.addOrReplaceChild("right_ear",
                CubeListBuilder.create().texOffs(56, 0)
                        .addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(-2.2F, -3.4F, -1.4F, -0.08F, 0.0F, -0.12F));

        head.addOrReplaceChild("crest",
                CubeListBuilder.create().texOffs(48, 8)
                        .addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, -4.0F, -2.0F));

        root.addOrReplaceChild("front_right_leg",
                CubeListBuilder.create().texOffs(0, 40).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(-2.4F, 18.0F, -4.0F));
        root.addOrReplaceChild("front_left_leg",
                CubeListBuilder.create().texOffs(12, 40).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(2.4F, 18.0F, -4.0F));
        root.addOrReplaceChild("back_right_leg",
                CubeListBuilder.create().texOffs(24, 40).addBox(-1.7F, 0.0F, -1.7F, 3.0F, 6.0F, 4.0F),
                PartPose.offset(-2.4F, 18.0F, 4.0F));
        root.addOrReplaceChild("back_left_leg",
                CubeListBuilder.create().texOffs(38, 40).addBox(-1.3F, 0.0F, -1.7F, 3.0F, 6.0F, 4.0F),
                PartPose.offset(2.4F, 18.0F, 4.0F));

        PartDefinition tail = root.addOrReplaceChild("tail",
                CubeListBuilder.create()
                        .texOffs(0, 50).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, 14.5F, 5.0F, 0.42F, 0.0F, 0.0F));
        PartDefinition tailMid = tail.addOrReplaceChild("tail_mid",
                CubeListBuilder.create()
                        .texOffs(28, 50).addBox(-3.4F, -3.4F, 0.0F, 7.0F, 7.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 7.2F, 0.06F, 0.0F, 0.0F));
        tailMid.addOrReplaceChild("tail_tip",
                CubeListBuilder.create()
                        .texOffs(56, 50).addBox(-2.6F, -2.6F, 0.0F, 5.0F, 5.0F, 6.0F,
                                new CubeDeformation(0.06F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.4F, 0.08F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 80, 80);
    }

    @Override
    public void setupAnim(SpiritFoxRenderState state) {
        super.setupAnim(state);
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float age = state.ageInTicks;
        float run = state.runAmount;
        float hurt = state.hurtTime > 0.0F ? Mth.sin(state.hurtTime * 0.9F) : 0.0F;
        float leap = state.leapProgress;

        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        this.head.zRot = hurt * 0.2F;
        this.body.xRot = Mth.sin(age * 0.055F) * 0.025F + state.breathPhase * 0.012F - leap * 0.14F;

        float stride = Mth.lerp(run, 1.0F, 1.4F);
        this.frontRightLeg.xRot = Mth.cos(walk * 0.6662F) * 1.15F * speed * stride;
        this.frontLeftLeg.xRot = Mth.cos(walk * 0.6662F + Mth.PI) * 1.15F * speed * stride;
        this.backRightLeg.xRot = Mth.cos(walk * 0.6662F + Mth.PI) * 1.05F * speed * stride;
        this.backLeftLeg.xRot = Mth.cos(walk * 0.6662F) * 1.05F * speed * stride;

        if (leap > 0.05F) {
            this.frontRightLeg.xRot -= leap * 0.5F;
            this.frontLeftLeg.xRot -= leap * 0.5F;
            this.backRightLeg.xRot += leap * 0.4F;
            this.backLeftLeg.xRot += leap * 0.4F;
        }

        float wag = state.tailWag;
        this.tail.yRot = Mth.sin(age * 0.085F) * (0.24F + state.spiritAffinity * 0.12F) + wag * 0.10F;
        this.tail.xRot = 0.42F + Mth.cos(age * 0.07F) * 0.08F - run * 0.16F;
        this.tailMid.yRot = Mth.sin(age * 0.11F + 0.5F) * (0.18F + state.spiritAffinity * 0.1F);
        this.tailMid.xRot = 0.06F + Mth.sin(age * 0.09F + 0.4F) * 0.05F;
        this.tailTip.yRot = Mth.sin(age * 0.14F + 1.0F) * (0.22F + state.spiritAffinity * 0.1F);
        this.tailTip.xRot = 0.08F + Mth.sin(age * 0.1F + 0.9F) * 0.06F;

        float attack = Mth.sin(state.attackProgress * Mth.PI);
        this.head.xRot -= attack * 0.55F;
        this.body.xRot -= attack * 0.12F;
        this.frontRightLeg.xRot -= attack * 0.42F;
        this.frontLeftLeg.xRot -= attack * 0.42F;

        if (state.sitting) {
            this.body.xRot = 0.18F;
            this.frontRightLeg.xRot = -0.10F;
            this.frontLeftLeg.xRot = -0.10F;
            this.backRightLeg.xRot = -1.08F;
            this.backLeftLeg.xRot = -1.08F;
            this.tail.xRot = 1.02F;
        }

        if (state.inWater) {
            float paddle = Mth.sin(age * 0.5F);
            this.frontRightLeg.xRot = paddle * 0.5F;
            this.frontLeftLeg.xRot = -paddle * 0.5F;
            this.backRightLeg.xRot = -paddle * 0.45F;
            this.backLeftLeg.xRot = paddle * 0.45F;
            this.body.xRot = 0.05F;
        }

        float earTilt = hurt * 0.6F + (state.aggressive ? 0.2F : 0.0F);
        this.ears[0].xRot = -0.08F - earTilt * 0.55F + Mth.sin(age * 0.13F) * 0.02F;
        this.ears[1].xRot = this.ears[0].xRot;
        this.ears[0].zRot = 0.12F + earTilt * 0.4F;
        this.ears[1].zRot = -0.12F - earTilt * 0.4F;

        this.snout.xRot = state.aggressive ? -0.06F : 0.0F;

        float pulse = 1.0F + Mth.sin(age * 0.18F) * (0.05F + state.spiritAffinity * 0.05F);
        this.crest.xScale = pulse;
        this.crest.yScale = pulse;
        this.crest.zScale = pulse;
    }
}
