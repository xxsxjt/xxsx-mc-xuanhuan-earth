package com.xxsx.earthonline.xuanhuan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BasicTalismanItem extends GuidedXuanhuanItem {
    private static final double TRIGGER_MANA_COST = 1.0D;

    public BasicTalismanItem(Properties properties, String hintKey) {
        super(properties, hintKey);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            if (!ArcanaPower.trySpendMana(player, TRIGGER_MANA_COST)) {
                player.sendSystemMessage(Component.translatable(
                        "message.earth_online_xuanhuan.talisman.mana_low",
                        ArcanaPower.format(TRIGGER_MANA_COST),
                        ArcanaPower.format(ArcanaPower.getCurrentMana(player)),
                        ArcanaPower.format(ArcanaPower.getMaxMana(player))).withStyle(ChatFormatting.YELLOW));
                return InteractionResult.SUCCESS_SERVER;
            }
            if (player instanceof ServerPlayer serverPlayer) {
                CultivationNetwork.broadcastVisual(serverPlayer, CultivationVisualAction.TALISMAN);
            }
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 160, 0, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 160, 0, true, false, true));
            EarthHumanCompat.RecoveryReport report = player instanceof ServerPlayer serverPlayer
                    ? EarthHumanCompat.recoverCore(serverPlayer, 0.8D, 0.10D)
                    : new EarthHumanCompat.RecoveryReport(0.0D, 0.0D);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.talisman.used",
                    ArcanaPower.format(TRIGGER_MANA_COST),
                    ArcanaPower.format(ArcanaPower.getCurrentMana(player)),
                    ArcanaPower.format(ArcanaPower.getMaxMana(player))).withStyle(ChatFormatting.GOLD));
            if (report.changed()) {
                player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.human_recovery",
                        ArcanaPower.format(report.fatigueReduced()),
                        ArcanaPower.format(report.bodyHealed())).withStyle(ChatFormatting.GREEN));
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }
}
