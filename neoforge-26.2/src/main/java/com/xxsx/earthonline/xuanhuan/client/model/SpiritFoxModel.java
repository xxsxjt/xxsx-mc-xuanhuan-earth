package com.xxsx.earthonline.xuanhuan.client.model;

import com.xxsx.earthonline.xuanhuan.EarthOnlineXuanhuan;
import com.xxsx.earthonline.xuanhuan.client.renderer.SpiritFoxRenderState;
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
            EarthOnlineXuanhuan.id("spirit_fox"), "main");

    private final ModelPart head;
    private final ModelPart crest;
    private final ModelPart body;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart tail;

    public SpiritFoxModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.head = root.getChild("head");
        this.crest = this.head.getChild("crest");
        this.body = root.getChild("body");
        this.frontRightLeg = root.getChild("front_right_leg");
        this.frontLeftLeg = root.getChild("front_left_leg");
        this.backRightLeg = root.getChild("back_right_leg");
        this.backLeftLeg = root.getChild("back_left_leg");
        this.tail = root.getChild("tail");
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
                        .texOffs(0, 0).addBox(-4.0F, -4.0F, -5.0F, 8.0F, 7.0F, 7.0F)
                        .texOffs(30, 0).addBox(-2.0F, -0.5F, -8.0F, 4.0F, 3.0F, 4.0F)
                        .texOffs(46, 0).addBox(-3.7F, -7.0F, -2.0F, 3.0F, 4.0F, 2.0F)
                        .texOffs(56, 0).addBox(0.7F, -7.0F, -2.0F, 3.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, 12.5F, -6.0F));

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

        root.addOrReplaceChild("tail",
                CubeListBuilder.create()
                        .texOffs(0, 50).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 12.0F)
                        .texOffs(36, 52).addBox(-2.5F, -2.5F, 10.0F, 5.0F, 5.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 14.5F, 5.0F, 0.42F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 80, 80);
    }

    @Override
    public void setupAnim(SpiritFoxRenderState state) {
        super.setupAnim(state);
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float age = state.ageInTicks;

        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        this.body.xRot = Mth.sin(age * 0.055F) * 0.025F;
        this.frontRightLeg.xRot = Mth.cos(walk * 0.6662F) * 1.15F * speed;
        this.frontLeftLeg.xRot = Mth.cos(walk * 0.6662F + Mth.PI) * 1.15F * speed;
        this.backRightLeg.xRot = Mth.cos(walk * 0.6662F + Mth.PI) * 1.05F * speed;
        this.backLeftLeg.xRot = Mth.cos(walk * 0.6662F) * 1.05F * speed;
        this.tail.yRot = Mth.sin(age * 0.085F) * (0.24F + state.spiritAffinity * 0.12F);
        this.tail.xRot = 0.42F + Mth.cos(age * 0.07F) * 0.08F;
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

        float pulse = 1.0F + Mth.sin(age * 0.18F) * (0.05F + state.spiritAffinity * 0.05F);
        this.crest.xScale = pulse;
        this.crest.yScale = pulse;
        this.crest.zScale = pulse;
    }
}
