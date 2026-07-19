package com.xxsx.earthonline.xuanhuan.client;

import com.xxsx.earthonline.xuanhuan.client.model.CrystalTurtleModel;
import com.xxsx.earthonline.xuanhuan.client.model.CultivationSettlerModel;
import com.xxsx.earthonline.xuanhuan.client.model.EmberCraneModel;
import com.xxsx.earthonline.xuanhuan.client.model.SpiritFoxModel;
import com.xxsx.earthonline.xuanhuan.client.model.StonehornGoatModel;
import com.xxsx.earthonline.xuanhuan.client.model.WindWolfModel;
import com.xxsx.earthonline.xuanhuan.client.renderer.CultivationSettlerRenderer;
import com.xxsx.earthonline.xuanhuan.client.renderer.SpiritBeastRenderer;
import com.xxsx.earthonline.xuanhuan.client.renderer.SpiritFoxRenderer;
import com.xxsx.earthonline.xuanhuan.EarthOnlineXuanhuan;
import com.xxsx.earthonline.xuanhuan.CultivationActionPayload;
import com.xxsx.earthonline.xuanhuan.CultivationFocus;
import com.xxsx.earthonline.xuanhuan.CultivationStatusPayload;
import com.xxsx.earthonline.xuanhuan.CultivationVisualPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.lwjgl.glfw.GLFW;

