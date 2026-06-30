package dev.ultreon.devices.platform.services;

import dev.ultreon.devices.platform.client.PayloadHandler;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import javax.xml.transform.URIResolver;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IPlatformHelper {
    String NETWORK_VERSION = "1";

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    <T extends Block> RegistrySupplier<T> registerBlock(String id, Function<BlockBehaviour.Properties, T> blockFactory, Supplier<BlockBehaviour.Properties> settings, boolean registerItem);

    <T extends Item> RegistrySupplier<T> registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings);

    <T extends Entity> RegistrySupplier<EntityType<T>> registerEntityType(String name, EntityType.Builder<T> builder);

    RegistrySupplier<SoundEvent> registerSound(String name, Function<Identifier, SoundEvent> func);

    <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> registerBlockEntity(String id, BlockEntitySupplier<T> factory, Set<RegistrySupplier<? extends Block>> validBlocks);

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    RegistrySupplier<CreativeModeTab> registerCreativeModeTab(String name, Component title, CreativeModeTab.DisplayItemsGenerator displayItems);

    <T> RegistrySupplier<DataComponentType<T>> registerDataComponent(String name, Supplier<DataComponentType<T>> type);

    void sendToPlayer(ServerPlayer player, CustomPacketPayload syncBlockPacket);

    <T extends CustomPacketPayload> void registerClientboundPlay(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> streamCodec);

    <T extends CustomPacketPayload> void registerServerboundPlay(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec, PayloadHandler<T, ServerPayloadContext> handler);

    boolean isClient();

    boolean isServer();

    String getModVersion();

    Path getConfigDir();

    Path getGameDir();
}
