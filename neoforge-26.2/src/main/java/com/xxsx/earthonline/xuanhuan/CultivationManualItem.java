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
        if (!level.isClientSide()) {
            boolean learned = switch (type) {
                case BIGU -> ArcanaPower.learnBigu(player);
                case BODY_TEMPERING -> ArcanaPower.learnBodyTempering(player);
                case FETAL_BREATH -> ArcanaPower.learnFetalBreath(player);
            };
            int ambientQi = Spirituality.measure(level, player.blockPosition());
            double restored = ArcanaPower.absorbAmbientQi(player, ambientQi);
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
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        lines.accept(Component.translatable("tooltip.earth_online_xuanhuan." + type.id).withStyle(ChatFormatting.GOLD));
        lines.accept(Component.translatable("tooltip.earth_online_xuanhuan." + type.id + ".use").withStyle(ChatFormatting.GRAY));
    }
}
