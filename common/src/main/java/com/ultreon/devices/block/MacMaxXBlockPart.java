package com.ultreon.devices.block;

import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * @author Qboi123
 */
public class MacMaxXBlockPart extends HorizontalDirectionalBlock {
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    private static final VoxelShape BL_SHAPE_NORTH = Shapes.or(
            Block.box(-16 + 16, 31.0, 5.0,  32 + 16, 32.0, 7),
            Block.box(-15 + 16,  4.0, 5.5,  31 + 16, 32.0, 7),
            Block.box(-16 + 16,  5.0, 5.0, -15 + 16, 32.0, 7),
            Block.box(-16 + 16,  3.0, 5.0,  32 + 16,  7.0, 7),
            Block.box( 31 + 16,  5.0, 5.0,  32 + 16, 32.0, 7),
            Block.box(- 6 + 16,  0.5, 6.5, - 2 + 16, 19.0, 9),
            Block.box( 20 + 16,  0.5, 6.5,  24 + 16, 19.0, 9),
            Block.box(  0 + 16, 13.0, 6.5,  18 + 16, 27.0, 8),
            Block.box( 19 + 16,  0.0, 6.5,  25 + 16,  0.5, 9),
            Block.box(- 7 + 16,  0.0, 6.5, - 1 + 16,  0.5, 9));
    private static final VoxelShape BL_SHAPE_EAST = Shapes.or(
            Block.box(9,  31, -16 - 16,   11,  32,  32 - 16),
            Block.box(9,   4, -15 - 16, 10.5,  32,  31 - 16),
            Block.box(9,   5, -16 - 16,   11,  32, -15 - 16),
            Block.box(9,   3, -16 - 16,   11,   7,  32 - 16),
            Block.box(9,   5,  31 - 16,   11,  32,  32 - 16),
            Block.box(7, 0.5,  -6 - 16,  9.5,  19,  -2 - 16),
            Block.box(7, 0.5,  20 - 16,  9.5,  19,  24 - 16),
            Block.box(8,  13,   0 - 16,  9.5,  27,  18 - 16),
            Block.box(7,   0,  19 - 16,  9.5, 0.5,  25 - 16),
            Block.box(7,   0,  -7 - 16,  9.5, 0.5,  -1 - 16));
    private static final VoxelShape BL_SHAPE_SOUTH = Shapes.or(
            Block.box(-16 - 16,  31, 9,  32 - 16,  32,   11),
            Block.box(-15 - 16,   4, 9,  31 - 16,  32, 10.5),
            Block.box( 31 - 16,   5, 9,  32 - 16,  32,   11),
            Block.box(-16 - 16,   3, 9,  32 - 16,   7,   11),
            Block.box(-16 - 16,   5, 9, -15 - 16,  32,   11),
            Block.box( 18 - 16, 0.5, 7,  22 - 16,  19,  9.5),
            Block.box( -8 - 16, 0.5, 7,  -4 - 16,  19,  9.5),
            Block.box( -2 - 16,  13, 8,  16 - 16,  27,  9.5),
            Block.box( -9 - 16,   0, 7,  -3 - 16, 0.5,  9.5),
            Block.box( 17 - 16,   0, 7,  23 - 16, 0.5,  9.5));
    private static final VoxelShape BL_SHAPE_WEST = Shapes.or(
            Block.box(  5,  31, -16 + 16, 7,  32,  32 + 16),
            Block.box(5.5,   4, -15 + 16, 7,  32,  31 + 16),
            Block.box(  5,   5,  31 + 16, 7,  32,  32 + 16),
            Block.box(  5,   3, -16 + 16, 7,   7,  32 + 16),
            Block.box(  5,   5, -16 + 16, 7,  32, -15 + 16),
            Block.box(6.5, 0.5,  18 + 16, 9,  19,  22 + 16),
            Block.box(6.5, 0.5,  -8 + 16, 9,  19,  -4 + 16),
            Block.box(6.5,  13,  -2 + 16, 8,  27,  16 + 16),
            Block.box(6.5,   0,  -9 + 16, 9, 0.5,  -3 + 16),
            Block.box(6.5,   0,  17 + 16, 9, 0.5,  23 + 16));

