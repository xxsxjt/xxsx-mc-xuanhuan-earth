package com.xxsx.earthonline.xuanhuan;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class XuanhuanBlock extends Block {
    private final String messageKey;

    public XuanhuanBlock(Properties properties, String messageKey) {
        super(properties);
        this.messageKey = messageKey;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return describe(level, pos, player);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return describe(level, pos, player);
    }

    private InteractionResult describe(Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        var arrayCenter = XuanhuanStructures.findFormalSpiritArrayCenter(level, pos);
        if (arrayCenter.isPresent()) {
            return XuanhuanMachineBlock.openMachineAt(level, arrayCenter.get(), player);
        }
        ArcanaChunkField.Reading reading = Spirituality.reading(level, pos);
        player.sendSystemMessage(Component.translatable(messageKey, Spirituality.gradeName(reading.value()), reading.value())
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.block.field",
                reading.mainSource(),
                ArcanaPower.format(reading.depletion())).withStyle(ChatFormatting.DARK_AQUA));
        return InteractionResult.SUCCESS_SERVER;
    }
}
