package com.xxsx.earthonline.xuanhuan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Locale;

public final class ArcanaPower {
    public static final String CURRENT_MANA = "earth_online_arcana.current_mana";
    public static final String BASE_MANA = "earth_online_arcana.base_mana";
    public static final String XUANHUAN_MANA_BONUS = "earth_online_arcana.xuanhuan_mana_bonus";
    public static final String MAGIC_MANA_BONUS = "earth_online_arcana.magic_mana_bonus";
    public static final String EQUIPMENT_MANA_BONUS = "earth_online_arcana.equipment_mana_bonus";
    public static final String TEMPORARY_MANA_BONUS = "earth_online_arcana.temporary_mana_bonus";
    public static final String QI_ABSORPTION_RATE = "earth_online_arcana.qi_absorption_rate";
    public static final String MAGIC_ATTUNEMENT_RATE = "earth_online_arcana.magic_attunement_rate";
    public static final String CULTIVATION_LEVEL = "earth_online_arcana.cultivation_level";
    public static final String MAGIC_RESEARCH_LEVEL = "earth_online_arcana.magic_research_level";
    public static final String FASTING_FOOD_BONUS = "earth_online_arcana.fasting_food_bonus";
    public static final String BREATH_CAPACITY_BONUS = "earth_online_arcana.breath_capacity_bonus";
    public static final String ENDURANCE_BONUS = "earth_online_arcana.endurance_bonus";
    public static final String BODY_TEMPERING_BONUS = "earth_online_arcana.body_tempering_bonus";
    public static final String BIGU_LEVEL = "earth_online_arcana.bigu_level";
    public static final String BODY_TEMPERING_LEVEL = "earth_online_arcana.body_tempering_level";
    public static final String FETAL_BREATH_LEVEL = "earth_online_arcana.fetal_breath_level";
    public static final String QI_MEDITATION_COOLDOWN_UNTIL = "earth_online_arcana.qi_meditation_cooldown_until";
    public static final String CULTIVATION_FOCUS = "earth_online_arcana.cultivation_focus";
    public static final String XUANHUAN_LAST_ACTION = "earth_online_arcana.xuanhuan_last_action";
    public static final String XUANHUAN_LAST_ACTION_TICK = "earth_online_arcana.xuanhuan_last_action_tick";

    private static final double DEFAULT_BASE_MANA = 20.0D;
    private static final double MAX_REASONABLE_MANA = 1_000_000.0D;
    public static final int QI_MEDITATION_COOLDOWN_TICKS = 20 * 18;

    private ArcanaPower() {
    }

    public static double getBaseMana(Player player) {
        return Math.max(0.0D, data(player).getDoubleOr(BASE_MANA, DEFAULT_BASE_MANA));
    }

    public static double getXuanhuanBonus(Player player) {
        return Math.max(0.0D, data(player).getDoubleOr(XUANHUAN_MANA_BONUS, 0.0D));
    }

    public static double getMagicBonus(Player player) {
        return Math.max(0.0D, data(player).getDoubleOr(MAGIC_MANA_BONUS, 0.0D));
    }

    public static double getMaxMana(Player player) {
        CompoundTag tag = data(player);
        double max = getBaseMana(player)
                + getXuanhuanBonus(player)
                + getMagicBonus(player)
                + Math.max(0.0D, tag.getDoubleOr(EQUIPMENT_MANA_BONUS, 0.0D))
                + Math.max(0.0D, tag.getDoubleOr(TEMPORARY_MANA_BONUS, 0.0D));
        return clamp(max, 0.0D, MAX_REASONABLE_MANA);
    }

    public static double getCurrentMana(Player player) {
        double max = getMaxMana(player);
        return clamp(data(player).getDoubleOr(CURRENT_MANA, max), 0.0D, max);
    }

    public static void setCurrentMana(Player player, double value) {
        data(player).putDouble(CURRENT_MANA, clamp(value, 0.0D, getMaxMana(player)));
    }

    public static boolean trySpendMana(Player player, double amount) {
        double cost = Math.max(0.0D, amount);
        double current = getCurrentMana(player);
        if (current + 0.0001D < cost) {
            return false;
        }
        setCurrentMana(player, current - cost);
        return true;
    }

    public static int getCultivationLevel(Player player) {
        return Math.max(0, data(player).getIntOr(CULTIVATION_LEVEL, 0));
    }

    public static double getQiAbsorptionRate(Player player) {
        return Math.max(0.0D, data(player).getDoubleOr(QI_ABSORPTION_RATE, 0.0D));
    }

    public static int getBiguLevel(Player player) {
        return Math.max(0, data(player).getIntOr(BIGU_LEVEL, 0));
    }

    public static int getBodyTemperingLevel(Player player) {
        return Math.max(0, data(player).getIntOr(BODY_TEMPERING_LEVEL, 0));
    }

    public static int getFetalBreathLevel(Player player) {
        return Math.max(0, data(player).getIntOr(FETAL_BREATH_LEVEL, 0));
    }