public final class EarthOnlineXuanhuanClient {
    private static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(EarthOnlineXuanhuan.id("controls"));
    private static final KeyMapping OPEN_CULTIVATION = new KeyMapping(
            "key.earth_online_xuanhuan.open_cultivation",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            CATEGORY);
    private static final KeyMapping ACTIVATE_TECHNIQUE = new KeyMapping(
            "key.earth_online_xuanhuan.activate_technique",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY);
    private static CultivationStatusPayload cultivationStatus = CultivationStatusPayload.empty();
    private EarthOnlineXuanhuanClient() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(EarthOnlineXuanhuanClient::registerScreens);
        modBus.addListener(EarthOnlineXuanhuanClient::registerLayerDefinitions);
        modBus.addListener(EarthOnlineXuanhuanClient::registerEntityRenderers);
        modBus.addListener(EarthOnlineXuanhuanClient::registerGuiLayers);
        modBus.addListener(EarthOnlineXuanhuanClient::registerPayloadHandlers);
        modBus.addListener(EarthOnlineXuanhuanClient::registerKeyMappings);
        NeoForge.EVENT_BUS.addListener(EarthOnlineXuanhuanClient::clientTick);
        NeoForge.EVENT_BUS.addListener(CultivationPlayerAnimations::renderPlayer);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(EarthOnlineXuanhuan.XUANHUAN_MACHINE_MENU.get(), XuanhuanMachineScreen::new);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EarthOnlineXuanhuan.MEDITATION_SEAT.get(), InvisibleSeatRenderer::new);
        event.registerEntityRenderer(EarthOnlineXuanhuan.SPIRIT_FOX.get(), SpiritFoxRenderer::new);
        event.registerEntityRenderer(EarthOnlineXuanhuan.WIND_WOLF.get(), context ->
                new SpiritBeastRenderer<>(context, WindWolfModel.LAYER_LOCATION, WindWolfModel::new,
                        EarthOnlineXuanhuan.id("textures/entity/wind_wolf.png"), 0.5F, 1.0F));
        event.registerEntityRenderer(EarthOnlineXuanhuan.STONEHORN_GOAT.get(), context ->
                new SpiritBeastRenderer<>(context, StonehornGoatModel.LAYER_LOCATION, StonehornGoatModel::new,
                        EarthOnlineXuanhuan.id("textures/entity/stonehorn_goat.png"), 0.68F, 1.0F));
        event.registerEntityRenderer(EarthOnlineXuanhuan.CRYSTAL_TURTLE.get(), context ->
                new SpiritBeastRenderer<>(context, CrystalTurtleModel.LAYER_LOCATION, CrystalTurtleModel::new,
                        EarthOnlineXuanhuan.id("textures/entity/crystal_turtle.png"), 0.62F, 1.0F));
        event.registerEntityRenderer(EarthOnlineXuanhuan.EMBER_CRANE.get(), context ->
                new SpiritBeastRenderer<>(context, EmberCraneModel.LAYER_LOCATION, EmberCraneModel::new,
                        EarthOnlineXuanhuan.id("textures/entity/ember_crane.png"), 0.42F, 1.0F));
        event.registerEntityRenderer(EarthOnlineXuanhuan.CULTIVATION_SETTLER.get(), CultivationSettlerRenderer::new);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpiritFoxModel.LAYER_LOCATION, SpiritFoxModel::createBodyLayer);
        event.registerLayerDefinition(WindWolfModel.LAYER_LOCATION, WindWolfModel::createBodyLayer);
        event.registerLayerDefinition(StonehornGoatModel.LAYER_LOCATION, StonehornGoatModel::createBodyLayer);
        event.registerLayerDefinition(CrystalTurtleModel.LAYER_LOCATION, CrystalTurtleModel::createBodyLayer);
        event.registerLayerDefinition(EmberCraneModel.LAYER_LOCATION, EmberCraneModel::createBodyLayer);
        event.registerLayerDefinition(CultivationSettlerModel.LAYER_LOCATION, CultivationSettlerModel::createBodyLayer);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(EarthOnlineXuanhuan.id("meditation_hud"), MeditationHud::render);
    }

    private static void registerPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(CultivationStatusPayload.TYPE, EarthOnlineXuanhuanClient::handleCultivationStatus);
        event.register(CultivationVisualPayload.TYPE, EarthOnlineXuanhuanClient::handleCultivationVisual);
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(OPEN_CULTIVATION);
        event.register(ACTIVATE_TECHNIQUE);
    }

    private static void clientTick(ClientTickEvent.Post event) {
        CultivationPlayerAnimations.tick();
        while (OPEN_CULTIVATION.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null && minecraft.getConnection() != null) {
                requestOpenCultivation();
            }
        }
        while (ACTIVATE_TECHNIQUE.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null && minecraft.getConnection() != null && minecraft.gui.screen() == null) {
                requestActivateTechnique();
            }
        }
    }

    private static void handleCultivationStatus(CultivationStatusPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            cultivationStatus = payload;
            if (payload.openScreen() && !(Minecraft.getInstance().gui.screen() instanceof CultivationScreen)) {
                Minecraft.getInstance().gui.setScreen(new CultivationScreen());
            }
        });
    }

    private static void handleCultivationVisual(CultivationVisualPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> CultivationPlayerAnimations.start(payload));
    }

    public static CultivationStatusPayload cultivationStatus() {
        return cultivationStatus;
    }

    public static void requestCultivationFocus(CultivationFocus focus) {
        ClientPacketDistributor.sendToServer(new CultivationActionPayload(focus.id()));
    }

    public static void requestStopCultivating() {
        ClientPacketDistributor.sendToServer(new CultivationActionPayload(CultivationActionPayload.STOP_RIDING));
    }

    public static void requestOpenCultivation() {
        ClientPacketDistributor.sendToServer(new CultivationActionPayload(CultivationActionPayload.OPEN_PANEL));
    }

    public static void requestPractice() {
        ClientPacketDistributor.sendToServer(new CultivationActionPayload(CultivationActionPayload.PRACTICE));
    }

    public static void requestActivateTechnique() {
        ClientPacketDistributor.sendToServer(new CultivationActionPayload(CultivationActionPayload.ACTIVATE_SKILL));
    }

    public static void requestCultivationRefresh() {
        ClientPacketDistributor.sendToServer(new CultivationActionPayload(CultivationActionPayload.REFRESH_STATUS));
    }

    public static void openHandbook() {
        if (Minecraft.getInstance().getConnection() != null) {
            requestCultivationRefresh();
        }
        Minecraft.getInstance().gui.setScreen(new XuanhuanHandbookScreen());
    }
}
