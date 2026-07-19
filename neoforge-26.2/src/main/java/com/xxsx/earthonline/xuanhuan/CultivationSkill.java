package com.xxsx.earthonline.xuanhuan;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class CultivationSkill {
    private CultivationSkill() {
    }

    public static boolean activate(ServerLevel level, ServerPlayer player) {
        CultivationFocus focus = ArcanaPower.getCultivationFocus(player);
        if (!focus.isUnlocked(player)) {
            player.sendSystemMessage(Component.translatable(
                    "message.earth_online_xuanhuan.skill.requires_technique")
                    .withStyle(ChatFormatting.RED));
            return false;
        }
        long remaining = ArcanaPower.getSkillCooldownTicks(player, level);
        if (remaining > 0L) {
            player.sendSystemMessage(Component.translatable(
                    "message.earth_online_xuanhuan.skill.cooldown", (remaining + 19L) / 20L)
                    .withStyle(ChatFormatting.YELLOW));
            return false;
        }

        double cost = switch (focus) {
            case CIRCULATION, BIGU -> 8.0D;
            case FETAL_BREATH -> 10.0D;
            case BODY_TEMPERING -> 12.0D;
        };
        if (!ArcanaPower.trySpendMana(player, cost)) {
            player.sendSystemMessage(Component.translatable(
                    "message.earth_online_xuanhuan.skill.no_mana", ArcanaPower.format(cost))
                    .withStyle(ChatFormatting.RED));
            return false;
        }

        int levelValue = ArcanaPower.getFocusLevel(player, focus);
        switch (focus) {
            case CIRCULATION -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80 + levelValue * 8,
                        levelValue >= 7 ? 1 : 0, true, false, true));
                EarthHumanCompat.recoverCore(player, 1.2D + levelValue * 0.35D, 0.08D + levelValue * 0.02D);
            }
            case BODY_TEMPERING -> {
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 120 + levelValue * 10,
                        levelValue >= 8 ? 1 : 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 140 + levelValue * 10,
                        levelValue >= 6 ? 1 : 0, true, false, true));
            }
            case FETAL_BREATH -> {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 180 + levelValue * 20,
                        0, true, false, true));
                player.setAirSupply(player.getMaxAirSupply()
                        + (int) Math.round(ArcanaPower.getBreathCapacityBonus(player)));
            }
            case BIGU -> {
                player.getFoodData().setFoodLevel(Math.min(20,
                        player.getFoodData().getFoodLevel() + 2 + levelValue / 4));
                player.getFoodData().setSaturation(Math.min(8.0F,
                        player.getFoodData().getSaturationLevel() + 1.0F + levelValue * 0.1F));
            }
        }

        player.swing(InteractionHand.MAIN_HAND, true);
        ArcanaPower.startSkillCooldown(player, level);
        CultivationNetwork.broadcastVisual(player, CultivationVisualAction.forFocus(focus));
        level.sendParticles(switch (focus) {
                    case CIRCULATION -> ParticleTypes.END_ROD;
                    case BODY_TEMPERING -> ParticleTypes.FLAME;
                    case FETAL_BREATH -> ParticleTypes.CLOUD;
                    case BIGU -> ParticleTypes.HAPPY_VILLAGER;
                }, player.getX(), player.getY() + 1.0D, player.getZ(),
                20, 0.65D, 0.85D, 0.65D, 0.025D);
        level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE,
                SoundSource.PLAYERS, 0.8F, 0.9F + focus.id() * 0.08F);
        player.sendSystemMessage(Component.translatable(
                "message.earth_online_xuanhuan.skill.activated." + focus.path(),
                ArcanaPower.format(cost)).withStyle(ChatFormatting.AQUA));
        return true;
    }
}
