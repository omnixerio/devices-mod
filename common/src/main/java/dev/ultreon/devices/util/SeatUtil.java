package dev.ultreon.devices.util;

import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.entity.Seat;
import dev.ultreon.devices.init.DeviceEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SeatUtil {
    public static void createSeatAndSit(Level worldIn, BlockPos pos, Player playerIn, double yOffset) {
        List<Seat> seats = worldIn.getEntitiesOfClass(Seat.class, new AABB(pos));
        if(!seats.isEmpty()) {
            Seat seat = seats.get(0);
            if(seat.getFirstPassenger() == null) {
                playerIn.startRiding(seat);
            }
        } else {
            Seat seat = DeviceEntities.SEAT.get().create(worldIn);// new SeatEntity(worldIn, pos, yOffset);
            assert seat != null;
            seat.setYOffset(yOffset);
            seat.setViaYOffset(pos);
            DebugLog.log(seat);
            worldIn.addFreshEntity(seat);
            playerIn.startRiding(seat);
        }
    }
}