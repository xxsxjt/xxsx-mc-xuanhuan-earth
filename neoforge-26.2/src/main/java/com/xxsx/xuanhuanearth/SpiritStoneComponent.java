package com.xxsx.xuanhuanearth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

/** Persistent per-stack state of a spirit stone. */
public record SpiritStoneComponent(double chargeAeu, int matrixGrade, int schemaVersion) {
    public static final int CURRENT_SCHEMA = SpiritStoneCharge.SCHEMA_VERSION;
    public static final Codec<SpiritStoneComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("charge_aeu").forGetter(SpiritStoneComponent::chargeAeu),
            Codec.INT.optionalFieldOf("matrix_grade", 1).forGetter(SpiritStoneComponent::matrixGrade),
            Codec.INT.optionalFieldOf("schema_version", CURRENT_SCHEMA).forGetter(SpiritStoneComponent::schemaVersion)
    ).apply(instance, SpiritStoneComponent::new));
    public static final SpiritStoneComponent EMPTY = new SpiritStoneComponent(0.0D, 1, CURRENT_SCHEMA);

    public SpiritStoneComponent {
        chargeAeu = SpiritStoneCharge.clampCharge(chargeAeu);
        matrixGrade = Math.max(1, Math.min(3, matrixGrade));
        schemaVersion = Math.max(1, schemaVersion);
    }

    public static SpiritStoneComponent read(ItemStack stack) {
        SpiritStoneComponent value = stack.get(XuanhuanEarth.SPIRIT_STONE_CHARGE);
        return value == null ? EMPTY : value;
    }

    public SpiritStoneComponent withCharge(double charge) {
        return new SpiritStoneComponent(charge, matrixGrade, CURRENT_SCHEMA);
    }
}