    public static double getFastingFoodBonus(Player player) {
        return Math.max(0.0D, data(player).getDoubleOr(FASTING_FOOD_BONUS, 0.0D));
    }

    public static double getBreathCapacityBonus(Player player) {
        return Math.max(0.0D, data(player).getDoubleOr(BREATH_CAPACITY_BONUS, 0.0D));
    }

    public static double getEnduranceBonus(Player player) {
        return Math.max(0.0D, data(player).getDoubleOr(ENDURANCE_BONUS, 0.0D));
    }

    public static double getBodyTemperingBonus(Player player) {
        return Math.max(0.0D, data(player).getDoubleOr(BODY_TEMPERING_BONUS, 0.0D));
    }

    public static CultivationFocus getCultivationFocus(Player player) {
        CultivationFocus focus = CultivationFocus.byId(data(player).getIntOr(CULTIVATION_FOCUS, 0));
        return focus.isUnlocked(player) ? focus : CultivationFocus.CIRCULATION;
    }

    public static boolean setCultivationFocus(Player player, CultivationFocus focus) {
        if (!focus.isUnlocked(player) || getCultivationFocus(player) == focus) {
            return false;
        }
        data(player).putInt(CULTIVATION_FOCUS, focus.id());
        return true;
    }

    public static int getCultivationFocusMask(Player player) {
        int mask = 0;
        for (CultivationFocus focus : CultivationFocus.values()) {
            if (focus.isUnlocked(player)) {
                mask |= 1 << focus.id();
            }
        }
        return mask;
    }

    public static boolean learnQiGuiding(Player player) {
        CompoundTag tag = data(player);
        if (getCultivationLevel(player) > 0) {
            return false;
        }
        tag.putInt(CULTIVATION_LEVEL, 1);
        add(tag, XUANHUAN_MANA_BONUS, 30.0D);
        add(tag, QI_ABSORPTION_RATE, 0.08D);
        add(tag, ENDURANCE_BONUS, 0.04D);
        setCurrentMana(player, getMaxMana(player));
        return true;
    }

    public static boolean learnBigu(Player player) {
        CompoundTag tag = data(player);
        if (getBiguLevel(player) > 0) {
            return false;
        }
        tag.putInt(BIGU_LEVEL, 1);
        add(tag, FASTING_FOOD_BONUS, 0.60D);
        add(tag, ENDURANCE_BONUS, 0.10D);
        add(tag, XUANHUAN_MANA_BONUS, 10.0D);
        setCurrentMana(player, getMaxMana(player));
        return true;
    }

    public static boolean learnBodyTempering(Player player) {
        CompoundTag tag = data(player);
        if (getBodyTemperingLevel(player) > 0) {
            return false;
        }
        tag.putInt(BODY_TEMPERING_LEVEL, 1);
        add(tag, BODY_TEMPERING_BONUS, 0.45D);
        add(tag, ENDURANCE_BONUS, 0.22D);
        add(tag, BREATH_CAPACITY_BONUS, 60.0D);
        add(tag, XUANHUAN_MANA_BONUS, 15.0D);
        setCurrentMana(player, getMaxMana(player));
        return true;
    }

    public static boolean learnFetalBreath(Player player) {
        CompoundTag tag = data(player);
        if (getFetalBreathLevel(player) > 0) {
            return false;
        }
        tag.putInt(FETAL_BREATH_LEVEL, 1);
        add(tag, BREATH_CAPACITY_BONUS, 180.0D);
        add(tag, ENDURANCE_BONUS, 0.08D);
        add(tag, XUANHUAN_MANA_BONUS, 12.0D);
        setCurrentMana(player, getMaxMana(player));
        return true;
    }

    public static double absorbAmbientQi(Player player, int ambientQi) {
        double rate = getQiAbsorptionRate(player);
        if (rate <= 0.0D) {
            return 0.0D;
        }
        double before = getCurrentMana(player);
        double amount = Math.max(1.0D, ambientQi * rate);
        setCurrentMana(player, before + amount);
        return getCurrentMana(player) - before;
    }

    public static long getQiMeditationCooldownTicks(Player player, Level level) {
        long until = data(player).getLongOr(QI_MEDITATION_COOLDOWN_UNTIL, 0L);
        return Math.max(0L, until - level.getGameTime());
    }

    public static void startQiMeditationCooldown(Player player, Level level) {
        data(player).putLong(QI_MEDITATION_COOLDOWN_UNTIL, level.getGameTime() + QI_MEDITATION_COOLDOWN_TICKS);
    }

    public static void recordAction(Player player, Level level, String action) {
        CompoundTag tag = data(player);
        tag.putString(XUANHUAN_LAST_ACTION, action);
        tag.putLong(XUANHUAN_LAST_ACTION_TICK, level.getGameTime());
    }

    public static String format(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.05D) {
            return Integer.toString((int) Math.rint(value));
        }
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static CompoundTag data(Player player) {
        return player.getPersistentData();
    }

    private static void add(CompoundTag tag, String key, double amount) {
        tag.putDouble(key, Math.max(0.0D, tag.getDoubleOr(key, 0.0D) + amount));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
