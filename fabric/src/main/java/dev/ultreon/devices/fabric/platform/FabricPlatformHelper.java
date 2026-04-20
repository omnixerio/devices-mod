package dev.ultreon.devices.fabric.platform;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.init.DeviceBlocks;
import dev.ultreon.devices.platform.client.PayloadHandler;
import dev.ultreon.devices.platform.services.BlockEntitySupplier;
import dev.ultreon.devices.platform.services.IPlatformHelper;
import dev.ultreon.devices.platform.services.RegistrySupplier;
import dev.ultreon.devices.platform.services.ServerPayloadContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.UnknownNullability;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static class FabricRegistrySupplier<T> implements RegistrySupplier<T> {
        private final T value;
        private final Identifier id;

        public FabricRegistrySupplier(T value, Identifier id) {
            this.value = value;
            this.id = id;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Identifier getId() {
            return id;
        }

        @Override
        public boolean isBound() {
            return true;
        }
    }

    @Override
    public <T extends Block> RegistrySupplier<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory, @UnknownNullability Supplier<BlockBehaviour.Properties> settings, boolean registerItem) {
        // Create a registry key for the block
        ResourceKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        T block = blockFactory.apply(settings.get().setId(blockKey));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (registerItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            ResourceKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        return new FabricRegistrySupplier<>(block, blockKey.identifier());
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, name));
    }

    @Override
    public <T extends Item> RegistrySupplier<T> registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, name));

        // Create the item instance.
        T item = itemFactory.apply(settings.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return new FabricRegistrySupplier<>(item, itemKey.identifier());
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, name));
    }

    @Override
    public <T extends Entity> RegistrySupplier<EntityType<T>> registerEntityType(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, name));
        EntityType<T> build = builder.build(key);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, key, build);
        return new FabricRegistrySupplier<>(build, key.identifier());
    }

    @Override
    public RegistrySupplier<SoundEvent> registerSound(String name, Function<Identifier, SoundEvent> func) {
        SoundEvent apply = func.apply(OmnixerioDevicesCommon.id(name));
        Registry.register(BuiltInRegistries.SOUND_EVENT, OmnixerioDevicesCommon.id(name), apply);
        return new FabricRegistrySupplier<>(apply, OmnixerioDevicesCommon.id(name));
    }

    @Override
    public final <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> registerBlockEntity(String name, BlockEntitySupplier<T> factory, Set<RegistrySupplier<? extends Block>> validBlocks) {
        FabricBlockEntityTypeBuilder<T> builder = FabricBlockEntityTypeBuilder.create(factory::create);
        for (RegistrySupplier<? extends Block> validBlock : validBlocks) {
            builder.addBlock(validBlock.get());
        }
        BlockEntityType<T> blockEntityType = builder.build();
        Identifier id = Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, name);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, blockEntityType);
        return new FabricRegistrySupplier<>(blockEntityType, id);
    }

    @Override
    public RegistrySupplier<CreativeModeTab> registerCreativeModeTab(String name, Component title, CreativeModeTab.DisplayItemsGenerator displayItems) {
        CreativeModeTab build = FabricCreativeModeTab.builder().title(title).icon(() -> new ItemStack(DeviceBlocks.LAPTOPS.of(DyeColor.RED).asItem())).displayItems(displayItems).build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, OmnixerioDevicesCommon.id(name), build);
        return new FabricRegistrySupplier<>(build, OmnixerioDevicesCommon.id(name));
    }

    @Override
    public <T> RegistrySupplier<DataComponentType<T>> registerDataComponent(String name, Supplier<DataComponentType<T>> type) {
        DataComponentType<T> componentType = type.get();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, OmnixerioDevicesCommon.id(name), componentType);
        return new FabricRegistrySupplier<>(componentType, OmnixerioDevicesCommon.id(name));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload syncBlockPacket) {
        ServerPlayNetworking.send(player, syncBlockPacket);
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientboundPlay(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> streamCodec) {
        PayloadTypeRegistry.clientboundPlay().register(type, streamCodec);
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerboundPlay(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec, PayloadHandler<T, ServerPayloadContext> handler) {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (packet, context) -> handler.handle(packet, new ServerPayloadContext(context.player(), context.server())));
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean isServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    @Override
    public String getModVersion() {
        return FabricLoader.getInstance().getModContainer(OmnixerioDevicesCommon.MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
