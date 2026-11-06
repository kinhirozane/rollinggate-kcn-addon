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

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

public final class Messenger {
    public static void command(final CommandSourceStack source, final Object... composable) {
        source.sendSuccess(() -> compose(composable), true);
    }

    public static void command(final ServerPlayer player, final Object... composable) {
        player.sendSystemMessage(compose(composable));
    }

    public static Component compose(final Object... composable) {
        final var message = Component.literal("");
        MutableComponent previous = null;
        for (final var composed : composable) {
            if (composed instanceof MutableComponent component) {
                message.append(component);
                previous = component;
                continue;
            }

            final var component = getComponentFrom(previous, composed.toString());
            if (!Objects.equals(component, previous)) message.append(component);
            previous = component;
        }

        return message;
    }

    public static Component text(final Component text) {
        return text(text.getString());
    }

    public static Component text(final Component text, final String format) {
        return text(text.getString(), format);
    }

    public static Component text(final String text) {
        return text(text, "");
    }

    public static Component text(final String text, final String format) {
        return Component.literal(text).setStyle(parseStyle(format));
    }

    public static Component format(final Component text) {
        final var message = text.getString();
        final var segments = new ArrayList<String>();
        var start = 0;
        while (true) {
            final var delimiter = findDelimiter(message, start);
            if (delimiter < 0) {
                segments.add(uneascape(message.substring(start)));
                break;
            }

            segments.add(uneascape(message.substring(start, delimiter)));
            start = delimiter + 2;
        }

        return compose(segments.toArray());
    }

    public static Style parseStyle(final String format) {
        var customStyle = Style.EMPTY.withColor(DyeColor.WHITE.getTextColor());
        for (final var formatting : MessageFormatting.values()) customStyle = formatting.apply(customStyle, format);
        return customStyle;
    }

    private static MutableComponent getComponentFrom(@Nullable final MutableComponent previous, String message) {
        if (message.isEmpty()) return Component.literal("");
        if (Character.isWhitespace(message.charAt(0))) message = "0" + message;
        final var limit = message.indexOf(' ');
        var text = "";
        var description = message;
        if (limit >= 0) {
            text = message.substring(limit + 1);
            description = message.substring(0, limit);
        }

        if (previous == null) return Component.literal(text).setStyle(parseStyle(description));
        final var previousStyle = previous.getStyle();
        var result = previous;
        previous.setStyle(switch (description.charAt(0)) {
            case '?' -> previousStyle.withClickEvent(new ClickEvent.SuggestCommand(message.substring(1)));
            case '!' -> previousStyle.withClickEvent(new ClickEvent.RunCommand(message.substring(1)));
            case '^' -> previousStyle.withHoverEvent(new HoverEvent.ShowText(compose(message.substring(1))));
            case '@' -> previousStyle.withClickEvent(new ClickEvent.OpenUrl(URI.create(message.substring(1))));
            case '&' -> previousStyle.withClickEvent(new ClickEvent.CopyToClipboard(message.substring(1)));
            default -> {
                result = Component.literal(text).setStyle(parseStyle(description));
                yield previousStyle;
            }
        });

        return result;
    }

    private static int findDelimiter(final String message, final int start) {
        for (var index = start; index < message.length() - 1; index++) {
            if (message.charAt(index) != '-' || message.charAt(index + 1) != '-') continue;
            if (index > start && message.charAt(index - 1) == '\\') continue;
            return index;
        }

        return -1;
    }

    private static String uneascape(final String message) {
        return message.replace("\\--", "--");
    }
}
