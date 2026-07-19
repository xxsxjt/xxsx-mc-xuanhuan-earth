package com.xxsx.xuanhuanearth;

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

public final class XuanhuanHandbookItem extends Item {
    public XuanhuanHandbookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            openClientHandbook();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> lines, TooltipFlag flag) {
        lines.accept(Component.translatable("tooltip.xuanhuan_earth.handbook.use")
                .withStyle(ChatFormatting.GRAY));
        lines.accept(Component.translatable("tooltip.xuanhuan_earth.handbook.scope")
                .withStyle(ChatFormatting.DARK_GREEN));
    }

    private static void openClientHandbook() {
        try {
            Class.forName("com.xxsx.xuanhuanearth.client.XuanhuanEarthClient")
                    .getMethod("openHandbook")
                    .invoke(null);
        } catch (ReflectiveOperationException ignored) {
        }
    }
}
