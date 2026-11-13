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

import com.kinhiro.rgkcn.RGKCNAddon;
import com.kinhiro.rgkcn.util.FakePlayerRejoin;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.anvilcraft.rg.sd.command.PlayerCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(PlayerCommand.class)
public abstract class MixinPlayerCommand {
    @Shadow
    public static int spawnPlayer(@NotNull CommandContext<CommandSourceStack> context) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Inject(
        method = "register(Lcom/mojang/brigadier/CommandDispatcher;)V",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"
        )
    )
    private static void rgkcn$register$playerCommandRejoin(
        final CommandDispatcher<CommandSourceStack> dispatcher,
        final CallbackInfo ci,
        final @Local(name = "player") RequiredArgumentBuilder<CommandSourceStack, String> player
    ) {
        if (RGKCNAddon.isLoaded("silicone_dolls"))
            player.then(Commands.literal("rejoin").executes(context -> {
                FakePlayerRejoin.IS_REJOIN.set(true);
                final int result = spawnPlayer(context);
                if (result > 0) context.getSource().getServer().execute(() -> FakePlayerRejoin.IS_REJOIN.set(false));
                else FakePlayerRejoin.IS_REJOIN.set(false);
                return result;
            }));
    }
}