    private static final VoxelShape BR_SHAPE_NORTH = Shapes.or(
            Block.box(-16 - 16,  31,   5,  32 - 16,  32, 7),
            Block.box(-15 - 16,   4, 5.5,  31 - 16,  32, 7),
            Block.box(-16 - 16,   5,   5, -15 - 16,  32, 7),
            Block.box(-16 - 16,   3,   5,  32 - 16,   7, 7),
            Block.box( 31 - 16,   5,   5,  32 - 16,  32, 7),
            Block.box( -6 - 16, 0.5, 6.5,  -2 - 16,  19, 9),
            Block.box( 20 - 16, 0.5, 6.5,  24 - 16,  19, 9),
            Block.box(  0 - 16,  13, 6.5,  18 - 16,  27, 8),
            Block.box( 19 - 16,   0, 6.5,  25 - 16, 0.5, 9),
            Block.box( -7 - 16,   0, 6.5,  -1 - 16, 0.5, 9));
    private static final VoxelShape BR_SHAPE_EAST = Shapes.or(
            Block.box(9,  31, -16 + 16,   11,  32,  32 + 16),
            Block.box(9,   4, -15 + 16, 10.5,  32,  31 + 16),
            Block.box(9,   5, -16 + 16,   11,  32, -15 + 16),
            Block.box(9,   3, -16 + 16,   11,   7,  32 + 16),
            Block.box(9,   5,  31 + 16,   11,  32,  32 + 16),
            Block.box(7, 0.5,  -6 + 16,  9.5,  19,  -2 + 16),
            Block.box(7, 0.5,  20 + 16,  9.5,  19,  24 + 16),
            Block.box(8,  13,   0 + 16,  9.5,  27,  18 + 16),
            Block.box(7,   0,  19 + 16,  9.5, 0.5,  25 + 16),
            Block.box(7,   0,  -7 + 16,  9.5, 0.5,  -1 + 16));
    private static final VoxelShape BR_SHAPE_SOUTH = Shapes.or(
            Block.box(-16 + 16,  31, 9,  32 + 16,  32,   11),
            Block.box(-15 + 16,   4, 9,  31 + 16,  32, 10.5),
            Block.box( 31 + 16,   5, 9,  32 + 16,  32,   11),
            Block.box(-16 + 16,   3, 9,  32 + 16,   7,   11),
            Block.box(-16 + 16,   5, 9, -15 + 16,  32,   11),
            Block.box( 18 + 16, 0.5, 7,  22 + 16,  19,  9.5),
            Block.box( -8 + 16, 0.5, 7,  -4 + 16,  19,  9.5),
            Block.box( -2 + 16,  13, 8,  16 + 16,  27,  9.5),
            Block.box( -9 + 16,   0, 7,  -3 + 16, 0.5,  9.5),
            Block.box( 17 + 16,   0, 7,  23 + 16, 0.5,  9.5));
    private static final VoxelShape BR_SHAPE_WEST = Shapes.or(
            Block.box(  5,  31, -16 - 16, 7,  32,  32 - 16),
            Block.box(5.5,   4, -15 - 16, 7,  32,  31 - 16),
            Block.box(  5,   5,  31 - 16, 7,  32,  32 - 16),
            Block.box(  5,   3, -16 - 16, 7,   7,  32 - 16),
            Block.box(  5,   5, -16 - 16, 7,  32, -15 - 16),
            Block.box(6.5, 0.5,  18 - 16, 9,  19,  22 - 16),
            Block.box(6.5, 0.5,  -8 - 16, 9,  19,  -4 - 16),
            Block.box(6.5,  13,  -2 - 16, 8,  27,  16 - 16),
            Block.box(6.5,   0,  -9 - 16, 9, 0.5,  -3 - 16),
            Block.box(6.5,   0,  17 - 16, 9, 0.5,  23 - 16));

