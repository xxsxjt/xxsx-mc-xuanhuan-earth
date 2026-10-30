package com.xxsx.xuanhuanearth;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class SpiritStoneItem extends GuidedXuanhuanItem {
    private static final double CHARGE_BATCH_AEU = 24.0D;
    private static final double RELEASE_BATCH_MANA = 10.0D;

    public SpiritStoneItem(Properties properties, String hintKey) {
        super(properties, hintKey);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            releaseIntoMana(level, player, stack);
        } else {
            chargeFromField(level, player, stack);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    private static void chargeFromField(Level level, Player player, ItemStack stack) {
        SpiritStoneComponent state = SpiritStoneComponent.read(stack);
        ArcanaChunkField.Reading reading = Spirituality.reading(level, player.blockPosition());
        double usableField = SpiritFieldLedger.availableField(reading.value(), reading.depletion());
        double coupling = usableField / SpiritFieldLedger.FIELD_MAX;
        SpiritStoneCharge.ChargeResult result = SpiritStoneCharge.capture(
                state.chargeAeu(), coupling, CHARGE_BATCH_AEU);
        if (result.fieldSpent() <= 0.0D) {
            player.sendSystemMessage(Component.translatable("message.xuanhuan_earth.spirit_stone.no_field")
                    .withStyle(ChatFormatting.YELLOW));
            return;
        }
        stack.set(XuanhuanEarth.SPIRIT_STONE_CHARGE, state.withCharge(result.charge()));
        Spirituality.consumeField(level, player.blockPosition(), result.fieldSpent());
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            XuanhuanEffects.stoneCharge(serverLevel, player.position());
        }
        player.sendSystemMessage(Component.translatable("message.xuanhuan_earth.spirit_stone.charged",
                ArcanaPower.format(result.fieldSpent()), ArcanaPower.format(result.gained()),
                ArcanaPower.format(result.charge()), ArcanaPower.format(SpiritStoneCharge.CAPACITY_AEU),
                ArcanaPower.format(result.loss()), Spirituality.gradeName(reading.value()))
                .withStyle(ChatFormatting.AQUA));
    }

    private static void releaseIntoMana(Level level, Player player, ItemStack stack) {
        SpiritStoneComponent state = SpiritStoneComponent.read(stack);
        double missing = ArcanaPower.getMaxMana(player) - ArcanaPower.getCurrentMana(player);
        if (missing <= 0.0001D) {
            player.sendSystemMessage(Component.translatable("message.xuanhuan_earth.spirit_stone.mana_full")
                    .withStyle(ChatFormatting.YELLOW));
            return;
        }
        SpiritStoneCharge.ReleaseResult result = SpiritStoneCharge.release(
                state.chargeAeu(), Math.min(RELEASE_BATCH_MANA, missing));
        if (result.aeuSpent() <= 0.0D || result.manaRestored() <= 0.0D) {
            player.sendSystemMessage(Component.translatable("message.xuanhuan_earth.spirit_stone.empty")
                    .withStyle(ChatFormatting.YELLOW));
            return;
        }
        double before = ArcanaPower.getCurrentMana(player);
        ArcanaPower.setCurrentMana(player, before + result.manaRestored());
        double restored = ArcanaPower.getCurrentMana(player) - before;
        stack.set(XuanhuanEarth.SPIRIT_STONE_CHARGE, state.withCharge(result.charge()));
        if (player instanceof ServerPlayer serverPlayer) {
            CultivationNetwork.broadcastVisual(serverPlayer, CultivationVisualAction.RECOVERY);
            XuanhuanJourney.complete(serverPlayer, XuanhuanJourney.Milestone.ARTIFACT_USE);
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                XuanhuanEffects.stoneRelease(serverLevel, player.position());
            }
        }
        player.sendSystemMessage(Component.translatable("message.xuanhuan_earth.spirit_stone.released",
                ArcanaPower.format(result.aeuSpent()), ArcanaPower.format(restored),
                ArcanaPower.format(result.charge()), ArcanaPower.format(SpiritStoneCharge.CAPACITY_AEU))
                .withStyle(ChatFormatting.GOLD));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, lines, flag);
        SpiritStoneComponent state = SpiritStoneComponent.read(stack);
        lines.accept(Component.translatable("tooltip.xuanhuan_earth.spirit_stone.matrix")
                .withStyle(ChatFormatting.GRAY));
        lines.accept(Component.translatable("tooltip.xuanhuan_earth.spirit_stone.charge",
                ArcanaPower.format(state.chargeAeu()),
                ArcanaPower.format(SpiritStoneCharge.CAPACITY_AEU),
                Component.translatable("spirit_stone.xuanhuan_earth.grade." + SpiritStoneCharge.grade(state.chargeAeu())))
                .withStyle(ChatFormatting.AQUA));
        lines.accept(Component.translatable("tooltip.xuanhuan_earth.spirit_stone.use")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
