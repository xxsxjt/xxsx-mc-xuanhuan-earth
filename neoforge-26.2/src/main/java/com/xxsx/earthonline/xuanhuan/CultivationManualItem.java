package com.xxsx.earthonline.xuanhuan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class CultivationManualItem extends Item {
    public enum Type {
        BIGU("bigu_manual"),
        BODY_TEMPERING("body_tempering_manual"),
        FETAL_BREATH("fetal_breath_manual");

        private final String id;

        Type(String id) {
            this.id = id;
        }
    }

    private final Type type;

    public CultivationManualItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            boolean learned = switch (type) {
                case BIGU -> ArcanaPower.learnBigu(player);
                case BODY_TEMPERING -> ArcanaPower.learnBodyTempering(player);
                case FETAL_BREATH -> ArcanaPower.learnFetalBreath(player);
            };
            long cooldown = ArcanaPower.getQiMeditationCooldownTicks(player, level);
            if (!learned && cooldown > 0L) {
                player.sendSystemMessage(Component.translatable(
                        "message.earth_online_xuanhuan.qi_manual.cooldown",
                        (cooldown + 19L) / 20L).withStyle(ChatFormatting.YELLOW));
                return InteractionResult.SUCCESS_SERVER;
            }
            int ambientQi = Spirituality.measure(level, player.blockPosition());
            double restored = ArcanaPower.absorbAmbientQi(player, ambientQi);
            Spirituality.consume(level, player.blockPosition(), Math.max(1.0D, restored));
            ArcanaPower.startQiMeditationCooldown(player, level);
            ArcanaPower.recordAction(player, level, "cultivation_" + type.id);
            EarthHumanCompat.RecoveryReport report = player instanceof ServerPlayer serverPlayer
                    ? recoverHuman(serverPlayer, restored)
                    : new EarthHumanCompat.RecoveryReport(0.0D, 0.0D);
            player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan." + type.id + (learned ? ".learned" : ".practiced"))
                    .withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.cultivation_manual.body_status",
                    ArcanaPower.getBiguLevel(player),
                    ArcanaPower.format(ArcanaPower.getBodyTemperingBonus(player)),
                    ArcanaPower.format(ArcanaPower.getBreathCapacityBonus(player)),
                    ArcanaPower.format(ArcanaPower.getEnduranceBonus(player))).withStyle(ChatFormatting.AQUA));
            player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.qi_manual.absorbed",
                    Spirituality.gradeName(ambientQi),
                    ambientQi,
                    ArcanaPower.format(restored)).withStyle(ChatFormatting.LIGHT_PURPLE));
            if (report.changed()) {
                player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.human_recovery",
                        ArcanaPower.format(report.fatigueReduced()),
                        ArcanaPower.format(report.bodyHealed())).withStyle(ChatFormatting.GREEN));
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    private EarthHumanCompat.RecoveryReport recoverHuman(ServerPlayer player, double restoredMana) {
        return switch (type) {
            case BIGU -> EarthHumanCompat.recover(player, 2.0D + restoredMana * 0.04D, 0.05D,
                    EarthHumanCompat.BodyTarget.TORSO);
            case BODY_TEMPERING -> EarthHumanCompat.recoverCore(player, 2.5D + restoredMana * 0.05D,
                    0.28D + restoredMana * 0.012D);
            case FETAL_BREATH -> EarthHumanCompat.recoverBreath(player, 1.8D + restoredMana * 0.03D,
                    0.20D + restoredMana * 0.010D);
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        lines.accept(Component.translatable("tooltip.earth_online_xuanhuan." + type.id).withStyle(ChatFormatting.GOLD));
        lines.accept(Component.translatable("tooltip.earth_online_xuanhuan." + type.id + ".use").withStyle(ChatFormatting.GRAY));
    }
}
