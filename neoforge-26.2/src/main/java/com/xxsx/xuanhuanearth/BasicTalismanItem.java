package com.xxsx.xuanhuanearth;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class BasicTalismanItem extends GuidedXuanhuanItem {
    private static final double TRIGGER_MANA_COST = SpellCatalog.WARD_TALISMAN.manaCost();

    public BasicTalismanItem(Properties properties, String hintKey) {
        super(properties, hintKey);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, lines, flag);
        TalismanComponent charge = TalismanComponent.read(stack);
        lines.accept(Component.translatable("tooltip.xuanhuan_earth.basic_talisman.charge",
                ArcanaPower.format(charge.chargeAeu()),
                ArcanaPower.format(TalismanCharge.CAPACITY_AEU))
                .withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.SUCCESS_SERVER;
            }
            if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
                return InteractionResult.SUCCESS_SERVER;
            }
            SpellExecutor.Plan plan = SpellExecutor.plan(serverPlayer, SpellCatalog.wardTalisman(), hand);
            if (!plan.accepted()) {
                player.sendSystemMessage(Component.translatable(
                        "message.xuanhuan_earth.talisman.plan_rejected",
                        plan.errorCode(),
                        plan.message(),
                        ArcanaPower.format(TRIGGER_MANA_COST),
                        ArcanaPower.format(ArcanaPower.getCurrentMana(player)),
                        ArcanaPower.format(ArcanaPower.getMaxMana(player))).withStyle(ChatFormatting.YELLOW));
                return InteractionResult.SUCCESS_SERVER;
            }
            if (!SpellExecutor.commitTalisman(serverLevel, serverPlayer, hand)) {
                player.sendSystemMessage(Component.translatable(
                        "message.xuanhuan_earth.talisman.mana_low",
                        ArcanaPower.format(TRIGGER_MANA_COST),
                        ArcanaPower.format(ArcanaPower.getCurrentMana(player)),
                        ArcanaPower.format(ArcanaPower.getMaxMana(player))).withStyle(ChatFormatting.YELLOW));
                return InteractionResult.SUCCESS_SERVER;
            }
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 160, 0, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 160, 0, true, false, true));
            EarthHumanCompat.RecoveryReport report = EarthHumanCompat.recoverCore(serverPlayer, 0.8D, 0.10D);
            player.sendSystemMessage(Component.translatable("message.xuanhuan_earth.talisman.used",
                    ArcanaPower.format(TRIGGER_MANA_COST),
                    ArcanaPower.format(ArcanaPower.getCurrentMana(player)),
                    ArcanaPower.format(ArcanaPower.getMaxMana(player))).withStyle(ChatFormatting.GOLD));
            if (report.changed()) {
                player.sendSystemMessage(Component.translatable("message.xuanhuan_earth.human_recovery",
                        ArcanaPower.format(report.fatigueReduced()),
                        ArcanaPower.format(report.bodyHealed())).withStyle(ChatFormatting.GREEN));
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }
}
