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

public final class CrystalTurtleModel extends EntityModel<SpiritBeastRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            XuanhuanEarth.id("crystal_turtle"), "main");

    private final ModelPart head;
    private final ModelPart shell;
    private final ModelPart crystals;
    private final ModelPart tail;
    private final ModelPart frontRight;
    private final ModelPart frontLeft;
    private final ModelPart backRight;
    private final ModelPart backLeft;

    public CrystalTurtleModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.head = root.getChild("head");
        this.shell = root.getChild("shell");
        this.crystals = this.shell.getChild("crystals");
        this.tail = root.getChild("tail");
        this.frontRight = root.getChild("front_right");
        this.frontLeft = root.getChild("front_left");
        this.backRight = root.getChild("back_right");
        this.backLeft = root.getChild("back_left");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.2F, 0.0F, -4.8F, 8.4F, 2.2F, 9.6F),
                PartPose.offset(0.0F, 18.5F, 0.0F));
        PartDefinition shell = root.addOrReplaceChild(
                "shell",
                CubeListBuilder.create()
                        .texOffs(19, 0).addBox(-5.0F, -2.0F, -5.6F, 10.0F, 2.8F, 11.2F)
                        .texOffs(42, 0).addBox(-5.6F, 0.7F, -6.2F, 11.2F, 1.2F, 12.4F, new CubeDeformation(0.04F)),
                PartPose.offset(0.0F, 15.4F, 0.0F));
        PartDefinition shell_crystals = shell.addOrReplaceChild(
                "crystals",
                CubeListBuilder.create()
                        .texOffs(67, 0).addBox(-1.1F, -3.8F, -1.1F, 2.2F, 3.8F, 2.2F),
                PartPose.offset(0.0F, -2.2F, 0.0F));
        shell_crystals.addOrReplaceChild(
                "left_crystal",
                CubeListBuilder.create()
                        .texOffs(72, 0).addBox(-0.7F, -2.4F, -0.7F, 1.4F, 2.4F, 1.4F),
                PartPose.offsetAndRotation(-2.2F, 0.4F, 1.0F, -0.2F, 0.0F, -0.42F));
        shell_crystals.addOrReplaceChild(
                "rear_crystal",
                CubeListBuilder.create()
                        .texOffs(75, 0).addBox(-0.7F, -2.0F, -0.7F, 1.4F, 2.0F, 1.4F),
                PartPose.offsetAndRotation(2.0F, 0.6F, 2.4F, 0.16F, 0.0F, 0.36F));
        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(78, 0).addBox(-2.1F, -2.6F, -3.8F, 4.2F, 5.2F, 5.2F)
                        .texOffs(89, 0).addBox(-1.0F, -0.9F, -4.6F, 2.0F, 1.8F, 1.2F)
                        .texOffs(94, 0).addBox(-0.6F, -3.6F, -1.2F, 1.2F, 1.4F, 2.0F),
                PartPose.offset(0.0F, 14.6F, -7.4F));
        head.addOrReplaceChild(
                "neck",
                CubeListBuilder.create()
                        .texOffs(99, 0).addBox(-2.6F, -2.2F, 0.0F, 5.2F, 4.4F, 4.2F),
                PartPose.offset(0.0F, 2.0F, 1.6F));
        root.addOrReplaceChild(
                "front_right",
                CubeListBuilder.create()
                        .texOffs(110, 0).addBox(-2.4F, -1.2F, -2.2F, 4.8F, 2.6F, 4.4F),
                PartPose.offset(-4.8F, 19.6F, -3.2F));
        root.addOrReplaceChild(
                "front_left",
                CubeListBuilder.create()
                        .texOffs(119, 0).addBox(-2.4F, -1.2F, -2.2F, 4.8F, 2.6F, 4.4F),
                PartPose.offset(4.8F, 19.6F, -3.2F));
        root.addOrReplaceChild(
                "back_right",
                CubeListBuilder.create()
                        .texOffs(0, 14).addBox(-2.4F, -1.2F, -2.2F, 4.8F, 2.6F, 4.4F),
                PartPose.offset(-4.8F, 19.6F, 3.2F));
        root.addOrReplaceChild(
                "back_left",
                CubeListBuilder.create()
                        .texOffs(9, 14).addBox(-2.4F, -1.2F, -2.2F, 4.8F, 2.6F, 4.4F),
                PartPose.offset(4.8F, 19.6F, 3.2F));
        root.addOrReplaceChild(
                "tail",
                CubeListBuilder.create()
                        .texOffs(18, 14).addBox(-0.9F, -0.8F, 0.0F, 1.8F, 1.6F, 2.8F),
                PartPose.offset(0.0F, 19.4F, 5.6F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(SpiritBeastRenderState state) {
        super.setupAnim(state);
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float hurt = state.hurtTime > 0.0F ? Mth.sin(state.hurtTime * 0.9F) : 0.0F;

        this.head.yRot = state.yRot * Mth.DEG_TO_RAD * 0.65F;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD * 0.5F;
        this.head.zRot = hurt * 0.12F;
        this.frontRight.yRot = Mth.cos(walk * 0.58F) * 0.55F * speed;
        this.frontLeft.yRot = Mth.cos(walk * 0.58F + Mth.PI) * 0.55F * speed;
        this.backRight.yRot = this.frontLeft.yRot;
        this.backLeft.yRot = this.frontRight.yRot;

        float pulse = 1.0F + Mth.sin(state.ageInTicks * 0.16F) * (0.025F + state.affinity * 0.045F);
        this.crystals.xScale = pulse;
        this.crystals.yScale = pulse;
        this.crystals.zScale = pulse;
        this.crystals.xRot = Mth.sin(state.ageInTicks * 0.07F) * 0.03F;
        this.shell.yRot = Mth.sin(state.ageInTicks * 0.025F) * 0.012F;
        this.shell.xRot = state.breathPhase * 0.008F;
        this.tail.yRot = state.tailWag * 0.18F;

        float attack = Mth.sin(state.attackProgress * Mth.PI);
        this.head.z -= attack * 1.8F;
        this.head.xRot -= attack * 0.32F;

        if (state.sitting) {
            this.head.z += 2.2F;
            this.head.xRot = 0.42F;
            this.frontRight.yRot = 0.72F;
            this.frontLeft.yRot = -0.72F;
            this.backRight.yRot = -0.62F;
            this.backLeft.yRot = 0.62F;
            this.crystals.yScale *= 0.96F;
        }

        if (state.inWater) {
            float paddle = Mth.sin(state.ageInTicks * 0.3F);
            this.frontRight.yRot = paddle * 0.7F;
            this.frontLeft.yRot = -paddle * 0.7F;
            this.backRight.yRot = -paddle * 0.6F;
            this.backLeft.yRot = paddle * 0.6F;
            this.head.xRot = 0.1F + state.xRot * Mth.DEG_TO_RAD * 0.3F;
        }
    }
}
