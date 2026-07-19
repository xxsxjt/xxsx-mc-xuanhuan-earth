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
    private final ModelPart frontRight;
    private final ModelPart frontLeft;
    private final ModelPart backRight;
    private final ModelPart backLeft;

    public CrystalTurtleModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        head = root.getChild("head");
        shell = root.getChild("shell");
        crystals = shell.getChild("crystals");
        frontRight = root.getChild("front_right");
        frontLeft = root.getChild("front_left");
        backRight = root.getChild("back_right");
        backLeft = root.getChild("back_left");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 24)
                        .addBox(-6.0F, -2.5F, -7.0F, 12.0F, 5.0F, 14.0F),
                PartPose.offset(0.0F, 19.0F, 0.0F));
        PartDefinition shell = root.addOrReplaceChild("shell", CubeListBuilder.create()
                        .texOffs(0, 44).addBox(-7.0F, -5.0F, -7.5F, 14.0F, 6.0F, 15.0F, new CubeDeformation(0.24F)),
                PartPose.offset(0.0F, 17.0F, 0.0F));
        PartDefinition crystals = shell.addOrReplaceChild("crystals", CubeListBuilder.create(),
                PartPose.offset(0.0F, -4.0F, 0.0F));
        crystals.addOrReplaceChild("main_crystal", CubeListBuilder.create()
                        .texOffs(60, 0).addBox(-1.5F, -8.0F, -1.5F, 3.0F, 8.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -0.6F, -0.08F, 0.0F, 0.06F));
        crystals.addOrReplaceChild("left_crystal", CubeListBuilder.create()
                        .texOffs(72, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(-3.2F, 0.5F, 0.8F, -0.22F, 0.0F, -0.34F));
        crystals.addOrReplaceChild("rear_crystal", CubeListBuilder.create()
                        .texOffs(82, 0).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(2.8F, 0.8F, 2.1F, 0.18F, 0.0F, 0.30F));
        root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -2.5F, -5.0F, 6.0F, 5.0F, 6.0F),
                PartPose.offset(0.0F, 19.0F, -8.0F));
        limb(root, "front_right", 54, 30, -6.0F, -5.0F);
        limb(root, "front_left", 70, 30, 6.0F, -5.0F);
        limb(root, "back_right", 54, 44, -6.0F, 5.0F);
        limb(root, "back_left", 70, 44, 6.0F, 5.0F);
        return LayerDefinition.create(mesh, 96, 80);
    }

    private static void limb(PartDefinition root, String name, int u, int v, float x, float z) {
        root.addOrReplaceChild(name, CubeListBuilder.create().texOffs(u, v)
                        .addBox(-2.5F, -1.5F, -3.0F, 5.0F, 3.0F, 6.0F),
                PartPose.offset(x, 21.0F, z));
    }

    @Override
    public void setupAnim(SpiritBeastRenderState state) {
        super.setupAnim(state);
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        head.yRot = state.yRot * Mth.DEG_TO_RAD * 0.65F;
        head.xRot = state.xRot * Mth.DEG_TO_RAD * 0.5F;
        frontRight.yRot = Mth.cos(walk * 0.58F) * 0.55F * speed;
        frontLeft.yRot = Mth.cos(walk * 0.58F + Mth.PI) * 0.55F * speed;
        backRight.yRot = frontLeft.yRot;
        backLeft.yRot = frontRight.yRot;
        float pulse = 1.0F + Mth.sin(state.ageInTicks * 0.16F) * (0.025F + state.affinity * 0.045F);
        crystals.xScale = pulse;
        crystals.yScale = pulse;
        crystals.zScale = pulse;
        shell.yRot = Mth.sin(state.ageInTicks * 0.025F) * 0.012F;
        float attack = Mth.sin(state.attackProgress * Mth.PI);
        head.z -= attack * 1.8F;
        head.xRot -= attack * 0.32F;
        if (state.sitting) {
            head.z += 2.2F;
            head.xRot = 0.42F;
            frontRight.yRot = 0.72F;
            frontLeft.yRot = -0.72F;
            backRight.yRot = -0.62F;
            backLeft.yRot = 0.62F;
            crystals.yScale *= 0.96F;
        }
    }
}
