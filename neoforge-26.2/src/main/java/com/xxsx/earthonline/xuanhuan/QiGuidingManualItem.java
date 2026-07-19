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

public class QiGuidingManualItem extends Item {
    public QiGuidingManualItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        boolean learned = ArcanaPower.learnQiGuiding(player);
        if (learned) {
            player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.qi_manual.learned")
                    .withStyle(ChatFormatting.GREEN));
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                XuanhuanJourney.complete(serverPlayer, XuanhuanJourney.Milestone.INITIATION);
            }
        } else {
            player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.qi_manual.already_learned")
                    .withStyle(ChatFormatting.YELLOW));
        }
        CultivationManualItem.sendAdaptationStatus(player);
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            CultivationNetwork.sync(serverPlayer, player.blockPosition(), true);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        lines.accept(Component.translatable("tooltip.earth_online_xuanhuan.qi_guiding_manual").withStyle(ChatFormatting.GOLD));
        lines.accept(Component.translatable("tooltip.earth_online_xuanhuan.qi_guiding_manual.use")
                .withStyle(ChatFormatting.GRAY));
    }
}
