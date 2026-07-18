package com.xxsx.earthonline.xuanhuan;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class EarthMaterialTags {
    public static final TagKey<Item> SPIRITUAL_MINERAL_SUBSTRATES = tag("spiritual_mineral_substrates");
    public static final TagKey<Item> ARCANA_GEOLOGY_CATALYSTS = tag("arcana_geology_catalysts");
    public static final TagKey<Item> MANA_CONDUCTORS = tag("mana_conductors");

    private EarthMaterialTags() {
    }

    private static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("earth_on_minecraft", path));
    }
}
