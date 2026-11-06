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
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class MixinPlayer {
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z"
        )
    )
    private boolean rgkcn$tick$creativePlayerNoClip(final boolean original) {
        final var player = (Player) (Object) this;
        if (CreativeNoClip.shouldApply(player)) {
            CreativeNoClip.update(player);
            return true;
        }

        CreativeNoClip.clearSuppression(player);
        return original;
    }

    @ModifyExpressionValue(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z"
        )
    )
    private boolean rgkcn$aiStep$creativePlayerNoClip(final boolean original) {
        final var player = (Player) (Object) this;
        if (CreativeNoClip.shouldApply(player)) {
            CreativeNoClip.update(player);
            return true;
        }

        CreativeNoClip.clearSuppression(player);
        return original;
    }

    @ModifyExpressionValue(
        method = "updatePlayerPose",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z"
        )
    )
    private boolean rgkcn$updatePlayerPose$creativePlayerNoClip(final boolean original) {
        final var player = (Player) (Object) this;
        if (CreativeNoClip.shouldApply(player)) {
            CreativeNoClip.update(player);
            return true;
        }

        CreativeNoClip.clearSuppression(player);
        return original;
    }
}
