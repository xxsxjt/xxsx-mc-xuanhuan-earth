package com.xxsx.xuanhuanearth;

import net.minecraft.world.effect.MobEffects;

/**
 * The pill catalogue.
 *
 * <p>Every pill is one row here: what it restores, which route it advances, and what it
 * costs in materials at the furnace. Keeping them together makes the alchemy ladder readable
 * as a whole instead of being scattered across item classes and recipe tables.</p>
 */
public final class XuanhuanPills {
    /** Fast, cheap field recovery: the everyday pill. */
    public static final XuanhuanPillItem.Pill QI_RECOVERY = new XuanhuanPillItem.Pill(
            "qi_recovery_pill", 35.0D, CultivationFocus.CIRCULATION, 6, 4.0D, 0.16D,
            new net.minecraft.world.effect.MobEffectInstance[0]);

    /** Slower but stronger; also repairs the body. */
    public static final XuanhuanPillItem.Pill FOUNDATION = new XuanhuanPillItem.Pill(
            "foundation_pill", 70.0D, CultivationFocus.CIRCULATION, 18, 10.0D, 0.40D,
            new net.minecraft.world.effect.MobEffectInstance[]{
                    XuanhuanPillItem.Pill.regeneration(20, 0)
            });

    /** Body tempering in a pill: damage resistance and absorption. */
    public static final XuanhuanPillItem.Pill IRONBODY = new XuanhuanPillItem.Pill(
            "ironbody_pill", 25.0D, CultivationFocus.BODY_TEMPERING, 22, 12.0D, 0.55D,
            new net.minecraft.world.effect.MobEffectInstance[]{
                    XuanhuanPillItem.Pill.effect(MobEffects.RESISTANCE, 90, 0),
                    XuanhuanPillItem.Pill.effect(MobEffects.ABSORPTION, 90, 0)
            });

    /** Breath route: water breathing plus a large air refill. */
    public static final XuanhuanPillItem.Pill CLOUDBREATH = new XuanhuanPillItem.Pill(
            "cloudbreath_pill", 25.0D, CultivationFocus.FETAL_BREATH, 22, 8.0D, 0.20D,
            new net.minecraft.world.effect.MobEffectInstance[]{
                    XuanhuanPillItem.Pill.effect(MobEffects.WATER_BREATHING, 180, 0)
            });

    /** Fasting route: suppresses hunger and heals slowly. */
    public static final XuanhuanPillItem.Pill GRAINFAST = new XuanhuanPillItem.Pill(
            "grainfast_pill", 20.0D, CultivationFocus.BIGU, 22, 8.0D, 0.25D,
            new net.minecraft.world.effect.MobEffectInstance[]{
                    XuanhuanPillItem.Pill.effect(MobEffects.SATURATION, 10, 0)
            });

    /** Rare breakthrough pill: big mana, big experience, short regeneration. */
    public static final XuanhuanPillItem.Pill BREAKTHROUGH = new XuanhuanPillItem.Pill(
            "breakthrough_pill", 120.0D, CultivationFocus.CIRCULATION, 45, 20.0D, 0.80D,
            new net.minecraft.world.effect.MobEffectInstance[]{
                    XuanhuanPillItem.Pill.regeneration(30, 1),
                    XuanhuanPillItem.Pill.effect(MobEffects.ABSORPTION, 120, 1)
            });

    /** Night vision and speed: a scout's pill. */
    public static final XuanhuanPillItem.Pill NIGHTSIGHT = new XuanhuanPillItem.Pill(
            "nightsight_pill", 30.0D, null, 0, 5.0D, 0.10D,
            new net.minecraft.world.effect.MobEffectInstance[]{
                    XuanhuanPillItem.Pill.effect(MobEffects.NIGHT_VISION, 240, 0),
                    XuanhuanPillItem.Pill.effect(MobEffects.SPEED, 120, 0)
            });

    public static final XuanhuanPillItem.Pill[] ALL = {
            QI_RECOVERY, FOUNDATION, IRONBODY, CLOUDBREATH, GRAINFAST, BREAKTHROUGH, NIGHTSIGHT
    };

    private XuanhuanPills() {
    }
}
