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
        if (!level.isClientSide()) {
            ArcanaChunkField.Reading reading = Spirituality.reading(level, player.blockPosition());
            boolean learned = ArcanaPower.learnQiGuiding(player);
            long cooldown = ArcanaPower.getQiMeditationCooldownTicks(player, level);
            if (!learned && cooldown > 0L) {
                long seconds = (cooldown + 19L) / 20L;
                player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.qi_manual.cooldown",
                        seconds).withStyle(ChatFormatting.YELLOW));
                player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.arcana.status",
                        ArcanaPower.format(ArcanaPower.getCurrentMana(player)),
                        ArcanaPower.format(ArcanaPower.getMaxMana(player)),
                        ArcanaPower.format(ArcanaPower.getXuanhuanBonus(player)),
                        ArcanaPower.format(ArcanaPower.getMagicBonus(player))).withStyle(ChatFormatting.LIGHT_PURPLE));
                return InteractionResult.SUCCESS_SERVER;
            }

            double restored = ArcanaPower.absorbAmbientQi(player, reading.value());
            if (restored > 0.0D) {
                Spirituality.consume(level, player.blockPosition(), restored);
                ArcanaPower.startQiMeditationCooldown(player, level);
            }
            ArcanaChunkField.Reading after = Spirituality.reading(level, player.blockPosition());
            player.sendSystemMessage(Component.translatable(learned
                    ? "message.earth_online_xuanhuan.qi_manual.learned"
                    : "message.earth_online_xuanhuan.qi_manual.practiced").withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.qi_manual.absorbed",
                    Spirituality.gradeName(reading.value()),
                    reading.value(),
                    ArcanaPower.format(restored)).withStyle(ChatFormatting.AQUA));
            player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.qi_manual.field",
                    after.mainSource(),
                    ArcanaPower.format(after.depletion())).withStyle(ChatFormatting.DARK_AQUA));
            player.sendSystemMessage(Component.translatable("message.earth_online_xuanhuan.arcana.status",
                    ArcanaPower.format(ArcanaPower.getCurrentMana(player)),
                    ArcanaPower.format(ArcanaPower.getMaxMana(player)),
                    ArcanaPower.format(ArcanaPower.getXuanhuanBonus(player)),
                    ArcanaPower.format(ArcanaPower.getMagicBonus(player))).withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        lines.accept(Component.translatable("tooltip.earth_online_xuanhuan.qi_guiding_manual").withStyle(ChatFormatting.GOLD));
        lines.accept(Component.translatable("tooltip.earth_online_xuanhuan.qi_guiding_manual.use").withStyle(ChatFormatting.GRAY));
    }
}
