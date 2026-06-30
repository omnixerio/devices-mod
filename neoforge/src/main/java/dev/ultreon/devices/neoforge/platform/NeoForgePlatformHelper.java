package dev.ultreon.devices.neoforge.platform;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.platform.client.PayloadHandler;
import dev.ultreon.devices.platform.services.BlockEntitySupplier;
import dev.ultreon.devices.platform.services.IPlatformHelper;
import dev.ultreon.devices.platform.services.RegistrySupplier;
import dev.ultreon.devices.platform.services.ServerPayloadContext;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.i18n.MavenVersionTranslator;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = OmnixerioDevicesCommon.MOD_ID)
public class NeoForgePlatformHelper implements IPlatformHelper {

    private static final List<ClientNetworkCodec<?>> CLIENT_NETWORK_CODECS = new ArrayList<>();
    private static final List<ServerNetworkHandler<?>> SERVER_NETWORK_HANDLERS = new ArrayList<>();

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.getCurrent().isProduction();
    }

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(OmnixerioDevicesCommon.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OmnixerioDevicesCommon.MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, OmnixerioDevicesCommon.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OmnixerioDevicesCommon.MOD_ID);
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, OmnixerioDevicesCommon.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OmnixerioDevicesCommon.MOD_ID);
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, OmnixerioDevicesCommon.MOD_ID);

    public void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
    }

    public static class NeoForgeRegistrySupplier<T> implements RegistrySupplier<T> {
        private final DeferredHolder<? super T, T> value;

        public NeoForgeRegistrySupplier(DeferredHolder<? super T, T> value) {
            this.value = value;
        }

        @Override
        public Identifier getId() {
            return value.getId();
        }

        @Override
        public boolean isBound() {
            return value.isBound();
        }

        @Override
        public T get() {
            return value.get();
        }
    }

    @Override
    public <T extends Block> RegistrySupplier<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory, Supplier<BlockBehaviour.Properties> settings, boolean registerItem) {
        DeferredBlock<T> deferredBlock = BLOCKS.register(name, (id) -> blockFactory.apply(settings.get().setId(ResourceKey.create(Registries.BLOCK, id))));
        if (registerItem) {
            ITEMS.register(name, (id) -> new BlockItem(deferredBlock.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
        }
        return new NeoForgeRegistrySupplier<>(deferredBlock);
    }

    @Override
    public <T extends Item> RegistrySupplier<T> registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        DeferredItem<T> deferredItem = ITEMS.register(name, (id) -> itemFactory.apply(settings.setId(ResourceKey.create(Registries.ITEM, id))));
        return new NeoForgeRegistrySupplier<>(deferredItem);
    }

    @Override
    public <T extends Entity> RegistrySupplier<EntityType<T>> registerEntityType(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, OmnixerioDevicesCommon.id(name));
        DeferredHolder<EntityType<?>, EntityType<T>> entityType = ENTITY_TYPES.register(name, () -> builder.build(key));
        return new NeoForgeRegistrySupplier<>(entityType);
    }

    @Override
    public RegistrySupplier<SoundEvent> registerSound(String name, Function<Identifier, SoundEvent> func) {
        DeferredHolder<SoundEvent, SoundEvent> soundEvent = SOUND_EVENTS.register(name, func);
        return new NeoForgeRegistrySupplier<>(soundEvent);
    }

    @Override
    public <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> registerBlockEntity(String id, BlockEntitySupplier<T> factory, Set<RegistrySupplier<? extends Block>> validBlocks) {
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> blockEntityType = BLOCK_ENTITY_TYPES.register(id, () -> new BlockEntityType<>(factory::create, validBlocks.stream().map(RegistrySupplier::get).collect(Collectors.toSet())));
        return new NeoForgeRegistrySupplier<>(blockEntityType);
    }

    public RegistrySupplier<CreativeModeTab> registerCreativeModeTab(String name, Component title, CreativeModeTab.DisplayItemsGenerator displayItems) {
        return new NeoForgeRegistrySupplier<>(CREATIVE_MODE_TABS.register(name, () -> CreativeModeTab.builder().title(title).displayItems(displayItems).build()));
    }

    @Override
    public <T> RegistrySupplier<DataComponentType<T>> registerDataComponent(String name, Supplier<DataComponentType<T>> type) {
        return new NeoForgeRegistrySupplier<>(DATA_COMPONENTS.register(name, type));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload syncBlockPacket) {
        PacketDistributor.sendToPlayer(player, syncBlockPacket);
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientboundPlay(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec) {
        CLIENT_NETWORK_CODECS.add(new ClientNetworkCodec<>(type, codec));
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerboundPlay(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec, PayloadHandler<T, ServerPayloadContext> handler) {
        SERVER_NETWORK_HANDLERS.add(new ServerNetworkHandler<>(type, codec, handler));
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.getDist() == Dist.CLIENT;
    }

    @Override
    public boolean isServer() {
        return FMLEnvironment.getDist() == Dist.DEDICATED_SERVER;
    }

    @Override
    public String getModVersion() {
        return MavenVersionTranslator.artifactVersionToString(ModList.get().getModContainerById(OmnixerioDevicesCommon.MOD_ID).orElseThrow().getModInfo().getVersion());
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @SubscribeEvent
    public static void registerNetwork(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(IPlatformHelper.NETWORK_VERSION);
        for (ClientNetworkCodec<?> codec : CLIENT_NETWORK_CODECS) {
            codec.register(registrar);
        }
        for (ServerNetworkHandler<?> handler : SERVER_NETWORK_HANDLERS) {
            handler.register(registrar);
        }
    }
}
