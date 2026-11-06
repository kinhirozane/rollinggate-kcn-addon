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

import com.kinhiro.rgkcn.RGKCNAddon;
import com.kinhiro.rgkcn.command.argument.DyeColorArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RGKCNCommands {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES =
        DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, RGKCNAddon.NAMESPACE);

    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<DyeColorArgument>> DYE_COLOR =
        ARGUMENT_TYPES.register(
            "dye_color",
            () -> ArgumentTypeInfos.registerByClass(
                DyeColorArgument.class,
                SingletonArgumentInfo.contextFree(DyeColorArgument::dyeColor)
            )
        );
}
