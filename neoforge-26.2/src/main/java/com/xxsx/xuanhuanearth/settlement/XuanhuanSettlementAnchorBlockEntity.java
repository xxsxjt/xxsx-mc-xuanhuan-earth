package com.xxsx.xuanhuanearth.settlement;

import com.xxsx.xuanhuanearth.XuanhuanEarth;
import com.xxsx.xuanhuanearth.entity.CultivationSettlerEntity;
import com.xxsx.xuanhuanearth.entity.ContractableSpiritBeastEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class XuanhuanSettlementAnchorBlockEntity extends BlockEntity {
    private String settlementType = XuanhuanSettlementFeature.Type.WANDERING_MARKET.id();
    private boolean initialized;
    private int warmupTicks;

    public XuanhuanSettlementAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(XuanhuanEarth.SETTLEMENT_ANCHOR_BLOCK_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  XuanhuanSettlementAnchorBlockEntity anchor) {
        if (anchor.initialized || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (++anchor.warmupTicks < 40) {
            return;
        }
        anchor.spawnSettlementResidents(serverLevel, pos);
        anchor.initialized = true;
        anchor.setChanged();
    }

    private void spawnSettlementResidents(ServerLevel level, BlockPos pos) {
        XuanhuanSettlementFeature.Type type = XuanhuanSettlementFeature.Type.byId(settlementType);
        switch (type) {
            case WANDERING_MARKET -> {
                spawnResident(level, pos.offset(-2, 1, 1), CultivationSettlerEntity.Role.WANDERING_MERCHANT);
                spawnResident(level, pos.offset(2, 1, 1), CultivationSettlerEntity.Role.WANDERING_MERCHANT);
                spawnResident(level, pos.offset(0, 1, -2), CultivationSettlerEntity.Role.SECT_STEWARD);
            }
            case SPIRIT_SPRING_VILLAGE -> {
                spawnResident(level, pos.offset(-2, 1, 0), CultivationSettlerEntity.Role.SPIRIT_SPRING_KEEPER);
                spawnResident(level, pos.offset(2, 1, 0), CultivationSettlerEntity.Role.SPIRIT_SPRING_KEEPER);
                spawnResident(level, pos.offset(0, 1, 3), CultivationSettlerEntity.Role.WANDERING_MERCHANT);
                spawnBeast(level, pos.offset(3, 1, 3), XuanhuanEarth.SPIRIT_FOX.get());
            }
            case SECT_OUTPOST -> {
                spawnResident(level, pos.offset(-2, 1, 0), CultivationSettlerEntity.Role.SECT_STEWARD);
                spawnResident(level, pos.offset(2, 1, 0), CultivationSettlerEntity.Role.SECT_STEWARD);
                spawnResident(level, pos.offset(0, 1, 3), CultivationSettlerEntity.Role.SPIRIT_SPRING_KEEPER);
            }
        }
        XuanhuanEarth.LOGGER.info("Initialized xuanhuan settlement {} at {}", settlementType, pos);
    }

    private static void spawnResident(ServerLevel level, BlockPos pos, CultivationSettlerEntity.Role role) {
        CultivationSettlerEntity resident = XuanhuanEarth.CULTIVATION_SETTLER.get()
                .create(level, EntitySpawnReason.STRUCTURE);
        if (resident == null) {
            return;
        }
        BlockPos spawnPos = findOpenSpawn(level, pos);
        resident.setRole(role);
        resident.snapTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        resident.setPersistenceRequired();
        level.addFreshEntity(resident);
    }

    private static void spawnBeast(ServerLevel level, BlockPos pos,
                                   EntityType<? extends ContractableSpiritBeastEntity> type) {
        ContractableSpiritBeastEntity beast = type.create(level, EntitySpawnReason.STRUCTURE);
        if (beast == null) {
            return;
        }
        BlockPos spawnPos = findOpenSpawn(level, pos);
        beast.snapTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        beast.setPersistenceRequired();
        level.addFreshEntity(beast);
    }

    private static BlockPos findOpenSpawn(ServerLevel level, BlockPos preferred) {
        int[] verticalOffsets = {-1, 0, 1, -2};
        for (int verticalOffset : verticalOffsets) {
            for (int radius = 0; radius <= 3; radius++) {
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (Math.max(Math.abs(x), Math.abs(z)) != radius) {
                            continue;
                        }
                        BlockPos candidate = preferred.offset(x, verticalOffset, z);
                        if (!level.isEmptyBlock(candidate.below())
                                && level.isEmptyBlock(candidate)
                                && level.isEmptyBlock(candidate.above())) {
                            return candidate;
                        }
                    }
                }
            }
        }
        return preferred;
    }

    public void configure(String settlementType) {
        this.settlementType = XuanhuanSettlementFeature.Type.byId(settlementType).id();
        this.initialized = false;
        this.warmupTicks = 0;
        setChanged();
    }

    public String settlementType() {
        return settlementType;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("SettlementType", settlementType);
        output.putBoolean("Initialized", initialized);
        output.putInt("WarmupTicks", warmupTicks);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        settlementType = XuanhuanSettlementFeature.Type.byId(
                input.getStringOr("SettlementType", XuanhuanSettlementFeature.Type.WANDERING_MARKET.id())).id();
        initialized = input.getBooleanOr("Initialized", false);
        warmupTicks = input.getIntOr("WarmupTicks", 0);
    }

}
