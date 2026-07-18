package com.xxsx.earthonline.xuanhuan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class QiRecoveryPillItem extends GuidedXuanhuanItem {
    public QiRecoveryPillItem(Properties properties, String hintKey) {
        super(properties, hintKey);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            double before = ArcanaPower.getCurrentMana(player);
            if (before >= ArcanaPower.getMaxMana(player) - 0.0001D && !EarthHumanCompat.canRecover(player)) {
                player.sendSystemMessage(Component.translatable(
                        "message.earth_online_xuanhuan.qi_pill.full",
                        ArcanaPower.format(before),
                        ArcanaPower.format(ArcanaPower.getMaxMana(player))).withStyle(ChatFormatting.YELLOW));
                return InteractionResult.SUCCESS_SERVER;
            }
            ArcanaPower.setCurrentMana(player, before + 35.0D);
            double restored = ArcanaPower.getCurrentMana(player) - before;
            ArcanaPower.recordAction(player, level, "recovery_qi_pill");
            EarthHumanCompat.RecoveryReport report = player instanceof ServerPlayer serverPlayer
                    ? EarthHumanCompat.recoverCore(serverPlayer, 4.0D, 0.16D)
                    : new EarthHumanCompat.RecoveryReport(0.0D, 0.0D);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.qi_pill.used",
                    ArcanaPower.format(restored),
                    ArcanaPower.format(ArcanaPower.getCurrentMana(player)),
                    ArcanaPower.format(ArcanaPower.getMaxMana(player))).withStyle(ChatFormatting.AQUA));
            if (report.changed()) {
                player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.human_recovery",
                        ArcanaPower.format(report.fatigueReduced()),
                        ArcanaPower.format(report.bodyHealed())).withStyle(ChatFormatting.GREEN));
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }
}
