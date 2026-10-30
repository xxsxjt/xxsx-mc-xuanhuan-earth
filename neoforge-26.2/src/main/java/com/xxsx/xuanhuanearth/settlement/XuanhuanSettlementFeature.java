package com.xxsx.xuanhuanearth.settlement;

import com.mojang.serialization.Codec;
import com.xxsx.xuanhuanearth.TalismanComponent;
import com.xxsx.xuanhuanearth.XuanhuanEarth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class XuanhuanSettlementFeature extends Feature<NoneFeatureConfiguration> {
    private static final int UPDATE_FLAGS = 2;
    private final Type type;

    public XuanhuanSettlementFeature(Codec<NoneFeatureConfiguration> codec, Type type) {
        super(codec);
        this.type = type;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos center = context.origin();
        while (level.isEmptyBlock(center) && center.getY() > level.getMinY() + 2) {
            center = center.below();
        }
        center = center.above();
        if (level.isEmptyBlock(center.below())) {
            return false;
        }

        int radius = type == Type.SECT_OUTPOST ? 5 : 4;
        prepareSite(level, center, radius);
        switch (type) {
            case WANDERING_MARKET -> buildWanderingMarket(level, center);
            case SPIRIT_SPRING_VILLAGE -> buildSpiritSpringVillage(level, center);
            case SECT_OUTPOST -> buildSectOutpost(level, center);
        }

        level.setBlock(center, XuanhuanEarth.SETTLEMENT_ANCHOR.get().defaultBlockState(), UPDATE_FLAGS);
        if (level.getBlockEntity(center) instanceof XuanhuanSettlementAnchorBlockEntity anchor) {
            anchor.configure(type.id());
        }
        return true;
    }

    private static void prepareSite(WorldGenLevel level, BlockPos center, int radius) {
        BlockState foundation = Blocks.STONE_BRICKS.defaultBlockState();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos floor = center.offset(x, -1, z);
                level.setBlock(floor, foundation, UPDATE_FLAGS);
                for (int y = 0; y <= 5; y++) {
                    level.setBlock(center.offset(x, y, z), Blocks.AIR.defaultBlockState(), UPDATE_FLAGS);
                }
            }
        }
    }

    private static void buildWanderingMarket(WorldGenLevel level, BlockPos center) {
        fillFloor(level, center, 4, Blocks.PACKED_MUD.defaultBlockState());
        buildCanopy(level, center.offset(-3, 0, 0), Blocks.GREEN_WOOL.defaultBlockState());
        buildCanopy(level, center.offset(3, 0, 0), Blocks.WHITE_WOOL.defaultBlockState());
        line(level, center.offset(-1, 0, -3), 3, true, Blocks.SPRUCE_SLAB.defaultBlockState());
        level.setBlock(center.offset(0, 0, -3), Blocks.CAMPFIRE.defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(-3, 0, 2), Blocks.BARREL.defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(3, 0, 2), Blocks.CHEST.defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(-2, 0, 1), XuanhuanEarth.TALISMAN_TABLE.get().defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(2, 0, 1), XuanhuanEarth.SPIRIT_ARRAY_CORE.get().defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(0, 0, 1), XuanhuanEarth.SPIRIT_VEIN_NODE.get().defaultBlockState(), UPDATE_FLAGS);
        stock(level, center.offset(-3, 0, 2), new ItemStack(XuanhuanEarth.TALISMAN_PAPER.get(), 16),
                new ItemStack(XuanhuanEarth.CINNABAR_SPIRIT_INK.get(), 6),
                new ItemStack(XuanhuanEarth.SPIRIT_GRASS.get(), 8));
        stock(level, center.offset(3, 0, 2), new ItemStack(XuanhuanEarth.SPIRIT_CRYSTAL_SHARD.get(), 8),
                new ItemStack(XuanhuanEarth.SPIRIT_SPRING_BOTTLE.get(), 4),
                new ItemStack(XuanhuanEarth.BASIC_TALISMAN.get(), 3));
        placeBoard(level, center.offset(0, 0, -2));
        cornerLanterns(level, center, 4, Blocks.SPRUCE_FENCE.defaultBlockState());
    }

    private static void buildSpiritSpringVillage(WorldGenLevel level, BlockPos center) {
        fillFloor(level, center, 4, Blocks.MOSSY_STONE_BRICKS.defaultBlockState());
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                level.setBlock(center.offset(x, 0, z), Blocks.WATER.defaultBlockState(), UPDATE_FLAGS);
            }
        }
        level.setBlock(center, XuanhuanEarth.SETTLEMENT_ANCHOR.get().defaultBlockState(), UPDATE_FLAGS);
        buildOpenHut(level, center.offset(-3, 0, 2), Blocks.SPRUCE_PLANKS.defaultBlockState());
        buildOpenHut(level, center.offset(3, 0, 2), Blocks.BIRCH_PLANKS.defaultBlockState());
        level.setBlock(center.offset(-3, 0, 2), XuanhuanEarth.ALCHEMY_FURNACE.get().defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(3, 0, 2), XuanhuanEarth.SPIRIT_SPRING_STONE.get().defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(0, 0, 4), XuanhuanEarth.SPIRIT_SOIL.get().defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(0, 0, -4), XuanhuanEarth.SPIRIT_VEIN_NODE.get().defaultBlockState(), UPDATE_FLAGS);
        stock(level, center.offset(-4, 0, 2), new ItemStack(XuanhuanEarth.SPIRIT_GRASS.get(), 16),
                new ItemStack(XuanhuanEarth.SPIRIT_SPRING_BOTTLE.get(), 8),
                new ItemStack(XuanhuanEarth.SPIRIT_CRYSTAL_SHARD.get(), 4));
        stock(level, center.offset(4, 0, 2), new ItemStack(XuanhuanEarth.TALISMAN_PAPER.get(), 12),
                new ItemStack(XuanhuanEarth.CINNABAR_SPIRIT_INK.get(), 6),
                new ItemStack(XuanhuanEarth.BASIC_TALISMAN.get(), 3));
        for (int x = -3; x <= 3; x += 2) {
            level.setBlock(center.offset(x, 0, -3), Blocks.FLOWERING_AZALEA.defaultBlockState(), UPDATE_FLAGS);
        }
        placeBoard(level, center.offset(0, 0, 2));
    }

    private static void buildSectOutpost(WorldGenLevel level, BlockPos center) {
        fillFloor(level, center, 5, Blocks.POLISHED_DEEPSLATE.defaultBlockState());
        for (int i = -5; i <= 5; i++) {
            if (Math.abs(i) > 1) {
                level.setBlock(center.offset(i, 0, -5), Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState(), UPDATE_FLAGS);
            }
            level.setBlock(center.offset(i, 0, 5), Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState(), UPDATE_FLAGS);
            level.setBlock(center.offset(-5, 0, i), Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState(), UPDATE_FLAGS);
            level.setBlock(center.offset(5, 0, i), Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState(), UPDATE_FLAGS);
        }
        for (int x : new int[]{-2, 2}) {
            column(level, center.offset(x, 0, -5), 4, Blocks.DARK_OAK_LOG.defaultBlockState());
        }
        line(level, center.offset(-2, 4, -5), 5, true, Blocks.DARK_OAK_PLANKS.defaultBlockState());
        level.setBlock(center.offset(-3, 0, 2), Blocks.BOOKSHELF.defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(3, 0, 2), Blocks.TARGET.defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(-3, 0, 2), XuanhuanEarth.ALCHEMY_FURNACE.get().defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(3, 0, 2), XuanhuanEarth.TALISMAN_TABLE.get().defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(0, 0, 2), XuanhuanEarth.SPIRIT_ARRAY_CORE.get().defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(center.offset(0, 0, -2), XuanhuanEarth.SPIRIT_VEIN_NODE.get().defaultBlockState(), UPDATE_FLAGS);
        stock(level, center.offset(-4, 0, 2), new ItemStack(XuanhuanEarth.SPIRIT_GRASS.get(), 12),
                new ItemStack(XuanhuanEarth.SPIRIT_CRYSTAL_SHARD.get(), 8));
        stock(level, center.offset(4, 0, 2), new ItemStack(XuanhuanEarth.TALISMAN_PAPER.get(), 16),
                new ItemStack(XuanhuanEarth.CINNABAR_SPIRIT_INK.get(), 8),
                new ItemStack(XuanhuanEarth.BASIC_TALISMAN.get(), 4));
        placeBoard(level, center.offset(0, 0, -3));
        cornerLanterns(level, center, 4, Blocks.DARK_OAK_FENCE.defaultBlockState());
    }

    private static void fillFloor(WorldGenLevel level, BlockPos center, int radius, BlockState state) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                level.setBlock(center.offset(x, -1, z), state, UPDATE_FLAGS);
            }
        }
    }

    private static void buildCanopy(WorldGenLevel level, BlockPos center, BlockState cloth) {
        for (int x : new int[]{-1, 1}) {
            for (int z : new int[]{-1, 1}) {
                column(level, center.offset(x, 0, z), 3, Blocks.SPRUCE_FENCE.defaultBlockState());
            }
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                level.setBlock(center.offset(x, 3, z), cloth, UPDATE_FLAGS);
            }
        }
    }

    private static void buildOpenHut(WorldGenLevel level, BlockPos center, BlockState wall) {
        for (int x : new int[]{-1, 1}) {
            for (int z : new int[]{-1, 1}) {
                column(level, center.offset(x, 0, z), 3, Blocks.STRIPPED_SPRUCE_LOG.defaultBlockState());
            }
        }
        line(level, center.offset(-1, 0, 1), 3, true, wall);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                level.setBlock(center.offset(x, 3, z), Blocks.SPRUCE_SLAB.defaultBlockState(), UPDATE_FLAGS);
            }
        }
    }

    private static void cornerLanterns(WorldGenLevel level, BlockPos center, int radius, BlockState post) {
        for (int x : new int[]{-radius, radius}) {
            for (int z : new int[]{-radius, radius}) {
                column(level, center.offset(x, 0, z), 2, post);
                level.setBlock(center.offset(x, 2, z), Blocks.LANTERN.defaultBlockState(), UPDATE_FLAGS);
            }
        }
    }

    private static void column(WorldGenLevel level, BlockPos start, int height, BlockState state) {
        for (int y = 0; y < height; y++) {
            level.setBlock(start.above(y), state, UPDATE_FLAGS);
        }
    }

    private static void line(WorldGenLevel level, BlockPos start, int length, boolean alongX, BlockState state) {
        for (int i = 0; i < length; i++) {
            level.setBlock(start.offset(alongX ? i : 0, 0, alongX ? 0 : i), state, UPDATE_FLAGS);
        }
    }

    private static void stock(WorldGenLevel level, BlockPos pos, ItemStack... items) {
        level.setBlock(pos, Blocks.BARREL.defaultBlockState(), UPDATE_FLAGS);
        if (level.getBlockEntity(pos) instanceof Container container) {
            for (int slot = 0; slot < Math.min(container.getContainerSize(), items.length); slot++) {
                container.setItem(slot, sealed(items[slot].copy()));
            }
        }
    }

    /** Village stock must be usable; an unsealed talisman could never be triggered. */
    private static ItemStack sealed(ItemStack stack) {
        return stack.is(XuanhuanEarth.BASIC_TALISMAN.get()) ? TalismanComponent.drawn(stack) : stack;
    }

    private static void placeBoard(WorldGenLevel level, BlockPos pos) {
        Block board = BuiltInRegistries.BLOCK.getValue(
                Identifier.fromNamespaceAndPath("earth_on_minecraft", "settlement_board"));
        if (board != null && board != Blocks.AIR) {
            level.setBlock(pos, board.defaultBlockState(), UPDATE_FLAGS);
        }
    }

    public enum Type {
        WANDERING_MARKET("wandering_cultivator_market"),
        SPIRIT_SPRING_VILLAGE("spirit_spring_village"),
        SECT_OUTPOST("sect_frontier_outpost");

        private final String id;

        Type(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public static Type byId(String id) {
            for (Type type : values()) {
                if (type.id.equals(id)) {
                    return type;
                }
            }
            return WANDERING_MARKET;
        }
    }
}
