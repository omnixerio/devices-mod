package dev.ultreon.devices.neoforge.platform.client;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.platform.client.ClientPayloadContext;
import dev.ultreon.devices.platform.client.IClientPlatformHelper;
import dev.ultreon.devices.platform.client.PayloadHandler;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = OmnixerioDevicesCommon.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientPlatformHelper implements IClientPlatformHelper {
    private static final List<BlockEntityRendererEntry<?, ?>> BLOCK_ENTITY_RENDERERS = new ArrayList<>();
    private static final List<EntityRendererEntry<?, ?>> ENTITY_RENDERERS = new ArrayList<>();
    private static final List<dev.ultreon.devices.neoforge.platform.client.ClientNetworkHandler<?>> NETWORK_HANDLERS = new ArrayList<>();

    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (EntityRendererEntry<?, ?> entry : ENTITY_RENDERERS) {
            entry.register(event);
        }

        for (BlockEntityRendererEntry<?, ?> entry : BLOCK_ENTITY_RENDERERS) {
            entry.register(event);
        }
    }

    @Override
    public <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> blockEntityType, BlockEntityRendererProvider<T, S> blockEntityRenderer) {
        BLOCK_ENTITY_RENDERERS.add(new BlockEntityRendererEntry<>(blockEntityType, blockEntityRenderer));
    }

    @Override
    public <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> entityType, EntityRendererProvider<T> entityRenderer) {
        ENTITY_RENDERERS.add(new EntityRendererEntry<>(entityType, entityRenderer));
    }

    @Override
    public void sendToServer(CustomPacketPayload syncBlockPacket) {
        ClientPacketDistributor.sendToServer(syncBlockPacket);
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientboundPlay(CustomPacketPayload.Type<T> type, PayloadHandler<T, ClientPayloadContext> o) {
        NETWORK_HANDLERS.add(new ClientNetworkHandler<>(type, o));
    }

    @SubscribeEvent // on the mod event bus only on the physical client
    public static void register(RegisterClientPayloadHandlersEvent event) {
        for (dev.ultreon.devices.neoforge.platform.client.ClientNetworkHandler<?> handler : NETWORK_HANDLERS) {
            handler.register(event);
        }
    }
}
