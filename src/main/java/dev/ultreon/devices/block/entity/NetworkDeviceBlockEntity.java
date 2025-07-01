package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.DyeColor;
import dev.ultreon.devices.core.network.Connection;
import dev.ultreon.devices.core.network.Router;
import dev.ultreon.devices.util.Colorable;
import dev.ultreon.devices.util.Tickable;
import dev.ultreon.quantum.block.entity.BlockEntityType;
import dev.ultreon.quantum.ubo.types.MapType;
import dev.ultreon.quantum.world.World;
import dev.ultreon.quantum.world.vec.BlockVec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class NetworkDeviceBlockEntity extends DeviceBlockEntity implements Tickable {
    private int counter;
    private Connection connection;

    public NetworkDeviceBlockEntity(BlockEntityType<?> type, World world, BlockVec pos) {
        super(type, world, pos);
    }

    public void tick() {
        if (world.isClientSide())
            return;

        if (connection != null) {
            if (++counter >= DeviceConfig.BEACON_INTERVAL.getValue() * 2) {
                connection.setRouterPos(null);
                counter = 0;
            }
        }
    }

    public void connect(Router router) {
        assert world != null;
        if (router == null) {
            if (connection != null) {
                Router connectedRouter = connection.getRouter(world);
                if (connectedRouter != null) {
                    connectedRouter.removeDevice(this);
                }
            }

            connection = null;
            return;
        }
        connection = new Connection(router);
        counter = 0;
        this.setChanged();
    }

    public Connection getConnection() {
        return connection;
    }

    @Nullable
    public Router getRouter() {
        return connection != null ? connection.getRouter(Objects.requireNonNull(world)) : null;
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public boolean receiveBeacon(Router router) {
        if (counter >= DeviceConfig.BEACON_INTERVAL.get() * 2) {
            connect(router);
            return true;
        }
        if (connection != null && connection.getRouterId().equals(router.getId())) {
            connection.setRouterPos(router.getPos());
            counter = 0;
            return true;
        }
        return false;
    }

    public int getSignalStrength() {
        BlockPos routerPos = connection != null ? connection.getRouterPos() : null;
        if (routerPos != null) {
            double distance = Math.sqrt(worldPosition.distToCenterSqr(routerPos.getX() + 0.5, routerPos.getY() + 0.5, routerPos.getZ() + 0.5));
            double world = DeviceConfig.SIGNAL_RANGE.get() / 3d;
            return distance > world * 2 ? 2 : distance > world ? 1 : 0;
        }
        return -1;
    }

    @Nullable
    @Override
    public Component getDisplayName() {
        return Component.literal(getCustomName());
    }

    @Override
    public void save(@NotNull MapType tag) {
        super.save(tag);
        if (connection != null) {
            tag.put("connection", connection.toTag());
        }
    }

    @Override
    public void load(@NotNull MapType tag) {
        super.load(tag);
        if (tag.contains("connection", Tag.TAG_COMPOUND)) {
            connection = Connection.fromTag(tag.getCompound("connection"));
        }
    }

    public static abstract class Colored extends NetworkDeviceBlockEntity implements Colorable {
        private DyeColor color = DyeColor.RED;

        public Colored(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(pType, pWorldPosition, pBlockState);
        }

        @Override
        public void load(@NotNull MapType tag) {
            super.load(tag);
            if (tag.contains("color", Tag.TAG_STRING)) {
                color = DyeColor.byId(tag.getByte("color"));
            }
        }

        @Override
        public void save(@NotNull MapType tag) {
            super.save(tag);
            tag.putByte("color", (byte) color.getId());
        }

        @Override
        public MapType saveSyncTag() {
            MapType tag = super.saveSyncTag();
            tag.putByte("color", (byte) color.getId());
            return tag;
        }

        @Override
        public void setColor(DyeColor color) {
            this.color = color;
        }

        @Override
        public DyeColor getColor() {
            return color;
        }
    }
}
