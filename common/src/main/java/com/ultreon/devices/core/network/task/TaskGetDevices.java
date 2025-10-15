package com.ultreon.devices.core.network.task;

import com.mojang.datafixers.util.Either;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.core.network.NetworkDevice;
import com.ultreon.devices.core.network.Router;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collection;

/**
 * @author MrCrayfish
 */
public class TaskGetDevices extends Task {
    private TagKey<BlockEntityType<?>> targetTypes;
    private BlockPos devicePos;
    private BlockEntityType<?> targetType;

    private Collection<NetworkDevice> foundDevices;
    private String reason;

    public TaskGetDevices() {
        super("get_network_devices");
    }

    public TaskGetDevices(BlockPos devicePos) {
        this();
        this.devicePos = devicePos;
    }

    public TaskGetDevices(BlockPos devicePos, BlockEntityType<?> targetType) {
        this();
        this.devicePos = devicePos;
        this.targetType = targetType;
    }

    public TaskGetDevices(BlockPos devicePos, TagKey<BlockEntityType<?>> targetTypes) {
        this();
        this.devicePos = devicePos;
        this.targetTypes = targetTypes;
    }

    /**
     * @deprecated Use {@link #TaskGetDevices(BlockPos, BlockEntityType)} instead.
     */
    @Deprecated(forRemoval = true)
    public TaskGetDevices(BlockPos devicePos, Class<? extends NetworkDeviceBlockEntity> ignoredTargetDeviceClass) {
        this();
        this.devicePos = devicePos;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putLong("devicePos", devicePos.asLong());
        if (targetType != null) {
            tag.putInt("targetType", BuiltInRegistries.BLOCK_ENTITY_TYPE.getId(targetType));
        } else if (targetTypes != null) {
            tag.putString("targetTypes", this.targetTypes.location().toString());
        }
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockPos devicePos = BlockPos.of(tag.getLong("devicePos"));
        Either<BlockEntityType<?>, TagKey<BlockEntityType<?>>> targetTypes = null;
        if (tag.contains("targetType", Tag.TAG_INT)) {
            int typeId = tag.getInt("targetType");
            if (typeId < 0) {
                this.reason = "Invalid target ID received: " + typeId;
                return;
            }

            targetTypes = Either.left(BuiltInRegistries.BLOCK_ENTITY_TYPE.byId(typeId));
        } else if (tag.contains("targetTypes", Tag.TAG_STRING)) {
            ResourceLocation tagKeyId = ResourceLocation.tryParse(tag.getString("targetTypes"));
            var tagPair = BuiltInRegistries.BLOCK_ENTITY_TYPE.getTags()
                    .filter(pair -> pair.getFirst().location().equals(tagKeyId))
                    .findFirst();

            if (tagPair.isPresent()) {
                targetTypes = Either.right(tagPair.get().getFirst());
            }
        }

        BlockEntity blockEntity = level.getBlockEntity(devicePos);

        if (!(blockEntity instanceof NetworkDeviceBlockEntity networkDevice)) {
            this.reason = "Not a network device";
            return;
        }

        if (!networkDevice.isConnected()) {
            this.reason = "Not connected to router";
            return;
        }

        Router router = networkDevice.getRouter();
        if (router == null) {
            this.reason = "No internet access";
            return;
        }

        if (targetTypes != null) {
            targetTypes.ifLeft(type -> this.foundDevices = router.getConnectedDevices(level, type));
            targetTypes.ifRight(tagKey -> this.foundDevices = router.getConnectedDevices(level, tagKey));
        } else {
            this.foundDevices = router.getConnectedDevices(level);
        }

        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (this.isSucessful()) {
            ListTag deviceList = new ListTag();
            foundDevices.forEach(device -> deviceList.add(device.toTag(true)));
            tag.put("network_devices", deviceList);
        } else {
            tag.putString("reason", this.reason);
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {
        // Does not need response processing
    }
}
