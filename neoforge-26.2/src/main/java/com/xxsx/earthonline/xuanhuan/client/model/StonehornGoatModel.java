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

public final class StonehornGoatModel extends EntityModel<SpiritBeastRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            EarthOnlineXuanhuan.id("stonehorn_goat"), "main");
    private final ModelPart head;
    private final ModelPart leftHorn;
    private final ModelPart rightHorn;
    private final ModelPart body;
    private final ModelPart armor;
    private final ModelPart[] legs;

    public StonehornGoatModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        head = root.getChild("head");
        leftHorn = head.getChild("left_horn");
        rightHorn = head.getChild("right_horn");
        body = root.getChild("body");
        armor = root.getChild("armor");
        legs = new ModelPart[]{root.getChild("front_right_leg"), root.getChild("front_left_leg"),
                root.getChild("back_right_leg"), root.getChild("back_left_leg")};
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 30)
                        .addBox(-6.0F, -6.0F, -8.0F, 12.0F, 12.0F, 16.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));
        root.addOrReplaceChild("armor", CubeListBuilder.create()
                        .texOffs(58, 30).addBox(-6.5F, -6.8F, -7.0F, 13.0F, 5.0F, 14.0F, new CubeDeformation(0.28F))
                        .texOffs(64, 52).addBox(-5.6F, -7.8F, -3.0F, 11.2F, 2.0F, 8.0F, new CubeDeformation(0.2F)),
                PartPose.offset(0.0F, 14.0F, 0.0F));
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.5F, -5.0F, -5.0F, 9.0F, 9.0F, 8.0F)
                        .texOffs(34, 0).addBox(-2.5F, 0.0F, -8.0F, 5.0F, 4.0F, 4.0F)
                        .texOffs(0, 18).addBox(-2.0F, 3.0F, -5.0F, 4.0F, 7.0F, 2.0F),
                PartPose.offset(0.0F, 10.0F, -8.0F));
        head.addOrReplaceChild("left_horn", CubeListBuilder.create()
                        .texOffs(54, 0).addBox(-1.0F, -10.0F, -1.0F, 2.0F, 11.0F, 2.0F)
                        .texOffs(64, 0).addBox(-1.0F, -13.0F, 0.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(3.2F, -4.0F, 0.0F, -0.35F, 0.0F, 0.42F));
        head.addOrReplaceChild("right_horn", CubeListBuilder.create()
                        .texOffs(74, 0).addBox(-1.0F, -10.0F, -1.0F, 2.0F, 11.0F, 2.0F)
                        .texOffs(84, 0).addBox(-1.0F, -13.0F, 0.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(-3.2F, -4.0F, 0.0F, -0.35F, 0.0F, -0.42F));
        leg(root, "front_right_leg", 0, 62, -4.0F, -5.5F);
        leg(root, "front_left_leg", 18, 62, 4.0F, -5.5F);
        leg(root, "back_right_leg", 36, 62, -4.0F, 5.5F);
        leg(root, "back_left_leg", 54, 62, 4.0F, 5.5F);
        return LayerDefinition.create(mesh, 128, 96);
    }

    private static void leg(PartDefinition root, String name, int u, int v, float x, float z) {
        root.addOrReplaceChild(name, CubeListBuilder.create().texOffs(u, v)
                        .addBox(-2.2F, 0.0F, -2.2F, 4.4F, 10.0F, 4.4F),
                PartPose.offset(x, 14.0F, z));
    }

    @Override
    public void setupAnim(SpiritBeastRenderState state) {
        super.setupAnim(state);
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        head.xRot = state.xRot * Mth.DEG_TO_RAD + (state.aggressive ? -0.22F : 0.0F);
        legs[0].xRot = Mth.cos(walk * 0.58F) * 0.85F * speed;
        legs[1].xRot = Mth.cos(walk * 0.58F + Mth.PI) * 0.85F * speed;
        legs[2].xRot = legs[1].xRot;
        legs[3].xRot = legs[0].xRot;
        body.zRot = Mth.sin(state.ageInTicks * 0.035F) * 0.018F;
        armor.yScale = 1.0F + Mth.sin(state.ageInTicks * 0.12F) * 0.015F * state.affinity;
        leftHorn.zRot = 0.42F + Mth.sin(state.ageInTicks * 0.08F) * 0.02F;
        rightHorn.zRot = -leftHorn.zRot;
    }
}