    private static final VoxelShape TL_SHAPE_NORTH = Shapes.or(
            Block.box(-16 + 16,  31 - 16,   5,  32 + 16,  32 - 16, 7),
            Block.box(-15 + 16,   4 - 16, 5.5,  31 + 16,  32 - 16, 7),
            Block.box(-16 + 16,   5 - 16,   5, -15 + 16,  32 - 16, 7),
            Block.box(-16 + 16,   3 - 16,   5,  32 + 16,   7 - 16, 7),
            Block.box( 31 + 16,   5 - 16,   5,  32 + 16,  32 - 16, 7),
            Block.box( -6 + 16, 0.5 - 16, 6.5,  -2 + 16,  19 - 16, 9),
            Block.box( 20 + 16, 0.5 - 16, 6.5,  24 + 16,  19 - 16, 9),
            Block.box(  0 + 16,  13 - 16, 6.5,  18 + 16,  27 - 16, 8),
            Block.box( 19 + 16,   0 - 16, 6.5,  25 + 16, 0.5 - 16, 9),
            Block.box( -7 + 16,   0 - 16, 6.5,  -1 + 16, 0.5 - 16, 9));
    private static final VoxelShape TL_SHAPE_EAST = Shapes.or(
            Block.box(9,  31 - 16, -16 - 16,   11,  32 - 16,  32 - 16),
            Block.box(9,   4 - 16, -15 - 16, 10.5,  32 - 16,  31 - 16),
            Block.box(9,   5 - 16, -16 - 16,   11,  32 - 16, -15 - 16),
            Block.box(9,   3 - 16, -16 - 16,   11,   7 - 16,  32 - 16),
            Block.box(9,   5 - 16,  31 - 16,   11,  32 - 16,  32 - 16),
            Block.box(7, 0.5 - 16,  -6 - 16,  9.5,  19 - 16,  -2 - 16),
            Block.box(7, 0.5 - 16,  20 - 16,  9.5,  19 - 16,  24 - 16),
            Block.box(8,  13 - 16,   0 - 16,  9.5,  27 - 16,  18 - 16),
            Block.box(7,   0 - 16,  19 - 16,  9.5, 0.5 - 16,  25 - 16),
            Block.box(7,   0 - 16,  -7 - 16,  9.5, 0.5 - 16,  -1 - 16));
    private static final VoxelShape TL_SHAPE_SOUTH = Shapes.or(
            Block.box(-16 - 16,  31 - 16, 9,  32 - 16,  32 - 16,   11),
            Block.box(-15 - 16,   4 - 16, 9,  31 - 16,  32 - 16, 10.5),
            Block.box( 31 - 16,   5 - 16, 9,  32 - 16,  32 - 16,   11),
            Block.box(-16 - 16,   3 - 16, 9,  32 - 16,   7 - 16,   11),
            Block.box(-16 - 16,   5 - 16, 9, -15 - 16,  32 - 16,   11),
            Block.box( 18 - 16, 0.5 - 16, 7,  22 - 16,  19 - 16,  9.5),
            Block.box( -8 - 16, 0.5 - 16, 7,  -4 - 16,  19 - 16,  9.5),
            Block.box( -2 - 16,  13 - 16, 8,  16 - 16,  27 - 16,  9.5),
            Block.box( -9 - 16,   0 - 16, 7,  -3 - 16, 0.5 - 16,  9.5),
            Block.box( 17 - 16,   0 - 16, 7,  23 - 16, 0.5 - 16,  9.5));
    private static final VoxelShape TL_SHAPE_WEST = Shapes.or(
            Block.box(  5,  31 - 16, -16 + 16, 7,  32 - 16,  32 + 16),
            Block.box(5.5,   4 - 16, -15 + 16, 7,  32 - 16,  31 + 16),
            Block.box(  5,   5 - 16,  31 + 16, 7,  32 - 16,  32 + 16),
            Block.box(  5,   3 - 16, -16 + 16, 7,   7 - 16,  32 + 16),
            Block.box(  5,   5 - 16, -16 + 16, 7,  32 - 16, -15 + 16),
            Block.box(6.5, 0.5 - 16,  18 + 16, 9,  19 - 16,  22 + 16),
            Block.box(6.5, 0.5 - 16,  -8 + 16, 9,  19 - 16,  -4 + 16),
            Block.box(6.5,  13 - 16,  -2 + 16, 8,  27 - 16,  16 + 16),
            Block.box(6.5,   0 - 16,  -9 + 16, 9, 0.5 - 16,  -3 + 16),
            Block.box(6.5,   0 - 16,  17 + 16, 9, 0.5 - 16,  23 + 16));

