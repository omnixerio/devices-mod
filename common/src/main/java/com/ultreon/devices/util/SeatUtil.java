package com.ultreon.devices.util;

import com.ultreon.devices.debug.DebugLog;
import com.ultreon.devices.entity.SeatEntity;
import com.ultreon.devices.init.DeviceEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SeatUtil {
    public static void createSeatAndSit(Level worldIn, BlockPos pos, Player playerIn, double yOffset) {
        List<SeatEntity> seats = worldIn.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
        if(!seats.isEmpty()) {
            SeatEntity seat = seats.get(0);
            if(seat.getFirstPassenger() == null) {
                playerIn.startRiding(seat);
            }
        } else {
            SeatEntity seat = DeviceEntities.SEAT.get().create(worldIn);// new SeatEntity(worldIn, pos, yOffset);
            assert seat != null;
            seat.setYOffset(yOffset);
            seat.setViaYOffset(pos);
            DebugLog.log(seat);
            worldIn.addFreshEntity(seat);
            playerIn.startRiding(seat);
        }
    }
}