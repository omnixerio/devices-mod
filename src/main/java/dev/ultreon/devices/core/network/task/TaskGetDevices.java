package dev.ultreon.devices.core.network.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import dev.ultreon.devices.core.network.NetworkDevice;
import dev.ultreon.devices.core.network.Router;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Collection;

/**
 * @author MrCrayfish
 */
public class TaskGetDevices extends Task {
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
        }
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockPos devicePos = BlockPos.of(tag.getLong("devicePos"));
        BlockEntityType<?> targetType;
        int typeId = tag.getInt("targetType");
        if (typeId < 0) {
            this.reason = "Invalid target ID received: " + typeId;
            return;
        }

        targetType = BuiltInRegistries.BLOCK_ENTITY_TYPE.byId(typeId);

        BlockEntity tileEntity = level.getChunkAt(devicePos).getBlockEntity(devicePos, LevelChunk.EntityCreationType.IMMEDIATE);

        if (!(tileEntity instanceof NetworkDeviceBlockEntity tileEntityNetworkDevice)) {
            this.reason = "Not a network device";
            return;
        }

        if (!tileEntityNetworkDevice.isConnected()) {
            this.reason = "Not connected to router";
            return;
        }

        Router router = tileEntityNetworkDevice.getRouter();
        if (router == null) {
            this.reason = "No internet access";
            return;
        }

        this.foundDevices = targetType != null ? router.getConnectedDevices(level) : router.getConnectedDevices(level, targetType);
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