    private static final VoxelShape T_SHAPE_NORTH = Shapes.or(
            Block.box(-16,  31 - 16,   5,  32,  32 - 16, 7),
            Block.box(-15,   4 - 16, 5.5,  31,  32 - 16, 7),
            Block.box(-16,   5 - 16,   5, -15,  32 - 16, 7),
            Block.box(-16,   3 - 16,   5,  32,   7 - 16, 7),
            Block.box( 31,   5 - 16,   5,  32,  32 - 16, 7),
            Block.box( -6, 0.5 - 16, 6.5,  -2,  19 - 16, 9),
            Block.box( 20, 0.5 - 16, 6.5,  24,  19 - 16, 9),
            Block.box(  0,  13 - 16, 6.5,  18,  27 - 16, 8),
            Block.box( 19,   0 - 16, 6.5,  25, 0.5 - 16, 9),
            Block.box( -7,   0 - 16, 6.5,  -1, 0.5 - 16, 9));
    private static final VoxelShape T_SHAPE_EAST = Shapes.or(
            Block.box(9,  31 - 16, -16,   11,  32 - 16,  32),
            Block.box(9,   4 - 16, -15, 10.5,  32 - 16,  31),
            Block.box(9,   5 - 16, -16,   11,  32 - 16, -15),
            Block.box(9,   3 - 16, -16,   11,   7 - 16,  32),
            Block.box(9,   5 - 16,  31,   11,  32 - 16,  32),
            Block.box(7, 0.5 - 16,  -6,  9.5,  19 - 16,  -2),
            Block.box(7, 0.5 - 16,  20,  9.5,  19 - 16,  24),
            Block.box(8,  13 - 16,   0,  9.5,  27 - 16,  18),
            Block.box(7,   0 - 16,  19,  9.5, 0.5 - 16,  25),
            Block.box(7,   0 - 16,  -7,  9.5, 0.5 - 16,  -1));
    private static final VoxelShape T_SHAPE_SOUTH = Shapes.or(
            Block.box(-16,  31 - 16, 9,  32,  32 - 16,   11),
            Block.box(-15,   4 - 16, 9,  31,  32 - 16, 10.5),
            Block.box( 31,   5 - 16, 9,  32,  32 - 16,   11),
            Block.box(-16,   3 - 16, 9,  32,   7 - 16,   11),
            Block.box(-16,   5 - 16, 9, -15,  32 - 16,   11),
            Block.box( 18, 0.5 - 16, 7,  22,  19 - 16,  9.5),
            Block.box( -8, 0.5 - 16, 7,  -4,  19 - 16,  9.5),
            Block.box( -2,  13 - 16, 8,  16,  27 - 16,  9.5),
            Block.box( -9,   0 - 16, 7,  -3, 0.5 - 16,  9.5),
            Block.box( 17,   0 - 16, 7,  23, 0.5 - 16,  9.5));
    private static final VoxelShape T_SHAPE_WEST = Shapes.or(
            Block.box(  5,  31 - 16, -16, 7,  32 - 16,  32),
            Block.box(5.5,   4 - 16, -15, 7,  32 - 16,  31),
            Block.box(  5,   5 - 16,  31, 7,  32 - 16,  32),
            Block.box(  5,   3 - 16, -16, 7,   7 - 16,  32),
            Block.box(  5,   5 - 16, -16, 7,  32 - 16, -15),
            Block.box(6.5, 0.5 - 16,  18, 9,  19 - 16,  22),
            Block.box(6.5, 0.5 - 16,  -8, 9,  19 - 16,  -4),
            Block.box(6.5,  13 - 16,  -2, 8,  27 - 16,  16),
            Block.box(6.5,   0 - 16,  -9, 9, 0.5 - 16,  -3),
            Block.box(6.5,   0 - 16,  17, 9, 0.5 - 16,  23));

