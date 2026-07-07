package com.xxsx.earthonline.xuanhuan;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public final class ArcanaChunkField {
    private static final long REFRESH_INTERVAL_TICKS = 600L;
    private static final double MAX_DEPLETION = 45.0D;
    private static final double DEPLETION_RECOVERY_PER_TICK = 0.003D;
    private static final int SCAN_RADIUS_XZ = 8;
    private static final int SCAN_RADIUS_Y = 5;

    private static final Map<ResourceKey<Level>, Map<Long, FieldState>> CACHE = new HashMap<>();

    private ArcanaChunkField() {
    }

    public static Reading read(Level level, BlockPos pos) {
        synchronized (CACHE) {
            FieldState state = state(level, pos);
            state.refresh(level, pos);
            return state.reading();
        }
    }

    public static void consume(Level level, BlockPos pos, double restoredMana) {
        if (restoredMana <= 0.0D) {
            return;
        }
        synchronized (CACHE) {
            FieldState state = state(level, pos);
            state.refresh(level, pos);
            state.depletion = clamp(state.depletion + Math.max(0.5D, restoredMana * 0.35D), 0.0D, MAX_DEPLETION);
            state.lastUpdate = level.getGameTime();
        }
    }

    private static FieldState state(Level level, BlockPos pos) {
        ChunkPos chunk = ChunkPos.containing(pos);
        Map<Long, FieldState> dimension = CACHE.computeIfAbsent(level.dimension(), key -> new HashMap<>());
        return dimension.computeIfAbsent(chunk.pack(), key -> new FieldState(level, pos, chunk));
    }

    private static int naturalBase(Level level, BlockPos pos, ChunkPos chunk) {
        String dimension = level.dimension().identifier().toString();
        int value;
        if ("minecraft:overworld".equals(dimension)) {
            value = 34;
        } else if ("minecraft:the_nether".equals(dimension)) {
            value = 18;
        } else if ("minecraft:the_end".equals(dimension)) {
            value = 12;
        } else {
            value = 22;
        }

        int y = pos.getY();
        if (y < 0) {
            value += 12;
        } else if (y > 120) {
            value += 8;
        } else if (y > 72) {
            value += 4;
        }

        long seed = level instanceof ServerLevel serverLevel ? serverLevel.getSeed() : 0L;
        long mixed = mix(seed
                ^ ((long) chunk.x() * 0x9E3779B97F4A7C15L)
                ^ ((long) chunk.z() * 0xC2B2AE3D27D4EB4FL)
                ^ dimension.hashCode());
        value += Math.floorMod(mixed, 31) - 10;
        return clamp(value, 0, 100);
    }

    private static SourceScan scanSources(Level level, BlockPos pos) {
        int vein = 0;
        int spring = 0;
        int flora = 0;
        int structure = 0;

        int minX = pos.getX() - SCAN_RADIUS_XZ;
        int maxX = pos.getX() + SCAN_RADIUS_XZ;
        int minY = pos.getY() - SCAN_RADIUS_Y;
        int maxY = pos.getY() + SCAN_RADIUS_Y;
        int minZ = pos.getZ() - SCAN_RADIUS_XZ;
        int maxZ = pos.getZ() + SCAN_RADIUS_XZ;

        for (BlockPos sample : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            Block block = level.getBlockState(sample).getBlock();
            if (block == EarthOnlineXuanhuan.SPIRIT_VEIN_NODE.get()) {
                vein = Math.min(36, vein + 18);
            } else if (block == EarthOnlineXuanhuan.SPIRIT_SPRING_STONE.get()) {
                spring = Math.min(26, spring + 13);
            } else if (block == EarthOnlineXuanhuan.SPIRIT_SOIL.get()) {
                flora = Math.min(20, flora + 5);
            } else if (block == EarthOnlineXuanhuan.SPIRIT_ARRAY_CORE.get()) {
                structure = Math.min(28, structure + 16);
            } else if (block == EarthOnlineXuanhuan.ALCHEMY_FURNACE.get()
                    || block == EarthOnlineXuanhuan.TALISMAN_TABLE.get()) {
                structure = Math.min(28, structure + 4);
            }
        }

        Component source = Component.translatable("spirituality.earth_online_xuanhuan.source.natural");
        int strongest = 0;
        if (vein > strongest) {
            strongest = vein;
            source = Component.translatable("spirituality.earth_online_xuanhuan.source.vein");
        }
        if (spring > strongest) {
            strongest = spring;
            source = Component.translatable("spirituality.earth_online_xuanhuan.source.spring");
        }
        if (structure > strongest) {
            strongest = structure;
            source = Component.translatable("spirituality.earth_online_xuanhuan.source.array");
        }
        if (flora > strongest) {
            source = Component.translatable("spirituality.earth_online_xuanhuan.source.flora");
        }
        return new SourceScan(vein, spring, flora, structure, source);
    }

    private static long mix(long value) {
        value ^= value >>> 33;
        value *= 0xff51afd7ed558ccdL;
        value ^= value >>> 33;
        value *= 0xc4ceb9fe1a85ec53L;
        value ^= value >>> 33;
        return value;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public record Reading(int value, double depletion, Component mainSource) {
    }

    private record SourceScan(int vein, int spring, int flora, int structure, Component mainSource) {
    }

    private static final class FieldState {
        private int base;
        private int vein;
        private int spring;
        private int flora;
        private int structure;
        private double depletion;
        private long lastRefresh;
        private long lastUpdate;
        private Component mainSource = Component.translatable("spirituality.earth_online_xuanhuan.source.natural");

        private FieldState(Level level, BlockPos pos, ChunkPos chunk) {
            this.base = naturalBase(level, pos, chunk);
            this.lastRefresh = Long.MIN_VALUE;
            this.lastUpdate = level.getGameTime();
        }

        private void refresh(Level level, BlockPos pos) {
            long now = level.getGameTime();
            recover(now - lastUpdate);
            if (lastRefresh == Long.MIN_VALUE || now - lastRefresh >= REFRESH_INTERVAL_TICKS) {
                ChunkPos chunk = ChunkPos.containing(pos);
                base = naturalBase(level, pos, chunk);
                SourceScan scan = scanSources(level, pos);
                vein = scan.vein();
                spring = scan.spring();
                flora = scan.flora();
                structure = scan.structure();
                mainSource = scan.mainSource();
                lastRefresh = now;
            }
            lastUpdate = now;
        }

        private void recover(long elapsedTicks) {
            if (elapsedTicks <= 0L || depletion <= 0.0D) {
                return;
            }
            depletion = clamp(depletion - elapsedTicks * DEPLETION_RECOVERY_PER_TICK, 0.0D, MAX_DEPLETION);
        }

        private Reading reading() {
            int value = clamp((int) Math.round(base + vein + spring + flora + structure - depletion), 0, 100);
            Component source = depletion >= 28.0D && value < 35
                    ? Component.translatable("spirituality.earth_online_xuanhuan.source.depleted")
                    : mainSource;
            return new Reading(value, depletion, source);
        }
    }
}
