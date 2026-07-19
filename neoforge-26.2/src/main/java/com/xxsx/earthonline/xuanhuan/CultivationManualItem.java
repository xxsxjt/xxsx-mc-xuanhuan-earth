package com.xxsx.earthonline.xuanhuan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (ArcanaPower.getCultivationLevel(player) <= 0) {
            player.sendSystemMessage(Component.translatable(
                    "message.earth_online_xuanhuan.cultivation_manual.requires_qi_guiding")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.SUCCESS_SERVER;
        }
        boolean learned = switch (type) {
            case BIGU -> ArcanaPower.learnBigu(player);
            case BODY_TEMPERING -> ArcanaPower.learnBodyTempering(player);
            case FETAL_BREATH -> ArcanaPower.learnFetalBreath(player);
        };
        CultivationFocus focus = switch (type) {
            case BIGU -> CultivationFocus.BIGU;
            case BODY_TEMPERING -> CultivationFocus.BODY_TEMPERING;
            case FETAL_BREATH -> CultivationFocus.FETAL_BREATH;
        };
        ArcanaPower.setCultivationFocus(player, focus);
        player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan." + type.id
                        + (learned ? ".learned" : ".already_learned"))
                .withStyle(learned ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        sendAdaptationStatus(player);
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            CultivationNetwork.sync(serverPlayer, player.blockPosition(), true);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        lines.accept(Component.translatable("tooltip.earth_online_xuanhuan." + type.id).withStyle(ChatFormatting.GOLD));
        lines.accept(Component.translatable("tooltip.earth_online_xuanhuan." + type.id + ".use").withStyle(ChatFormatting.GRAY));
    }

    static void sendAdaptationStatus(Player player) {
        player.sendSystemMessage(Component.translatable(
                "message.earth_online_xuanhuan.cultivation_manual.body_status",
                Math.round(ArcanaPower.getExhaustionReduction(player) * 100.0D),
                Math.round(ArcanaPower.getCombatDamageReduction(player) * 100.0D),
                Math.round((ArcanaPower.getBreathMultiplier(player) - 1.0D) * 100.0D),
                ArcanaPower.format(ArcanaPower.getMaxMana(player)))
                .withStyle(ChatFormatting.DARK_AQUA));
    }
}
