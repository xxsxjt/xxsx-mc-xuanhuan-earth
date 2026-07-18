package com.xxsx.earthonline.xuanhuan;

import com.mojang.logging.LogUtils;
import com.xxsx.earthonline.xuanhuan.client.EarthOnlineXuanhuanClient;
import com.xxsx.earthonline.xuanhuan.entity.ContractableSpiritBeastEntity;
import com.xxsx.earthonline.xuanhuan.entity.CrystalTurtleEntity;
import com.xxsx.earthonline.xuanhuan.entity.CultivationSettlerEntity;
import com.xxsx.earthonline.xuanhuan.entity.EmberCraneEntity;
import com.xxsx.earthonline.xuanhuan.entity.SpiritFoxEntity;
import com.xxsx.earthonline.xuanhuan.entity.StonehornGoatEntity;
import com.xxsx.earthonline.xuanhuan.entity.WindWolfEntity;
import com.xxsx.earthonline.xuanhuan.settlement.XuanhuanSettlementAnchorBlock;
import com.xxsx.earthonline.xuanhuan.settlement.XuanhuanSettlementAnchorBlockEntity;
import com.xxsx.earthonline.xuanhuan.settlement.XuanhuanSettlementCatalog;
import com.xxsx.earthonline.xuanhuan.settlement.XuanhuanSettlementFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
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
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MODID);

    private static final List<ItemLike> TAB_ITEMS = new ArrayList<>();
    private static final List<DeferredBlock<XuanhuanMachineBlock>> MACHINE_BLOCKS = new ArrayList<>();

    public static final DeferredBlock<XuanhuanBlock> SPIRIT_VEIN_NODE = block("spirit_vein_node",
            () -> stone(MapColor.COLOR_PURPLE, 4.0F), "message.earth_online_xuanhuan.block.spirit_vein_node");
    public static final DeferredBlock<XuanhuanBlock> SPIRIT_SPRING_STONE = block("spirit_spring_stone",
            () -> stone(MapColor.WATER, 2.5F), "message.earth_online_xuanhuan.block.spirit_spring_stone");
    public static final DeferredBlock<XuanhuanBlock> SPIRIT_SOIL = block("spirit_soil",
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.6F).sound(SoundType.GRAVEL),
            "message.earth_online_xuanhuan.block.spirit_soil");
    public static final DeferredBlock<MeditationCushionBlock> MEDITATION_CUSHION = meditationBlock("meditation_cushion",
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.25F).sound(SoundType.WOOL).noOcclusion());
    public static final DeferredHolder<EntityType<?>, EntityType<MeditationSeatEntity>> MEDITATION_SEAT =
            ENTITY_TYPES.register("meditation_seat", () -> EntityType.Builder
                    .of(MeditationSeatEntity::new, MobCategory.MISC)
                    .sized(0.01F, 0.01F)
                    .passengerAttachments(new Vec3(0.0D, 0.18D, 0.0D))
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .noSummon()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("meditation_seat"))));
    public static final DeferredHolder<EntityType<?>, EntityType<SpiritFoxEntity>> SPIRIT_FOX =
            ENTITY_TYPES.register("spirit_fox", () -> EntityType.Builder
                    .of(SpiritFoxEntity::new, MobCategory.CREATURE)
                    .sized(0.75F, 0.8F)
                    .eyeHeight(0.58F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("spirit_fox"))));
    public static final DeferredHolder<EntityType<?>, EntityType<WindWolfEntity>> WIND_WOLF =
            ENTITY_TYPES.register("wind_wolf", () -> EntityType.Builder
                    .of(WindWolfEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 1.15F)
                    .eyeHeight(0.86F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("wind_wolf"))));
    public static final DeferredHolder<EntityType<?>, EntityType<StonehornGoatEntity>> STONEHORN_GOAT =
            ENTITY_TYPES.register("stonehorn_goat", () -> EntityType.Builder
                    .of(StonehornGoatEntity::new, MobCategory.CREATURE)
                    .sized(1.25F, 1.5F)
                    .eyeHeight(1.22F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("stonehorn_goat"))));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalTurtleEntity>> CRYSTAL_TURTLE =
            ENTITY_TYPES.register("crystal_turtle", () -> EntityType.Builder
                    .of(CrystalTurtleEntity::new, MobCategory.CREATURE)
                    .sized(1.25F, 0.72F)
                    .eyeHeight(0.42F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("crystal_turtle"))));
    public static final DeferredHolder<EntityType<?>, EntityType<EmberCraneEntity>> EMBER_CRANE =
            ENTITY_TYPES.register("ember_crane", () -> EntityType.Builder
                    .of(EmberCraneEntity::new, MobCategory.CREATURE)
                    .sized(0.78F, 1.82F)
                    .eyeHeight(1.58F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("ember_crane"))));
    public static final DeferredHolder<EntityType<?>, EntityType<CultivationSettlerEntity>> CULTIVATION_SETTLER =
            ENTITY_TYPES.register("cultivation_settler", () -> EntityType.Builder
                    .of(CultivationSettlerEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .eyeHeight(1.62F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("cultivation_settler"))));
    public static final DeferredBlock<XuanhuanSettlementAnchorBlock> SETTLEMENT_ANCHOR =
            BLOCKS.registerBlock("settlement_anchor", XuanhuanSettlementAnchorBlock::new,
                    () -> stone(MapColor.COLOR_CYAN, 5.0F).noOcclusion());
    public static final DeferredBlock<XuanhuanMachineBlock> ALCHEMY_FURNACE = machineBlock("alchemy_furnace",
            XuanhuanMachineBlock.Kind.ALCHEMY_FURNACE, () -> stone(MapColor.TERRACOTTA_BLACK, 3.5F));
    public static final DeferredBlock<XuanhuanMachineBlock> TALISMAN_TABLE = machineBlock("talisman_table",
            XuanhuanMachineBlock.Kind.TALISMAN_TABLE,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<XuanhuanMachineBlock> SPIRIT_ARRAY_CORE = machineBlock("spirit_array_core",
            XuanhuanMachineBlock.Kind.SPIRIT_ARRAY_CORE, () -> stone(MapColor.COLOR_CYAN, 4.0F));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<XuanhuanMachineBlockEntity>> XUANHUAN_MACHINE_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("xuanhuan_machine", () -> new BlockEntityType<>(
                    XuanhuanMachineBlockEntity::new,
                    MACHINE_BLOCKS.stream().map(DeferredBlock::get).toArray(Block[]::new)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<XuanhuanSettlementAnchorBlockEntity>> SETTLEMENT_ANCHOR_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("settlement_anchor", () -> new BlockEntityType<>(
                    XuanhuanSettlementAnchorBlockEntity::new, SETTLEMENT_ANCHOR.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<XuanhuanMachineMenu>> XUANHUAN_MACHINE_MENU =
            MENUS.register("xuanhuan_machine", () -> IMenuTypeExtension.create(XuanhuanMachineMenu::new));

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
    public static final DeferredItem<BasicTalismanItem> BASIC_TALISMAN = item("basic_talisman",
            props -> new BasicTalismanItem(props, "tooltip.earth_online_xuanhuan.basic_talisman"));
    public static final DeferredItem<GuidedXuanhuanItem> SPIRIT_CRYSTAL_SHARD = material("spirit_crystal_shard", "tooltip.earth_online_xuanhuan.spirit_crystal_shard");
    public static final DeferredItem<GuidedXuanhuanItem> SPIRIT_IRON_BLANK = material("spirit_iron_blank", "tooltip.earth_online_xuanhuan.spirit_iron_blank");
    public static final DeferredItem<QiRecoveryPillItem> QI_RECOVERY_PILL = item("qi_recovery_pill",
            props -> new QiRecoveryPillItem(props, "tooltip.earth_online_xuanhuan.qi_recovery_pill"));
    public static final DeferredItem<GuidedXuanhuanItem> SPIRIT_BEAST_SEAL = material(
            "spirit_beast_seal", "tooltip.earth_online_xuanhuan.spirit_beast_seal");
    public static final DeferredItem<SpawnEggItem> SPIRIT_FOX_SPAWN_EGG = item("spirit_fox_spawn_egg",
            props -> new SpawnEggItem(props.spawnEgg(SPIRIT_FOX.get())));
    public static final DeferredItem<SpawnEggItem> WIND_WOLF_SPAWN_EGG = item("wind_wolf_spawn_egg",
            props -> new SpawnEggItem(props.spawnEgg(WIND_WOLF.get())));
    public static final DeferredItem<SpawnEggItem> STONEHORN_GOAT_SPAWN_EGG = item("stonehorn_goat_spawn_egg",
            props -> new SpawnEggItem(props.spawnEgg(STONEHORN_GOAT.get())));
    public static final DeferredItem<SpawnEggItem> CRYSTAL_TURTLE_SPAWN_EGG = item("crystal_turtle_spawn_egg",
            props -> new SpawnEggItem(props.spawnEgg(CRYSTAL_TURTLE.get())));
    public static final DeferredItem<SpawnEggItem> EMBER_CRANE_SPAWN_EGG = item("ember_crane_spawn_egg",
            props -> new SpawnEggItem(props.spawnEgg(EMBER_CRANE.get())));
    public static final DeferredItem<SpawnEggItem> CULTIVATION_SETTLER_SPAWN_EGG = item("cultivation_settler_spawn_egg",
            props -> new SpawnEggItem(props.spawnEgg(CULTIVATION_SETTLER.get())));

    public static final DeferredHolder<Feature<?>, XuanhuanSettlementFeature> WANDERING_MARKET_FEATURE =
            FEATURES.register("wandering_cultivator_market", () -> new XuanhuanSettlementFeature(
                    NoneFeatureConfiguration.CODEC, XuanhuanSettlementFeature.Type.WANDERING_MARKET));
    public static final DeferredHolder<Feature<?>, XuanhuanSettlementFeature> SPIRIT_SPRING_VILLAGE_FEATURE =
            FEATURES.register("spirit_spring_village", () -> new XuanhuanSettlementFeature(
                    NoneFeatureConfiguration.CODEC, XuanhuanSettlementFeature.Type.SPIRIT_SPRING_VILLAGE));
    public static final DeferredHolder<Feature<?>, XuanhuanSettlementFeature> SECT_OUTPOST_FEATURE =
            FEATURES.register("sect_frontier_outpost", () -> new XuanhuanSettlementFeature(
                    NoneFeatureConfiguration.CODEC, XuanhuanSettlementFeature.Type.SECT_OUTPOST));

    public EarthOnlineXuanhuan(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        ENTITY_TYPES.register(modBus);
        BLOCK_ENTITY_TYPES.register(modBus);
        MENUS.register(modBus);
        FEATURES.register(modBus);
        modBus.addListener(CultivationNetwork::registerPayloads);
        modBus.addListener(this::registerEntityAttributes);
        modBus.addListener(this::registerSpawnPlacements);
        NeoForge.EVENT_BUS.addListener(ArcanaEvents::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(XuanhuanSettlementCatalog::register);
        modBus.addListener(this::registerCreativeTab);
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            EarthOnlineXuanhuanClient.register(modBus);
        }
        LOGGER.info("[Xuanhuan Earth on Minecraft] NeoForge 26.2 module loaded");
    }

    private static DeferredBlock<XuanhuanBlock> block(String id, Supplier<BlockBehaviour.Properties> props, String hintKey) {
        DeferredBlock<XuanhuanBlock> block = BLOCKS.registerBlock(id, p -> new XuanhuanBlock(p, hintKey), props);
        DeferredItem<?> item = ITEMS.registerItem(id,
                itemProps -> new GuidedXuanhuanBlockItem(block.get(), itemProps, "tooltip.earth_online_xuanhuan.block"),
                itemProps -> itemProps);
        TAB_ITEMS.add(item);
        return block;
    }

    private static DeferredBlock<XuanhuanMachineBlock> machineBlock(String id, XuanhuanMachineBlock.Kind kind,
                                                                    Supplier<BlockBehaviour.Properties> props) {
        DeferredBlock<XuanhuanMachineBlock> block = BLOCKS.registerBlock(id, p -> new XuanhuanMachineBlock(p, kind), props);
        DeferredItem<?> item = ITEMS.registerItem(id,
                itemProps -> new GuidedXuanhuanBlockItem(block.get(), itemProps, "tooltip.earth_online_xuanhuan.machine.block"),
                itemProps -> itemProps);
        MACHINE_BLOCKS.add(block);
        TAB_ITEMS.add(item);
        return block;
    }

    private static DeferredBlock<MeditationCushionBlock> meditationBlock(String id, Supplier<BlockBehaviour.Properties> props) {
        DeferredBlock<MeditationCushionBlock> block = BLOCKS.registerBlock(id, MeditationCushionBlock::new, props);
        DeferredItem<?> item = ITEMS.registerItem(id,
                itemProps -> new GuidedXuanhuanBlockItem(block.get(), itemProps, "tooltip.earth_online_xuanhuan.meditation_cushion"),
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

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(SPIRIT_FOX.get(), SpiritFoxEntity.createAttributes().build());
        event.put(WIND_WOLF.get(), WindWolfEntity.createAttributes().build());
        event.put(STONEHORN_GOAT.get(), StonehornGoatEntity.createAttributes().build());
        event.put(CRYSTAL_TURTLE.get(), CrystalTurtleEntity.createAttributes().build());
        event.put(EMBER_CRANE.get(), EmberCraneEntity.createAttributes().build());
        event.put(CULTIVATION_SETTLER.get(), CultivationSettlerEntity.createAttributes().build());
    }

    private void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(SPIRIT_FOX.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SpiritFoxEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(WIND_WOLF.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ContractableSpiritBeastEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(STONEHORN_GOAT.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ContractableSpiritBeastEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CRYSTAL_TURTLE.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ContractableSpiritBeastEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EMBER_CRANE.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ContractableSpiritBeastEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
