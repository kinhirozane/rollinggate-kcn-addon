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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PistonMovingBlockEntity.class)
public abstract class MixinPistonMovingBlockEntity {
    @ModifyExpressionValue(
        method = "moveCollidedEntities",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntities" +
                "(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)" +
                "Ljava/util/List;"
        )
    )
    private static List<Entity> rgkcn$moveCollidedEntities$creativePlayerNoClip(final List<Entity> original) {
        original.removeIf(entity -> entity instanceof final Player player && CreativeNoClip.shouldApply(player));
        return original;
    }

    @ModifyExpressionValue(
        method = "moveStuckEntities",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getEntities" +
                "(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;" +
                "Ljava/util/function/Predicate;)Ljava/util/List;"
        )
    )
    private static List<Entity> rgkcn$moveStuckEntities$creativePlayerNoClip(final List<Entity> original) {
        original.removeIf(entity -> entity instanceof final Player player && CreativeNoClip.shouldApply(player));
        return original;
    }
}
