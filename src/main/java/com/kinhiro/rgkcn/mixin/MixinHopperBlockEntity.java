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
package com.kinhiro.rgkcn.mixin;

import com.kinhiro.rgkcn.RGKCNRules;
import com.kinhiro.rgkcn.util.HopperCounter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(HopperBlockEntity.class)
public abstract class MixinHopperBlockEntity extends RandomizableContainerBlockEntity {
    @Shadow
    private static boolean ejectItems(Level level, BlockPos blockPos, HopperBlockEntity self) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    protected MixinHopperBlockEntity(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "ejectItems", at = @At("HEAD"), cancellable = true)
    private static void rgkcn$ejectItems$hopperCounters(
        final Level level,
        final BlockPos blockPos,
        final HopperBlockEntity self,
        final CallbackInfoReturnable<Boolean> cir
    ) {
        if (level.isClientSide()) return;
        if (!RGKCNRules.hopperCounters) return;
        final var facing = level.getBlockState(blockPos).getValue(HopperBlock.FACING);
        final var channel = HopperCounter.getChannelAtPosition(level, blockPos.relative(facing));
        if (channel == null) return;
        final var handler = HopperBlockEntity.getContainerOrHandlerAt(level, blockPos, null);
        for (var slot = 0; slot < handler.container().getContainerSize(); ++slot)
            if (!handler.container().getItem(slot).isEmpty()) {
                final var stack = handler.container().getItem(slot);
                HopperCounter.getCounter(channel).add(level.getServer(), stack);
                handler.container().setItem(slot, ItemStack.EMPTY);
            }

        cir.setReturnValue(true);
    }

    @Inject(
        method = "tryMoveItems",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;setCooldown(I)V",
            shift = At.Shift.AFTER
        )
    )
    private static void rgkcn$tryMoveItems$hopperCountersUnlimitedSpeed(
        final Level level,
        final BlockPos pos,
        final BlockState state,
        final HopperBlockEntity entity,
        final BooleanSupplier action,
        final CallbackInfoReturnable<Boolean> cir
    ) {
        if (RGKCNRules.hopperCounters) {
            final var channel = HopperCounter.findChannelForHopper(entity);
            if (channel == null) return;
            for (var i = Short.MAX_VALUE - 1; i >= 0; --i) {
                var flag = false;
                if (!entity.isEmpty()) flag = ejectItems(level, pos, entity);
                if (!entity.inventoryFull()) flag |= action.getAsBoolean();
                if (flag) break;
            }

            entity.setCooldown(0);
        }
    }

    @Inject(
        method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void rgkcn$addItem$hopperCountersUnlimitedSpeed(
        final Container container,
        final ItemEntity entity,
        final CallbackInfoReturnable<Boolean> cir
    ) {
        if (RGKCNRules.hopperCounters && entity.isRemoved()) cir.setReturnValue(false);
    }
}
