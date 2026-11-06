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
package com.kinhiro.rgkcn.command;

import com.kinhiro.rgkcn.RGKCNRules;
import com.kinhiro.rgkcn.command.argument.DyeColorArgument;
import com.kinhiro.rgkcn.util.HopperCounter;
import com.kinhiro.rgkcn.util.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.anvilcraft.rg.api.RGValidator;
import dev.anvilcraft.rg.tools.ModCommands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class CounterCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            ModCommands.root(dispatcher, "counter")
                .requires(stack ->
                    RGValidator.CommandRuleValidator.hasPermission(() -> RGKCNRules.commandCounter, stack)
                )
                .executes(context -> listAllCounters(context, false))
                .then(Commands.literal("realtime").executes(context -> listAllCounters(context, true)))
                .then(Commands.literal("reset").executes(CounterCommand::resetCounters))
                .then(
                    Commands.argument("channel", DyeColorArgument.dyeColor())
                        .executes(context -> displayCounter(context, false))
                        .then(Commands.literal("realtime").executes(context -> displayCounter(context, true)))
                        .then(Commands.literal("reset").executes(CounterCommand::resetCounter))
                )
        );
    }

    private static int listAllCounters(final CommandContext<CommandSourceStack> context, final boolean realtime) {
        for (final var message : HopperCounter.formatAll(context.getSource().getServer(), realtime))
            context.getSource().sendSuccess(() -> message, false);

        return 1;
    }

    private static int resetCounters(final CommandContext<CommandSourceStack> context) {
        HopperCounter.resetAll(context.getSource().getServer(), false);
        Messenger.command(context.getSource(), Messenger.format(
            Component.translatable("message.rgkcn.hopper_counter.reset_all")
        ));

        return 1;
    }

    private static int displayCounter(final CommandContext<CommandSourceStack> context, final boolean realtime) {
        final var channel = DyeColorArgument.getDyeColor(context, "channel");
        final var counter = HopperCounter.getCounter(channel);
        for (final var message : counter.format(context.getSource().getServer(), realtime, false))
            context.getSource().sendSuccess(() -> message, false);

        return 1;
    }

    private static int resetCounter(final CommandContext<CommandSourceStack> context) {
        final var channel = DyeColorArgument.getDyeColor(context, "channel");
        HopperCounter.getCounter(channel).reset(context.getSource().getServer());
        Messenger.command(context.getSource(), Messenger.format(
            Component.translatable("message.rgkcn.hopper_counter.reset", channel.getSerializedName())
        ));

        return 1;
    }
}
