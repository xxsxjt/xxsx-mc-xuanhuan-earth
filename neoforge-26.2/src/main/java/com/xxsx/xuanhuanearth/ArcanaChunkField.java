package com.xxsx.xuanhuanearth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ArcanaChunkField extends SavedData {
    private static final long REFRESH_INTERVAL_TICKS = 600L;
    private static final double MAX_DEPLETION = 45.0D;
    private static final double DEPLETION_RECOVERY_PER_TICK = 0.003D;
    private static final int SCAN_RADIUS_XZ = 8;
    private static final int SCAN_RADIUS_Y = 5;
    private static final int MAX_SOURCE_CACHE_ENTRIES = 256;

    private static final Codec<ArcanaChunkField> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, DepletionState.CODEC)
                    .optionalFieldOf("chunks", Map.<String, DepletionState>of())
                    .forGetter(data -> data.depletedChunks)
    ).apply(instance, ArcanaChunkField::new));
    private static final SavedDataType<ArcanaChunkField> TYPE = new SavedDataType<>(
            XuanhuanEarth.id("arcana_chunk_field"), ArcanaChunkField::new, CODEC);

    private final Map<String, DepletionState> depletedChunks = new HashMap<>();
    private final Map<String, CachedSources> sourceCache = new LinkedHashMap<>(64, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, CachedSources> eldest) {
            return size() > MAX_SOURCE_CACHE_ENTRIES;
        }
    };

    private ArcanaChunkField() {
    }

    private ArcanaChunkField(Map<String, DepletionState> states) {
        states.forEach((key, state) -> {
            double depletion = clamp(state.depletion(), 0.0D, MAX_DEPLETION);
            if (depletion > 0.0D) {
                depletedChunks.put(key, new DepletionState(depletion, state.lastUpdate()));
            }
        });
    }

    public static Reading read(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            return data(serverLevel).readAt(serverLevel, pos);
        }
        return buildReading(level, pos, scanSources(level, pos), 0.0D);
    }

    public static void consume(Level level, BlockPos pos, double restoredMana) {
        if (restoredMana <= 0.0D || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        data(serverLevel).consumeAt(serverLevel, pos, restoredMana);
    }

    private static ArcanaChunkField data(ServerLevel level) {
        return level.getServer().getDataStorage().computeIfAbsent(TYPE);
    }

    private Reading readAt(ServerLevel level, BlockPos pos) {
        String key = chunkKey(level, pos);
        double depletion = currentDepletion(key, level.getGameTime());
        SourceScan sources = cachedSources(level, pos, key);
        return buildReading(level, pos, sources, depletion);
    }

    private void consumeAt(ServerLevel level, BlockPos pos, double restoredMana) {
        String key = chunkKey(level, pos);
        long now = level.getGameTime();
        double current = currentDepletion(key, now);
        double added = Math.max(0.5D, restoredMana * 0.35D);
        double updated = clamp(current + added, 0.0D, MAX_DEPLETION);
        depletedChunks.put(key, new DepletionState(updated, now));
        setDirty();
    }

    private double currentDepletion(String key, long now) {
        DepletionState state = depletedChunks.get(key);
        if (state == null) {
            return 0.0D;
        }
        if (now < state.lastUpdate()) {
            depletedChunks.put(key, new DepletionState(state.depletion(), now));
            setDirty();
            return state.depletion();
        }
        double recovered = clamp(
                state.depletion() - (now - state.lastUpdate()) * DEPLETION_RECOVERY_PER_TICK,
                0.0D,
                MAX_DEPLETION);
        if (recovered <= 0.0D) {
            depletedChunks.remove(key);
            setDirty();
        }
        return recovered;
    }

    private SourceScan cachedSources(ServerLevel level, BlockPos pos, String key) {
        long now = level.getGameTime();
        CachedSources cached = sourceCache.get(key);
        if (cached == null
                || now < cached.lastRefresh()
                || now - cached.lastRefresh() >= REFRESH_INTERVAL_TICKS
                || Math.abs(pos.getY() - cached.anchorY()) > SCAN_RADIUS_Y) {
            SourceScan scan = scanSources(level, pos);
            sourceCache.put(key, new CachedSources(scan, now, pos.getY()));
            return scan;
        }
        return cached.sources();
    }

    private static Reading buildReading(Level level, BlockPos pos, SourceScan scan, double depletion) {
        int base = naturalBase(level, pos, ChunkPos.containing(pos));
        int value = clamp((int) Math.round(
                base + scan.vein() + scan.spring() + scan.flora() + scan.structure() - depletion), 0, 100);
        String sourceKey = depletion >= 28.0D && value < 35
                ? "spirituality.xuanhuan_earth.source.depleted"
                : scan.mainSourceKey();
        return new Reading(value, depletion, sourceKey);
    }

    private static String chunkKey(Level level, BlockPos pos) {
        return level.dimension().identifier() + "|" + ChunkPos.containing(pos).pack();
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
            if (block == XuanhuanEarth.SPIRIT_VEIN_NODE.get()) {
                vein = Math.min(36, vein + 18);
            } else if (block == XuanhuanEarth.SPIRIT_SPRING_STONE.get()) {
                spring = Math.min(26, spring + 13);
            } else if (block == XuanhuanEarth.SPIRIT_SOIL.get()) {
                flora = Math.min(20, flora + 5);
            } else if (block == XuanhuanEarth.SPIRIT_ARRAY_CORE.get()) {
                structure = Math.min(36, structure + (XuanhuanStructures.isFormalSpiritArray(level, sample) ? 28 : 12));
            } else if (block == XuanhuanEarth.MEDITATION_CUSHION.get()) {
                structure = Math.min(30, structure + 8);
            } else if (block == XuanhuanEarth.ALCHEMY_FURNACE.get()
                    || block == XuanhuanEarth.TALISMAN_TABLE.get()) {
                structure = Math.min(28, structure + 4);
            }
        }

        String sourceKey = "spirituality.xuanhuan_earth.source.natural";
        int strongest = 0;
        if (vein > strongest) {
            strongest = vein;
            sourceKey = "spirituality.xuanhuan_earth.source.vein";
        }
        if (spring > strongest) {
            strongest = spring;
            sourceKey = "spirituality.xuanhuan_earth.source.spring";
        }
        if (structure > strongest) {
            strongest = structure;
            sourceKey = "spirituality.xuanhuan_earth.source.array";
        }
        if (flora > strongest) {
            sourceKey = "spirituality.xuanhuan_earth.source.flora";
        }
        return new SourceScan(vein, spring, flora, structure, sourceKey);
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

    public record Reading(int value, double depletion, String mainSourceKey) {
        public net.minecraft.network.chat.Component mainSource() {
            return net.minecraft.network.chat.Component.translatable(mainSourceKey);
        }
    }

    private record SourceScan(int vein, int spring, int flora, int structure, String mainSourceKey) {
    }

    private record CachedSources(SourceScan sources, long lastRefresh, int anchorY) {
    }

    private record DepletionState(double depletion, long lastUpdate) {
        private static final Codec<DepletionState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("depletion").forGetter(DepletionState::depletion),
                Codec.LONG.fieldOf("last_update").forGetter(DepletionState::lastUpdate)
        ).apply(instance, DepletionState::new));
    }
}
