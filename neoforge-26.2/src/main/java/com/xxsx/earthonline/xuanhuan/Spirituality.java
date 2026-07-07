package com.xxsx.earthonline.xuanhuan;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public final class Spirituality {
    private Spirituality() {
    }

    public static int measure(Level level, BlockPos pos) {
        return reading(level, pos).value();
    }

    public static ArcanaChunkField.Reading reading(Level level, BlockPos pos) {
        return ArcanaChunkField.read(level, pos);
    }

    public static void consume(Level level, BlockPos pos, double restoredMana) {
        ArcanaChunkField.consume(level, pos, restoredMana);
    }

    public static Component gradeName(int value) {
        if (value >= 75) {
            return Component.translatable("spirituality.earth_online_xuanhuan.grade.rich");
        }
        if (value >= 50) {
            return Component.translatable("spirituality.earth_online_xuanhuan.grade.active");
        }
        if (value >= 25) {
            return Component.translatable("spirituality.earth_online_xuanhuan.grade.thin");
        }
        return Component.translatable("spirituality.earth_online_xuanhuan.grade.exhausted");
    }
}
