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
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Portal;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class CreativeNoClip {
    private static final Set<Player> PORTAL_SUPPRESSED =
        Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    private static final Set<Player> PORTAL_OUTSIDE =
        Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    public static boolean shouldApply(final Player player) {
        return RGKCNRules.creativePlayerNoClip
            && !player.isSpectator()
            && player.isCreative()
            && player.getAbilities().flying;
    }

    public static void update(final Player player) {
        final var noClip = shouldApply(player);
        if (noClip) {
            player.portalProcess = null;
            PORTAL_SUPPRESSED.add(player);
            PORTAL_OUTSIDE.remove(player);
            return;
        }

        if (!PORTAL_SUPPRESSED.contains(player)) return;
        player.portalProcess = null;
        if (isInsidePortal(player)) {
            PORTAL_OUTSIDE.remove(player);
            return;
        }

        if (PORTAL_OUTSIDE.add(player)) return;
        PORTAL_OUTSIDE.remove(player);
        PORTAL_SUPPRESSED.remove(player);
    }

    public static void clearSuppression(final Player player) {
        PORTAL_SUPPRESSED.remove(player);
        PORTAL_OUTSIDE.remove(player);
    }

    private static boolean isInsidePortal(final Player player) {
        final var box = player.getBoundingBox();
        final var level = player.level();
        final var min = BlockPos.containing(box.minX, box.minY, box.minZ);
        final var max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
        for (final var pos : BlockPos.betweenClosed(min, max)) {
            if (!(level.getBlockState(pos).getBlock() instanceof Portal)) continue;
            final var x = pos.getX();
            final var y = pos.getY();
            final var z = pos.getZ();
            if (box.intersects(x, y, z, x + 1d, y + 1d, z + 1d)) return true;
        }

        return false;
    }
}
