package com.xxsx.xuanhuanearth;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A pill with a declared effect, used for every pill beyond the basic qi recovery one.
 *
 * <p>Each pill is a small data object rather than its own class: kind, mana restored, route
 * experience granted, effects, and how much of the local field it disturbs. That keeps the
 * alchemy recipe table and the item behaviour in one place.</p>
 */
public class XuanhuanPillItem extends GuidedXuanhuanItem {
    private final Pill pill;

    public XuanhuanPillItem(Properties properties, String hintKey, Pill pill) {
        super(properties, hintKey);
        this.pill = pill;
    }

    public Pill pill() {
        return pill;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = player.getItemInHand(hand);

        boolean manaFull = ArcanaPower.getCurrentMana(player) >= ArcanaPower.getMaxMana(player) - 0.0001D;
        boolean humanFull = !EarthHumanCompat.canRecover(player);
        if (pill.mana() > 0.0D && manaFull && humanFull && pill.effects().length == 0) {
            player.sendSystemMessage(Component.translatable("message.xuanhuan_earth.pill.no_need",
                    ArcanaPower.format(ArcanaPower.getCurrentMana(player)),
                    ArcanaPower.format(ArcanaPower.getMaxMana(player)))
                    .withStyle(ChatFormatting.YELLOW));
            return InteractionResult.SUCCESS_SERVER;
        }

        double before = ArcanaPower.getCurrentMana(player);
        if (pill.mana() > 0.0D) {
            ArcanaPower.setCurrentMana(player, before + pill.mana());
        }
        double restored = ArcanaPower.getCurrentMana(player) - before;

        if (pill.focus() != null && pill.experience() > 0) {
            ArcanaPower.addCultivationExperience(player, pill.focus(), pill.experience());
        }

        for (MobEffectInstance effect : pill.effects()) {
            player.addEffect(new MobEffectInstance(effect));
        }

        if (pill.fatigue() > 0.0D || pill.bodyHeal() > 0.0D) {
            EarthHumanCompat.recoverCore((ServerPlayer) player, pill.fatigue(), pill.bodyHeal());
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            CultivationNetwork.broadcastVisual(serverPlayer, CultivationVisualAction.RECOVERY);
            XuanhuanJourney.complete(serverPlayer, XuanhuanJourney.Milestone.ARTIFACT_USE);
            if (level instanceof ServerLevel serverLevel) {
                XuanhuanEffects.pillBurst(serverLevel, player.position());
            }
        }

        player.sendSystemMessage(Component.translatable(pill.messageKey(),
                ArcanaPower.format(restored),
                ArcanaPower.format(ArcanaPower.getCurrentMana(player)),
                ArcanaPower.format(ArcanaPower.getMaxMana(player)))
                .withStyle(ChatFormatting.AQUA));
        return InteractionResult.SUCCESS_SERVER;
    }

    /**
     * Static definition of one pill.
     *
     * @param id        recipe and translation id
     * @param mana      mana restored immediately
     * @param focus     route that gains experience, or null
     * @param experience route experience granted
     * @param fatigue   Earth Human fatigue relieved
     * @param bodyHeal  Earth Human body healing per part
     * @param effects   potion effects applied on use
     */
    public record Pill(String id, double mana, CultivationFocus focus, int experience,
                       double fatigue, double bodyHeal, MobEffectInstance[] effects) {
        public String messageKey() {
            return "message.xuanhuan_earth.pill." + id;
        }

        public String translationKey() {
            return "item.xuanhuan_earth." + id;
        }

        public String hintKey() {
            return "tooltip.xuanhuan_earth." + id;
        }

        public static MobEffectInstance effect(net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect,
                                               int seconds, int amplifier) {
            return new MobEffectInstance(effect, seconds * 20, amplifier, true, false, true);
        }

        public static MobEffectInstance regeneration(int seconds, int amplifier) {
            return new MobEffectInstance(MobEffects.REGENERATION, seconds * 20, amplifier, true, false, true);
        }
    }
}
