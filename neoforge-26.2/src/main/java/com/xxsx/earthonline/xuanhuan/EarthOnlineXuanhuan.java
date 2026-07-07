package com.xxsx.earthonline.xuanhuan;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(EarthOnlineXuanhuan.MODID)
public class EarthOnlineXuanhuan {
    public static final String MODID = "earth_online_xuanhuan";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    private static final List<ItemLike> TAB_ITEMS = new ArrayList<>();

    public static final DeferredBlock<XuanhuanBlock> SPIRIT_VEIN_NODE = block("spirit_vein_node",
            () -> stone(MapColor.COLOR_PURPLE, 4.0F), "message.earth_online_xuanhuan.block.spirit_vein_node");
    public static final DeferredBlock<XuanhuanBlock> SPIRIT_SPRING_STONE = block("spirit_spring_stone",
            () -> stone(MapColor.WATER, 2.5F), "message.earth_online_xuanhuan.block.spirit_spring_stone");
    public static final DeferredBlock<XuanhuanBlock> SPIRIT_SOIL = block("spirit_soil",
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.6F).sound(SoundType.GRAVEL),
            "message.earth_online_xuanhuan.block.spirit_soil");
    public static final DeferredBlock<XuanhuanBlock> ALCHEMY_FURNACE = block("alchemy_furnace",
            () -> stone(MapColor.TERRACOTTA_BLACK, 3.5F), "message.earth_online_xuanhuan.block.alchemy_furnace");
    public static final DeferredBlock<XuanhuanBlock> TALISMAN_TABLE = block("talisman_table",
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F, 3.0F).sound(SoundType.WOOD),
            "message.earth_online_xuanhuan.block.talisman_table");
    public static final DeferredBlock<XuanhuanBlock> SPIRIT_ARRAY_CORE = block("spirit_array_core",
            () -> stone(MapColor.COLOR_CYAN, 4.0F), "message.earth_online_xuanhuan.block.spirit_array_core");

    public static final DeferredItem<QiGuidingManualItem> QI_GUIDING_MANUAL = item("qi_guiding_manual",
            props -> new QiGuidingManualItem(props.stacksTo(1)));
    public static final DeferredItem<CultivationManualItem> BIGU_MANUAL = item("bigu_manual",
            props -> new CultivationManualItem(props.stacksTo(1), CultivationManualItem.Type.BIGU));
    public static final DeferredItem<CultivationManualItem> BODY_TEMPERING_MANUAL = item("body_tempering_manual",
            props -> new CultivationManualItem(props.stacksTo(1), CultivationManualItem.Type.BODY_TEMPERING));
    public static final DeferredItem<CultivationManualItem> FETAL_BREATH_MANUAL = item("fetal_breath_manual",
            props -> new CultivationManualItem(props.stacksTo(1), CultivationManualItem.Type.FETAL_BREATH));
    public static final DeferredItem<SpiritCompassItem> SPIRIT_COMPASS = item("spirit_compass",
            props -> new SpiritCompassItem(props.stacksTo(1)));
    public static final DeferredItem<GuidedXuanhuanItem> SPIRIT_SPRING_BOTTLE = material("spirit_spring_bottle", "tooltip.earth_online_xuanhuan.spirit_spring_bottle");
    public static final DeferredItem<GuidedXuanhuanItem> SPIRIT_GRASS = material("spirit_grass", "tooltip.earth_online_xuanhuan.spirit_grass");
    public static final DeferredItem<GuidedXuanhuanItem> CINNABAR_SPIRIT_INK = material("cinnabar_spirit_ink", "tooltip.earth_online_xuanhuan.cinnabar_spirit_ink");
    public static final DeferredItem<GuidedXuanhuanItem> TALISMAN_PAPER = material("talisman_paper", "tooltip.earth_online_xuanhuan.talisman_paper");
    public static final DeferredItem<GuidedXuanhuanItem> BASIC_TALISMAN = material("basic_talisman", "tooltip.earth_online_xuanhuan.basic_talisman");
    public static final DeferredItem<GuidedXuanhuanItem> SPIRIT_CRYSTAL_SHARD = material("spirit_crystal_shard", "tooltip.earth_online_xuanhuan.spirit_crystal_shard");
    public static final DeferredItem<GuidedXuanhuanItem> SPIRIT_IRON_BLANK = material("spirit_iron_blank", "tooltip.earth_online_xuanhuan.spirit_iron_blank");
    public static final DeferredItem<GuidedXuanhuanItem> QI_RECOVERY_PILL = material("qi_recovery_pill", "tooltip.earth_online_xuanhuan.qi_recovery_pill");

    public EarthOnlineXuanhuan(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(this::registerCreativeTab);
        LOGGER.info("[Earth Online: Xuanhuan] NeoForge 26.2 module loaded");
    }

    private static DeferredBlock<XuanhuanBlock> block(String id, Supplier<BlockBehaviour.Properties> props, String hintKey) {
        DeferredBlock<XuanhuanBlock> block = BLOCKS.registerBlock(id, p -> new XuanhuanBlock(p, hintKey), props);
        DeferredItem<?> item = ITEMS.registerItem(id,
                itemProps -> new GuidedXuanhuanBlockItem(block.get(), itemProps, "tooltip.earth_online_xuanhuan.block"),
                itemProps -> itemProps);
        TAB_ITEMS.add(item);
        return block;
    }

    private static BlockBehaviour.Properties stone(MapColor color, float strength) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(strength, strength * 2.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE);
    }

    private static DeferredItem<GuidedXuanhuanItem> material(String id, String hintKey) {
        return item(id, props -> new GuidedXuanhuanItem(props, hintKey));
    }

    private static <T extends Item> DeferredItem<T> item(String id, Function<Item.Properties, T> factory) {
        DeferredItem<T> item = ITEMS.registerItem(id, factory, props -> props);
        TAB_ITEMS.add(item);
        return item;
    }

    private void registerCreativeTab(RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> helper.register(id("earth_online_xuanhuan"),
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.earth_online_xuanhuan"))
                        .icon(() -> new ItemStack(SPIRIT_COMPASS.get()))
                        .displayItems((params, output) -> TAB_ITEMS.forEach(output::accept))
                        .build()));
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
