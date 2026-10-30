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

public final class StonehornGoatModel extends EntityModel<SpiritBeastRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            XuanhuanEarth.id("stonehorn_goat"), "main");

    private final ModelPart head;
    private final ModelPart muzzle;
    private final ModelPart beard;
    private final ModelPart leftHorn;
    private final ModelPart rightHorn;
    private final ModelPart body;
    private final ModelPart armor;
    private final ModelPart tail;
    private final ModelPart[] legs;

    public StonehornGoatModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.head = root.getChild("head");
        this.muzzle = this.head.getChild("muzzle");
        this.beard = this.head.getChild("beard");
        this.leftHorn = this.head.getChild("left_horn");
        this.rightHorn = this.head.getChild("right_horn");
        this.body = root.getChild("body");
        this.armor = root.getChild("armor");
        this.tail = root.getChild("tail");
        this.legs = new ModelPart[]{root.getChild("front_right_leg"), root.getChild("front_left_leg"),
                root.getChild("back_right_leg"), root.getChild("back_left_leg")};
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, -5.0F, -7.0F, 10.0F, 10.0F, 14.0F)
                        .texOffs(29, 0).addBox(-4.4F, -5.6F, -6.4F, 8.8F, 1.0F, 12.8F, new CubeDeformation(0.04F)),
                PartPose.offset(0.0F, 13.0F, 0.0F));
        root.addOrReplaceChild(
                "armor",
                CubeListBuilder.create()
                        .texOffs(54, 0).addBox(-5.6F, -2.4F, -5.0F, 11.2F, 3.0F, 10.0F, new CubeDeformation(0.05F))
                        .texOffs(77, 0).addBox(-3.6F, -3.0F, -2.0F, 7.2F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 13.0F, 0.0F));
        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(92, 0).addBox(-3.6F, -3.6F, -4.4F, 7.2F, 7.2F, 8.0F),
                PartPose.offset(0.0F, 8.0F, -7.0F));
        head.addOrReplaceChild(
                "muzzle",
                CubeListBuilder.create()
                        .texOffs(109, 0).addBox(-2.2F, -1.2F, -3.0F, 4.4F, 3.6F, 3.0F)
                        .texOffs(118, 0).addBox(-1.1F, 0.4F, -4.0F, 2.2F, 1.6F, 1.0F),
                PartPose.offset(0.0F, 1.2F, -4.4F));
        head.addOrReplaceChild(
                "beard",
                CubeListBuilder.create()
                        .texOffs(123, 0).addBox(-1.4F, 0.0F, -1.0F, 2.8F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 2.6F, -3.0F, 0.2F, 0.0F, 0.0F));
        PartDefinition head_left_horn = head.addOrReplaceChild(
                "left_horn",
                CubeListBuilder.create()
                        .texOffs(0, 25).addBox(-1.3F, -4.4F, -1.3F, 2.6F, 4.4F, 2.6F),
                PartPose.offsetAndRotation(2.2F, -2.6F, -0.4F, -0.35F, 0.0F, -0.55F));
        PartDefinition head_left_horn_mid = head_left_horn.addOrReplaceChild(
                "mid",
                CubeListBuilder.create()
                        .texOffs(5, 25).addBox(-1.1F, -3.6F, -1.1F, 2.2F, 3.6F, 2.2F),
                PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.55F, 0.0F, -0.42F));
        head_left_horn_mid.addOrReplaceChild(
                "tip",
                CubeListBuilder.create()
                        .texOffs(10, 25).addBox(-0.8F, -2.8F, -0.8F, 1.6F, 2.8F, 1.6F),
                PartPose.offsetAndRotation(0.0F, -3.2F, 0.0F, -0.55F, 0.0F, -0.38F));
        PartDefinition head_right_horn = head.addOrReplaceChild(
                "right_horn",
                CubeListBuilder.create()
                        .texOffs(13, 25).addBox(-1.3F, -4.4F, -1.3F, 2.6F, 4.4F, 2.6F),
                PartPose.offsetAndRotation(-2.2F, -2.6F, -0.4F, -0.35F, 0.0F, 0.55F));
        PartDefinition head_right_horn_mid = head_right_horn.addOrReplaceChild(
                "mid",
                CubeListBuilder.create()
                        .texOffs(18, 25).addBox(-1.1F, -3.6F, -1.1F, 2.2F, 3.6F, 2.2F),
                PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.55F, 0.0F, 0.42F));
        head_right_horn_mid.addOrReplaceChild(
                "tip",
                CubeListBuilder.create()
                        .texOffs(23, 25).addBox(-0.8F, -2.8F, -0.8F, 1.6F, 2.8F, 1.6F),
                PartPose.offsetAndRotation(0.0F, -3.2F, 0.0F, -0.55F, 0.0F, 0.38F));
        root.addOrReplaceChild(
                "front_right_leg",
                CubeListBuilder.create()
                        .texOffs(26, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F)
                        .texOffs(35, 25).addBox(-2.2F, 6.0F, -2.4F, 4.4F, 1.6F, 4.8F),
                PartPose.offset(-4.0F, 14.0F, -5.5F));
        root.addOrReplaceChild(
                "front_left_leg",
                CubeListBuilder.create()
                        .texOffs(44, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F)
                        .texOffs(53, 25).addBox(-2.2F, 6.0F, -2.4F, 4.4F, 1.6F, 4.8F),
                PartPose.offset(4.0F, 14.0F, -5.5F));
        root.addOrReplaceChild(
                "back_right_leg",
                CubeListBuilder.create()
                        .texOffs(62, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F)
                        .texOffs(71, 25).addBox(-2.2F, 6.0F, -2.4F, 4.4F, 1.6F, 4.8F),
                PartPose.offset(-4.0F, 14.0F, 5.5F));
        root.addOrReplaceChild(
                "back_left_leg",
                CubeListBuilder.create()
                        .texOffs(80, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F)
                        .texOffs(89, 25).addBox(-2.2F, 6.0F, -2.4F, 4.4F, 1.6F, 4.8F),
                PartPose.offset(4.0F, 14.0F, 5.5F));
        root.addOrReplaceChild(
                "tail",
                CubeListBuilder.create()
                        .texOffs(98, 25).addBox(-1.6F, -1.6F, 0.0F, 3.2F, 3.2F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 12.0F, 7.0F, 0.6F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(SpiritBeastRenderState state) {
        super.setupAnim(state);
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float run = state.runAmount;
        float hurt = state.hurtTime > 0.0F ? Mth.sin(state.hurtTime * 0.9F) : 0.0F;
        float leap = state.leapProgress;

        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD + (state.aggressive ? -0.22F : 0.0F);
        this.head.zRot = hurt * 0.16F;

        float stride = Mth.lerp(run, 1.0F, 1.35F);
        this.legs[0].xRot = Mth.cos(walk * 0.58F) * 0.85F * speed * stride;
        this.legs[1].xRot = Mth.cos(walk * 0.58F + Mth.PI) * 0.85F * speed * stride;
        this.legs[2].xRot = this.legs[1].xRot;
        this.legs[3].xRot = this.legs[0].xRot;

        if (leap > 0.05F) {
            this.legs[0].xRot -= leap * 0.4F;
            this.legs[1].xRot -= leap * 0.4F;
            this.legs[2].xRot += leap * 0.35F;
            this.legs[3].xRot += leap * 0.35F;
        }

        this.body.zRot = Mth.sin(state.ageInTicks * 0.035F) * 0.018F + state.breathPhase * 0.01F;
        this.body.xRot = state.breathPhase * 0.012F - leap * 0.1F;
        this.armor.yScale = 1.0F + Mth.sin(state.ageInTicks * 0.12F) * 0.015F * state.affinity;
        this.tail.yRot = Mth.sin(state.ageInTicks * 0.12F) * 0.22F + state.tailWag * 0.1F;

        float attack = Mth.sin(state.attackProgress * Mth.PI);
        this.head.xRot -= attack * 0.9F;
        this.body.xRot += attack * 0.08F;
        this.beard.xRot = attack * 0.3F + Mth.sin(state.ageInTicks * 0.09F) * 0.03F;

        if (state.sitting) {
            this.body.xRot = 0.12F;
            for (ModelPart leg : this.legs) {
                leg.xRot = -1.15F;
            }
            this.head.xRot += 0.18F;
        }

        if (state.inWater) {
            float paddle = Mth.sin(state.ageInTicks * 0.4F);
            for (int i = 0; i < this.legs.length; i++) {
                this.legs[i].xRot = (i % 2 == 0 ? paddle : -paddle) * 0.42F;
            }
            this.body.xRot = 0.06F;
        }
    }
}
