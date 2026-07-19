package com.xxsx.earthonline.xuanhuan.client;

import com.xxsx.earthonline.xuanhuan.CultivationVisualAction;
import com.xxsx.earthonline.xuanhuan.CultivationVisualPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import java.util.HashMap;
import java.util.Map;

public final class CultivationPlayerAnimations {
    private static final Map<Integer, ActiveAction> ACTIVE = new HashMap<>();
    private static ClientLevel activeLevel;

    private CultivationPlayerAnimations() {
    }

    public static void start(CultivationVisualPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        ensureLevel(minecraft.level);
        CultivationVisualAction action = CultivationVisualAction.byId(payload.actionId());
        ACTIVE.put(payload.entityId(), new ActiveAction(action, minecraft.level.getGameTime()));
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            ACTIVE.clear();
            activeLevel = null;
            return;
        }
        ensureLevel(minecraft.level);
        long now = minecraft.level.getGameTime();
        ACTIVE.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }

    public static void renderPlayer(RenderPlayerEvent.Pre<?> event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        ActiveAction active = ACTIVE.get(event.getRenderState().id);
        if (active == null) {
            return;
        }
        float progress = active.progress(minecraft.level.getGameTime(), event.getPartialTick());
        if (progress < 0.0F || progress >= 1.0F) {
            return;
        }
        apply(event.getRenderer().getModel(), active.action(), progress);
    }

    private static void apply(PlayerModel model, CultivationVisualAction action, float progress) {
        float pulse = Mth.sin(progress * Mth.PI);
        switch (action) {
            case MEDITATION -> {
                model.rightArm.xRot = -0.95F - pulse * 0.18F;
                model.leftArm.xRot = -0.95F - pulse * 0.18F;
                model.rightArm.yRot = -0.34F;
                model.leftArm.yRot = 0.34F;
                model.rightArm.zRot = 0.18F;
                model.leftArm.zRot = -0.18F;
                model.body.xRot += 0.06F * pulse;
            }
            case RECOVERY -> {
                model.rightArm.xRot = -1.28F + pulse * 0.16F;
                model.leftArm.xRot = -1.28F + pulse * 0.16F;
                model.rightArm.yRot = -0.42F;
                model.leftArm.yRot = 0.42F;
                model.head.xRot += 0.10F * pulse;
            }
            case CIRCULATION -> {
                model.rightArm.xRot = -1.05F - pulse * 0.42F;
                model.leftArm.xRot = -1.05F - pulse * 0.42F;
                model.rightArm.yRot = -0.58F + progress * 0.36F;
                model.leftArm.yRot = 0.58F - progress * 0.36F;
            }
            case BODY_TEMPERING -> {
                model.rightArm.xRot = -1.18F;
                model.leftArm.xRot = -1.18F;
                model.rightArm.zRot = 0.72F - pulse * 0.18F;
                model.leftArm.zRot = -0.72F + pulse * 0.18F;
                model.body.xRot -= 0.08F * pulse;
            }
            case FETAL_BREATH -> {
                model.rightArm.xRot = -0.78F - pulse * 0.22F;
                model.leftArm.xRot = -0.78F - pulse * 0.22F;
                model.rightArm.yRot = -0.28F;
                model.leftArm.yRot = 0.28F;
                model.head.xRot += 0.16F * pulse;
            }
            case BIGU -> {
                model.rightArm.xRot = -0.72F;
                model.leftArm.xRot = -0.72F;
                model.rightArm.yRot = -0.50F + pulse * 0.08F;
                model.leftArm.yRot = 0.50F - pulse * 0.08F;
                model.body.xRot += 0.04F * pulse;
            }
            case TALISMAN -> {
                model.rightArm.xRot = -1.65F + progress * 0.75F;
                model.rightArm.yRot = -0.36F + progress * 0.72F;
                model.rightArm.zRot = 0.18F + pulse * 0.34F;
                model.leftArm.xRot = -0.55F;
                model.leftArm.yRot = 0.34F;
            }
        }
        copyPose(model.rightArm, model.rightSleeve);
        copyPose(model.leftArm, model.leftSleeve);
        copyPose(model.body, model.jacket);
    }

    private static void copyPose(ModelPart source, ModelPart target) {
        target.x = source.x;
        target.y = source.y;
        target.z = source.z;
        target.xRot = source.xRot;
        target.yRot = source.yRot;
        target.zRot = source.zRot;
        target.xScale = source.xScale;
        target.yScale = source.yScale;
        target.zScale = source.zScale;
    }

    private static void ensureLevel(ClientLevel level) {
        if (activeLevel != level) {
            ACTIVE.clear();
            activeLevel = level;
        }
    }

    private record ActiveAction(CultivationVisualAction action, long startTick) {
        boolean isExpired(long now) {
            return now - startTick >= action.durationTicks();
        }

        float progress(long now, float partialTick) {
            return (now + partialTick - startTick) / action.durationTicks();
        }
    }
}
