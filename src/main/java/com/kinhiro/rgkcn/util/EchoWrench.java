/*
 * MIT License
 *
 * Copyright (c) 2025 Kinhiro Zane and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.kinhiro.rgkcn.util;

import com.kinhiro.rgkcn.RGKCNRules;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class EchoWrench {
    public static boolean rotateBlockWithWrench(
        final Level level,
        final BlockState state,
        final Player player,
        final InteractionHand hand,
        final BlockHitResult hit
    ) {
        if (hand != InteractionHand.MAIN_HAND) return false;
        if (shouldApplyMainhand(player)) return rotateBlock(level, state, player, hand, hit);
        return false;
    }

    public static boolean rotateBlock(
        final Level level,
        final BlockState state,
        final Player player,
        final InteractionHand hand,
        final BlockHitResult hit
    ) {
        final var block = state.getBlock();
        final var pos = hit.getBlockPos();
        final var hitPos = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        final var facing = hit.getDirection();
        final var newState = switch (block) {
            case HorizontalDirectionalBlock _, BaseRailBlock _ when !(block instanceof BedBlock) ->
                state.rotate(level, pos, Rotation.CLOCKWISE_90);

            case ObserverBlock _, EndRodBlock _ ->
                state.setValue(DirectionalBlock.FACING, rotateFacing(state.getValue(DirectionalBlock.FACING), player));

            case DispenserBlock _ ->
                state.setValue(DispenserBlock.FACING, rotateFacing(state.getValue(DispenserBlock.FACING), player));

            case PistonBaseBlock _ when !state.getValue(PistonBaseBlock.EXTENDED) ->
                state.setValue(PistonBaseBlock.FACING, rotateFacing(state.getValue(PistonBaseBlock.FACING), player));

            case SlabBlock _ when state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE -> state.setValue(
                SlabBlock.TYPE,
                state.getValue(SlabBlock.TYPE) == SlabType.TOP ? SlabType.BOTTOM : SlabType.TOP
            );

            case HopperBlock _ when state.getValue(HopperBlock.FACING) != Direction.DOWN ->
                state.setValue(HopperBlock.FACING, state.getValue(HopperBlock.FACING).getClockWise());

            case StairBlock _ -> {
                if ((facing == Direction.UP && hitPos.y == 1f) || (facing == Direction.DOWN && hitPos.y == 0f))
                    yield state.setValue(
                        StairBlock.HALF,
                        state.getValue(StairBlock.HALF) == Half.TOP ? Half.BOTTOM : Half.TOP
                    );
                else yield state.rotate(level, pos, switch (facing) {
                    case EAST -> hitPos.z <= 0.5;
                    case WEST -> hitPos.z > 0.5;
                    case SOUTH -> hitPos.x > 0.5;
                    case NORTH -> hitPos.x <= 0.5;
                    default -> false;
                } ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90);
            }

            case RotatedPillarBlock _ ->
                state.setValue(RotatedPillarBlock.AXIS, switch (state.getValue(RotatedPillarBlock.AXIS)) {
                    case X -> Direction.Axis.Z;
                    case Y -> Direction.Axis.X;
                    case Z -> Direction.Axis.Y;
                });

            default -> null;
        };

        if (newState != null) {
            level.setBlock(pos, newState, Block.UPDATE_CLIENTS | Block.UPDATE_SKIP_ALL_SIDEEFFECTS);
            level.setBlocksDirty(pos, state, newState);
            return true;
        }

        return false;
    }

    public static ItemStack rotateDispenser(final ItemStack stack, final BlockSource source) {
        final var sourceFacing = source.state().getValue(DispenserBlock.FACING);
        final var level = source.level();
        final var pos = source.pos().relative(sourceFacing);
        final var state = level.getBlockState(pos);
        final var block = state.getBlock();
        switch (block) {
            case DirectionalBlock _, DispenserBlock _ -> {
                final var facing = state.getValue(DirectionalBlock.FACING);
                if (block instanceof final PistonBaseBlock piston
                    && (state.getValue(PistonBaseBlock.EXTENDED)
                    || (piston.getNeighborSignal(level, pos, facing)
                    && new PistonStructureResolver(level, pos, facing, true).resolve()))) return stack;

                var rotatedFacing = facing.getClockWise(sourceFacing.getAxis());
                if (sourceFacing.get3DDataValue() % 2 == 0 || rotatedFacing == facing)
                    rotatedFacing = rotatedFacing.getOpposite();

                level.setBlock(pos, state.setValue(DirectionalBlock.FACING, rotatedFacing), Block.UPDATE_ALL);
            }
            case HorizontalDirectionalBlock _ -> {
                if (block instanceof BedBlock) return stack;
                var facing = state.getValue(HorizontalDirectionalBlock.FACING).getClockWise(Direction.Axis.Y);
                if (sourceFacing == Direction.DOWN) facing = facing.getOpposite();
                level.setBlock(pos, state.setValue(HorizontalDirectionalBlock.FACING, facing), Block.UPDATE_ALL);
            }
            case HopperBlock _ -> {
                var facing = state.getValue(HopperBlock.FACING);
                if (facing != Direction.DOWN) {
                    facing = facing.getClockWise(Direction.Axis.Y);
                    level.setBlock(pos, state.setValue(HopperBlock.FACING, facing), Block.UPDATE_ALL);
                }
            }
            default -> {
            }
        }

        level.neighborChanged(pos, block, null);
        return stack;
    }

    public static boolean shouldApplyMainhand(final Player player) {
        return RGKCNRules.echoWrench && player.isCreative() && player.getMainHandItem().is(Items.ECHO_SHARD);
    }

    public static boolean shouldApplyOffhand(final @Nullable Player player) {
        if (player == null) return false;
        return RGKCNRules.echoWrench && player.isCreative() && player.getOffhandItem().is(Items.ECHO_SHARD);
    }

    private static Direction rotateFacing(final Direction current, final Player player) {
        final var facing = Direction.orderedByNearest(player)[0];
        if (facing.getAxis().isVertical()) return current.getAxis().isVertical() ? current.getOpposite() : facing;
        return current.getAxis().isVertical() ? facing : current.getClockWise();
    }

    public static boolean shouldApplyOffhandWithWrench(final @Nullable Player player) {
        if (player == null) return false;
        return RGKCNRules.echoWrench
            && player.isCreative()
            && player.getMainHandItem().isEmpty()
            && player.getOffhandItem().is(Items.ECHO_SHARD);
    }

    public static class DispenseBehavior extends OptionalDispenseItemBehavior implements DispenseItemBehavior {
        @Override
        protected @NonNull ItemStack execute(@NonNull BlockSource source, @NonNull ItemStack dispensed) {
            if (RGKCNRules.echoWrench) return EchoWrench.rotateDispenser(dispensed, source);
            return super.execute(source, dispensed);
        }
    }
}