    private static final VoxelShape TR_SHAPE_NORTH = Shapes.or(
            Block.box(-16 - 16,  31 - 16,   5,  32 - 16,  32 - 16, 7),
            Block.box(-15 - 16,   4 - 16, 5.5,  31 - 16,  32 - 16, 7),
            Block.box(-16 - 16,   5 - 16,   5, -15 - 16,  32 - 16, 7),
            Block.box(-16 - 16,   3 - 16,   5,  32 - 16,   7 - 16, 7),
            Block.box( 31 - 16,   5 - 16,   5,  32 - 16,  32 - 16, 7),
            Block.box( -6 - 16, 0.5 - 16, 6.5,  -2 - 16,  19 - 16, 9),
            Block.box( 20 - 16, 0.5 - 16, 6.5,  24 - 16,  19 - 16, 9),
            Block.box(  0 - 16,  13 - 16, 6.5,  18 - 16,  27 - 16, 8),
            Block.box( 19 - 16,   0 - 16, 6.5,  25 - 16, 0.5 - 16, 9),
            Block.box( -7 - 16,   0 - 16, 6.5,  -1 - 16, 0.5 - 16, 9));
    private static final VoxelShape TR_SHAPE_EAST = Shapes.or(
            Block.box(9,  31 - 16, -16 + 16,   11,  32 - 16,  32 + 16),
            Block.box(9,   4 - 16, -15 + 16, 10.5,  32 - 16,  31 + 16),
            Block.box(9,   5 - 16, -16 + 16,   11,  32 - 16, -15 + 16),
            Block.box(9,   3 - 16, -16 + 16,   11,   7 - 16,  32 + 16),
            Block.box(9,   5 - 16,  31 + 16,   11,  32 - 16,  32 + 16),
            Block.box(7, 0.5 - 16,  -6 + 16,  9.5,  19 - 16,  -2 + 16),
            Block.box(7, 0.5 - 16,  20 + 16,  9.5,  19 - 16,  24 + 16),
            Block.box(8,  13 - 16,   0 + 16,  9.5,  27 - 16,  18 + 16),
            Block.box(7,   0 - 16,  19 + 16,  9.5, 0.5 - 16,  25 + 16),
            Block.box(7,   0 - 16,  -7 + 16,  9.5, 0.5 - 16,  -1 + 16));
    private static final VoxelShape TR_SHAPE_SOUTH = Shapes.or(
            Block.box(-16 + 16,  31 - 16, 9,  32 + 16,  32 - 16,   11),
            Block.box(-15 + 16,   4 - 16, 9,  31 + 16,  32 - 16, 10.5),
            Block.box( 31 + 16,   5 - 16, 9,  32 + 16,  32 - 16,   11),
            Block.box(-16 + 16,   3 - 16, 9,  32 + 16,   7 - 16,   11),
            Block.box(-16 + 16,   5 - 16, 9, -15 + 16,  32 - 16,   11),
            Block.box( 18 + 16, 0.5 - 16, 7,  22 + 16,  19 - 16,  9.5),
            Block.box( -8 + 16, 0.5 - 16, 7,  -4 + 16,  19 - 16,  9.5),
            Block.box( -2 + 16,  13 - 16, 8,  16 + 16,  27 - 16,  9.5),
            Block.box( -9 + 16,   0 - 16, 7,  -3 + 16, 0.5 - 16,  9.5),
            Block.box( 17 + 16,   0 - 16, 7,  23 + 16, 0.5 - 16,  9.5));
    private static final VoxelShape TR_SHAPE_WEST = Shapes.or(
            Block.box(  5,  31 - 16, -16 - 16, 7,  32 - 16,  32 - 16),
            Block.box(5.5,   4 - 16, -15 - 16, 7,  32 - 16,  31 - 16),
            Block.box(  5,   5 - 16,  31 - 16, 7,  32 - 16,  32 - 16),
            Block.box(  5,   3 - 16, -16 - 16, 7,   7 - 16,  32 - 16),
            Block.box(  5,   5 - 16, -16 - 16, 7,  32 - 16, -15 - 16),
            Block.box(6.5, 0.5 - 16,  18 - 16, 9,  19 - 16,  22 - 16),
            Block.box(6.5, 0.5 - 16,  -8 - 16, 9,  19 - 16,  -4 - 16),
            Block.box(6.5,  13 - 16,  -2 - 16, 8,  27 - 16,  16 - 16),
            Block.box(6.5,   0 - 16,  -9 - 16, 9, 0.5 - 16,  -3 - 16),
            Block.box(6.5,   0 - 16,  17 - 16, 9, 0.5 - 16,  23 - 16));

