package com.xxsx.earthonline.xuanhuan;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class CultivationPractice {
    public enum Support {
        FREE(0, 0.72D, 0.72D, "free"),
        CUSHION(16, 1.0D, 1.0D, "cushion");

        private final int fieldBonus;
        private final double efficiency;
        private final double recoveryScale;
        private final String translationSuffix;

        Support(int fieldBonus, double efficiency, double recoveryScale, String translationSuffix) {
            this.fieldBonus = fieldBonus;
            this.efficiency = efficiency;
            this.recoveryScale = recoveryScale;
            this.translationSuffix = translationSuffix;
        }

        int fieldBonus() {
            return fieldBonus;
        }

        double efficiency() {
            return efficiency;
        }

        double recoveryScale() {
            return recoveryScale;
        }

        int effectDuration(int baseTicks) {
            return Math.max(20, (int) Math.round(baseTicks * recoveryScale));
        }
    }

    private CultivationPractice() {
    }

    public static boolean perform(ServerLevel level, BlockPos pos, ServerPlayer player,
                                  Support support, boolean quietOutput) {
        if (ArcanaPower.getCultivationLevel(player) <= 0) {
            if (!quietOutput) {
                player.sendSystemMessage(Component.translatable(
                        "message.earth_online_xuanhuan.practice.requires_qi_guiding")
                        .withStyle(ChatFormatting.RED));
            }
            return false;
        }
        long cooldown = ArcanaPower.getQiMeditationCooldownTicks(player, level);
        if (cooldown > 0L) {
            if (!quietOutput && cooldown > 20L) {
                player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.qi_manual.cooldown",
                        (cooldown + 19L) / 20L).withStyle(ChatFormatting.YELLOW));
            }
            return false;
        }

        CultivationFocus focus = ArcanaPower.getCultivationFocus(player);
        ArcanaChunkField.Reading reading = Spirituality.reading(level, pos);
        int focusedQi = Math.min(100, reading.value() + support.fieldBonus());
        double focusScale = switch (focus) {
            case CIRCULATION -> 1.0D;
            case BODY_TEMPERING -> 0.72D;
            case FETAL_BREATH -> 0.82D;
            case BIGU -> 0.64D;
        };
        int usableQi = Math.max(1, (int) Math.round(focusedQi * focusScale * support.efficiency()));
        double restored = ArcanaPower.absorbAmbientQi(player, usableQi);
        int gainedXp = Math.max(2, (int) Math.round((3.0D + focusedQi * 0.12D) * support.efficiency()));
        ArcanaPower.ProgressResult progress = ArcanaPower.addCultivationExperience(player, focus, gainedXp);
        Spirituality.consume(level, pos, Math.max(1.0D, restored));
        ArcanaPower.startQiMeditationCooldown(player, level);
        CultivationNetwork.broadcastVisual(player, CultivationVisualAction.MEDITATION);

        double recoveryScale = support.recoveryScale();
        EarthHumanCompat.RecoveryReport report;
        switch (focus) {
            case BODY_TEMPERING -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                        support.effectDuration(140), 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,
                        support.effectDuration(120), 0, true, false, true));
                report = EarthHumanCompat.recoverCore(player,
                        (5.5D + restored * 0.08D) * recoveryScale,
                        (0.55D + restored * 0.018D) * recoveryScale);
            }
            case FETAL_BREATH -> {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING,
                        support.effectDuration(140), 0, true, false, true));
                int airCap = player.getMaxAirSupply() + (int) Math.round(ArcanaPower.getBreathCapacityBonus(player));
                player.setAirSupply(Math.min(airCap,
                        player.getAirSupply() + (int) Math.round(120.0D * recoveryScale)));
                report = EarthHumanCompat.recoverBreath(player,
                        (4.0D + restored * 0.06D) * recoveryScale,
                        (0.32D + restored * 0.012D) * recoveryScale);
            }
            case BIGU -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                        support.effectDuration(80), 0, true, false, true));
                var food = player.getFoodData();
                food.setFoodLevel(Math.min(20, food.getFoodLevel() + 1));
                food.setSaturation(Math.min(6.0F,
                        food.getSaturationLevel() + (float) (0.5D * recoveryScale)));
                report = EarthHumanCompat.recoverCore(player,
                        (4.5D + restored * 0.05D) * recoveryScale,
                        (0.20D + restored * 0.008D) * recoveryScale);
            }
            default -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                        support.effectDuration(100), 0, true, false, true));
                report = EarthHumanCompat.recoverCore(player,
                        (3.5D + restored * 0.06D) * recoveryScale,
                        (0.30D + restored * 0.012D) * recoveryScale);
            }
        }

        if (!quietOutput) {
            player.sendSystemMessage(Component.translatable(
                    "message.earth_online_xuanhuan.practice.used." + support.translationSuffix)
                    .withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.practice.result",
                    Spirituality.gradeName(reading.value()),
                    reading.value(),
                    ArcanaPower.format(restored),
                    progress.gainedXp(),
                    progress.level(),
                    progress.atCap() ? "MAX" : progress.xp() + "/" + progress.xpNeeded(),
                    ArcanaPower.format(report.fatigueReduced()),
                    ArcanaPower.format(report.bodyHealed()),
                    Math.round(support.efficiency() * 100.0D)).withStyle(ChatFormatting.AQUA));
        }
        if (progress.leveledUp()) {
            player.sendSystemMessage(Component.translatable(
                    "message.earth_online_xuanhuan.practice.level_up",
                    Component.translatable(focus.titleKey()), progress.level())
                    .withStyle(ChatFormatting.GOLD));
        }
        emitPractice(level, pos, focus, support);
        return true;
    }

    public static void emitFocusChange(ServerLevel level, BlockPos pos, CultivationFocus focus, boolean supported) {
        var particle = switch (focus) {
            case CIRCULATION -> ParticleTypes.END_ROD;
            case BODY_TEMPERING -> ParticleTypes.FLAME;
            case FETAL_BREATH -> ParticleTypes.CLOUD;
            case BIGU -> ParticleTypes.HAPPY_VILLAGER;
        };
        level.sendParticles(particle,
                pos.getX() + 0.5D, pos.getY() + (supported ? 0.55D : 1.05D), pos.getZ() + 0.5D,
                supported ? 14 : 8, 0.42D, supported ? 0.28D : 0.45D, 0.42D, 0.02D);
    }

    private static void emitPractice(ServerLevel level, BlockPos pos, CultivationFocus focus, Support support) {
        var particle = switch (focus) {
            case CIRCULATION -> ParticleTypes.ENCHANT;
            case BODY_TEMPERING -> ParticleTypes.FLAME;
            case FETAL_BREATH -> ParticleTypes.CLOUD;
            case BIGU -> ParticleTypes.HAPPY_VILLAGER;
        };
        boolean supported = support == Support.CUSHION;
        level.sendParticles(particle,
                pos.getX() + 0.5D, pos.getY() + (supported ? 0.55D : 1.0D), pos.getZ() + 0.5D,
                supported ? 12 : 7, 0.38D, supported ? 0.26D : 0.55D, 0.38D, 0.012D);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS,
                supported ? 0.65F : 0.42F, supported ? 1.25F : 1.05F);
    }
}
