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

import com.kinhiro.rgkcn.util.CreativeNoClip;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem {
    @ModifyExpressionValue(
        method = "canPlace",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;isUnobstructed" +
                "(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;" +
                "Lnet/minecraft/world/phys/shapes/CollisionContext;)Z"
        )
    )
    private boolean rgkcn$canPlace$creativePlayerNoClip(
        final boolean original,
        final BlockPlaceContext context,
        final BlockState stateForPlacement
    ) {
        final var player = context.getPlayer();
        if (player != null && CreativeNoClip.shouldApply(player)) {
            final var level = context.getLevel();
            final var pos = context.getClickedPos();
            final var shape = stateForPlacement.getCollisionShape(level, pos);
            return shape.isEmpty() || level.isUnobstructed(player, shape.move(pos.getX(), pos.getY(), pos.getZ()));
        }

        return original;
    }
}