    public MacMaxXBlockPart() {
        super(Properties.of().mapColor(DyeColor.WHITE).strength(6f).sound(SoundType.METAL));
        registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(PART, Part.T));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        switch (pState.getValue(PART)) {
            case BL -> {
                return switch (pState.getValue(FACING)) {
                    case NORTH -> BL_SHAPE_NORTH;
                    case EAST -> BL_SHAPE_EAST;
                    case SOUTH -> BL_SHAPE_SOUTH;
                    case WEST -> BL_SHAPE_WEST;
                    default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
                };
            }
            case BR -> {
                return switch (pState.getValue(FACING)) {
                    case NORTH -> BR_SHAPE_NORTH;
                    case EAST -> BR_SHAPE_EAST;
                    case SOUTH -> BR_SHAPE_SOUTH;
                    case WEST -> BR_SHAPE_WEST;
                    default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
                };
            }
            case TL -> {
                return switch (pState.getValue(FACING)) {
                    case NORTH -> TL_SHAPE_NORTH;
                    case EAST -> TL_SHAPE_EAST;
                    case SOUTH -> TL_SHAPE_SOUTH;
                    case WEST -> TL_SHAPE_WEST;
                    default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
                };
            }
            case T -> {
                return switch (pState.getValue(FACING)) {
                    case NORTH -> T_SHAPE_NORTH;
                    case EAST -> T_SHAPE_EAST;
                    case SOUTH -> T_SHAPE_SOUTH;
                    case WEST -> T_SHAPE_WEST;
                    default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
                };
            }
            case TR -> {
                return switch (pState.getValue(FACING)) {
                    case NORTH -> TR_SHAPE_NORTH;
                    case EAST -> TR_SHAPE_EAST;
                    case SOUTH -> TR_SHAPE_SOUTH;
                    case WEST -> TR_SHAPE_WEST;
                    default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
                };
            }
        }
        throw new IllegalStateException("Unexpected value: " + pState.getValue(PART));
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        BlockPos originPos = getOriginPos(pos, state);
        switch (state.getValue(FACING)) {
            case NORTH -> {
                destroyBlockExcept(level, pos, originPos.above().west(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.above().east(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.above(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.west(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.east(), Blocks.AIR.defaultBlockState(), 3);
            }
            case SOUTH -> {
                destroyBlockExcept(level, pos, originPos.above().east(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.above().west(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.above(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.east(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.west(), Blocks.AIR.defaultBlockState(), 3);
            }
            case WEST -> {
                destroyBlockExcept(level, pos, originPos.above().north(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.above().south(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.above(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.north(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.south(), Blocks.AIR.defaultBlockState(), 3);
            }
            case EAST -> {
                destroyBlockExcept(level, pos, originPos.above().south(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.above().north(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.above(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.south(), Blocks.AIR.defaultBlockState(), 3);
                destroyBlockExcept(level, pos, originPos.north(), Blocks.AIR.defaultBlockState(), 3);
            }
            default -> throw new IllegalStateException("Unexpected value: " + state.getValue(FACING));
        }
        destroyBlockExcept(level, pos, originPos, Blocks.AIR.defaultBlockState(), 3);
    }

    @SuppressWarnings("SameParameterValue")
    private void destroyBlockExcept(Level level, BlockPos except, BlockPos pos, BlockState state, int flags) {
        if (except.equals(pos)) return;
        level.setBlock(pos, state, flags);
    }

    private static BlockPos getOriginPos(@NotNull BlockPos pos, BlockState state) {
        return switch (state.getValue(FACING)) {
            case NORTH -> switch (state.getValue(PART)) {
                case TL -> pos.east().below();
                case TR -> pos.west().below();
                case T -> pos.below();
                case BL -> pos.east();
                case BR -> pos.west();
            };
            case SOUTH -> switch (state.getValue(PART)) {
                case TL -> pos.west().below();
                case TR -> pos.east().below();
                case T -> pos.below();
                case BL -> pos.west();
                case BR -> pos.east();
            };
            case WEST -> switch (state.getValue(PART)) {
                case TL -> pos.south().below();
                case TR -> pos.north().below();
                case T -> pos.below();
                case BL -> pos.south();
                case BR -> pos.north();
            };
            case EAST -> switch (state.getValue(PART)) {
                case TL -> pos.north().below();
                case TR -> pos.south().below();
                case T -> pos.below();
                case BL -> pos.north();
                case BR -> pos.south();
            };
            default -> throw new IllegalStateException("Unexpected value: " + state.getValue(FACING));
        };
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockPos originPos = getOriginPos(pos, state);
        BlockState originState = level.getBlockState(originPos);
        if (originState.getBlock() instanceof MacMaxXBlock block) {
            return block.use(originState, level, originPos, player, hand, hit);
        }
        return InteractionResult.FAIL;
    }

    @Override
    public MutableComponent getName() {
        MutableComponent normalName = Component.translatable("block.devices.mac_max_x");
        if (Platform.isModLoaded("emojiful")) {
            return Component.translatable("block.devices.mac_max_x_emoji");
        }
        return normalName;
    }

    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(PART, FACING);
    }

    public enum Part implements StringRepresentable {
        BL, BR, TL, T, TR;

        @Override
        public String getSerializedName() {
            return switch (this) {
                case BL -> "bottom_left";
                case BR -> "bottom_right";
                case TL -> "top_left";
                case T -> "top";
                case TR -> "top_right";
            };
        }
    }
}
