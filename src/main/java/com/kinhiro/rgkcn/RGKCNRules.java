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
package com.kinhiro.rgkcn;

import dev.anvilcraft.rg.RollingGateCategories;
import dev.anvilcraft.rg.api.RGValidator;
import dev.anvilcraft.rg.api.Rule;
import dev.anvilcraft.rg.api.server.RGServerRules;

@RGServerRules(value = RGKCNAddon.NAMESPACE, languages = {"zh_cn", "en_us"})
public final class RGKCNRules {
    @Rule(
        onlyAllowed = true,
        allowed = {"ops", "true", "false", "1", "2", "3", "4"},
        categories = {RGKCNAddon.NAMESPACE, RollingGateCategories.COMMAND, RollingGateCategories.CREATIVE},
        validator = RGValidator.CommandRuleValidator.class
    )
    public static String commandCounter = "ops";

    @Rule(categories = {RGKCNAddon.NAMESPACE, RollingGateCategories.CREATIVE, RollingGateCategories.FEATURE})
    public static boolean hopperCounters = false;

    @Rule(categories = {RGKCNAddon.NAMESPACE, RollingGateCategories.CREATIVE, RollingGateCategories.FEATURE})
    public static boolean creativePlayerNoClip = false;

    @Rule(categories = {RGKCNAddon.NAMESPACE, RollingGateCategories.CREATIVE, RollingGateCategories.FEATURE})
    public static boolean echoWrench = false;
}
